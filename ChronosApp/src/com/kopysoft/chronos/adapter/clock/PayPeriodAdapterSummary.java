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
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import com.kopysoft.chronos.content.Chronos;
import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.types.Job;
import com.kopysoft.chronos.types.holders.PunchPair;
import com.kopysoft.chronos.types.holders.PunchTable;
import com.kopysoft.chronos.view.RowElement;
import org.joda.time.DateMidnight;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

public class PayPeriodAdapterSummary extends BaseExpandableListAdapter {

    private static final String TAG = Defines.TAG + " - PayPeriodAdapterSummary";

    private Context gContext;
    //PayPeriodHolder gPayPeriod;
    private PunchTable gPunchesByDay;


    public PayPeriodAdapterSummary(Context context, Job inJob){
        gContext = context;
        Chronos chrono = new Chronos(gContext);

        gPunchesByDay = chrono.getAllPunchesForThisPayPeriodByJob(inJob);
        //gPayPeriod = new PayPeriodHolder(inJob);
        Log.d(TAG, "Size of Punches: " + gPunchesByDay.getDays().size());
    }

    @Override
    public int getGroupCount() {
        return gPunchesByDay.getDays().size();
        //return gPayPeriod.getDays();
    }

    @Override
    public int getChildrenCount(int i) {
        DateMidnight start = gPunchesByDay.getDays().get(i);

        return gPunchesByDay.getPunchPair(start).size();
    }

    @Override
    public Object getGroup(int i) {
        DateMidnight start = gPunchesByDay.getPayPeriodInfo().getStartOfPayPeriod();
        start = start.plusDays(i);

        return gPunchesByDay.getPunchPair(start);
    }

    @Override
    public PunchPair getChild(int i, int i1) {
        DateMidnight start = gPunchesByDay.getPayPeriodInfo().getStartOfPayPeriod();
        start = start.plusDays(i);

        return gPunchesByDay.getPunchPair(start).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return gPunchesByDay.getDays().get(i).getMillis();
    }

    @Override
    public long getChildId(int i, int i1) {
        DateMidnight start = gPunchesByDay.getPayPeriodInfo().getStartOfPayPeriod();
        start = start.plusDays(i);

        return gPunchesByDay.getPunchesByDay(start).get(i1).getID();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {

        RowElement curr = new RowElement(gContext, 10, 15, 15, 10);
        TextView left = curr.left();
        TextView right = curr.right();
        TextView center = curr.center();

        //Reset current view
        center.setText("");
        left.setText("");

        DateMidnight start = gPunchesByDay.getPayPeriodInfo().getStartOfPayPeriod();
        start = start.plusDays(i);
        //Log.d(TAG, "Start of " + i + ": " + start.getMillis());
        List<PunchPair> ppList = gPunchesByDay.getPunchPair(start);
        //Log.d(TAG, "ppList: " + ppList.size());

        Duration dur = new Duration(0);
        for(PunchPair pp : ppList){
            //Log.d(TAG, "Punch Size: " + pp.getInterval().toDurationMillis());
            dur = dur.plus(pp.getInterval().toDuration());
        }

        //Log.d(TAG, "Dur Total: " + dur.getMillis());

        DateTimeFormatter fmt = DateTimeFormat.forPattern("E, MMM d, yyyy");
        String time = fmt.print(start);
        //String time = String.format("Hours %d:%02d ", dur.toPeriod().getHours(), dur.toPeriod().getMinutes());
        center.setText(time);
        //center.setTypeface(null, Typeface.ITALIC);


        time = String.format("%02d:%02d ", dur.toPeriod().getHours(), dur.toPeriod().getMinutes());
        right.setText(time);

        return curr;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        DateMidnight start = gPunchesByDay.getPayPeriodInfo().getStartOfPayPeriod();
        start = start.plusDays(i);
        PunchPair pp = gPunchesByDay.getPunchPair(start).get(i1);
        
        if(view == null){
            view = new RowElement(gContext);
        }

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

        return curr;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
