package com.kopysoft.chronos;

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
import com.kopysoft.chronos.singelton.PreferenceSingelton;
import com.kopysoft.chronos.singelton.ViewingPayPeriod;
import com.kopysoft.chronos.types.Day;
import com.kopysoft.chronos.types.PayPeriod;

public class PastView extends ListActivity{

	//private Chronos chronoSaver = null;
	PreferenceSingelton prefs = null;

	private static final String TAG = Defines.TAG + " - PV";

	RowHelperPastView adapter = null;

	private double PAY_RATE = 8.75;
	int[] date;
	int weeks_in_pp;

	float overtimeRate;
	boolean overtimeEnable;
	int overtimeSetting;

	TimeFormat StringFormat = TimeFormat.HOUR_MIN_SEC;
	ViewingPayPeriod holder = null;
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
		
		prefs = PreferenceSingelton.getInstance();
		ProgressDialog dialog = ProgressDialog.show(PastView.this, "",
			"Generating. Please wait...");

		prefs = PreferenceSingelton.getInstance();
		//prefs.updatePreferences(getApplicationContext());

		//chronoSaver = new Chronos(getApplicationContext());	//Connect to content provider

		weeks_in_pp = prefs.getWeeksInPP();
		StringFormat = prefs.getViewStringFormat();
		overtimeRate = prefs.getOvertimeRate();
		overtimeEnable = prefs.isOvertimeEnable();
		overtimeSetting = prefs.getOvertimeSetting();

		//updatePayRate();		

		//int[] dateHold = Chronos.getDate(ppStart);
		int[] dateHold = prefs.getStartOfThisPP();
		weeks_in_pp = prefs.getWeeksInPP();
		int[] startOfThisPP =  Chronos.getPP(dateHold, weeks_in_pp);

		GregorianCalendar cal = new GregorianCalendar(startOfThisPP[0], startOfThisPP[1], startOfThisPP[2]);
		cal.add(GregorianCalendar.DAY_OF_YEAR, weeks_in_pp * 7);
		int[] endOfThisPP = {cal.get(GregorianCalendar.YEAR), cal.get(GregorianCalendar.MONTH),
				cal.get(GregorianCalendar.DAY_OF_MONTH)};

		PayPeriod thisPP = new PayPeriod(startOfThisPP, endOfThisPP, getApplicationContext());
		date = startOfThisPP;

		//for Prefereneces
		prefs.addPropertyChangeListener(new PropertyChangeListener(){

			public void propertyChange(PropertyChangeEvent event) {	
				weeks_in_pp = prefs.getWeeksInPP();
				StringFormat = prefs.getViewStringFormat();
				overtimeRate = prefs.getOvertimeRate();
				overtimeEnable = prefs.isOvertimeEnable();
				overtimeSetting = prefs.getOvertimeSetting();
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

				Intent intent = new Intent(getApplicationContext(), com.kopysoft.chronos.note.ViewNote.class);
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
		PAY_RATE = prefs.getPayRate();	

		//Hide or show the time amount
		boolean showPay = prefs.isShowPay();
		TextView payTitle = (TextView)findViewById(R.id.pastViewAmountMadeLabel);
		TextView payValue = (TextView)findViewById(R.id.pastViewAmountMade);
		if(showPay == false){
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
		for(int i = 0; i < adapter.getCount(); i++){
			temp = adapter.getItem(i);
			if ( temp.getTimeWithBreaks() >= 0 ){
				tempTime = temp.getTimeWithBreaks();
				if(overtimeSetting == Defines.OVERTIME_8HOUR && overtimeEnable == true){
					if(tempTime > Defines.SECONDS_IN_HOUR * 8){
						returnValue += Defines.SECONDS_IN_HOUR * 8;
						returnValue += (tempTime - Defines.SECONDS_IN_HOUR * 8) * overtimeRate;	

					} else {
						returnValue += temp.getTimeWithBreaks();
					}
				}  else {
					returnValue += temp.getTimeWithBreaks();
				}
			}
		}

		if (overtimeSetting == Defines.OVERTIME_40HOUR && overtimeEnable == true ){
			returnValue = 0;
			long weekTemp = 0;
			for(int i = 0; i < prefs.getWeeksInPP(); i++){
				for(int j = 0; j < 7; j++){
					temp = adapter.getItem(i * 7 + j);
					weekTemp += temp.getTimeWithBreaks();
				}
				if ( weekTemp > Defines.SECONDS_IN_HOUR * 40){
					tempTime = weekTemp - Defines.SECONDS_IN_HOUR * 40;
					weekTemp = Defines.SECONDS_IN_HOUR * 40;
					weekTemp += tempTime * overtimeRate;
				}
				returnValue += weekTemp;
			}
		}
		return returnValue / Defines.MS_TO_SECOND;
	}

	private long getTime(){
		long returnValue = 0;
		Day temp;
		for(int i = 0; i < adapter.getCount(); i++){
			temp = adapter.getItem(i);
			if ( temp.getSeconds() >= 0 ){
				returnValue += temp.getSeconds();
			}
		}

		return returnValue;
	}

	private void updateTime(){
		long time = getTime();
		long money = getTimeForMoney();
		updateTime(time, money);
	}
	private void updateTime(long time, long calcTime){
		TextView timePay = (TextView) findViewById(R.id.pastViewTimeTotal);

		timePay.setText(StaticFunctions.generateTimeString(time, StringFormat, false));

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
		int[]dateHold = prefs.getStartOfThisPP();
		weeks_in_pp = prefs.getWeeksInPP();
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
			
			PreferenceSingelton prefs = PreferenceSingelton.getInstance();		
			int weeks_in_pp = prefs.getWeeksInPP();

			GregorianCalendar cal = new GregorianCalendar(startOfThisPP[0], 
					startOfThisPP[1], startOfThisPP[2]);
			
			cal.add(GregorianCalendar.DAY_OF_YEAR, weeks_in_pp * 7);
			endOfThisPP[0] = cal.get(GregorianCalendar.YEAR);
			endOfThisPP[1] = cal.get(GregorianCalendar.MONTH);
			endOfThisPP[2] = cal.get(GregorianCalendar.DAY_OF_MONTH);
		}

		protected void onProgressUpdate(Object... progress) {
			adapter.updateDay((Integer)progress[0], (Day)progress[1]);
		}

		protected void onPostExecute (Void param){
			updateTime();
		}

		@Override
		protected Void doInBackground(Context... param) {
			PayPeriod thisPP = new PayPeriod(date, endOfThisPP, (Context)param[0]);
			
			for(int i = 0; i < thisPP.size(); i++){
				publishProgress((Object)i, (Object)thisPP.get(i));
			}
			return null;
		}

	}
}
