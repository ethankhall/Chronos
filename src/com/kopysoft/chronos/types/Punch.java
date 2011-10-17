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

import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.kopysoft.chronos.R;
import com.kopysoft.chronos.content.Chronos;
import com.kopysoft.chronos.enums.Defines;

public class Punch implements Comparable<Punch> {

	private long time;
	private int type;
	private long id;
	private int actionReason;
	private boolean needToUpdate = false;
	private boolean needToRemove = false;
	
	private final static String insertString = 
		"INSERT INTO " + Chronos.TABLE_NAME_CLOCK + 
		"(time, actionReason) VALUES (?, ?)";
	//@SuppressWarnings("unused")
	private static final String TAG = Defines.TAG + " - Punch";

	public Punch(){
		this(0, Defines.IN, -1, Defines.REGULAR_TIME);
	}
	
	/**
	 * Creates a Punch object
	 * @param i_time Time in milliseconds
	 * @param i_type Type of the punch
	 * @param i_id	ID used in the database
	 */
	public Punch(long i_time, int i_type, long i_id, int i_actionReason){
		time = i_time;
		type = i_type;
		id = i_id;
		actionReason = i_actionReason;
	}
	
	public Punch(long i_id, Context gContext){
		id = i_id;
		type = Defines.IN;
		genPunch(gContext);	
	}
	
	public synchronized void genPunch(Context context){
		if(Defines.DEBUG_PRINT) Log.d(TAG, "ID: " + id);
		Chronos chrono = new Chronos(context);
		SQLiteDatabase db = chrono.getReadableDatabase();
		Cursor cursor = db.query(Chronos.TABLE_NAME_CLOCK, 
				new String[] { "_id", "time", "actionReason" }, 
				" _id = ? ", 
				new String[] {Long.toString(id) }, 
				null, null, "time ASC ");	//Get all time punches between today at midnight and midnight

		final int colTime = cursor.getColumnIndex("time");
		final int colAR = cursor.getColumnIndex("actionReason");
		
		if (cursor.moveToFirst()) {
			do {
				time = cursor.getLong(colTime);
				actionReason = (int)cursor.getLong(colAR);

			} while (cursor.moveToNext());
			cursor.close();
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		db.close();
	}
	
	public void setAction(int i_actionReason){
		needToUpdate = true;
		actionReason = i_actionReason;
		if(Defines.DEBUG_PRINT) Log.d(TAG, "New Action_Reason:" + actionReason + "\tID: " + id);
	}
	
	public int getAction(){
		return actionReason;
	}

	public long getTime(){
		return time;
	}

	/** Method for getType()
	 * 
	 * @return returns either Defines.IN or Defines.OUT
	 */
	public int getType(){
		return type;
	}

	public void setTime(long i_time){
		needToUpdate = true;
		if(Defines.DEBUG_PRINT) Log.d(TAG, "setTime: " + time);
		time = i_time;
	}

	public void setType(int i_type){
		type = i_type;
	}
	
	public long getId(){
		return id;
	}

	public String generatePunchString(){
		String returnValue;
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(time);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int min = cal.get(Calendar.MINUTE);
		int sec = cal.get(Calendar.SECOND);
		returnValue = String.format("%d:%02d:%02d", hour, min, sec);
		return returnValue;
	}

	public String generateTypeString(Context contex){
		String[] stringArray = contex.getResources().getStringArray(R.array.TimeTitles);
		String actReason = stringArray[ actionReason ];
		String returnValue = "";
		if(Defines.DEBUG_PRINT) Log.d(TAG, "Action_Reason:" + actionReason + "\tID: " + id);
		if(Defines.DEBUG_PRINT) Log.d(TAG, "Reason: " + returnValue);
		if( type == Defines.IN ){
			returnValue = "In  - ("  + actReason + ")";
		} else {
			returnValue = "Out - ("  + actReason + ")";
		}
		return returnValue;
	}

	public static class PunchComparator implements Comparator<Punch> {
		public int compare(Punch object1, Punch object2) {
			return object1.compareTo(object2);
		}
	}

	public int compareTo(Punch another) {
		return (int)( time - another.getTime());
	}
	
	public synchronized long commitToDb(Context context){
		Chronos chrono = new Chronos(context);
		SQLiteDatabase db = chrono.getWritableDatabase();
		long returnValue = commitToDb(db);
		db.close();
		return returnValue;
	}
	
	public long commitToDb(SQLiteDatabase db){
		long returnValue = -1;
		if(id == -1 && needToRemove == false){
			SQLiteStatement insertStmt = db.compileStatement(insertString);
			long temp = time - (time % 1000);
			
			insertStmt.bindLong(1, temp);
			insertStmt.bindLong(2, actionReason);
			returnValue = insertStmt.executeInsert();
			id = returnValue;
			
		} else if(needToRemove == true){
			returnValue = 
				db.delete(Chronos.TABLE_NAME_CLOCK, "( _id = ? )", new String[] {Long.toString(id)});
		} else if(needToUpdate == true){
			if(Defines.DEBUG_PRINT) Log.d(TAG, "time: " + time);
			ContentValues newConent = new ContentValues();
			newConent.put("time", time);
			newConent.put("actionReason", actionReason);
			if(Defines.DEBUG_PRINT) Log.d(TAG, "ID: " + id);

			returnValue = db.update(Chronos.TABLE_NAME_CLOCK, newConent, " _id = ? ",
					new String[] {Long.toString(id)});
		}
		if(Defines.DEBUG_PRINT) Log.d(TAG, "return value: " + returnValue);
		return returnValue;
	}

	public void setNeedToUpdate(boolean needToUpdate) {
		this.needToUpdate = needToUpdate;
	}

	public boolean isNeedToUpdate() {
		return needToUpdate;
	}

	public void setNeedToRemove(boolean value){
		this.needToRemove = value;
	}
	
	public boolean NeedToRemove(){
		return this.needToRemove;
	}
	
	public void removeId(){
		id = -1;
	}
}
