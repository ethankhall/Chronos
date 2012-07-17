/*******************************************************************************
 * Copyright (c) 2011-2012 Ethan Hall
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

import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TimePicker;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.ehdev.chronos.lib.Chronos;
import com.kopysoft.chronos.R;
import com.ehdev.chronos.enums.Defines;
import com.ehdev.chronos.types.Job;
import com.ehdev.chronos.types.Punch;
import com.ehdev.chronos.types.Task;
import com.ehdev.chronos.lib.Chronos;
import org.joda.time.DateTime;

import java.util.List;

public class QuickBreakActivity extends SherlockActivity{
    //add the ability to move punches by date - Added on 3/5/12

    private static String TAG = Defines.TAG + " - QuickBreakActivity";
    public static final int NEW_BREAK = 10;

    List<Task> tasks;
    DateTime date;
    private static final boolean enableLog = Defines.DEBUG_PRINT;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if(enableLog) Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quick_break);

        if(savedInstanceState != null){
            date = new DateTime(savedInstanceState.getLong("date"));
        } else {
            date = new DateTime(getIntent().getExtras().getLong("date"));
        }

        @SuppressWarnings("unchecked")
        ArrayAdapter spinnerAdapter = ArrayAdapter.createFromResource( this,
                R.array.breaks, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        
        Spinner timeSpinner = (Spinner)findViewById(R.id.timeSpinner);
        timeSpinner.setAdapter(spinnerAdapter);

        Chronos chron = new Chronos(this);
        tasks = chron.getAllTasks();
        chron.close();

        @SuppressWarnings("unchecked")
        ArrayAdapter taskSpinner = new ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                tasks);
        taskSpinner.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = (Spinner)findViewById(R.id.spinner);
        spinner.setAdapter(taskSpinner);
        //end task

        //set for 24 or 12 hour time
        boolean twentyFourHourTime = DateFormat.is24HourFormat(this);
        TimePicker breakTime = (TimePicker)findViewById(R.id.timePicker);
        breakTime.setIs24HourView(twentyFourHourTime);
        breakTime.setCurrentMinute(DateTime.now().getMinuteOfHour());
        breakTime.setCurrentHour(DateTime.now().getHourOfDay());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //This is a workaround for http://b.android.com/15340 from http://stackoverflow.com/a/5852198/132047
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            BitmapDrawable bg = (BitmapDrawable)getResources().getDrawable(R.drawable.bg_striped);
            bg.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
            getSupportActionBar().setBackgroundDrawable(bg);

            BitmapDrawable bgSplit = (BitmapDrawable)getResources()
                    .getDrawable(R.drawable.bg_striped_split_img);
            bgSplit.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
            getSupportActionBar().setSplitBackgroundDrawable(bgSplit);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getSupportMenuInflater().inflate(R.menu.save_cancel_menu, menu);
        
        menu.findItem(R.id.RemoveMenu).setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    private void updateDatabase(){
        int hour, min;
        TimePicker inTime = (TimePicker)findViewById(R.id.timePicker);
        Spinner timeSpinner = (Spinner)findViewById(R.id.timeSpinner);
        Spinner taskSpinner = (Spinner)findViewById(R.id.spinner);
        inTime.clearFocus();
        hour = inTime.getCurrentHour();
        min= inTime.getCurrentMinute();
        
        Task inTask =  tasks.get(taskSpinner.getSelectedItemPosition());
        
        DateTime date1 = new DateTime(
                date.getYear(),
                date.getMonthOfYear(),
                date.getDayOfMonth(),
                hour,
                min);

        Chronos chrono = new Chronos(this);
        Job thisJob = chrono.getAllJobs().get(0);

        DateTime startOfPP = thisJob.getStartOfPayPeriod();
        if(startOfPP.getSecondOfDay() > date1.getSecondOfDay()){
            date1 = date1.plusDays(1);
        }
        Punch startPunch = new Punch(thisJob, inTask, date1);
        
        DateTime endDate;
        switch (timeSpinner.getSelectedItemPosition()){
            case(0):
                endDate = date1.plusMinutes(5);
                break;
            case(1):
                endDate = date1.plusMinutes(10);
                break;
            case(2):
                endDate = date1.plusMinutes(15);
                break;
            case(3):
                endDate = date1.plusMinutes(30);
                break;
            case(4):
                endDate = date1.plusMinutes(45);
                break;
            case(5):
                endDate = date1.plusMinutes(60);
                break;
            default:
                endDate = date1.plusMinutes(1);
        }
        Punch endPunch = new Punch(thisJob, inTask, endDate);
        if(enableLog) Log.d(TAG, "Date Time: " + startPunch.getTime().getMillis());
        
        Log.d(TAG, "Start Punch" + startPunch.getTime());
        Log.d(TAG, "End Punch" + endPunch.getTime());

        chrono.insertPunch(startPunch);
        chrono.insertPunch(endPunch);
        chrono.close();
        //int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minuteOfHour

    }

    @Override
    protected void onSaveInstanceState (Bundle outState){
        outState.putLong("date", date.getMillis());
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
        if(enableLog) Log.d(TAG, "Selected item: " + item);
        if(enableLog) Log.d(TAG, "Selected item id: " + item.getItemId());
        switch (item.getItemId()) {
            case R.id.menuSave:
                updateDatabase();
                setResult(RESULT_OK);
                finish();
                return true;
            case android.R.id.home:
            case R.id.menuCancel:
                setResult(RESULT_CANCELED);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
