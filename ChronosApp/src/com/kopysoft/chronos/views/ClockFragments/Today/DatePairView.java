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

package com.kopysoft.chronos.views.ClockFragments.Today;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockActivity;
import com.kopysoft.chronos.R;
import com.kopysoft.chronos.activities.ClockActivity;
import com.kopysoft.chronos.activities.Editors.PairEditorActivity;
import com.kopysoft.chronos.adapter.clock.TodayAdapterPair;
import com.kopysoft.chronos.content.Chronos;
import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.types.Job;
import com.kopysoft.chronos.types.Punch;
import com.kopysoft.chronos.types.holders.PunchPair;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.List;

public class DatePairView extends LinearLayout {

    private SherlockActivity parent;
    private final String TAG = Defines.TAG + " - DatePairView";
    private TodayAdapterPair adapter;
    public static final boolean enableLog = Defines.DEBUG_PRINT;

    public DatePairView(SherlockActivity prnt, DateTime date){
        super(prnt.getApplicationContext());
        if(enableLog) Log.d(TAG, "Entry 1");

        parent = prnt;

        Chronos chrono = new Chronos(parent);
        Job thisJob = chrono.getJobs().get(0);
        adapter = new TodayAdapterPair( parent,
                chrono.getPunchesByJobAndDate(thisJob, date ) );
        chrono.close();

        createUI(adapter, thisJob);
    }

    public boolean showPay(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(parent);
        return pref.getBoolean("showPay", true);
    }

    public DatePairView(SherlockActivity prnt, List<Punch> punches){
        super(prnt.getApplicationContext());

        parent = prnt;
        if(enableLog) Log.d(TAG, "Entry 2");

        Chronos chrono = new Chronos(parent);
        Job thisJob = chrono.getJobs().get(0);
        Log.d(TAG, "Entry 2 Pay: " + thisJob.getPayRate());
        chrono.close();

        adapter = new TodayAdapterPair( parent, punches );
        createUI(adapter, thisJob);
    }

    private void createUI(TodayAdapterPair adpter, Job thisJob){


        //if(enableLog) Log.d(TAG, "Position: " + position);
        setOrientation(LinearLayout.VERTICAL);

        ListView retView = new ListView( parent );

        retView.setOnItemClickListener(listener);
        //retView.setOnItemLongClickListener(LongClickListener);

        View header = View.inflate(getContext(), R.layout.header, null);

        if(!showPay()){
            header.findViewById(R.id.moneyViewText).setVisibility(View.GONE);
            header.findViewById(R.id.moneyViewTotal).setVisibility(View.GONE);
        }

        TextView tx = (TextView)header.findViewById(R.id.timeViewTotal);
        Duration dur = adapter.getTime();
        int seconds = (int)dur.getStandardSeconds();
        int minutes = (seconds / 60) % 60;
        int hours = (seconds / 60 / 60);
        String output = String.format("%d:%02d:%02d", hours, minutes, seconds % 60);
        tx.setText(output);
        
        if(enableLog) Log.d(TAG, "job: " + thisJob);
        if(enableLog) Log.d(TAG, "seconds: " + seconds);
        if(enableLog) Log.d(TAG, "dur: " + dur.toString());
        if(enableLog) Log.d(TAG, "pay rate: " + thisJob.getPayRate());

        double money = adapter.getPayableTime();
        output = String.format("$ %.2f", money);
        tx = (TextView)header.findViewById(R.id.moneyViewTotal);
        tx.setText(output);
        if(enableLog) Log.d(TAG, "pay amount: " + output);


        //header to the row
        addView(header);
        addView(retView);

        retView.setAdapter( adpter );
        retView.setSelection( 0 );

    }

    AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            if(enableLog) Log.d(TAG, "Clicked: " + position);
            Intent newIntent =
                    new Intent().setClass(parent,
                            PairEditorActivity.class);
            PunchPair pp = adapter.getItem(position);
            int id1 = pp.getInPunch().getID();
            int id2 = -1;
            if(pp.getOutPunch() != null){
                id2 = pp.getOutPunch().getID();
            }
            newIntent.putExtra("punch1", id1);
            newIntent.putExtra("punch2", id2);
            parent.startActivityForResult(newIntent, ClockActivity.FROM_CLOCK_ACTIVITY);
        }
    };
}
