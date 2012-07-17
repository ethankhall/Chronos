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

package com.ehdev.chronos.types;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.joda.time.DateTime;

@DatabaseTable(tableName = "notes")
public class Note implements Comparable<Note> {

    public static final String DATE_FIELD = "note_date";
    public static final String NOTE_ID_FIELD = "note_id";

    @DatabaseField(generatedId = true, columnName = NOTE_ID_FIELD)
    private int id;
    @DatabaseField(canBeNull = false, defaultValue = "")
    String noteString;
    @DatabaseField(canBeNull = false, columnName = DATE_FIELD)
    private long gDateTime;
    @DatabaseField(canBeNull = false, foreign = true, columnName = Job.JOB_FIELD_NAME)
    private Job job;


    /**
     * Constructor needed for ORMLite
     */

    public Note(){    }
    /**
     *
     * @param date      Date
     * @param jobNumber Job Number
     * @param note   Note
     */
    public Note(DateTime date, Job jobNumber,  String note){
        gDateTime = date.getMillis();
        noteString = note;
        job = jobNumber;
    }
    
    public void setJob(Job newJob){
        job = newJob;
    }
    
    public Job getJob(){
        return job;
    }

    public void setTime(DateTime date){
        gDateTime = date.getMillis();
    }
    
    public DateTime getTime(){
        return new DateTime(gDateTime);
    }

    public void setNote(String message){
        noteString = message;
    }

    public String getNote(){
        return noteString;
    }

    @Override
    public int compareTo(Note punch) {
        return this.getTime().compareTo(punch.getTime());
    }
}
