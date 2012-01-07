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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.kopysoft.chronos.adapter.clock.TodayAdapterIndividual;
import com.kopysoft.chronos.content.Chronos;
import com.kopysoft.chronos.view.RowElement;

public class TodayIndividualFragment extends Fragment {

    public static TodayIndividualFragment newInstance() {
        TodayIndividualFragment f = new TodayIndividualFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("position", 0);
        f.setArguments(args);

        return f;
    }
    
    private int position = 0;
    private final String argumentString = "position";

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        position = getArguments() != null ? getArguments().getInt(argumentString) : 0;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(argumentString, position);
        super.onSaveInstanceState(savedInstanceState);
    }


    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        //Log.d(TAG, "Position: " + position);
        LinearLayout layout = new LinearLayout( getActivity() );
        layout.setOrientation(LinearLayout.VERTICAL);

        Chronos chrono = new Chronos( getActivity() );
        ListView retView = new ListView( getActivity() );
        BaseAdapter adapter;

        RowElement header = new RowElement( getActivity() );
        header.left().setText("In time");
        header.center().setText("");
        header.right().setText("Type");
        //retView.addHeaderView(header);
        layout.addView(header, 0);
        layout.addView(retView, 1);

        adapter = new TodayAdapterIndividual( getActivity(), chrono.getAllPunches());
        retView.setAdapter( adapter );
        retView.setSelection( position );

        chrono.close();

        return layout;

    }


}
