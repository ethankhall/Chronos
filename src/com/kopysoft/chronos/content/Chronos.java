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

package com.kopysoft.chronos.content;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.j256.ormlite.android.AndroidConnectionSource;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.kopysoft.chronos.types.Punch;

import java.sql.SQLException;
import java.util.List;

public class Chronos extends SQLiteOpenHelper {

    private static final String TAG = "Chronos - SQL";

    //0.9 = 7
    //1.0.1 - 1.1.0 = 10
    //1.2.0	= 11
    //2.0.0 = 12

    private static final int DATABASE_VERSION = 12;
    public static final String TABLE_NAME_CLOCK = "clockactions";
    public static final String TABLE_NAME_JOBS = "jobs";
    public static final String TABLE_NAME_NOTE = "notes";
    public static final String TABLE_NAME_OTHER = "misc";
    public static final String DATABASE_NAME = "Chronos";

    //String insertString = "INSERT INTO " + TABLE_NAME_CLOCK + "(time, actionReason) VALUES (?, ?, ?)";
    String insertNote = "INSERT INTO " + TABLE_NAME_NOTE + "(note_string, time) VALUES (?, ?)";
    public static final String insertLunch = "INSERT INTO " +
            TABLE_NAME_OTHER + "(day, lunchTaken) VALUES (?, ?)";

    public Chronos(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME_CLOCK +
                " ( _id INTEGER PRIMARY KEY NOT NULL, " +
                " time LONG NOT NULL, " +
                " actionReason INTEGER NOT NULL, " +
                " jobNumber INTEGER DEFAULT 0 )");
        db.execSQL("CREATE TABLE " + TABLE_NAME_NOTE +
                " ( _id LONG PRIMARY KEY, " +
                " note_string TEXT NOT NULL, " +
                " time LONG NOT NULL, " +
                " jobNumber INTEGER DEFAULT 0 )");
        db.execSQL("CREATE TABLE " + TABLE_NAME_OTHER +
                " ( _id INTEGER PRIMARY KEY NOT NULL, " +
                " day LONG NOT NULL, " +
                " lunchTaken INTEGER NOT NULL, " +
                " jobNumber INTEGER DEFAULT 0 ) ");
        db.execSQL("CREATE TABLE " + TABLE_NAME_JOBS +
                " ( _id INTEGER PRIMARY KEY NOT NULL, " +
                " name String NOT NULL," +
                " payRate FLOAT NOT NULL, " +
                " overTime FLOAT NOT NULL, " +
                " doubleTime FLOAT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database, this will drop tables and recreate.");
        Log.w(TAG, "oldVerion: " + oldVersion + "\tnewVersion: " + newVersion);


        if (oldVersion == 11){
            String dbCal1 = "ALTER TABLE " + TABLE_NAME_CLOCK + "ADD COLUMN jobNumber INTEGER DEFAULT 0";
            String dbCal2 = "ALTER TABLE " + TABLE_NAME_NOTE  + "ADD COLUMN jobNumber INTEGER DEFAULT 0";
            String dbCal3 = "ALTER TABLE " + TABLE_NAME_OTHER + "ADD COLUMN jobNumber INTEGER DEFAULT 0";
            String dbCal4 ="CREATE TABLE " + TABLE_NAME_JOBS +
                    " ( _id INTEGER PRIMARY KEY NOT NULL, " +
                    " name String NOT NULL, " +
                    " default INTEGER NOT NULL )";
            db.execSQL(dbCal1);
            db.execSQL(dbCal2);
            db.execSQL(dbCal3);
            db.execSQL(dbCal4);
        }
    }

    public List<Punch> getAllPunches(){

        List<Punch> retValue = null;
        try{
            ConnectionSource connectionSource = new AndroidConnectionSource(this);

            // instantiate the DAO to handle Account with String id
            Dao<Punch,String> punchDao = BaseDaoImpl.createDao(connectionSource, Punch.class);
            retValue = punchDao.queryForAll();


            connectionSource.close();
        } catch(SQLException e){
            Log.d(TAG, e.getMessage());
        } catch (Exception e) {
            Log.d(TAG,e.getMessage());
        }
        return retValue;
    }
}
