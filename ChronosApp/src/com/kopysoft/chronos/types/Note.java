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

@DatabaseTable(tableName = "notes")
public class Note {

    @DatabaseField(canBeNull = false, defaultValue = "")
    String noteString;
    @DatabaseField(canBeNull = false, dataType= DataType.SERIALIZABLE)
    private DateTime gDateTime;
    @DatabaseField(canBeNull = false, foreign = true, columnName = Punch.JOB_FIELD_NAME)
    private Job job;


    /**
     *
     * @param date      Date
     * @param jobNumber Job Number
     * @param note   Note
     */
    public Note(DateTime date, Job jobNumber,  String note){
        gDateTime = date;
        noteString = "";
        job = jobNumber;
    }

    public void setTime(DateTime date){
        gDateTime = date;
    }
    
    public DateTime getTime(){
        return gDateTime;
    }

    public void setNote(String message){
        noteString = message;
    }

    public String getNote(){
        return noteString;
    }

}
