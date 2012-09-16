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

package com.ehdev.chronos.lib.types;

import com.ehdev.chronos.lib.enums.PayPeriodDuration;
import org.apache.commons.lang.ArrayUtils;
import org.apache.maven.lifecycle.internal.TaskSegment;
import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class JsonObj {


    private String jobName;
    private float doubleTime;
    private boolean fourtyHourWeek;
    private PayPeriodDuration payPeriodDuration;
    private boolean overtimeEnabled;
    private float overtime;
    private float payrate;
    private long startOfPayperiod;

    private Task[] taskList;
    private Punch[] punchList;
    private Note[] noteList;
    //private Job job;

    public JsonObj(Job thisJob, List<Punch> punches, List<Task> tasks, List<Note> notes){

        //job = thisJob;

        jobName = thisJob.getName();
        doubleTime = thisJob.doubleTime;
        fourtyHourWeek = thisJob.fourtyHourWeek;
        payPeriodDuration = thisJob.getDuration();
        overtime = thisJob.getOvertime();
        overtimeEnabled = thisJob.isOverTimeEnabled();
        payrate = thisJob.getPayRate();
        startOfPayperiod = thisJob.getStartOfPayPeriod().getMillis();

        taskList = new Task[tasks.size()];
        for(int i = 0; i < tasks.size(); i++){
            taskList[i] = tasks.get(i).getShallowCopy();
        }

        punchList = new Punch[punches.size()];
        for(int i = 0; i < punches.size(); i++){
            punchList[i] = punches.get(i).getShallowCopy();
        }

        noteList = new Note[notes.size()];
        for(int i = 0; i < notes.size(); i++){
            noteList[i] = notes.get(i).getShallowCopy();
        }

    }

    public List<Task> getTasks(){
        return Arrays.asList(taskList);
    }

    public List<Punch> getPunches(){
        return Arrays.asList(punchList);
    }

    public List<Note> getNote(){
        return Arrays.asList(noteList);
    }

    public Job getJob(){
        Job newJob = new Job();
        newJob.setDuration(payPeriodDuration);
        newJob.setPayRate(payrate);
        newJob.setOvertime(overtime);
        newJob.setOvertimeEnabled(overtimeEnabled);
        newJob.setDoubletimeThreshold(doubleTime);
        newJob.setFortyHourWeek(fourtyHourWeek);
        newJob.setName(jobName);
        newJob.setStartOfPayPeriod(new DateTime(startOfPayperiod) );

        return newJob;
    }
}
