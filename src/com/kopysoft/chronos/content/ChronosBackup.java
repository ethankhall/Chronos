package com.kopysoft.chronos.content;

/**
 * 			Copyright (C) 2011 by Ethan Hall
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 * 	in the Software without restriction, including without limitation the rights
 * 	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * 	copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 *
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.types.Job;
import com.kopysoft.chronos.types.Punch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ChronosBackup extends SQLiteOpenHelper {

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

    public ChronosBackup(Context context) {
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

        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            if (sd.canWrite()) {
                String currentDBPath = "/data/com.kopysoft.chronos/databases/" + DATABASE_NAME;
                String backupDBPath = DATABASE_NAME + ".db";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);
                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        }catch (Exception e) {
            Log.e(TAG, "ERROR: Can not move file");
        }

        if(oldVersion < 11) {
            Log.d(TAG, "Update");
            //ArrayList<HoldNote> Notes;
            try{
                //Notes = getNotes(db);
            } catch (SQLiteException e){
                try{
                    //Notes = getNotes(db);
                } catch( SQLiteException e2){
                    //throw(e2);
                    //Notes = null;
                }
            }

            ArrayList<Punch> punches = new ArrayList<Punch>();
            Cursor cursor = db.query(TABLE_NAME_CLOCK, null,
                    null, null, null, null, "_id desc");

            final int colId = cursor.getColumnIndex("_id");
            final int colTime = cursor.getColumnIndex("time");
            final int colAR = cursor.getColumnIndex("actionReason");
            if (cursor.moveToFirst()) {
                do {

                    long id = cursor.getLong(colId);
                    long time = cursor.getLong(colTime);
                    int type = Defines.REGULAR_TIME;
                    if(colAR != -1){
                        type = cursor.getInt(colAR);
                    }
                    //Punch temp = new Punch(time, Defines.IN, id, type, 0);
                    Punch temp = new Punch();
                    punches.add(temp);


                } while (cursor.moveToNext());
            }

            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

            dropAll(db);

//            for (Punch temp : punches) {
//                temp.setNeedToUpdate(true);
//                temp.removeId();
//                temp.commitToDb(db);
//            }

//            if(Notes != null){
//                reloadNotes(db, Notes);
//            }
        } if (oldVersion == 11){
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

    /*
    private void reloadNotes(SQLiteDatabase db, ArrayList<HoldNote> Notes){

        for( int i = 0; i < Notes.size(); i++ ){
            SQLiteStatement insertStmt = db.compileStatement(insertNote);

            insertStmt.bindString(1, Notes.get(i).getText() );
            insertStmt.bindLong(2, Notes.get(i).getTime() );
            insertStmt.executeInsert();
            Log.d(TAG, "Adding entry: " + i);
        } //end loop
    }
    */

    //---------------------------------------------------------------
    //
    //				SQL Section
    //
    //---------------------------------------------------------------

    public ArrayList<Job> getJobNumbers(){
        ArrayList<Job> returnVal = new ArrayList<Job>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME_JOBS,
                null,
                null,
                null,
                null,
                null,
                "_id ASC ");

        int jobNumber;
        String jobName;

        if (cursor.moveToFirst()) {
            do {
                final int colNote = cursor.getColumnIndex("_id");
                final int colName = cursor.getColumnIndex("name");
                jobNumber = cursor.getInt(colNote);
                jobName =   cursor.getString(colName);
                returnVal.add(new Job(jobNumber, jobName));

            } while (cursor.moveToNext());
        }

        if (!cursor.isClosed()) {
            cursor.close();
        }
        db.close();
        return returnVal;
    }

    /**
     * @param db database
     *
     * @return Returns list of HoldNote's
     */
    /*
    private ArrayList<HoldNote> getNotes(SQLiteDatabase db){
        ArrayList<HoldNote> returnValue = new ArrayList<HoldNote>();

        Cursor cursor = db.query(TABLE_NAME_NOTE, new String[] { "note_string", "time" },
                null, null, null, null, "time ASC ");

        long time_temp;
        String text_temp;
        HoldNote tempNote;

        if (cursor.moveToFirst()) {
            do {
                final int colNote = cursor.getColumnIndex("note_string");
                final int colTime = cursor.getColumnIndex("time");
                text_temp = cursor.getString(colNote);
                time_temp = cursor.getLong(colTime);

                tempNote = new HoldNote(time_temp, text_temp);
                returnValue.add(tempNote);

            } while (cursor.moveToNext());
        }

        if (!cursor.isClosed()) {
            cursor.close();
        }
        return returnValue;
    }
    */


    /**
     * Drops TABLE_NAME and then recreates the database
     */
    public void dropAll(){
        SQLiteDatabase db = getWritableDatabase();
        Log.w(TAG, "Dropping tables then recreate.");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_CLOCK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_NOTE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_OTHER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_JOBS);
        onCreate(db);
        db.close();
    }

    public void dropAll(SQLiteDatabase db){
        Log.w(TAG, "Dropping tables then recreate.");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_CLOCK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_NOTE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_OTHER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_JOBS);
        onCreate(db);
    }

    /**
     * Gets the start of the current pay period
     * @param dateGiven The start date of any payperiod [year, month, day]
     * @param weeks_in_pp number of weeks in the pay period
     * @return [year, month, day] of the current pay period
     */
    public static int[] getPP(int[] dateGiven, int weeks_in_pp){
        int[] returnValue = new int[3];
        GregorianCalendar cal1 = new GregorianCalendar();
        GregorianCalendar cal2 = new GregorianCalendar(dateGiven[0], dateGiven[1], dateGiven[2]);
        long time1 = cal1.getTimeInMillis();
        long time2 = cal2.getTimeInMillis();

        long diff = time1 - time2;
        diff = diff / 1000;	//convert ms to s
        int weeks = (int) diff / 60 / 60 / 24 / 7;

        int pp_diff = weeks / weeks_in_pp;
        if ( Defines.DEBUG_PRINT )Log.d(TAG, "days to add: " + (pp_diff * weeks_in_pp * 7) );
        cal2.add(Calendar.DAY_OF_YEAR, pp_diff * weeks_in_pp * 7);

        returnValue[0] = cal2.get(Calendar.YEAR);
        returnValue[1] = cal2.get(Calendar.MONTH);
        returnValue[2] = cal2.get(Calendar.DAY_OF_MONTH);

        if ( Defines.DEBUG_PRINT )Log.d(TAG, "Start of PP - Y: " + returnValue[0] + "\tM: " +
                returnValue[1] + "\tD: " + returnValue[2]);
        if ( Defines.DEBUG_PRINT )Log.d(TAG, "Origonal PP: - Y: " + dateGiven[0] + "\tM: " +
                dateGiven[1] + "\tD: " + dateGiven[2]);

        return returnValue;
    }

    public static int[] getDate(String date){
        String temp[] = date.split("\\.");

        int[] returnValue = new int[3];
        returnValue[0] = Integer.parseInt(temp[0]);
        returnValue[1] = Integer.parseInt(temp[1]) - 1;
        returnValue[2] = Integer.parseInt(temp[2]);
        return returnValue;
    }
}
