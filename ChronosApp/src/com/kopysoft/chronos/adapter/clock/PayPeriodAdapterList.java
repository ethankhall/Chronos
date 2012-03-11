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
    private static final boolean enableLog = true;

    public PayPeriodAdapterList(Context context, Job inJob){
        gContext = context;
        Chronos chrono = new Chronos(gContext);

        gPunchesByDay = chrono.getAllPunchesForThisPayPeriodByJob(inJob);
        chrono.close();
        //gPayPeriod = new PayPeriodHolder(inJob);
        if(enableLog) Log.d(TAG, "Size of Punches: " + gPunchesByDay.getDays().size());
    }

    public PayPeriodAdapterList(Context context, PunchTable punchTable){
        gContext = context;

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
    
    public Duration getTime(){
        Duration dur = new Duration(0);

        for(DateTime date : gPunchesByDay.getDays()){
            dur = dur.plus(getTime(gPunchesByDay.getPunchPair(date)));
        }
        if(enableLog) Log.d(TAG, "Duration: " + dur);

        return dur;
    }

    public static Duration getTime(List<PunchPair> punches ){
        Duration dur = new Duration(0);
        for(PunchPair pp : punches){
            if(enableLog) Log.d(TAG, "Punch Size: " + pp.getInterval().toDurationMillis());
            if(!pp.getInPunch().getTask().getEnablePayOverride())
                dur = dur.plus(pp.getInterval().toDuration());
            else
                dur = dur.minus(pp.getInterval().toDuration());
        }

        return dur;
    }

    public float getPayableTime(){
        float totalPay = 0.0f;
        for(DateTime date : gPunchesByDay.getDays()){
            for(PunchPair pp : gPunchesByDay.getPunchPair(date)){
                long mili = pp.getInterval().toDurationMillis();
                if(pp.getTask().getEnablePayOverride()) {
                    totalPay -= pp.getTask().getPayOverride()/1000/60/60 * mili;
                } else {
                    totalPay += pp.getTask().getPayOverride()/1000/60/60 * mili;
                    //correct way, need to update to it, totalPay += pp.getJob().getPayRate()/1000/60/60 * mili;
                }
            }
        }

        if(totalPay < 0)
            totalPay = 0;

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

        Duration dur = getTime(getItem(i));

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
