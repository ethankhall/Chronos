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
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.ehdev.chronos.lib.enums.Defines;
import com.ehdev.chronos.lib.enums.OvertimeOptions;
import com.ehdev.chronos.lib.types.Job;
import com.ehdev.chronos.lib.types.Punch;
import com.ehdev.chronos.lib.types.Task;
import com.ehdev.chronos.lib.types.holders.PunchPair;
import com.ehdev.chronos.lib.types.holders.TaskTable;
import com.kopysoft.chronos.views.helpers.RowElement;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class TodayAdapterPair extends BaseAdapter {

    private static final String TAG = Defines.TAG + " - TodayAdapterPair";

    Context gContext;
    TaskTable gTaskTable = new TaskTable();
    List<PunchPair> listOfPunchPairs = new LinkedList<PunchPair>();
    //public static final boolean enableLog = Defines.DEBUG_PRINT;
    public static final boolean enableLog = Defines.DEBUG_PRINT;
    private Job thisJob;
    
    public TodayAdapterPair(Context context, List<Punch> listOfPunches, Job job){
        gContext = context;
        thisJob = job;

        if(enableLog)
            for(Punch p : listOfPunches){
                Log.d(TAG, "Punch in: " + p.getTime());
            }

        //Create a map of tasks
        if(listOfPunches != null)
            for(Punch temp : listOfPunches){
                Task tempTask = temp.getTask();
                gTaskTable.insert(tempTask, temp);
            }

        generatePunchPair();
    }

    public void generatePunchPair(){
        listOfPunchPairs.clear();
        List<Punch> punches;
        List<Integer> tasks = gTaskTable.getTasks();
        if(enableLog) Log.d(TAG, "Number of Tasks: " + tasks.size());
        for(Integer curTask : tasks){
            punches = gTaskTable.getPunchesForKey(curTask);
            Collections.sort(punches);
            if(enableLog) Log.d(TAG, "Task Number: " + curTask);
            //for(Punch temp : punches){
            //    if(enableLog) Log.d(TAG, "Punch ID: " + temp.getID());
            //}

            for(int i = 0; i < punches.size(); i += 2){
                //if(enableLog) Log.d(TAG, "Size: " + punches.size());
                //if(enableLog) Log.d(TAG, "index: " + i);
                Punch inTime = punches.get(i);
                if(i < punches.size() - 1) {
                    Punch outTime = punches.get(i + 1);
                    listOfPunchPairs.add(new PunchPair(inTime, outTime));
                } else {
                    listOfPunchPairs.add(new PunchPair(inTime, null));
                }
            }
        }
        sort();
        notifyDataSetChanged();

        //For Debug
        //for(PunchPair pp : listOfPunchPairs){
        //    if(enableLog) Log.d(TAG, "In Time: " + pp.getInPunch().getTime().toString());
        //    if(enableLog) Log.d(TAG, "In Time: " + pp.getPunch1().getTime().toString());
        //    if(pp.getOutPunch() != null){
        //       if(enableLog) Log.d(TAG, "Out Time: " + pp.getOutPunch().getTime().toString());
        //        if(enableLog) Log.d(TAG, "Out Time: " + pp.getPunch2().getTime().toString());
        //    }
        //}
    }

    public void addPunch(Punch input){
        gTaskTable.insert(input.getTask(), input);
        generatePunchPair();
        notifyDataSetChanged();
    }

    /**
     * Sorts the elements in the list
     */
    private void sort(){
        Collections.sort(listOfPunchPairs);
    }

    @Override
    public int getCount() {
        return listOfPunchPairs.size();
    }

    @Override
    public PunchPair getItem(int i) {
        if(i > listOfPunchPairs.size())
            return null;
        return listOfPunchPairs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public Duration getTime(){
        return getTime(listOfPunchPairs, false);
    }

    public Duration getTime(boolean enabled){
        return getTime(listOfPunchPairs, enabled);
    }
    
    public static Duration getTime(List<PunchPair> punches, boolean allowNegative){
        Duration dur = new Duration(0);

        for(PunchPair pp : punches){
            if(enableLog) Log.d(TAG, "Punch Size: " + pp.getDuration());
            if(!pp.getInPunch().getTask().getEnablePayOverride())
                dur = dur.plus(pp.getDuration());
            else if(pp.getTask().getPayOverride() == 0)
                continue;
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
        return getPayableTime(listOfPunchPairs, thisJob ,false);
    }

    public float getPayableTime(boolean enabled){
        return getPayableTime(listOfPunchPairs, thisJob ,enabled);
    }

    public static float getPayableTime(List<PunchPair> punches, Job curJob, boolean handleNegative){
        float totalPay;
        if(enableLog && punches.size()!= 0)
            Log.d(TAG, "Pay Rate: " + curJob.getPayRate());

        totalPay = getTime(punches, true).getMillis();
        if( totalPay < 0 && handleNegative){
            totalPay += DateTime.now().getMillis();
        }

        /*
        System.out.println("Time: " + totalPay);
        System.out.println("overtime: " + curJob.isOverTimeEnabled());
        System.out.println("40 h week: " + curJob.isFortyHourWeek());
        System.out.println("overtime: " + curJob.getOvertime());
        System.out.println("doubletime: " + curJob.getDoubleTime());
        */
        if(curJob.getOvertimeOptions() == OvertimeOptions.DAY)
            totalPay = getPay((long)totalPay, curJob.getPayRate(), curJob.getOvertime(), curJob.getDoubleTime());
        else
            totalPay = getPay((long)totalPay, curJob.getPayRate(), 1000, 1000);

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

        if(view == null){
            view = new RowElement(gContext);
        }
        PunchPair pp = listOfPunchPairs.get(i);

        RowElement curr = (RowElement) view;
        TextView left = curr.left();
        TextView right = curr.right();
        TextView center = curr.center();

        //Set date format
        DateTimeFormatter fmt;
        if (!DateFormat.is24HourFormat(gContext))
            fmt = DateTimeFormat.forPattern("h:mm a");
        else
            fmt = DateTimeFormat.forPattern("HH:mm");

        //Set center text
        center.setText(pp.getInPunch().getTime().toString(fmt));

        //Set right text
        String rightString = "---     ";
        if(pp.getOutPunch() != null){
            rightString = pp.getOutPunch().getTime().toString(fmt);
        }
        right.setText(rightString);

        //Set left text
        left.setText(pp.getTask().getName());

        return curr;  //To change body of implemented methods use File | Settings | File Templates.
    }
    
    
}
