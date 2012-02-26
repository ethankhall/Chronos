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
import com.kopysoft.chronos.activities.Viewers.DateViewerActivity;
import com.kopysoft.chronos.adapter.clock.PayPeriodAdapterList;
import com.kopysoft.chronos.content.Chronos;
import com.kopysoft.chronos.enums.Defines;

public class PayPeriodSummaryView extends LinearLayout {

    PayPeriodAdapterList adapter;

    private int position = 0;
    private final String argumentString = "position";
    private final String TAG = Defines.TAG + " - PayPeriod Summary Fragment";
    private SherlockActivity parent;


    public PayPeriodSummaryView(SherlockActivity prnt){
        super(prnt.getApplicationContext());
        parent = prnt;

        setOrientation(LinearLayout.VERTICAL);

        Chronos chrono = new Chronos(parent);
        ListView retView = new ListView( parent );
        retView.setOnItemClickListener(listener);
        //registerForContextMenu(retView);
        //retView.setOnChildClickListener(childClickListener);

        View header = View.inflate(getContext(), R.layout.header, null);
        TextView leftHeader = (TextView)header.findViewById(R.id.headerLeft);
        TextView centerHeader = (TextView)header.findViewById(R.id.headerCenter);
        TextView rightHeader = (TextView)header.findViewById(R.id.headerRight);

        leftHeader.setText("Date");
        centerHeader.setText("");
        rightHeader.setText("Hours Recorded");

        addView(header);
        addView(retView);

        adapter = new PayPeriodAdapterList(parent, chrono.getJobs().get(0));
        retView.setAdapter( adapter );
        retView.setSelection( position );

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
            parent.startActivity(newIntent);
        }
    };

}
