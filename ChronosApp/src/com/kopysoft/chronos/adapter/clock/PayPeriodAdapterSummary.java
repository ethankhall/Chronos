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
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import com.kopysoft.chronos.content.Chronos;
import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.types.Job;
import com.kopysoft.chronos.types.holders.PayPeriodHolder;
import com.kopysoft.chronos.types.holders.PunchTable;
import org.joda.time.DateMidnight;

public class PayPeriodAdapterSummary extends BaseExpandableListAdapter {

    private static final String TAG = Defines.TAG + " - PayPeriodAdapterSummary";

    private Context gContext;
    PayPeriodHolder gPayPeriod;
    private PunchTable gPunchesByDay;

    
    public PayPeriodAdapterSummary(Context context, Job inJob){
        gContext = context;
        Chronos chrono = new Chronos(gContext);

        gPunchesByDay = chrono.getAllPunchesForThisPayPeriodByJob(inJob);

        gPayPeriod = new PayPeriodHolder(inJob);

    }

    @Override
    public int getGroupCount() {
        return gPayPeriod.getDays();
    }

    @Override
    public int getChildrenCount(int i) {
        DateMidnight start = gPayPeriod.getStartOfPayPeriod();
        start = start.plus(i);

        return gPunchesByDay.getPunchesByDay(start).size();
    }

    @Override
    public Object getGroup(int i) {
        DateMidnight start = gPayPeriod.getStartOfPayPeriod();
        start = start.plus(i);

        return gPunchesByDay.getPunchesByDay(start);
    }

    @Override
    public Object getChild(int i, int i1) {
        DateMidnight start = gPayPeriod.getStartOfPayPeriod();
        start = start.plus(i);

        return gPunchesByDay.getPunchesByDay(start).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return gPunchesByDay.getDays().get(i).getMillis();
    }

    @Override
    public long getChildId(int i, int i1) {
        DateMidnight start = gPayPeriod.getStartOfPayPeriod();
        start = start.plus(i);

        return gPunchesByDay.getPunchesByDay(start).get(i1).getID();
    }

    @Override
    public boolean hasStableIds() {
        return false; 
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
