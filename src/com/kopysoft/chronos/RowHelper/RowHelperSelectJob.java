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

package com.kopysoft.chronos.RowHelper;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.types.Job;

import java.util.ArrayList;

public class RowHelperSelectJob extends BaseAdapter {

	Context gContext;
	ArrayList<Job> jobs;

	public RowHelperSelectJob(Context context, ArrayList<Job> inJobs) {
		gContext = context;
		jobs = inJobs;
	}

	public int getCount() {
		return jobs.size() + 1;
	}

	public Job getItem(int arg0) {
        if(arg0 == 0)
            return null;
        else
		    return jobs.get(arg0 - 1);
	}

    public long getItemId(int i) {
        return i;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        Log.d(Defines.TAG + " - RH_SJ", "Position: " + position);

        if(position == 0){
            convertView =  new NewJobRow(gContext);
        } else {
            JobRow job = new JobRow(gContext);
            job.getCheckBox().setText(jobs.get(position).getJobName());
            convertView = job;
        }

        Log.d(Defines.TAG + " - RH_SJ", "Return View: " + convertView);

        return convertView;
	}
}
