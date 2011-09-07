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

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import com.kopysoft.chronos.R;
import com.kopysoft.chronos.RowHelper.RowHelperSelectJob;
import com.kopysoft.chronos.content.Chronos;
import com.kopysoft.chronos.types.Job;

import java.util.ArrayList;

public class SelectJob extends ListActivity {

    ArrayList<Job> jobs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_job);

        Chronos chrono = new Chronos(getApplicationContext());
        jobs = chrono.getJobNumbers();

        RowHelperSelectJob adapter = new RowHelperSelectJob(getApplicationContext(), jobs);
        setListAdapter(adapter);

        getListView().setOnItemClickListener(itemClick);

        getWindow().setTitle("Select a Job");

    }

    private AdapterView.OnItemClickListener itemClick = new AdapterView.OnItemClickListener(){

        public void onItemClick(AdapterView<?> parent, View view,  int position, long id) {
            if(position == 0) {
                Intent jobClick = new Intent().setClass(getApplicationContext(), AddJob.class);
                jobClick.putExtra("id", -1);
                startActivity(jobClick);
            } else {
                Intent jobClick = new Intent().setClass(getApplicationContext(), AddJob.class);
                jobClick.putExtra("id", jobs.get(position).getJobNumber());
                startActivity(jobClick);
            }

        }
    };


}
