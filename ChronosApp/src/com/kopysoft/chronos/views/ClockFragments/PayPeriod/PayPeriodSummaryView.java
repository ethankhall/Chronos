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

package com.kopysoft.chronos.views.ClockFragments.PayPeriod;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockActivity;
import com.kopysoft.chronos.R;
import com.kopysoft.chronos.activities.ClockActivity;
import com.kopysoft.chronos.activities.Viewers.DateViewerActivity;
import com.kopysoft.chronos.adapter.clock.PayPeriodAdapterList;
import com.kopysoft.chronos.content.Chronos;
import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.types.Job;
import com.kopysoft.chronos.types.holders.PunchTable;
import org.joda.time.Duration;

public class PayPeriodSummaryView extends LinearLayout {

    PayPeriodAdapterList adapter;

    private int position = 0;
    private final String TAG = Defines.TAG + " - PayPeriod Summary Fragment";
    private SherlockActivity parent;


    public PayPeriodSummaryView(SherlockActivity prnt, PunchTable table){
        super(prnt.getApplicationContext());
        parent = prnt;

        setOrientation(LinearLayout.VERTICAL);

        Chronos chrono = new Chronos(parent);
        ListView retView = new ListView( parent );
        retView.setOnItemClickListener(listener);

        adapter = new PayPeriodAdapterList(parent, table);
        retView.setAdapter( adapter );
        retView.setSelection( position );
        //registerForContextMenu(retView);
        //retView.setOnChildClickListener(childClickListener);

        View header = View.inflate(getContext(), R.layout.header, null);

        TextView timeView = (TextView)header.findViewById(R.id.timeViewTotal);
        TextView moneyView = (TextView)header.findViewById(R.id.moneyViewTotal);        
        TextView leftHeader = (TextView)header.findViewById(R.id.headerLeft);
        TextView centerHeader = (TextView)header.findViewById(R.id.headerCenter);
        TextView rightHeader = (TextView)header.findViewById(R.id.headerRight);

        Duration dur = adapter.getTime();
        int seconds = (int)dur.getStandardSeconds();
        int minutes = (seconds / 60) % 60;
        int hours = (seconds / 60 / 60);
        String output = String.format("%d:%02d.%02d", hours, minutes, seconds % 60);
        timeView.setText(output);
        Job thisJob = chrono.getJobs().get(0);

        Log.d(TAG, "job: " + thisJob);
        Log.d(TAG, "seconds: " + seconds);
        Log.d(TAG, "dur: " + dur.toString());
        Log.d(TAG, "pay rate: " + thisJob.getPayRate());

        double money = seconds * (thisJob.getPayRate() / 60 / 60);
        output = String.format("$ %.2f", money);

        moneyView.setText(output);
        Log.d(TAG, "pay amount: " + output);

        leftHeader.setText("Date");
        centerHeader.setText("");
        rightHeader.setText("Hours Recorded");

        addView(header);
        addView(retView);

        chrono.close();
    }

    AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            Log.d(TAG, "Clicked: " + position);
            Intent newIntent =
                    new Intent().setClass(parent,
                            DateViewerActivity.class);

            newIntent.putExtra("dateTime", adapter.getDate(position).getMillis());
            parent.startActivityForResult(newIntent, ClockActivity.FROM_CLOCK_ACTIVITY);
            
            Log.d(TAG, "Date Viewer Activity: " + adapter.getDate(position).getMillis());
        }
    };

}
