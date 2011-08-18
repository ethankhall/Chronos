package com.kopysoft.chronos;

/**
 * 			Copyright (C) 2011 by Ethan Hall
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 * 	in the Software without restriction, including without limitation the rights
 * 	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * 	copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 *
 */

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.kopysoft.chronos.RowHelper.RowHelperPastView;
import com.kopysoft.chronos.content.Chronos;
import com.kopysoft.chronos.content.StaticFunctions;
import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.enums.TimeFormat;
import com.kopysoft.chronos.singelton.ListenerObj;
import com.kopysoft.chronos.singelton.PreferenceSingleton;
import com.kopysoft.chronos.singelton.ViewingPayPeriod;
import com.kopysoft.chronos.types.Day;
import com.kopysoft.chronos.types.PayPeriod;

public class PastView extends ListActivity{

    //private Chronos chronoSaver = null;
    private PreferenceSingleton prefs = null;

    private static final String TAG = Defines.TAG + " - PV";

    private RowHelperPastView adapter = null;

    private double PAY_RATE = 8.75;
    private int[] date;
    private int weeks_in_pp;

    private float overtimeRate;
    private boolean overtimeEnable;
    private int overtimeSetting;

    private TimeFormat StringFormat = TimeFormat.HOUR_MIN_SEC;
    private ViewingPayPeriod holder = null;
    private updateAdapter updateAdapt = null;

    @Override
    public void onResume(){
        super.onResume();

        //chronoSaver = new Chronos(getApplicationContext());	//Connect to content provider
        updatePayRate();

        updateAdapt = new updateAdapter();
        updateAdapt.execute(getApplicationContext());

        holder.setWeek(date);

        //setListAdapter(adapter);

        //updateTime();
        fixNext();
    }

    @Override
    public void onPause(){
        super.onPause();
        if(updateAdapt != null){
            updateAdapt.cancel(true);
        }
        updateAdapt = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pastview);
        if ( Defines.DEBUG_PRINT ) Log.d(TAG, "PastView");

        ProgressDialog dialog = ProgressDialog.show(PastView.this, "",
                "Generating. Please wait...");

        //prefs.updatePreferences(getApplicationContext());

        //chronoSaver = new Chronos(getApplicationContext());	//Connect to content provider

        prefs = new PreferenceSingleton();
        weeks_in_pp = prefs.getWeeksInPP(getApplicationContext());
        StringFormat = prefs.getPrefViewTime(getApplicationContext());
        overtimeRate = prefs.getOvertimeRate(getApplicationContext());
        overtimeEnable = prefs.getOvertimeEnable(getApplicationContext());
        overtimeSetting = prefs.getOvertimeSetting(getApplicationContext());

        //updatePayRate();

        //int[] dateHold = Chronos.getDate(ppStart);
        int[] dateHold = prefs.getStartOfThisPP(getApplicationContext());
        weeks_in_pp = prefs.getWeeksInPP(getApplicationContext());
        int[] startOfThisPP =  Chronos.getPP(dateHold, weeks_in_pp);

        GregorianCalendar cal = new GregorianCalendar(startOfThisPP[0], startOfThisPP[1], startOfThisPP[2]);
        cal.add(GregorianCalendar.DAY_OF_YEAR, weeks_in_pp * 7);
        int[] endOfThisPP = {cal.get(GregorianCalendar.YEAR), cal.get(GregorianCalendar.MONTH),
                cal.get(GregorianCalendar.DAY_OF_MONTH)};

        PayPeriod thisPP = new PayPeriod(startOfThisPP, endOfThisPP, getApplicationContext());
        date = startOfThisPP;

        //for Prefereneces
        ListenerObj.getInstance().addPropertyChangeListener(new PropertyChangeListener(){

            public void propertyChange(PropertyChangeEvent event) {
                weeks_in_pp = prefs.getWeeksInPP(getApplicationContext());
                StringFormat = prefs.getPrefViewTime(getApplicationContext());
                overtimeRate = prefs.getOvertimeRate(getApplicationContext());
                overtimeEnable = prefs.getOvertimeEnable(getApplicationContext());
                overtimeSetting = prefs.getOvertimeSetting(getApplicationContext());
                updatePayRate();

                updateTime();
                fixNext();
                adapter.setFormat(StringFormat);
                adapter.updateAll();

            }

        });

        adapter = new RowHelperPastView(getApplicationContext(), thisPP, StringFormat);
        adapter.setFormat(StringFormat);
        setListAdapter(adapter);

        holder = ViewingPayPeriod.getInstance();
        holder.setWeek(startOfThisPP);

        //For selecting note
        getListView().setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Day temp = adapter.getItem(position);
                int[] day = temp.getDay();

                Intent intent = new Intent(getApplicationContext(), com.kopysoft.chronos.subActivites.ViewDay.class);
                intent.putExtra("year", day[0]);
                intent.putExtra("month", day[1]);
                intent.putExtra("day", day[2]);

                startActivity(intent);

            }
        });

        dialog.cancel();
        //fixNext();

    }

    private void updatePayRate(){
        //update pay rate
        PAY_RATE = prefs.getPayRate(getApplicationContext());

        //Hide or show the time amount
        boolean showPay = prefs.getShowPay(getApplicationContext());
        TextView payTitle = (TextView)findViewById(R.id.pastViewAmountMadeLabel);
        TextView payValue = (TextView)findViewById(R.id.pastViewAmountMade);
        if(!showPay){
            payTitle.setVisibility(View.GONE);
            payValue.setVisibility(View.GONE);
        } else {
            payTitle.setVisibility(View.VISIBLE);
            payValue.setVisibility(View.VISIBLE);
        }
    }

    private long getTimeForMoney(){
        long returnValue = 0;
        long tempTime;
        Day temp;

        //Log.d(TAG, "overtime: " + overtimeEnable);
        for(int i = 0; i < adapter.getCount(); i++){
            temp = adapter.getItem(i);
            if ( temp.getTimeWithBreaks() >= 0 ){
                tempTime = temp.getTimeWithBreaks();
                if(overtimeSetting == Defines.OVERTIME_8HOUR && overtimeEnable){
                    if(tempTime > Defines.SECONDS_IN_HOUR * 8){
                        returnValue += Defines.SECONDS_IN_HOUR * 8;
                        returnValue += (tempTime - Defines.SECONDS_IN_HOUR * 8) * overtimeRate;

                    } else {
                        returnValue += tempTime;
                    }
                }  else {
                    returnValue += tempTime;
                    //Log.d(TAG, "time for day: " + tempTime);
                }
            }
        }

        if (overtimeSetting == Defines.OVERTIME_40HOUR && overtimeEnable){
            PreferenceSingleton prefs= new PreferenceSingleton();
            double overTime = prefs.getRegularTime(getApplicationContext());
            double doubleTime = prefs.getDoubleTime(getApplicationContext());

            Log.d(TAG, "Normal Val: " + overTime);
            Log.d(TAG, "Overtime Val: " + doubleTime);


            returnValue = 0;
            for(int i = 0; i < prefs.getWeeksInPP(getApplicationContext()); i++){
                long doubletime_calc = 0;
                long overtime_calc = 0;
                long weekTemp = 0;
                for(int j = 0; j < 7; j++){
                    temp = adapter.getItem(i * 7 + j);
                    if(temp.getTimeWithBreaks() > 0)
                        weekTemp += temp.getTimeWithBreaks();
                }

                if ( weekTemp > Defines.SECONDS_IN_HOUR * doubleTime){
                    doubletime_calc = (long)(weekTemp - Defines.SECONDS_IN_HOUR * doubleTime);
                    weekTemp = (long)(Defines.SECONDS_IN_HOUR * doubleTime);
                }

                if ( weekTemp > Defines.SECONDS_IN_HOUR * overTime){
                    overtime_calc = (long)(weekTemp - Defines.SECONDS_IN_HOUR * overTime);
                    weekTemp = (long)(Defines.SECONDS_IN_HOUR * overTime);
                }

                Log.d(TAG, "Normal: " + weekTemp);
                Log.d(TAG, "Overtime: " + overtime_calc);
                Log.d(TAG, "Double time: " + doubletime_calc);

                returnValue = returnValue + (long)(weekTemp +
                        overtime_calc * overtimeRate +
                        doubletime_calc * 2);
            }
        }
        return returnValue;
    }

    private long getTime(){
        long returnValue = 0;
        long tempTime;
        Day temp;

        for(int i = 0; i < adapter.getCount(); i++){
            temp = adapter.getItem(i);
            if ( temp.getTimeWithBreaks() >= 0 ){
                tempTime = temp.getTimeWithBreaks();

                returnValue += tempTime;
            }
        }
        return returnValue;

    }

    private void updateTime(){
        updateTime(getTime(), getTimeForMoney());
    }
    private void updateTime(long time, long calcTime){
        TextView timePay = (TextView) findViewById(R.id.pastViewTimeTotal);


        timePay.setText(StaticFunctions.generateTimeWeek(time, StringFormat, false));

        if ( Defines.DEBUG_PRINT ) Log.d(TAG, "Pay Rate: " + PAY_RATE);
        if ( Defines.DEBUG_PRINT ) Log.d(TAG, "Dol Amt:" +
                StaticFunctions.generateTimeWeek(calcTime, StringFormat, false));

        String dolAmount = StaticFunctions.generateDollarAmount(calcTime, PAY_RATE);

        if ( Defines.DEBUG_PRINT ) Log.d(TAG, "Amount: " + dolAmount);

        TextView timeView = (TextView) findViewById(R.id.pastViewAmountMade);
        timeView.setText(dolAmount);
    }

    public void callback(View v){
        switch(v.getId()){
            case R.id.next:
                nextButton();
                break;
            case R.id.prev:
                prevButton();
                break;
            case R.id.current:
                currentButton();
                break;
            default:
                break;
        }
        if(Defines.DEBUG_PRINT) Log.d(TAG, "Date was set to: " +
                String.format("(%d/%d/%d)", date[0], date[1], date[2]));
    }

    private void currentButton(){

        //int[]dateHold = Chronos.getDate(ppStart);
        int[]dateHold = prefs.getStartOfThisPP(getApplicationContext());
        weeks_in_pp = prefs.getWeeksInPP(getApplicationContext());
        date = Chronos.getPP(dateHold, weeks_in_pp);
        GregorianCalendar cal = new GregorianCalendar(date[0], date[1], date[2]);
        cal.add(GregorianCalendar.DAY_OF_YEAR, 7 * weeks_in_pp);
        int[] endOfPP = {
                cal.get(GregorianCalendar.YEAR),
                cal.get(GregorianCalendar.MONTH),
                cal.get(GregorianCalendar.DAY_OF_MONTH)
        };

        PayPeriod thisPP = new PayPeriod(date, endOfPP, getApplicationContext());
        for(int i = 0; i < thisPP.size(); i++){
            adapter.updateDay(i, thisPP.get(i));
        }
        adapter.notifyDataSetChanged();
        updateTime();

        holder.setWeek(date);
        fixNext();
    }

    private void nextButton(){
        GregorianCalendar cal = new GregorianCalendar(date[0], date[1], date[2]);
        GregorianCalendar newCal = new GregorianCalendar();
        cal.add(Calendar.DAY_OF_YEAR, weeks_in_pp * 7);
        if(newCal.compareTo(cal) == -1){
            return;	//Prevent Time Paradox
        }

        date[0] = cal.get(GregorianCalendar.YEAR);
        date[1] = cal.get(GregorianCalendar.MONTH);
        date[2] = cal.get(GregorianCalendar.DAY_OF_MONTH);

        cal.add(GregorianCalendar.DAY_OF_YEAR, weeks_in_pp * 7);
        int[] endOfPP = {
                cal.get(GregorianCalendar.YEAR),
                cal.get(GregorianCalendar.MONTH),
                cal.get(GregorianCalendar.DAY_OF_MONTH)
        };

        holder.setWeek(date);

        PayPeriod thisPP = new PayPeriod(date, endOfPP, getApplicationContext());
        for(int i = 0; i < thisPP.size(); i++){
            adapter.updateDay(i, thisPP.get(i));
        }

        adapter.notifyDataSetChanged();
        updateTime();
        fixNext();
    }

    private void prevButton(){
        GregorianCalendar cal = new GregorianCalendar(date[0], date[1], date[2]);
        int[] endOfPP = { date[0], date[1], date[2] };
        cal.add(Calendar.DAY_OF_YEAR, weeks_in_pp * -7);
        date[0] = cal.get(GregorianCalendar.YEAR);
        date[1] = cal.get(GregorianCalendar.MONTH);
        date[2] = cal.get(GregorianCalendar.DAY_OF_MONTH);

        holder.setWeek(date);

        PayPeriod thisPP = new PayPeriod(date, endOfPP, getApplicationContext());
        for(int i = 0; i < thisPP.size(); i++){
            adapter.updateDay(i, thisPP.get(i));
        }
        adapter.notifyDataSetChanged();
        updateTime();
        fixNext();
    }

    private void fixNext(){
        GregorianCalendar cal = new GregorianCalendar(date[0], date[1], date[2]);
        GregorianCalendar newCal = new GregorianCalendar();
        cal.add(Calendar.DAY_OF_YEAR, weeks_in_pp * 7);

        Button nextButton = (Button) findViewById(R.id.next);
        if(newCal.compareTo(cal) == -1){
            nextButton.setEnabled(false);
        } else {
            nextButton.setEnabled(true);
        }

    }

    private class updateAdapter extends AsyncTask<Context, Object, Void> {
        int[] endOfThisPP = new int[3];
        int[] startOfThisPP = null;
        protected void onPreExecute(){

            ViewingPayPeriod holder = ViewingPayPeriod.getInstance();
            startOfThisPP = holder.getWeek();

            int weeks_in_pp = prefs.getWeeksInPP(getApplicationContext());

            GregorianCalendar cal = new GregorianCalendar(startOfThisPP[0],
                    startOfThisPP[1], startOfThisPP[2]);

            cal.add(GregorianCalendar.DAY_OF_YEAR, weeks_in_pp * 7);
            endOfThisPP[0] = cal.get(GregorianCalendar.YEAR);
            endOfThisPP[1] = cal.get(GregorianCalendar.MONTH);
            endOfThisPP[2] = cal.get(GregorianCalendar.DAY_OF_MONTH);
        }

        protected void onProgressUpdate(Object... progress) {
            adapter.updateDay((Integer)progress[0], (Day)progress[1]);
            updateTime();
        }

        protected void onPostExecute (Void param){
            updateTime();
        }

        @Override
        protected Void doInBackground(Context... param) {
            PayPeriod thisPP = new PayPeriod(date, endOfThisPP, param[0]);

            for(int i = 0; i < thisPP.size(); i++){
                publishProgress(i, thisPP.get(i));
            }
            return null;
        }

    }
}
