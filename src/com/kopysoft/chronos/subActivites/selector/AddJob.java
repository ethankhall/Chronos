/*******************************************************************************
 * Copyright (C) 2011 by Ethan Hall
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/

package com.kopysoft.chronos.subActivites.selector;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import com.kopysoft.chronos.R;
import com.kopysoft.chronos.content.Chronos;
import com.kopysoft.chronos.types.Job;

public class AddJob extends Activity {

    Job editJob;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_job);

        getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        getWindow().setTitle("Create a Job");

        int id = getIntent().getExtras().getInt("id");

        if(id == -1 ){
            editJob = new Job(-1, "");
        } else {
            editJob = new Job(id, new Chronos(getApplicationContext()));
        }

        EditText doubleTime = (EditText) findViewById(R.id.doubleTime);
        EditText overTime = (EditText) findViewById(R.id.overtime);
        EditText payRate = (EditText) findViewById(R.id.payRate);
        EditText jobName = (EditText) findViewById(R.id.jobName);

        doubleTime.setText(String.format("%.2f", editJob.getDoubleTime()) );
        overTime.setText(String.format("%.2f", editJob.getOverTime() ) );
        payRate.setText(String.format("%.2f", editJob.getPayRate() ) );
        jobName.setText(editJob.getJobName());

    }

    private Button.OnClickListener onClick = new Button.OnClickListener(){

        public void onClick(View view)
        {
            EditText doubleTime = (EditText) findViewById(R.id.doubleTime);
            EditText overTime = (EditText) findViewById(R.id.overtime);
            EditText payRate = (EditText) findViewById(R.id.payRate);
            EditText jobName = (EditText) findViewById(R.id.jobName);

            String sJobName = jobName.toString();
            float fPayRate = Float.parseFloat(payRate.toString());
            float fOverTime = Float.parseFloat(overTime.toString());
            float fDoubleTime = Float.parseFloat(doubleTime.toString());

            editJob.setInfo(sJobName, fPayRate, fOverTime, fDoubleTime);
            editJob.commit(new Chronos(getApplicationContext()));

        }
    };
}
