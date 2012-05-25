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

package com.kopysoft.chronos.types;

import android.content.Context;
import android.text.format.DateFormat;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Comparator;

@DatabaseTable(tableName = "punches")
public class Punch implements Comparable<Punch> {
    
    public static final String TIME_OF_PUNCH = "punch_time";
    public static final String PUNCH_ID_FIELD = "punch_id";

    @DatabaseField(generatedId = true, columnName = PUNCH_ID_FIELD)
    private int id;
    @DatabaseField(canBeNull = false, columnName = TIME_OF_PUNCH)
    private long time;
    @DatabaseField(canBeNull = false, dataType= DataType.SERIALIZABLE)
    DateTimeZone timeZone;
    @DatabaseField(canBeNull = false, foreign = true, columnName = Job.JOB_FIELD_NAME)
    private Job job;
    @DatabaseField(canBeNull = false, foreign = true, columnName = Task.TASK_FIELD_NAME)
    private Task punchTask;

    //DateTime(int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minuteOfHour, int secondOfMinute)

    public Punch() {
    }

    public Punch(Job iJob, Task iPunchTask, DateTime iTime){
        job = iJob;
        punchTask = iPunchTask;
        time = iTime.getMillis();
        timeZone = iTime.getZone();
    }

    public int getID(){
        return id;
    }

    /**
     * Set the job number
     *
     * @param jobNum used to set the job number
     */
    public void setJob(Job jobNum){
        job = jobNum;
    }

    /**
     * Gets the job number of this punch
     *
     * @return int gets this job number
     */
    public Job getJob(){
        return job;
    }

    /**
     * Sets the time of the punch
     *
     * @param inputTime Set time for the punch
     */
    public void setTime(DateTime inputTime){
        time = inputTime.getMillis();
    }

    /**
     * Gets the time of this punch
     *
     * @return {@link DateTime} the time of the punch
     */
    public DateTime getTime(){
        return  new DateTime(time, timeZone);
    }

    /**
     * Set the tag that this punch has
     *
     * @param inTask The string of the tag for this punch. This is useful for setting job tags
     */
    public void setTask(Task inTask){
        punchTask = inTask;
    }

    /**
     * Returns the tag of this punch
     *
     * @return String of the punch tag
     */
    public Task getTask(){
        return punchTask;
    }


    public static class PunchComparator implements Comparator<Punch> {
        public int compare(Punch object1, Punch object2) {
            return object1.compareTo(object2);
        }
    }

    public int compareTo(Punch another) {
        //return (int)( time - another.getTime());
        return ((new DateTime(time)).compareTo(another.getTime()));
    }

    public String toCVS(Context context){
        //id,date,name,task name, date in ms, job num, task num

        DateTimeFormatter fmt;
        if (!DateFormat.is24HourFormat(context))
            fmt = DateTimeFormat.forPattern("E MMM d yyyy h:mm a");
        else
            fmt = DateTimeFormat.forPattern("E MMM d yyyy HH:mm");
        
        return String.format("%d,%s,%s,%s,%d,%d,%d\n",
                getID(),
                getTime().toString(fmt),
                getJob().getName(),
                getTask().getName(),
                getTime().getMillis(),
                getJob().getID(),
                getTask().getID() );
    }

    public String toCVSLegacy(Context context){
        //id,date,name,task name, date in ms, job num, task num
        /*
        returnValue += String.valueOf(id) + ",";
        returnValue += String.valueOf(time) + ",";
        returnValue += String.valueOf(type) + ",";
        returnValue += String.valueOf(actionReason) + ",";
        returnValue += note + "\n";
        */
        return String.format("%d,%d,%d,%d\n",
                getID(),
                getTime().getMillis(),
                getJob().getID(),
                getTask().getID() );
    }

}
