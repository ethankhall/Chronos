/*******************************************************************************
 * Copyright (c) 2011 Ethan Hall
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
import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.types.Punch;
import com.kopysoft.chronos.types.Task;
import com.kopysoft.chronos.types.holders.PunchPair;
import com.kopysoft.chronos.types.holders.TaskTable;
import com.kopysoft.chronos.views.helpers.RowElement;
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
    public static final boolean enableLog = Defines.DEBUG_PRINT;

    public TodayAdapterPair(Context context, List<Punch> listOfPunches){
        gContext = context;


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
        Duration dur = new Duration(0);
        for(PunchPair pp : listOfPunchPairs){
            if(enableLog) Log.d(TAG, "Punch Size: " + pp.getInterval().toDurationMillis());
            dur = dur.plus(pp.getInterval().toDuration());
        }

        return dur;
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

        //Set left text
        left.setText(pp.getInPunch().getTime().toString(fmt));

        //Set right text
        String rightString = "---     ";
        if(pp.getOutPunch() != null){
            rightString = pp.getOutPunch().getTime().toString(fmt);
        }
        right.setText(rightString);

        //Set Center text
        center.setText(pp.getTask().getName());

        return curr;  //To change body of implemented methods use File | Settings | File Templates.
    }
    
    
}
