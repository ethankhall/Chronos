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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.kopysoft.chronos.content.Chronos;
import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.types.Job;
import com.kopysoft.chronos.types.holders.PunchPair;
import com.kopysoft.chronos.types.holders.PunchTable;
import com.kopysoft.chronos.view.RowElement;
import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

public class PayPeriodAdapterList extends BaseAdapter {

    private static final String TAG = Defines.TAG + " - PayPeriodAdapterList";

    private Context gContext;
    private PunchTable gPunchesByDay;


    public PayPeriodAdapterList(Context context, Job inJob){
        gContext = context;
        Chronos chrono = new Chronos(gContext);

        gPunchesByDay = chrono.getAllPunchesForThisPayPeriodByJob(inJob);
        chrono.close();
        //gPayPeriod = new PayPeriodHolder(inJob);
        Log.d(TAG, "Size of Punches: " + gPunchesByDay.getDays().size());
    }

    public PayPeriodAdapterList(Context context, PunchTable punchTable){
        gContext = context;

        gPunchesByDay = punchTable;
        //gPayPeriod = new PayPeriodHolder(inJob);
        Log.d(TAG, "Size of Punches: " + gPunchesByDay.getDays().size());
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
        return gPunchesByDay.getDays().get(i).toDateTime();
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
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

        List<PunchPair> ppList = getItem(i);

        Duration dur = new Duration(0);
        for(PunchPair pp : ppList){
            //Log.d(TAG, "Punch Size: " + pp.getInterval().toDurationMillis());
            dur = dur.plus(pp.getInterval().toDuration());
        }

        Log.d(TAG, "Dur Total: " + dur.getMillis());

        DateTimeFormatter fmt = DateTimeFormat.forPattern("E, MMM d, yyyy");
        String time = fmt.print(gPunchesByDay.getDays().get(i));
        //String time = String.format("Hours %d:%02d ", dur.toPeriod().getHours(), dur.toPeriod().getMinutes());
        left.setText(time);
        //center.setTypeface(null, Typeface.ITALIC);


        time = String.format("%02d:%02d ", dur.toPeriod().getHours(), dur.toPeriod().getMinutes());
        right.setText(time);

        return curr;
    }
}
