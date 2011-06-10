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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.Environment;
import android.util.Log;

import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.types.Day;
import com.kopysoft.chronos.types.HoldNote;
import com.kopysoft.chronos.types.Note;
import com.kopysoft.chronos.types.Punch;

public class Chronos extends SQLiteOpenHelper {

	private static final String TAG = "Chronos - SQL";

	//0.9 = 7
	//1.0.1 - 1.1.0 = 10
	//1.2.0	= 11

	private static final int DATABASE_VERSION = 11;
	public static final String TABLE_NAME_CLOCK = "clockactions";
	public static final String TABLE_NAME_NOTE = "notes";
	public static final String TABLE_NAME_OTHER = "misc";
	public static final String DATABASE_NAME = "Chronos";

	String insertString = "INSERT INTO " + TABLE_NAME_CLOCK + "(time, actionReason) VALUES (?, ?, ?)";
	String insertNote = "INSERT INTO " + TABLE_NAME_NOTE + "(note_string, time) VALUES (?, ?)";
	public static final String insertLunch = "INSERT INTO " + 
	TABLE_NAME_OTHER + "(day, lunchTaken) VALUES (?, ?)";

	public Chronos(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + TABLE_NAME_CLOCK + 
		" ( _id INTEGER PRIMARY KEY NOT NULL, time LONG NOT NULL, actionReason INTEGER NOT NULL )");
		db.execSQL("CREATE TABLE " + TABLE_NAME_NOTE + 
		" ( _id LONG PRIMARY KEY, note_string TEXT NOT NULL, time LONG NOT NULL )");
		db.execSQL("CREATE TABLE " + TABLE_NAME_OTHER + 
		" ( _id INTEGER PRIMARY KEY NOT NULL, day LONG NOT NULL, lunchTaken INTEGER NOT NULL ) ");
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

		Log.d(TAG, "Update");
		ArrayList<HoldNote> Notes = null;
		try{
			Notes = getNotes(db);
		} catch (SQLiteException e){
			try{
				Notes = getNotes(db);
			} catch( SQLiteException e2){
				throw(e2);
			}
		} 

		ArrayList<Punch> punches = new ArrayList<Punch>();
		Cursor cursor = db.query(TABLE_NAME_CLOCK, new String[] { "_id", "time", "actionReason" }, 
				null, null, null, null, "_id desc");

		final int colId = cursor.getColumnIndex("_id");
		final int colTime = cursor.getColumnIndex("time");
		final int colAR = cursor.getColumnIndex("actionReason");
		if (cursor.moveToFirst()) {
			do {				

				long id = cursor.getLong(colId);
				long time = cursor.getLong(colTime);
				int type = cursor.getInt(colAR);
				Punch temp = new Punch(time, Defines.IN, id, type);
				punches.add(temp);


			} while (cursor.moveToNext());
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		dropAll(db);

		for(int i = 0; i < punches.size(); i++){
			Punch temp = punches.get(i);
			temp.setNeedToUpdate(true);
			temp.removeId();
			temp.commitToDb(db);
		}

		reloadNotes(db, Notes);
	}

	/**
	 * This method is intended to be used ONLY for testing purposes!!
	 * 
	 */
	public void replacePunches(ArrayList<Punch> punches){

		SQLiteDatabase db = getWritableDatabase();
		for(int i = 0; i < punches.size(); i++){
			Punch temp = punches.get(i);
			temp.setNeedToUpdate(true);
			temp.removeId();
			temp.commitToDb(db);
		}
		db.close();
	}

	/**
	 * This method is intended to be used ONLY for testing purposes!!
	 * 
	 * @return a list of the punches, so it is able to be restored
	 */
	public ArrayList<Punch> getPunces(){

		SQLiteDatabase db = getReadableDatabase();
		ArrayList<Punch> punches = new ArrayList<Punch>();
		Cursor cursor = db.query(TABLE_NAME_CLOCK, new String[] { "_id","punch_type", "time" }, 
				null, null, null, null, "_id desc");

		final int colId = cursor.getColumnIndex("_id");
		final int colTime = cursor.getColumnIndex("time");
		final int colType = cursor.getColumnIndex("punch_type");
		if (cursor.moveToFirst()) {
			do {				

				long id = cursor.getLong(colId);
				long time = cursor.getLong(colTime);
				int type = cursor.getInt(colType);
				Punch temp = new Punch(time, type, id, Defines.REGULAR_TIME);
				punches.add(temp);


			} while (cursor.moveToNext());
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		db.close();
		return punches;

	}

	private void reloadNotes(SQLiteDatabase db, ArrayList<HoldNote> Notes){
		for( int i = 0; i < Notes.size(); i++ ){
			SQLiteStatement insertStmt = db.compileStatement(insertNote);

			insertStmt.bindString(1, Notes.get(i).getText() );
			insertStmt.bindLong(2, Notes.get(i).getTime() );
			insertStmt.executeInsert();
			Log.d(TAG, "Adding entry: " + i); 
		} //end loop 
	}

	//---------------------------------------------------------------
	//
	//				SQL Section
	//
	//---------------------------------------------------------------

	/**
	 * 
	 * @param xml_info List of XMLDay's from XML file
	 * 
	 * @note This will destroy the DB. Use carefully!
	 */
	public void replaceFromXML(List<Day> xml_info, Object context){
		SQLiteDatabase db = getWritableDatabase();
		//delete DBgetApplicationContext
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_CLOCK);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_NOTE);

		//Create DB
		db.execSQL("CREATE TABLE " + TABLE_NAME_CLOCK + 
		" ( _id INTEGER PRIMARY KEY, punch_type INTEGER NOT NULL, time LONG NOT NULL )");
		db.execSQL("CREATE TABLE " + TABLE_NAME_NOTE + 
		" ( _id INTEGER PRIMARY KEY, note_string TEXT NOT NULL, time LONG NOT NULL )");

		for(int i = 0; i < xml_info.size(); i++){
			Day workingDay = xml_info.get(i);
			if(workingDay.getNote(false).trim() != ""){
				int[] dayTime = { workingDay.getDay()[0], 
						workingDay.getDay()[1], workingDay.getDay()[2]}; 

				Note newNote = null;
				if(context.getClass().equals(Chronos.class) == true){
					newNote = new Note(dayTime,(Chronos)context);
				} else if(context.getClass().equals(Context.class) == true) {
					newNote = new Note(dayTime,(Context)context);
				} else {
					return;
				}
				newNote.setNote(workingDay.getNote(true));
				newNote.update();
			}
			workingDay.forceWrite();
		}
	}

	/**
	 * 
	 * @return Returns list of HoldNote's
	 */
	private ArrayList<HoldNote> getNotes(SQLiteDatabase db){
		ArrayList<HoldNote> returnValue = new ArrayList<HoldNote>();

		Cursor cursor = db.query(TABLE_NAME_NOTE, new String[] { "note_string", "time" }, 
				null, null, null, null, "time ASC ");

		long time_temp;
		String text_temp;
		HoldNote tempNote = null;

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

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return returnValue;
	}

	/**
	 * Checks to see if the user has stayed clocked in over midnight and will clock them out
	 */
	/*
	public boolean checkForMidnight(){
		GregorianCalendar cal = new GregorianCalendar();
		SQLiteDatabase db = getWritableDatabase();

		boolean returnValue = false;

		int[] date1 = new int[2];
		int[] date2 = new int[2];

		date1[0] = cal.get(Calendar.DAY_OF_YEAR);
		date1[1] = cal.get(Calendar.YEAR);

		Cursor cursor = db.query(TABLE_NAME_CLOCK, new String[] { "punch_type", "time" }, 
				null, null, null, 
				null, "time ASC ");	//Get all time punches between today at midnight and midnight
		if(cursor.moveToLast()){
			int punch = (int)cursor.getLong(0);
			long time = cursor.getLong(1);
			if ( punch == Defines.IN ){
				cal.setTimeInMillis(time);
				date2[0] = cal.get(Calendar.DAY_OF_YEAR);
				date2[1] = cal.get(Calendar.YEAR);
				Punch newPunch = null;

				//Check if they are the same
				//if the day of year are not the same or years are not the same
				if( ( date2[0] != date1[0]) || (date2[1] != date1[1]) ){
					cal.set(Calendar.HOUR_OF_DAY, 23);
					cal.set(Calendar.MINUTE, 59);
					cal.set(Calendar.SECOND, 59);
					cal.set(Calendar.MILLISECOND, 0);
					long temp = cal.getTimeInMillis();
					newPunch = new Punch()

					//insertTime(db, Defines.OUT, temp);

					cal.add(Calendar.DAY_OF_YEAR, 1);
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 1);
					cal.set(Calendar.MILLISECOND, 0);
					date2[0] = cal.get(Calendar.DAY_OF_YEAR);
					date2[1] = cal.get(Calendar.YEAR);

					//check to see that the new day is todays
					if( ( date2[0] == date1[0]) || (date2[1] == date1[1]) ){	
						temp = cal.getTimeInMillis();
						//insertTime(db, Defines.IN, temp);
					}
					returnValue = true;
				}
			}
		}
		cursor.close();
		db.close();
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
		onCreate(db);
		db.close();
	}

	public void dropAll(SQLiteDatabase db){
		Log.w(TAG, "Dropping tables then recreate.");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_CLOCK);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_NOTE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_OTHER);
		onCreate(db);
	}


	/**
	 * Prints all entries in the database
	 */
	public void printAll(){
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME_CLOCK, new String[] { "_id","punch_type", "time" }, 
				null, null, null, null, "_id desc");
		long arg1, arg2, arg3;
		int hour, min, sec, month, day, year;
		GregorianCalendar cal = new GregorianCalendar();

		int colType = cursor.getColumnIndex("punch_type");
		int colTime = cursor.getColumnIndex("time");
		int colId = cursor.getColumnIndex("_id");

		if ( Defines.DEBUG_PRINT )Log.d(TAG, "Print All: ");

		if (cursor.moveToFirst()) {
			do {
				arg1 = cursor.getLong(colType);
				arg2 = cursor.getLong(colTime);
				arg3 = cursor.getLong(colId);

				cal.setTimeInMillis(arg2);
				hour = cal.get(Calendar.HOUR_OF_DAY);
				min = cal.get(Calendar.MINUTE);
				sec = cal.get(Calendar.SECOND);

				day = cal.get(Calendar.DAY_OF_MONTH);
				month = cal.get(Calendar.MONTH);
				year = cal.get(Calendar.YEAR);

				if ( Defines.DEBUG_PRINT )Log.d(TAG, "ID: " + arg3 + "\tTime:" + arg2 + "\tPunch Type: " + 
						arg1 + "\t" + month + "/" + day +
						"/" + year + " - " + hour + ":" + min + ":" + sec);
			} while (cursor.moveToNext());
			cursor.close();
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		db.close();
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
