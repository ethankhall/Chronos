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

package com.kopysoft.chronos.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.types.Punch;
import com.kopysoft.chronos.view.RowElement;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class TodayAdapterSummary extends BaseAdapter {

    private static final String TAG = Defines.TAG + " - TodayAdapterSummary";

    Context gContext;
    List<Punch> gListOfPunches;
    public TodayAdapterSummary(Context context, List<Punch> listOfPunches){
        gListOfPunches = new LinkedList<Punch>(listOfPunches);
        gContext = context;
        Log.d(TAG, "Size: " + gListOfPunches.size());

        sort();
    }

    public void addPunch(Punch input){
        gListOfPunches.add(input);
        sort();
        notifyDataSetChanged();
    }

    public void rmPunch(int id){
        gListOfPunches.remove(id);
        sort();
    }

    /**
     * Sorts the elements in the list
     */
    private void sort(){
        Collections.sort(gListOfPunches);
    }

    @Override
    public int getCount() {
        return (gListOfPunches.size() + gListOfPunches.size() / 2);
    }

    @Override
    public Object getItem(int i) {
        if(i > gListOfPunches.size())
            return null;
        return gListOfPunches.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if(view == null){
            view = new RowElement(gContext);
        }


        RowElement curr = (RowElement) view;
        TextView left = curr.left();
        TextView right = curr.right();
        TextView center = curr.center();

        //Reset current view
        center.setText("");
        left.setText("");
        right.setText("");

        if(i % 3 == 2){
            center.setText("---");
            left.setText("");
            right.setText("");
            int index = i - (i + 1) / 3;
            Log.d(TAG, "Position: " + i);
            Log.d(TAG, "Index: " + index);

            if( index <= gListOfPunches.size()){
                DateTime inTime = gListOfPunches.get(index - 1).getTime();
                DateTime outTime = gListOfPunches.get(index).getTime();
                Interval diff = new Interval(inTime, outTime);

                String time = String.format("Hours %d:%02d ", diff.toPeriod().getHours(), diff.toPeriod().getMinutes());
                center.setText(time);
                center.setTypeface(null, Typeface.ITALIC);
            }

        } else {
            int index = i - (i + 1) / 3;

            Punch inTime = gListOfPunches.get(index);

            DateTimeFormatter fmt;
            if (!DateFormat.is24HourFormat(gContext))
                fmt = DateTimeFormat.forPattern("h:mm a");
            else
                fmt = DateTimeFormat.forPattern("HH:mm");

            left.setText(inTime.getTime().toString(fmt));

            if(i % 3 == 0)
                right.setText("IN");
            else if(i % 3 == 1)
                right.setText("OUT");
        }

        return curr;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
