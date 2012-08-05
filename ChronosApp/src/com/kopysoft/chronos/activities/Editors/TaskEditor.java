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

package com.kopysoft.chronos.activities.Editors;

import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.ehdev.chronos.lib.Chronos;
import com.kopysoft.chronos.R;
import com.ehdev.chronos.lib.enums.Defines;
import com.ehdev.chronos.lib.types.Job;
import com.ehdev.chronos.lib.types.Task;

public class TaskEditor extends SherlockActivity {

    private static String TAG = Defines.TAG + " - TaskEditor";
    private final boolean enableLog = Defines.DEBUG_PRINT;
    private Task thisTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if(enableLog) Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_editor);

        int taskID = getIntent().getExtras().getInt("task");
        Log.d(TAG, "New Task Editor with ID: " + taskID);
        if(taskID != -1){
            Chronos chron = new Chronos(this);
            thisTask = chron.getTaskById(taskID);
            chron.close();
        }  else {
            Chronos chron = new Chronos(this);
            Job thisJob = chron.getAllJobs().get(0);
            thisTask = new Task(thisJob, chron.getAllTasks().size(), "New Task");
            chron.close();
        }

        TextView taskName = (TextView)findViewById(R.id.task_name);
        CheckBox overridePay = (CheckBox)findViewById(R.id.override_pay);
        TextView payRate = (TextView)findViewById(R.id.pay_rate);
        Spinner taskSpinnerOut = (Spinner)findViewById(R.id.how_to_pay);
        overridePay.setOnClickListener(listener);

        @SuppressWarnings("unchecked")
        ArrayAdapter adapter = ArrayAdapter.createFromResource( this,
                R.array.override_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        taskSpinnerOut.setAdapter(adapter);
        
        taskName.setText(thisTask.getName());
        overridePay.setChecked(thisTask.getEnablePayOverride());
        payRate.setEnabled(thisTask.getEnablePayOverride());
        taskSpinnerOut.setEnabled(thisTask.getEnablePayOverride());

        if(!thisTask.getEnablePayOverride()){
            payRate.setText(Float.toString(thisTask.getJob().getPayRate()));
            taskSpinnerOut.setSelection(0);
        } else {
            if(enableLog) Log.d(TAG, "getPayOverride: " + thisTask.getPayOverride());
            if(thisTask.getPayOverride() > 0){
                 payRate.setText(Float.toString(thisTask.getPayOverride()));
                taskSpinnerOut.setSelection(0);
            } else if(thisTask.getPayOverride() < 0) {
                payRate.setText(Float.toString(-1 * thisTask.getPayOverride()));
                taskSpinnerOut.setSelection(2);
            } else {
                taskSpinnerOut.setSelection(1);
            }
        }

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

    CheckBox.OnClickListener listener = new CheckBox.OnClickListener(){

        @Override
        public void onClick(View view) {
            TextView payRate = (TextView)findViewById(R.id.pay_rate);
            Spinner taskSpinnerOut = (Spinner)findViewById(R.id.how_to_pay);
            if(((CheckBox)view).isChecked()){
                payRate.setEnabled(true);
                taskSpinnerOut.setEnabled(true);
            } else {
                payRate.setEnabled(false);
                taskSpinnerOut.setEnabled(false);
            }
        }
    };

    private void update(){
        TextView taskName = (TextView)findViewById(R.id.task_name);
        CheckBox overridePay = (CheckBox)findViewById(R.id.override_pay);
        TextView payRate = (TextView)findViewById(R.id.pay_rate);
        Spinner taskSpinnerOut = (Spinner)findViewById(R.id.how_to_pay);
        
        thisTask.setName(taskName.getText().toString());
        thisTask.setEnablePayOverride(overridePay.isChecked());
        
        Log.d(TAG, "spinner position: " + taskSpinnerOut.getSelectedItemPosition());

        float payRateData = Float.parseFloat(payRate.getText().toString());
        if(taskSpinnerOut.getSelectedItemPosition() == 0){
            payRateData = Math.abs(payRateData);
        } else if(taskSpinnerOut.getSelectedItemPosition() == 2){
            payRateData = -1 * Math.abs(payRateData);
        }  else {
            payRateData = 0;
        }
        thisTask.setPayOverride(payRateData);
        Chronos chron = new Chronos(this);
        chron.updateTask(thisTask);
        chron.close();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getSupportMenuInflater().inflate(R.menu.save_cancel_menu, menu);
        menu.findItem(R.id.menuDelete).setVisible(false);
        menu.findItem(R.id.RemoveMenu).setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuDelete:
                Chronos chronos = new Chronos(this);
                chronos.deleteTask(thisTask);
                chronos.close();
                finish();
                return true;
            case R.id.menuSave:
                update();
                finish();
                return true;
            case R.id.menuCancel:
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
