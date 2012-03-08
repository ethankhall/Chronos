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

package com.kopysoft.chronos.types;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "tasks")
public class Task implements Comparable<Task>{

    public final static String TASK_FIELD_NAME = "task_id";

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(canBeNull = false)
    private int taskOrder;
    @DatabaseField(canBeNull = false, foreign = true)
    private Job job;
    @DatabaseField(canBeNull = false)
    private float payOverride;
    @DatabaseField(canBeNull = false)
    private boolean enablePayOverride;
    @DatabaseField
    private String taskName;

    public Task(Job iJob, int iTaskOrder,  String iTaskName){
        job = iJob;
        taskName = iTaskName;
        taskOrder = iTaskOrder;
        enablePayOverride = false;
        payOverride = 0.0f;
    }
    
    public void setPayOverride(float pay){
        payOverride = pay;
    }

    public float getPayOverride(){
        return payOverride;
    }

    public void setEnablePayOverride(boolean enabled){
        enablePayOverride = enabled;
    }

    public boolean getEnablePayOverride(){
        return enablePayOverride;
    }

    public void setName(String inName){
        taskName = inName;
    }

    public int getID(){
        return id;
    }

    public String getName(){
        return taskName;
    }

    public void setJob(Job iJob){
        job = iJob;
    }

    public Job getJob(){
        return job;
    }

    public Task(){ }

    @Override
    public String toString(){
        return taskName;
    }

    @Override
    public int compareTo(Task task) {
        return taskOrder - task.taskOrder;
    }
}
