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

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.joda.time.DateTime;

import java.util.Comparator;

@DatabaseTable(tableName = "punches")
public class Punch implements Comparable<Punch> {

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(canBeNull = false, dataType= DataType.SERIALIZABLE)
    private DateTime time;
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
        time = iTime;
    }

    public int getID(){
        return id;
    }

    /**
     * Set the job number
     *
     * @param jobNum used to set the job number
     */
    public void setJobNumber(Job jobNum){
        job = jobNum;
    }

    /**
     * Gets the job number of this punch
     *
     * @return int gets this job number
     */
    public Job getJobNumber(){
        return job;
    }

    /**
     * Sets the time of the punch
     *
     * @param inputTime Set time for the punch
     */
    public void setTime(DateTime inputTime){
        time = inputTime;
    }

    /**
     * Gets the time of this punch
     *
     * @return {@link DateTime} the time of the punch
     */
    public DateTime getTime(){
        return  (DateTime)time;
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
        return (time.compareTo(another.getTime()));
    }


}
