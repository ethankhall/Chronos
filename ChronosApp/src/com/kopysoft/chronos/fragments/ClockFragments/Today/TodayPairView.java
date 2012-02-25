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

package com.kopysoft.chronos.fragments.ClockFragments.Today;

import android.content.Context;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.kopysoft.chronos.adapter.clock.TodayAdapterPair;
import com.kopysoft.chronos.content.Chronos;
import com.kopysoft.chronos.view.RowElement;

public class TodayPairView extends LinearLayout {

    public String getTitle(){
        return "Today - Pair";
    }
    
    private int position = 0;
    private final String argumentString = "position";

    public TodayPairView (Context context){
        super(context);

        //Log.d(TAG, "Position: " + position);
        setOrientation(LinearLayout.VERTICAL);

        Chronos chrono = new Chronos(context);
        ListView retView = new ListView( context );
        BaseAdapter adapter;

        //header to the row
        RowElement header = new RowElement( context );
        header.left().setText("In time");
        header.center().setText("Task");
        header.right().setText("Out time");
        //retView.addHeaderView(header);
        addView(header);
        addView(retView);

        adapter = new TodayAdapterPair( context, chrono.getAllPunches());
        retView.setAdapter( adapter );
        retView.setSelection( position );

        chrono.close();

    }


}
