/*******************************************************************************
 * Copyright (c) 2011-2012 Ethan Hall
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 ******************************************************************************/

package com.kopysoft.chronos.adapter.clock;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.types.Job;
import com.kopysoft.chronos.types.holders.PunchPair;
import com.kopysoft.chronos.types.holders.PunchTable;
import com.kopysoft.chronos.views.helpers.RowElement;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

public class PayPeriodAdapterList extends BaseAdapter {

    private static final String TAG = Defines.TAG + " - PayPeriodAdapterList";

    private Context gContext;
    private PunchTable gPunchesByDay;
    private Job thisJob;
    private static final boolean enableLog = Defines.DEBUG_PRINT;

    public PayPeriodAdapterList(Context context, PunchTable punchTable, Job inJob){
        gContext = context;
        thisJob = inJob;

        gPunchesByDay = punchTable;
        //gPayPeriod = new PayPeriodHolder(inJob);
        if(enableLog) Log.d(TAG, "Size of Punches: " + gPunchesByDay.getDays().size());
    }

    public void update(PunchTable table){
        gPunchesByDay = table;
    }

    @Override
    public int getCount() {
        return gPunchesByDay.getDays().size();
    }

    @Override
    public List<PunchPair> getItem(int i) {
        if(i > gPunchesByDay.getDays().size())
            return null;
        return gPunchesByDay.getPunchPair(gPunchesByDay.getDays().get(i));
    }

    public DateTime getDate(int i){
        if(i > gPunchesByDay.getDays().size())
            return null;
        return gPunchesByDay.getDays().get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
    
    public Duration getTime(){
        Duration dur = new Duration(0);

        for(DateTime date : gPunchesByDay.getDays()){
            dur = dur.plus(getTime(gPunchesByDay.getPunchPair(date)));
        }
        if(enableLog) Log.d(TAG, "Duration: " + dur);

        return dur;
    }

    public static Duration getTime(PunchTable table){
        Duration dur = new Duration(0);

        for(DateTime date : table.getDays()){
            dur = dur.plus(getTime(table.getPunchPair(date)));
        }
        if(enableLog) Log.d(TAG, "Duration: " + dur);

        return dur;
    }

    public static Duration getTime(List<PunchPair> punches){
        return getTime(punches, false);
    }

    public static Duration getTime(List<PunchPair> punches, boolean allowNegative){
        Duration dur = new Duration(0);

        for(PunchPair pp : punches){
            if(enableLog) Log.d(TAG, "Punch Size: " + pp.getDuration());
            if(!pp.getInPunch().getTask().getEnablePayOverride())
                dur = dur.plus(pp.getDuration());
            else if(pp.getInPunch().getTask().getPayOverride() > 0)
                dur = dur.plus(pp.getDuration());
            else
                dur = dur.minus(pp.getDuration());
        }

        if(dur.getMillis() < 0 && !allowNegative)
            dur = new Duration(0);

        return dur;
    }

    public float getPayableTime(){
        return getPayableTime(gPunchesByDay, thisJob);
    }

    public static float getPayableTime(PunchTable punchTable, Job curJob){
        float totalPay = 0.0f;

        for(DateTime date : punchTable.getDays()){

            if(curJob.isFortyHourWeek()){
                totalPay += getTime(punchTable.getPunchPair(date), false).getMillis();
            } else {
                //totalPay += getPay(getTime(punchTable.getPunchPair(date), false).getMillis(),
                //        curJob.getPayRate(), 8, 10);
                totalPay += getPay(getTime(punchTable.getPunchPair(date), false).getMillis(),
                        curJob.getPayRate(), curJob.getOvertime(), curJob.getDoubleTime());
            }
            //Log.d(TAG, "pay: " + totalPay);;
        }

        if(curJob.isFortyHourWeek()){
            totalPay = getPay((long)totalPay, curJob.getPayRate(), curJob.getOvertime(), curJob.getDoubleTime());
        }

        if(totalPay < 0)
            totalPay = 0;

        return totalPay;
    }

    private static float hoursToMilli(float hours){
        return hours * 60 * 60 * 1000;
    }

    private static float getPay(long inputTime, float payRate, float overTimeValue, float doubleTimeValue){
        float totalPay;
        if(inputTime > hoursToMilli(doubleTimeValue)){
            totalPay = (inputTime - hoursToMilli(doubleTimeValue))
                    * 2 * (payRate / 60 / 60 / 1000);
            totalPay += (hoursToMilli(doubleTimeValue - overTimeValue) * 1.5 )
                    * (payRate / 60 / 60 / 1000);
            totalPay += payRate * overTimeValue;
        } else if(inputTime > hoursToMilli(overTimeValue)){
            totalPay = (float)((inputTime - hoursToMilli(overTimeValue)) * 1.5 )
                    * (payRate / 60 / 60 / 1000);
            totalPay += payRate * overTimeValue ;
        } else {
            totalPay = payRate / 60 / 60 / 1000 * inputTime;
        }
        return totalPay;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        RowElement curr = new RowElement(gContext, 10, 15, 15, 10);
        TextView left = curr.left();
        TextView right = curr.right();
        TextView center = curr.center();

        //Reset current view
        center.setText("");
        left.setText("");

        Duration dur = getTime(getItem(i), true);

        if(enableLog) Log.d(TAG, "Dur Total: " + dur.getMillis());

        DateTimeFormatter fmt = DateTimeFormat.forPattern("E, MMM d, yyyy");
        String time = fmt.print(gPunchesByDay.getDays().get(i));
        //String time = String.format("Hours %d:%02d ", dur.toPeriod().getHours(), dur.toPeriod().getMinutes());
        left.setText(time);
        //center.setTypeface(null, Typeface.ITALIC);




        if(dur.getMillis() >= 0)   {
            time = String.format("%02d:%02d ", dur.toPeriod().getHours(), dur.toPeriod().getMinutes());
            right.setText(time);
        } else {
            right.setText("--:-- ");
    }

        return curr;
    }
}
