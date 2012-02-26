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

package com.kopysoft.chronos.fragments.ClockFragments.PayPeriod;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import com.kopysoft.chronos.adapter.clock.PayPeriodAdapterSummary;
import com.kopysoft.chronos.content.Chronos;
import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.activities.Editors.PairEditorActivity;
import com.kopysoft.chronos.view.RowElement;

public class PayPeriodSummaryFragment extends Fragment {

    PayPeriodAdapterSummary adapter;

    public static PayPeriodSummaryFragment newInstance() {
        PayPeriodSummaryFragment f = new PayPeriodSummaryFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("position", 0);
        f.setArguments(args);

        return f;
    }
    
    private int position = 0;
    private final String argumentString = "position";
    private final String TAG = Defines.TAG + " - PayPeriod Summary Fragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments() != null ? getArguments().getInt(argumentString) : 0;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(argumentString, position);
        super.onSaveInstanceState(savedInstanceState);
    }
    
    public String getTitle(){
        return "Pay Period View";
    }

    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        //Log.d(TAG, "Position: " + position);
        LinearLayout layout = new LinearLayout( getActivity() );
        layout.setOrientation(LinearLayout.VERTICAL);

        Chronos chrono = new Chronos(getActivity());
        ExpandableListView retView = new ExpandableListView( getActivity() );
        //registerForContextMenu(retView);
        retView.setOnChildClickListener(childClickListener);


        RowElement header = new RowElement( getActivity() );
        header.left().setText("");
        header.center().setText("Date");
        header.right().setText("Time");
        //retView.addHeaderView(header);
        layout.addView(header, 0);
        layout.addView(retView, 1);

        adapter = new PayPeriodAdapterSummary(getActivity(), chrono.getJobs().get(0));
        retView.setAdapter( adapter );
        retView.setSelection( position );

        chrono.close();

        return layout;
    }

    public ExpandableListView.OnChildClickListener childClickListener =
            new ExpandableListView.OnChildClickListener() {
        @Override
        public boolean onChildClick(ExpandableListView parent,
                                    View v, int groupPosition, int childPosition, long id) {
            Log.d(TAG, "ID: " + id);
            Log.d(TAG, "In Time: " + adapter.getChild(groupPosition, childPosition).getInPunch().getTime().getMillis());
            Log.d(TAG, "Out Time: " + adapter.getChild(groupPosition, childPosition).getOutPunch().getTime().getMillis());
            Intent intent = new Intent(getActivity(), PairEditorActivity.class);
            startActivity(intent);
            return true;
        }
    };


}
