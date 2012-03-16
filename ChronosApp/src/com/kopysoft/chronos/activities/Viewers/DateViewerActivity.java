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

package com.kopysoft.chronos.activities.Viewers;


import android.content.Intent;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.kopysoft.chronos.R;
import com.kopysoft.chronos.activities.Editors.NewPunchActivity;
import com.kopysoft.chronos.activities.Editors.NoteEditor;
import com.kopysoft.chronos.activities.QuickBreakActivity;
import com.kopysoft.chronos.adapter.clock.PayPeriodAdapterList;
import com.kopysoft.chronos.content.Chronos;
import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.types.Job;
import com.kopysoft.chronos.types.Punch;
import com.kopysoft.chronos.types.holders.PunchTable;
import com.kopysoft.chronos.views.ClockFragments.Today.DatePairView;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.List;

public class DateViewerActivity extends SherlockActivity{
    
    private static String TAG = Defines.TAG + " - DateViewerActivity";
    private long date;
    private long jobId;
    
    private static final boolean enableLog = Defines.DEBUG_PRINT;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        date = getIntent().getExtras().getLong("dateTime");
        Chronos chronos = new Chronos(this);
        Job curJob = chronos.getAllJobs().get(0);
        jobId = curJob.getID();
        List<Punch> punches = chronos.getPunchesByJobAndDate(curJob, new DateTime(date));
        chronos.close();

        Log.d(TAG, "date: " + new  DateTime(date)) ;
        
        setContentView(new DatePairView(this,
                punches,
                new DateTime(date)));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //This is a workaround for http://b.android.com/15340 from http://stackoverflow.com/a/5852198/132047
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            BitmapDrawable bg = (BitmapDrawable)getResources().getDrawable(R.drawable.bg_striped);
            bg.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
            getSupportActionBar().setBackgroundDrawable(bg);

            BitmapDrawable bgSplit = (BitmapDrawable)getResources().getDrawable(R.drawable.bg_striped_split_img);
            bgSplit.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
            getSupportActionBar().setSplitBackgroundDrawable(bgSplit);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.action_bar_date_viewer, menu);

        //menu.findItem(R.id.menu_navigate).setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(enableLog) Log.d(TAG, "Selected item: " + item);
        if(enableLog) Log.d(TAG, "Selected item id: " + item.getItemId());
        switch (item.getItemId()) {
            case R.id.menu_insert:
                Intent newIntent =
                        new Intent().setClass(this,
                                NewPunchActivity.class);

                newIntent.putExtra("job", jobId);
                newIntent.putExtra("date", date);
                startActivityForResult(newIntent, NewPunchActivity.NEW_PUNCH);
                return true;
            case R.id.menu_note:
                newIntent =
                        new Intent().setClass(this,
                                NoteEditor.class);
                newIntent.putExtra("date", date);

                startActivity(newIntent);
                return true;
            case android.R.id.home:
                setResult(RESULT_OK);
                finish();
                return true;
            case R.id.menu_quick_note:
                newIntent =
                        new Intent().setClass(this,
                                QuickBreakActivity.class);

                newIntent.putExtra("date", date);
                startActivityForResult(newIntent, QuickBreakActivity.NEW_BREAK);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Chronos chronos = new Chronos(this);
        Job curJob = chronos.getAllJobs().get(0);
        jobId = curJob.getID();
        PunchTable localPunchTable = chronos.getAllPunchesForThisPayPeriodByJob(chronos.getAllJobs().get(0));
        //List<Punch> punches = chronos.getPunchesByJobAndDate(curJob, new DateTime(date));

        chronos.close();

        Duration dur = PayPeriodAdapterList.getTime(localPunchTable.getPunchPair(new DateTime()), true);
        Intent runIntent = new Intent().setClass(this,
                com.kopysoft.chronos.content.NotificationBroadcast.class);
        runIntent.putExtra("timeToday", dur.getMillis());
        this.sendBroadcast(runIntent);

        setContentView(new DatePairView(this,
                localPunchTable.getPunchesByDay(new DateTime(date)),
                new DateTime(date)) );
    }
}
