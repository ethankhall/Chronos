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

import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TimePicker;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.kopysoft.chronos.R;
import com.kopysoft.chronos.content.Chronos;
import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.types.Punch;
import com.kopysoft.chronos.types.Task;

import java.util.LinkedList;
import java.util.List;

public class PairEditorActivity extends SherlockActivity{

    private static String TAG = Defines.TAG + " - PairEditorActivity";
    private boolean twentyFourHourTime = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.punch_pair_editor);

        int punch1 = getIntent().getExtras().getInt("punch1");
        int punch2 = getIntent().getExtras().getInt("punch2");

        Log.d(TAG, "Punch 1: " + getIntent().getExtras().getInt("punch1"));
        Log.d(TAG, "Punch 2: " + getIntent().getExtras().getInt("punch2"));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //start task
        Spinner taskSpinner = (Spinner)findViewById(R.id.taskSpinner);

        Chronos chron = new Chronos(this);
        List<Task> tasks = chron.getAllTasks();
        List<String> taskString = new LinkedList<String>();
        for(Task task : tasks){
            taskString.add(task.getName());
        }

        @SuppressWarnings("unchecked")
        ArrayAdapter adapter = new ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                taskString);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        taskSpinner.setAdapter(adapter);
        taskSpinner.setOnItemSelectedListener(taskListener);
        //end task

        //set for 24 or 12 hour time
        twentyFourHourTime = DateFormat.is24HourFormat(this);
        TimePicker inTime = (TimePicker)findViewById(R.id.TimePicker1);
        inTime.setIs24HourView(twentyFourHourTime);
        TimePicker outTime = (TimePicker)findViewById(R.id.TimePicker2);
        outTime.setIs24HourView(twentyFourHourTime);

        //set the times
        Punch p1 = chron.getPunchById(punch1);
        Punch p2 = chron.getPunchById(punch2);

        Log.d(TAG, "P1 Current Hour: " + p1.getTime().getHourOfDay());
        Log.d(TAG, "P1 Current Minute: " + p1.getTime().getMinuteOfHour());

        inTime.setCurrentHour(p1.getTime().getHourOfDay());
        inTime.setCurrentMinute(p1.getTime().getMinuteOfHour());
        
        if(p2 != null){
            Log.d(TAG, "P2 Current Hour: " + p2.getTime().getHourOfDay());
            Log.d(TAG, "P2 Current Minute: " + p2.getTime().getMinuteOfHour());

            outTime.setCurrentHour(p2.getTime().getHourOfDay());
            outTime.setCurrentMinute(p2.getTime().getMinuteOfHour());
        } else {
            findViewById(R.id.outLayout).setVisibility(View.GONE);
        }

        //close chronos
        chron.close();
    }

    AdapterView.OnItemSelectedListener taskListener = new AdapterView.OnItemSelectedListener(){

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            Log.d(TAG, "Position: " + position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            return;
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
