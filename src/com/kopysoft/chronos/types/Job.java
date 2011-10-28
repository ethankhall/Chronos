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

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import com.kopysoft.chronos.content.Chronos;

public class Job {

    int gId = -1;
    String gName;
    float gPayRate;
    float gOverTime;
    float gDoubleTime;
    boolean needToRemove = false;
    boolean needToUpdate = false;

     private final static String insertString =
            "INSERT INTO " + Chronos.TABLE_NAME_JOBS +
                    "(name, payRate, overTime, doubleTime) VALUES (?, ?, ?, ?)";


    public Job(int jobNumber, String jobName){
        gName = jobName;
        gId = jobNumber;
    }

    public Job(int jobNumber, Chronos chrono){
        gId = jobNumber;
        SQLiteDatabase db = chrono.getReadableDatabase();

        Cursor cursor = db.query(Chronos.TABLE_NAME_JOBS,
                null,
                "_id = ?",
                new String[] { Integer.toString(jobNumber)},
                null,
                null,
                "_id ASC ");

        if (cursor.moveToFirst()) {
            final int colName = cursor.getColumnIndex("name");
            final int colPayRate = cursor.getColumnIndex("payRate");
            final int colOverTime = cursor.getColumnIndex("overTime");
            final int colDoubleTime = cursor.getColumnIndex("doubleTime");
            gName = cursor.getString(colName);
            gPayRate = cursor.getLong(colPayRate);
            gOverTime = cursor.getLong(colOverTime);
            gDoubleTime = cursor.getLong(colDoubleTime);
        }

        if (!cursor.isClosed()) {
            cursor.close();
        }
        db.close();
    }

    public int getJobNumber(){
        return gId;
    }

    public String getJobName(){
        return gName;
    }

    public void setInfo(String jobName, float payRate, float overTime, float doubleTime){
        gName = jobName;
        gPayRate = payRate;
        gOverTime = overTime;
        gDoubleTime = doubleTime;
        needToUpdate = true;
    }

    public float getPayRate(){
        return gPayRate;
    }

    public float getOverTime(){
        return gOverTime;
    }

    public float getDoubleTime(){
        return gDoubleTime;
    }

    public void remove(){
        needToRemove = true;
    }

    public void commit(Chronos chrono){

        SQLiteDatabase db = chrono.getWritableDatabase();
        ContentValues conecnt = new ContentValues();
            conecnt.put("name", gName);
            conecnt.put("overTime", gOverTime);
            conecnt.put("doubleTime", gDoubleTime);
            conecnt.put("payRate", gPayRate);

        if(gId == -1 && !needToRemove){
            gId = (int)db.insert(Chronos.TABLE_NAME_CLOCK, null, conecnt);

        } else if(needToRemove){
            db.delete(Chronos.TABLE_NAME_JOBS, "( _id = ? )", new String[] {Long.toString(gId)});
        } else if(needToUpdate){
            db.update(Chronos.TABLE_NAME_CLOCK, conecnt, " _id = ? ",
                    new String[] {Long.toString(gId)});
        }
        db.close();
    }

    public void setDefault(Context context){
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putInt("DefaultJobNumber", gId);
        editor.commit();
    }

}
