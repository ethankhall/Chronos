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

package com.kopysoft.chronos.activities.Editors;

import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.kopysoft.chronos.R;
import com.kopysoft.chronos.content.Chronos;
import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.types.Job;
import com.kopysoft.chronos.types.Punch;
import com.kopysoft.chronos.types.Task;
import org.joda.time.DateTime;

import java.util.List;

public class NewPunchActivity extends SherlockActivity{
    //add the ability to move punches by date - Added on 3/5/12

    private static String TAG = Defines.TAG + " - NewPunchActivity";
    public static final int NEW_PUNCH = 2;

    List<Task> tasks;
    long jobID;
    DateTime date;
    private static final boolean enableLog = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if(enableLog) Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.punch_pair_editor);

        Spinner taskSpinnerIn = (Spinner)findViewById(R.id.taskSpinnerIn);
        Spinner taskSpinnerOut = (Spinner)findViewById(R.id.taskSpinnerOut);
        try{
            ((TextView)findViewById(R.id.punchTitleText)).setText("In/Out Time");
        } catch (NullPointerException e){
            Log.e(TAG, "Could not find punchTitleText");
        }

        if(savedInstanceState != null){
            jobID = savedInstanceState.getLong("job");
            date = new DateTime(savedInstanceState.getLong("date"));
        } else {
            jobID = getIntent().getExtras().getLong("job");
            date = new DateTime(getIntent().getExtras().getLong("date"));
        }

        if(enableLog) Log.d(TAG, "JobID: " + jobID);
        if(enableLog) Log.d(TAG, "DateTime: " + date);

        Chronos chron = new Chronos(this);
        tasks = chron.getAllTasks();

        @SuppressWarnings("unchecked")
        ArrayAdapter spinnerAdapter = new ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                tasks);
        spinnerAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        taskSpinnerIn.setAdapter(spinnerAdapter);
        taskSpinnerOut.setAdapter(spinnerAdapter);
        //end task

        //set for 24 or 12 hour time
        boolean twentyFourHourTime = DateFormat.is24HourFormat(this);
        TimePicker inTime = (TimePicker)findViewById(R.id.TimePicker1);
        inTime.setIs24HourView(twentyFourHourTime);
        TimePicker outTime = (TimePicker)findViewById(R.id.TimePicker2);
        outTime.setIs24HourView(twentyFourHourTime);

        DateTime now = new DateTime();

        if(enableLog) Log.d(TAG, "P1 Current Hour: " + now.getHourOfDay());
        if(enableLog) Log.d(TAG, "P1 Current Minute: " + now.getMinuteOfHour());

        inTime.setCurrentHour(now.getHourOfDay());
        inTime.setCurrentMinute(now.getMinuteOfHour());
        taskSpinnerIn.setSelection(0);

        findViewById(R.id.outLayout).setVisibility(View.GONE);


        //close chronos
        chron.close();

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
        TimePicker inTime = (TimePicker)findViewById(R.id.TimePicker1);
        Spinner taskSpinnerIn = (Spinner)findViewById(R.id.taskSpinnerIn);
        inTime.clearFocus();
        hour = inTime.getCurrentHour();
        min= inTime.getCurrentMinute();
        
        Task inTask =  tasks.get(taskSpinnerIn.getSelectedItemPosition());
        
        DateTime date1 = new DateTime(
                date.getYear(),
                date.getMonthOfYear(),
                date.getDayOfMonth(),
                hour,
                min);
        

        Chronos chrono = new Chronos(this);
        Job thisJob = null;
        List<Job> jobs = chrono.getAllJobs();
        for(Job job : jobs){
            if(job.getID() == jobID)
                thisJob = job;
        }
        
        DateTime startOfPP = thisJob.getStartOfPayPeriod();
        if(startOfPP.getSecondOfDay() > date1.getSecondOfDay()){
            date1 = date1.plusDays(1);
        }
        Punch newPunch = new Punch(thisJob, inTask, date1);
        if(enableLog) Log.d(TAG, "Date Time: " + newPunch.getTime().getMillis());

        chrono.insertPunch(newPunch);
        chrono.close();
        //int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minuteOfHour

    }

    @Override
    protected void onSaveInstanceState (Bundle outState){
        outState.putLong("job", jobID);
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
