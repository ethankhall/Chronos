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

package com.kopysoft.chronos;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.actionbarsherlock.view.Menu;
import com.kopysoft.chronos.activities.MainActivity;
import com.kopysoft.chronos.content.Chronos;
import com.kopysoft.chronos.enums.Defines;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;


public class mainUI extends FragmentActivity {
    /** Called when the activity is first created. */
    private static final String TAG = Defines.TAG + " - Main";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //ArrayAdapter<CharSequence> list =
        //        ArrayAdapter.createFromResource(this, R.array.navigation, R.layout.abs__simple_spinner_item);

         //getSupportActionBar();
        //actionBar.setTitle("Clock");
        //actionBar.setDisplayHomeAsUpEnabled(true);
        
        Chronos chronos = new Chronos(this);
        chronos.getJobs();

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

        getSupportFragmentManager()
                .beginTransaction()
                .add(android.R.id.content, new MainActivity())
                .commit();

        //enable drop down navigation
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        //list.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //getSupportActionBar().setListNavigationCallbacks(list, this);

        /*
        ClockViewer adapter = new ClockViewer( this );

        android.support.v4.view.ViewPager pager =
                (android.support.v4.view.ViewPager) findViewById( R.id.viewpager );

        TitlePageIndicator indicator =
                (TitlePageIndicator)findViewById( R.id.indicator );

        pager.setAdapter( adapter );
        indicator.setViewPager( pager );
        indicator.setFooterIndicatorStyle(TitlePageIndicator.IndicatorStyle.None);
        */

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        /*
        menu.add("Add Punch")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add("New Note")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                */

        return super.onCreateOptionsMenu(menu);
    }

    /*

    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        Log.d(TAG, "Position: " + itemPosition);
        switch (itemPosition){
            case 0:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(android.R.id.content, ClockActivity.newInstance())
                        .commit();
                break;
            case 1:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(android.R.id.content, NoteActivity.newInstance())
                        .commit();
                break;
            default:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(android.R.id.content, ClockActivity.newInstance())
                        .commit();
                break;
        }
        return true;
    }
    */
}