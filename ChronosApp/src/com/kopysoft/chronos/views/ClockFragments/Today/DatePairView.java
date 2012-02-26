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
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.kopysoft.chronos.R;
import com.kopysoft.chronos.adapter.clock.TodayAdapterPair;
import com.kopysoft.chronos.content.Chronos;
import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.activities.Editors.PairEditorActivity;
import com.kopysoft.chronos.types.holders.PunchPair;
import org.joda.time.DateTime;

public class DatePairView extends LinearLayout {

    private SherlockActivity parent;
    private final String TAG = Defines.TAG + " - DatePairView";
    private TodayAdapterPair adapter;

    public DatePairView(SherlockActivity prnt, DateTime date){
        super(prnt.getApplicationContext());

        parent = prnt;

        //Log.d(TAG, "Position: " + position);
        setOrientation(LinearLayout.VERTICAL);

        Chronos chrono = new Chronos(parent);
        ListView retView = new ListView( parent );

        retView.setOnItemClickListener(listener);
        
        View header = View.inflate(getContext(), R.layout.header, null);

        //header to the row
        /*
        RowElement header = new RowElement( parent );
        header.left().setText("In time");
        header.center().setText("Task");
        header.right().setText("Out time");
        //retView.addHeaderView(header);
        */
        addView(header);
        addView(retView);

        adapter = new TodayAdapterPair( parent,
                chrono.getPunchesByJobAndDate(chrono.getJobs().get(0), date ) );
        retView.setAdapter( adapter );
        retView.setSelection( 0 );

        chrono.close();

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
            parent.startActivity(newIntent);
        }
    };

    private final class AnActionModeOfEpicProportions implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            menu.add("Edit")
                    .setIcon(R.drawable.ic_menu_edit)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

            menu.add("Remove")
                    .setIcon(R.drawable.ic_menu_delete)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            Log.d(TAG, "Got " + item);
            mode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
        }
    }
}
