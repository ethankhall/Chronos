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
import com.ehdev.chronos.lib.enums.Defines;
import com.ehdev.chronos.lib.enums.OvertimeOptions;
import com.ehdev.chronos.lib.enums.WeekendOverride;
import com.ehdev.chronos.lib.types.Job;
import com.ehdev.chronos.lib.overtime.DurationHolder;
import com.ehdev.chronos.lib.types.holders.PunchPair;
import com.ehdev.chronos.lib.types.holders.PunchTable;
import com.kopysoft.chronos.views.helpers.RowElement;
import org.joda.time.*;
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

    public DurationHolder getTime(WeekendOverride sat, WeekendOverride sun){
        DurationHolder durHolder = new DurationHolder();

        for(DateTime date : gPunchesByDay.getDays()){

            if(date.getDayOfWeek() == DateTimeConstants.SATURDAY && sat != WeekendOverride.NONE){
                Duration temp = getTime(gPunchesByDay.getPunchPair(date));
                durHolder.addSaturdayPay(date.toDateMidnight(), temp);

            } else if(date.getDayOfWeek() == DateTimeConstants.SUNDAY && sun != WeekendOverride.NONE){
                Duration temp = getTime(gPunchesByDay.getPunchPair(date));
                durHolder.addSundayPay(date.toDateMidnight(), temp);

            } else {
                Duration temp = getTime(gPunchesByDay.getPunchPair(date));
                durHolder.addNormalPay(date.toDateMidnight(), temp);
            }
        }

        return durHolder;
    }

    @Deprecated
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

    static public float getPayableTime(DurationHolder durationHolder, Job curJob){

        float totalPay = 0.0f;
        float savedTime = 0.0f;

        for(DurationHolder.DurationWrapper date : durationHolder.getNormalDates()){

            if(curJob.getOvertimeOptions() == OvertimeOptions.DAY){
                totalPay += getPay(date.duration.getMillis(),
                        curJob.getPayRate(), curJob.getOvertime(), curJob.getDoubleTime());
            } else if(curJob.getOvertimeOptions() == OvertimeOptions.WEEK_DAY) {
                savedTime += getPay(date.duration.getMillis(),
                        curJob.getPayRate(), 8, 10);
                totalPay += date.duration.getMillis();
            } else {
                totalPay += date.duration.getMillis();
            }
            //Log.d(TAG, "pay: " + totalPay);;
        }

        if(curJob.getOvertimeOptions() == OvertimeOptions.WEEK){
            totalPay = getPay((long)totalPay, curJob.getPayRate(), curJob.getOvertime(), curJob.getDoubleTime());
        }  else if(!curJob.isOverTimeEnabled()){
            totalPay = getPay((long)totalPay, curJob.getPayRate(), 1000, 1000);
        }  else if (curJob.getOvertimeOptions() == OvertimeOptions.WEEK_DAY){
            float week = getPay((long)totalPay, curJob.getPayRate(), curJob.getOvertime(), curJob.getDoubleTime());
            //float day = getPay((long)savedTime, curJob.getPayRate(), 1000, 1000);
            totalPay = (week > savedTime) ? week : savedTime;

            Log.d(TAG, "Week time: " + week);
            //Log.d(TAG, "Day time: " + day);
            Log.d(TAG, "Total Pay: " + totalPay);
            Log.d(TAG, "Saved Time: " + savedTime);
        }


        if(curJob.getSaturdayOverride() == WeekendOverride.OVERTIME){
            float dur = durationHolder.getSaturdayDuration().getMillis();
            dur = (long)(dur * 1.5 * curJob.getPayRate() / 60 / 60 / 1000);
            totalPay += dur;
        } else if(curJob.getSaturdayOverride() == WeekendOverride.DOUBLETIME){
            float dur = durationHolder.getSaturdayDuration().getMillis();
            dur = (long)(dur * 2 * curJob.getPayRate() / 60 / 60 / 1000);
            totalPay += dur;
        }

        if(curJob.getSundayOverride() == WeekendOverride.OVERTIME){
            float dur = durationHolder.getSundayDuration().getMillis();
            dur = (long)(dur * 1.5 * curJob.getPayRate() / 60 / 60 / 1000);
            totalPay += dur;
        } else if(curJob.getSundayOverride() == WeekendOverride.DOUBLETIME){
            float dur = durationHolder.getSundayDuration().getMillis();
            dur = (long)(dur * 2 * curJob.getPayRate() / 60 / 60 / 1000);
            totalPay += dur;
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
        Log.d(TAG, "Input: " + inputTime + ", output: " + totalPay);
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
