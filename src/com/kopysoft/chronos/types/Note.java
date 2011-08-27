package com.kopysoft.chronos.types;

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

import java.util.GregorianCalendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.kopysoft.chronos.content.Chronos;
import com.kopysoft.chronos.content.StaticFunctions;
import com.kopysoft.chronos.enums.Defines;

public class Note {

    String noteString;
    boolean needToUpdate = false;
    Chronos i_chrono;
    int[] i_date;
    int i_jobNumber;

    String insertNote = "INSERT INTO " + Chronos.TABLE_NAME_NOTE + "(note_string, time, jobNumber) VALUES (?, ?, ?)";

    /**
     *
     * @param date      Date
     * @param jobNumber Job Number
     * @param context   App Context
     */
    public Note(int[] date, int jobNumber,  Context context){
        i_chrono = new Chronos(context);
        i_date = date;
        noteString = "";
        getNote(true);
        i_jobNumber = jobNumber;
    }

     /**
     *
     * @param date      Date
     * @param jobNumber Job Number
     * @param chronos   Chronos
     */
    public Note(int[] date, int jobNumber, Chronos chronos){
        i_chrono = chronos;
        i_date = date;
        noteString = "";
        getNote(true);
        i_jobNumber = jobNumber;
    }

    public synchronized String getNote(boolean force){
        if(!noteString.equalsIgnoreCase("") && !force){
            return noteString;
        }

        SQLiteDatabase db = i_chrono.getReadableDatabase();
        GregorianCalendar cal = new GregorianCalendar(i_date[0], i_date[1], i_date[2]);
        Cursor cursor = db.query(Chronos.TABLE_NAME_NOTE, new String[] { "note_string" },
                "time = ? AND jobNumber = ?",
                new String[] {Long.toString(cal.getTimeInMillis()), Integer.toString(i_jobNumber)},
                null, null, "time ASC ");

        if (cursor.moveToFirst()) {
            do {
                final int colNote = cursor.getColumnIndex("note_string");
                noteString = cursor.getString(colNote);

                StaticFunctions.printLog(Defines.ALL, "Chronos - Note", "Note: " + noteString);
            } while (cursor.moveToNext());
        }

        if (!cursor.isClosed()) {
            cursor.close();
        }
        db.close();

        return noteString;
    }

    public void setNote(String message){
        if( !message.equalsIgnoreCase(noteString))
            needToUpdate = true;
        noteString = message;
    }

    public void update(){
        if(needToUpdate ){
            editNoteInDb(noteString);
        }
    }

    private synchronized void editNoteInDb(String textString){
        SQLiteDatabase db = i_chrono.getWritableDatabase();
        GregorianCalendar cal = new GregorianCalendar(i_date[0], i_date[1], i_date[2]);
        long id;

        Cursor cursor = db.query(Chronos.TABLE_NAME_NOTE, new String[] { "_id" },
                "time = ? AND jobNumber = ?",
                new String[] {Long.toString(cal.getTimeInMillis()), Integer.toString(i_jobNumber)},
                null, null, "time ASC ");
        //No entry exists
        if(cursor.getCount() == 0){
            SQLiteStatement insertStmt = db.compileStatement(insertNote);

            insertStmt.bindString(1, textString);
            insertStmt.bindLong(2, cal.getTimeInMillis());
            insertStmt.bindLong(3, i_jobNumber);
            insertStmt.executeInsert();

        } else{
            final int colId = cursor.getColumnIndex("_id");
            if (cursor.moveToFirst()) {

                StaticFunctions.printLog(Defines.ALL, "Chronos - Note", "Insert Note");
                id = cursor.getLong(colId);

                ContentValues newConent = new ContentValues();
                newConent.put("note_string", textString);

                db.update(Chronos.TABLE_NAME_NOTE, newConent,
                        " _id = ?", new String[] { Long.toString(id) } );
            }
        }

        if (!cursor.isClosed()) {
            cursor.close();
        }
        db.close();
    }

}
