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
import android.widget.TimePicker;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.kopysoft.chronos.R;
import com.kopysoft.chronos.content.Chronos;
import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.types.Punch;
import com.kopysoft.chronos.types.Task;
import org.joda.time.DateTime;

import java.util.List;

public class PairEditorActivity extends SherlockActivity{
    //add the ability to move punches by date - Added on 3/5/12

    private static String TAG = Defines.TAG + " - PairEditorActivity";

    Punch p1;
    Punch p2;
    List<Task> tasks;

    private enum RemoveOption {IN_TIME, OUT_TIME, BOTH};
    
    private final boolean enableLog = Defines.DEBUG_PRINT;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if(enableLog) Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.punch_pair_editor);

        int punch1;
        int punch2;
        
        if(savedInstanceState != null){
            punch1 = savedInstanceState.getInt("punch1");
            punch2 = savedInstanceState.getInt("punch2");
        } else {
            punch1 = getIntent().getExtras().getInt("punch1");
            punch2 = getIntent().getExtras().getInt("punch2");
        }

        if(enableLog) Log.d(TAG, "Punch 1: " + punch1);
        if(enableLog) Log.d(TAG, "Punch 2: " + punch2);
        updateUi(punch1, punch2);

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

    private void updateUi(int punch1, int punch2){
        //start task
        Spinner taskSpinnerIn = (Spinner)findViewById(R.id.taskSpinnerIn);
        Spinner taskSpinnerOut = (Spinner)findViewById(R.id.taskSpinnerOut);

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

        //set the times
        p1 = chron.getPunchById(punch1);
        p2 = chron.getPunchById(punch2);

        if(enableLog) Log.d(TAG, "P1 Current Hour: " + p1.getTime().getHourOfDay());
        if(enableLog) Log.d(TAG, "P1 Current Minute: " + p1.getTime().getMinuteOfHour());

        inTime.setCurrentHour(p1.getTime().getHourOfDay());
        inTime.setCurrentMinute(p1.getTime().getMinuteOfHour());
        for(int i = 0; i < spinnerAdapter.getCount(); i++){
            if(p1.getTask().getID() == tasks.get(i).getID())
                taskSpinnerIn.setSelection(i);
        }

        if(p2 != null){
            if(enableLog) Log.d(TAG, "P2 Current Hour: " + p2.getTime().getHourOfDay());
            if(enableLog) Log.d(TAG, "P2 Current Minute: " + p2.getTime().getMinuteOfHour());

            outTime.setCurrentHour(p2.getTime().getHourOfDay());
            outTime.setCurrentMinute(p2.getTime().getMinuteOfHour());

            for(int i = 0; i < spinnerAdapter.getCount(); i++){
                if(p1.getTask().getID() == tasks.get(i).getID())
                    taskSpinnerOut.setSelection(i);
            }

        } else {
            findViewById(R.id.outLayout).setVisibility(View.GONE);
        }

        //close chronos
        chron.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getSupportMenuInflater().inflate(R.menu.save_cancel_menu, menu);
        
        if(p2 == null){
            menu.findItem(R.id.remove_both).setVisible(false);
            menu.findItem(R.id.remove_out).setVisible(false);
        }

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
                p1.getTime().getYear(),
                p1.getTime().getMonthOfYear(),
                p1.getTime().getDayOfMonth(),
                hour,
                min);
        p1.setTime(date1);
        p1.setTask(inTask);
        
        Chronos chrono = new Chronos(this);
        chrono.updatePunch(p1);
        //int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minuteOfHour

        
        if(p2 != null){
            TimePicker outTime = (TimePicker)findViewById(R.id.TimePicker2);
            Spinner taskSpinnerOut = (Spinner)findViewById(R.id.taskSpinnerOut);
            outTime.clearFocus();
            hour = outTime.getCurrentHour();
            min= outTime.getCurrentMinute();

            Task outTask
                    =  tasks.get(taskSpinnerOut.getSelectedItemPosition());

            DateTime date2 = new DateTime(
                    p1.getTime().getYear(),
                    p2.getTime().getMonthOfYear(),
                    p2.getTime().getDayOfMonth(),
                    hour,
                    min);
            p2.setTime(date2);
            p2.setTask(outTask);
            chrono.updatePunch(p2);
        }
        chrono.close();
    }
    
    public void removePunches(RemoveOption option){

        Chronos chronos = new Chronos(getApplicationContext());
        switch(option){
            case IN_TIME:
                if(p1 != null)
                    chronos.deletePunch(p1);
                break;
            case BOTH:
                if(p1 != null)
                    chronos.deletePunch(p1);
                if(p2 != null)
                    chronos.deletePunch(p2);
                break;
            case OUT_TIME:
                if(p2 != null)
                    chronos.deletePunch(p2);
                break;
        }
        chronos.close();
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
            case R.id.remove_in:
                removePunches(RemoveOption.IN_TIME);
                setResult(RESULT_OK);
                finish();
                return true;
            case R.id.remove_both:
                removePunches(RemoveOption.BOTH);
                setResult(RESULT_OK);
                finish();
                return true;
            case R.id.remove_out:
                removePunches(RemoveOption.OUT_TIME);
                setResult(RESULT_OK);
                finish();
                return true;
            case R.id.RemoveMenu:
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
