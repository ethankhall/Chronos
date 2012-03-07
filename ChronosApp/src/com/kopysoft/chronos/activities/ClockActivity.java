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

package com.kopysoft.chronos.activities;


import android.content.Intent;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.kopysoft.chronos.R;
import com.kopysoft.chronos.activities.Editors.NewPunchActivity;
import com.kopysoft.chronos.content.Chronos;
import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.types.Job;
import com.kopysoft.chronos.types.holders.PayPeriodHolder;
import com.kopysoft.chronos.types.holders.PunchTable;
import com.kopysoft.chronos.views.ClockFragments.PayPeriod.PayPeriodSummaryView;
import com.kopysoft.chronos.views.ClockFragments.Today.DatePairView;
import org.joda.time.DateTime;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class ClockActivity extends SherlockActivity implements ActionBar.TabListener{
    
    private static String TAG = Defines.TAG + " - ClockActivity";
    private PunchTable localPunchTable;
    public static int FROM_CLOCK_ACTIVITY = 0;
    private Job jobId;
    private PayPeriodHolder payHolder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.header);

        Chronos chronos = new Chronos(this);
        Job curJob = chronos.getJobs().get(0);
        jobId = curJob;
        payHolder = new PayPeriodHolder(curJob);
        localPunchTable = chronos.getAllPunchesForThisPayPeriodByJob(curJob);
        chronos.close();

        //getSupportActionBar().setListNavigationCallbacks(list, this)
        //This is a workaround for http://b.android.com/15340 from http://stackoverflow.com/a/5852198/132047
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            BitmapDrawable bg = (BitmapDrawable)getResources().getDrawable(R.drawable.bg_striped);
            bg.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
            getSupportActionBar().setBackgroundDrawable(bg);

            BitmapDrawable bgSplit = (BitmapDrawable)getResources().getDrawable(R.drawable.bg_striped_split_img);
            bgSplit.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
            getSupportActionBar().setSplitBackgroundDrawable(bgSplit);
        }

        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        {
            ActionBar.Tab tab = getSupportActionBar().newTab();
            tab.setText("Today");
            tab.setTabListener(this);
            getSupportActionBar().addTab(tab);

            tab = getSupportActionBar().newTab();
            tab.setText("Pay Period");
            tab.setTabListener(this);
            getSupportActionBar().addTab(tab);
        }

        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            if (sd.canWrite()) {
                String currentDBPath = "/data/com.kopysoft.chronos/databases/" + Chronos.DATABASE_NAME;
                String backupDBPath = Chronos.DATABASE_NAME + ".db";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);
                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        }catch (Exception e) {
            Log.e(TAG, "ERROR: Can not move file");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        /*
        menu.add("Add")
                .setIcon(R.drawable.ic_menu_add)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        //collapsed menu
        SubMenu subMenu1 = menu.addSubMenu("Options")
                .setIcon(R.drawable.ic_menu_moreoverflow_holo_dark);
        subMenu1.add("Preferences")
                .setIcon(R.drawable.ic_menu_preferences);
        subMenu1.add("Items");

        MenuItem subMenu1Item = subMenu1.getItem();
        subMenu1Item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        */
        getSupportMenuInflater().inflate(R.menu.action_bar, menu);

        int pos = getSupportActionBar().getSelectedTab().getPosition();
        if(pos == 1){
            menu.findItem(R.id.menu_insert).setVisible(false);
        } else {
            menu.findItem(R.id.menu_navigate).setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }
    
    private PunchTable getPunchesByDate(){
        Chronos chronos = new Chronos(this);
        PunchTable temp =  chronos.getAllPunchesForPayPeriodByJob(jobId,
                payHolder.getStartOfPayPeriod(), payHolder.getEndOfPayPeriod());
        chronos.close();
        return temp;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab) {

        invalidateOptionsMenu();    //Redo the menu
        if(tab.getPosition() == 0){
            setContentView(new DatePairView(this, localPunchTable.getPunchesByDay(new DateTime())));
        } else if(tab.getPosition() == 1){
            setContentView(new PayPeriodSummaryView(this, getPunchesByDate( ) ) );
        }
        Log.d(TAG, "onTabSelected: " + tab);
        Log.d(TAG, "onTabSelected Position: " + tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab) {
        Log.d(TAG, "onTabUnselected: " + tab);
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab) {
        Log.d(TAG, "onTabReselected: " + tab);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "Request Code: " + requestCode);
        Log.d(TAG, "Result Code: " + resultCode);
        Log.d(TAG, "Selected Navigation Index: " + getSupportActionBar().getSelectedNavigationIndex());

        if (requestCode == FROM_CLOCK_ACTIVITY) {
            Chronos chronos = new Chronos(this);
            localPunchTable = chronos.getAllPunchesForThisPayPeriodByJob(chronos.getJobs().get(0));
            chronos.close();
        } else if(requestCode == NewPunchActivity.NEW_PUNCH){
            Log.d(TAG, "New Punch Created");
            if (resultCode == RESULT_OK) {
                Chronos chronos = new Chronos(this);
                localPunchTable = chronos.getAllPunchesForThisPayPeriodByJob(chronos.getJobs().get(0));
                chronos.close();
            }
        }

        if(getSupportActionBar().getSelectedNavigationIndex() == 0){
            setContentView(new DatePairView(this, localPunchTable.getPunchesByDay(new DateTime())));
        } else if(getSupportActionBar().getSelectedNavigationIndex() == 1){
            setContentView(new PayPeriodSummaryView(this, getPunchesByDate( ) ) );
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "Selected item: " + item);
        Log.d(TAG, "Selected item id: " + item.getItemId());
        switch (item.getItemId()) {
            case R.id.menu_insert:
                Intent newIntent =
                        new Intent().setClass(this,
                                NewPunchActivity.class);

                newIntent.putExtra("job", jobId.getID());
                newIntent.putExtra("date", DateTime.now().getMillis());
                startActivityForResult(newIntent, NewPunchActivity.NEW_PUNCH);
                return true;
            case R.id.menu_navigate_today:
                payHolder.generate();
                setContentView(new PayPeriodSummaryView(this, getPunchesByDate( ) ) );
                return true;
            case R.id.menu_navigate_back:
                payHolder.moveBackwards();
                setContentView(new PayPeriodSummaryView(this, getPunchesByDate( ) ) );
                return true;
            case R.id.menu_navigate_forward:
                payHolder.moveForwards();
                setContentView(new PayPeriodSummaryView(this, getPunchesByDate( ) ) );
                return true;
            case android.R.id.home:
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
