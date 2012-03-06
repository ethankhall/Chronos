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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.ActionMode;
import com.kopysoft.chronos.R;
import com.kopysoft.chronos.activities.ClockActivity;
import com.kopysoft.chronos.activities.Editors.PairEditorActivity;
import com.kopysoft.chronos.adapter.clock.TodayAdapterPair;
import com.kopysoft.chronos.content.Chronos;
import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.types.Punch;
import com.kopysoft.chronos.types.holders.PunchPair;
import org.joda.time.DateTime;

import java.util.List;

public class DatePairView extends LinearLayout {

    private SherlockActivity parent;
    private final String TAG = Defines.TAG + " - DatePairView";
    private TodayAdapterPair adapter;
    private ActionMode mMode;

    public DatePairView(SherlockActivity prnt, DateTime date){
        super(prnt.getApplicationContext());

        parent = prnt;

        Chronos chrono = new Chronos(parent);
        adapter = new TodayAdapterPair( parent,
                chrono.getPunchesByJobAndDate(chrono.getJobs().get(0), date ) );

        createUI(adapter);

        chrono.close();
    }


    public DatePairView(SherlockActivity prnt, List<Punch> punches){
        super(prnt.getApplicationContext());

        parent = prnt;

        adapter = new TodayAdapterPair( parent, punches );
        createUI(adapter);
    }

    private void createUI(TodayAdapterPair adpter){


        //Log.d(TAG, "Position: " + position);
        setOrientation(LinearLayout.VERTICAL);

        ListView retView = new ListView( parent );

        retView.setOnItemClickListener(listener);
        //retView.setOnItemLongClickListener(LongClickListener);

        View header = View.inflate(getContext(), R.layout.header, null);

        //header to the row
        addView(header);
        addView(retView);

        retView.setAdapter( adpter );
        retView.setSelection( 0 );

    }

    AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            Log.d(TAG, "Clicked: " + position);
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

    /*
    AdapterView.OnItemLongClickListener LongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
            Log.d(TAG, "Long Clicked: " + position);

            PunchPair pp = adapter.getItem(position);
            mMode = parent.startActionMode(new AnActionModeOfEpicProportions(pp.getOutPunch() != null));
            adapter.setSelected(position);

            return true;
        }
    };

    private final class AnActionModeOfEpicProportions implements ActionMode.Callback {
        private boolean enableBoth; 
        public AnActionModeOfEpicProportions(boolean bothVisible){
            super();
            enableBoth = bothVisible;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            menu.add("Remove IN")
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

            if(enableBoth){

                menu.add("Remove OUT")
                        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

                menu.add("Remove Both")
                        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            }

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            adapter.clearSelected();
            mode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
        }
    }
    */
}
