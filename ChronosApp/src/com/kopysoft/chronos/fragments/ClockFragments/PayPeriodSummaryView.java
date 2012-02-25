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

package com.kopysoft.chronos.fragments.ClockFragments;

import android.content.Context;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import com.kopysoft.chronos.adapter.clock.PayPeriodAdapterSummary;
import com.kopysoft.chronos.content.Chronos;
import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.view.RowElement;

public class PayPeriodSummaryView extends LinearLayout {

    PayPeriodAdapterSummary adapter;

    private int position = 0;
    private final String argumentString = "position";
    private final String TAG = Defines.TAG + " - PayPeriod Summary Fragment";
    private Context gContext;


    public PayPeriodSummaryView(Context context){
        super(context);
        gContext = context;

        setOrientation(LinearLayout.VERTICAL);

        Chronos chrono = new Chronos(context);
        ExpandableListView retView = new ExpandableListView( context );
        //registerForContextMenu(retView);
        //retView.setOnChildClickListener(childClickListener);


        RowElement header = new RowElement( context );
        header.left().setText("");
        header.center().setText("Date");
        header.right().setText("Time   ");


        addView(header);
        addView(retView);

        adapter = new PayPeriodAdapterSummary(context, chrono.getJobs().get(0));
        retView.setAdapter( adapter );
        retView.setSelection( position );

        chrono.close();
    }

    /*
    public ExpandableListView.OnChildClickListener childClickListener =
            new ExpandableListView.OnChildClickListener() {
        @Override
        public boolean onChildClick(ExpandableListView parent,
                                    View v, int groupPosition, int childPosition, long id) {
            Log.d(TAG, "ID: " + id);
            Log.d(TAG, "In Time: " + adapter.getChild(groupPosition, childPosition).getInPunch().getTime().getMillis());
            Log.d(TAG, "Out Time: " + adapter.getChild(groupPosition, childPosition).getOutPunch().getTime().getMillis());
            Intent intent = new Intent(gContext, PairEditorFragment.class);
            gContext.startActivity(intent);
            return true;
        }
    };
    */


}
