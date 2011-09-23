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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import com.kopysoft.chronos.content.Chronos;
import com.kopysoft.chronos.enums.Defines;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;


public class Day {

	private GregorianCalendar _date = null;
	private ArrayList<Punch> _punches = null;
	private ArrayList<Punch> _removed = null;
    private int _jobNumber;
	private static final String TAG = Defines.TAG + " - DAY";
	private boolean printDebug = Defines.DEBUG_PRINT;

	/**
	 * Creates a Day class
	 * @param dayInfo Day information [year, month, day]
   * @param jobNumber job number
   * @param context Context for this app
	 */
	public Day(int dayInfo[], int jobNumber, Context context){
		_date = new GregorianCalendar(dayInfo[0], dayInfo[1], dayInfo[2]);
		_punches = getPunchesForDay(_date);
		_removed = new ArrayList<Punch>();
        _jobNumber = jobNumber;
	}

    /**
     *
     * @param dayInfo   Day information [year, month, day]
     * @param punches   List of punches
     * @param jobNumber Job Number
     */
	public Day(int dayInfo[], ArrayList<Punch> punches, int jobNumber){
		_date = new GregorianCalendar(dayInfo[0], dayInfo[1], dayInfo[2]);
		_punches = punches;
		_removed = new ArrayList<Punch>();
        _jobNumber = jobNumber;
	}

    /**
     *
     * @param dayInfo       Day information [year, month, day]
     * @param jobNumber     Job Number
     */
	public Day(int dayInfo[], int jobNumber){
		_date = new GregorianCalendar(dayInfo[0], dayInfo[1], dayInfo[2]);
		_punches = getPunchesForDay(_date);
		_removed = new ArrayList<Punch>();
        _jobNumber = jobNumber;
	}
	
	public void setHasLunchBeenTaken(Context context, boolean setLunch){
		Chronos chrono = new Chronos(context);
		SQLiteDatabase db = chrono.getWritableDatabase();
		
		Cursor cursor = db.query(Chronos.TABLE_NAME_OTHER, 
				new String[] {"_id", "day", "lunchTaken"}, 
				"( day = ? )", 
				new String[] {Long.toString(_date.getTimeInMillis()) }, 
				null, null, "_id ASC ");	//Get all time punches between today at midnight and midnight

		final int colID = cursor.getColumnIndex("_id");

		int fromDB = -1;
		if (cursor.moveToFirst()) {
			fromDB = cursor.getInt(colID);
		}

		if (!cursor.isClosed()) {
			cursor.close();
		}
		
		int lunchSetting = 0;
		if(setLunch){
			lunchSetting = 1;
		}
		
		if(fromDB == -1 ){
			SQLiteStatement insertStmt = db.compileStatement(Chronos.insertLunch);
			//day, lunchTaken
			
			insertStmt.bindLong(1, _date.getTimeInMillis());
			insertStmt.bindLong(2, lunchSetting);
			insertStmt.executeInsert();

		} else {
			ContentValues newConent = new ContentValues();
			newConent.put("day", _date.getTimeInMillis());
			newConent.put("lunchTaken", lunchSetting);
			
			db.update(Chronos.TABLE_NAME_CLOCK, newConent, " _id = ? ",
					new String[] {Long.toString(fromDB)});
		}
		db.close();
	}

	public boolean hasLunchBeenTaken(Context context){
		boolean returnValue;

		Chronos chrono = new Chronos(context);
		SQLiteDatabase db = chrono.getReadableDatabase();
		Cursor cursor = db.query(Chronos.TABLE_NAME_OTHER, 
				new String[] {"day", "lunchTaken"}, 
				"( day = ? )", 
				new String[] {Long.toString(_date.getTimeInMillis()) }, 
				null, null, "day ASC ");	//Get all time punches between today at midnight and midnight

		final int colLunch = cursor.getColumnIndex("lunchTaken");

		int fromDB = 0;
		if (cursor.moveToFirst()) {
			fromDB = cursor.getInt(colLunch);
		}

		if (!cursor.isClosed()) {
			cursor.close();
		}
		db.close();

		if(fromDB == 0)
			returnValue = false;
		else
			returnValue = true;

		return returnValue;
	}

    /*
	public String getNote(boolean force){
		return _todayNote.getNote(force);
	}

	public void updateNote(String newNote){
		_todayNote.setNote(newNote);
		_todayNote.update();
	}
	*/

	private synchronized ArrayList<Punch> getPunchesForDay(GregorianCalendar cal){
		if(printDebug)  Log.d(TAG, "update day");
		ArrayList<Punch> returnValue = new ArrayList<Punch>();
		Chronos chrono = new Chronos(AppContext.getAppContext());
		SQLiteDatabase db = chrono.getReadableDatabase();
		long startMilli, endMilli;

		startMilli = cal.getTimeInMillis();
		endMilli = startMilli + 24 * 60 * 60 * 1000;

		Cursor cursor = db.query(Chronos.TABLE_NAME_CLOCK, 
				new String[] { "_id", "time", "actionReason" }, 
				"( time >= ? AND time <= ? ) AND jobNumber = ?",
				new String[] {Long.toString(startMilli), Long.toString(endMilli), Integer.toString(_jobNumber) },
				null, null, "time ASC ");	//Get all time punches between today at midnight and midnight

		final int colTime = cursor.getColumnIndex("time");
		final int colId = cursor.getColumnIndex("_id");
		final int colAR = cursor.getColumnIndex("actionReason");

		int id, actionReason;
		long time;
		if (cursor.moveToFirst()) {
			do {
				time = cursor.getLong(colTime);
				id = (int)cursor.getLong(colId);
				actionReason = (int)cursor.getLong(colAR);
				returnValue.add(new Punch(time, Defines.IN, id, actionReason, _jobNumber));

			} while (cursor.moveToNext());
			cursor.close();
		}

		if ( !cursor.isClosed()) {
			cursor.close();
		}

		db.close();
		
		//Update the day's punch Types
		returnValue = updateType(returnValue);
		return returnValue;
	}
	
	private ArrayList<Punch> updateType(ArrayList<Punch> returnValue){
		//ArrayList<Punch> returnValue = new ArrayList<Punch>();
		int[] typePunches = new int[Defines.MAX_CLOCK_OPT];
		for(int i = 0; i < Defines.MAX_CLOCK_OPT; i++)
			typePunches[i] = 0;
		for(int i = 0; i< returnValue.size(); i++){
			Punch tempPunch = returnValue.get(i);
			int reason = tempPunch.getAction();
			if((( typePunches[reason]) % 2 ) == 1){
				tempPunch.setType(Defines.OUT);
				if(printDebug) Log.d(TAG, "index: " + i + "\tSet Out");
			} else {
				tempPunch.setType(Defines.IN);
			}
			if(printDebug) Log.d(TAG, "index: " + i + "\t Type: " + tempPunch.getType() + "\tAction: " + tempPunch.getAction());
			typePunches[reason] = typePunches[reason] + 1;
			returnValue.set(i, tempPunch);
		}
		for(int i = 0; i < Defines.MAX_CLOCK_OPT; i++)
			if(printDebug) Log.d(TAG, "typePunces[" + i + "]: " + typePunches[i]);
		return returnValue;
	}

	public int[] getDay(){
        return new int[]{_date.get(Calendar.YEAR), _date.get(Calendar.MONTH),
                _date.get(Calendar.DAY_OF_MONTH)};
	}

	public long[] getArrayOfTime(){
		long[] returnValue = new long[Defines.MAX_CLOCK_OPT];

		//init to 0
		for(int i = 0; i < Defines.MAX_CLOCK_OPT; i++)
			returnValue[i] = 0;

		for(int i = Defines.REGULAR_TIME; i < Defines.MAX_CLOCK_OPT; i++){
            for (Punch _punch : _punches) {
                if (_punch.getAction() == i) {
                    //if the action reason is what were counting
                    if (_punch.getType() == Defines.IN) {
                        returnValue[i] -= _punch.getTime();
                    } else {
                        returnValue[i] += _punch.getTime();
                    }
                }
            }
		}
		return returnValue;
	}

	public long getTimeWithBreaks(){
		long returnValue = 0;

		long[] listOfTimes =  getArrayOfTime();
		for(int i = 0; i < Defines.MAX_CLOCK_OPT; i++){
			if(i != Defines.LUNCH_TIME && i != Defines.BREAK_TIME){
				returnValue += listOfTimes[i];
			}
		}

		if(returnValue != 0)
		{
			if(listOfTimes[Defines.LUNCH_TIME] < 0 ){
				returnValue = Math.abs( returnValue - listOfTimes[Defines.LUNCH_TIME]);
			} else {
				returnValue = returnValue - Math.abs( listOfTimes[Defines.LUNCH_TIME] );
			}

			if(listOfTimes[Defines.BREAK_TIME] < 0 ){
				returnValue = Math.abs( returnValue - listOfTimes[Defines.BREAK_TIME]);
			} else {
				returnValue = returnValue - listOfTimes[Defines.BREAK_TIME];
			}
		}
		
		return returnValue;
	}

	public boolean needToUpdateClock(){
		long[] listOfTimes =  getArrayOfTime();
		if(listOfTimes[Defines.REGULAR_TIME] <= 0 || listOfTimes[Defines.HOLIDAY_TIME] <= 0){
			if(listOfTimes[Defines.LUNCH_TIME] <= 0 || listOfTimes[Defines.BREAK_TIME] <= 0){
				return false;
			}
		}
		return true;
	}

	public long getSeconds(){
		return (getTimeWithBreaks() / 1000);
	}

	public void updateDay(){
        for (Punch _punch : _punches) {
            if (_punch.isNeedToUpdate()) {
                _punch.commitToDb(AppContext.getAppContext());
                if (printDebug) Log.d(TAG, "Punch: " + _punch.getTime());
            }
        }
        for (Punch a_removed : _removed) {
            a_removed.commitToDb(AppContext.getAppContext());
        }

		_removed.clear();
		_punches = getPunchesForDay(_date);
	}

    /*
	public void forceWrite(){
		for(int i = 0; i < _punches.size(); i++){
			_punches.get(i).setNeedToUpdate(true);
			_punches.get(i).commitToDb(AppContext.getAppContext());
			if(printDebug) Log.d(TAG, "Punch: " + _punches.get(i).getTime());
		}
	}
	*/

	public int getSize(){
		return _punches.size();
	}

	public Punch get(int id){
		try{
			return _punches.get(id);
		} catch (Exception e){
			return null;
		}
	}

	public void set(int id, Punch replace){
		_punches.set(id, replace);
	}

	public void add(Punch add){
		_punches.add(add);
	}

	public void remove(int id){
		Punch temp = _punches.get(id);
		_punches.remove(id);
		temp.setNeedToRemove(true);
		_removed.add(temp);
	}

	public void removeByID(long id){
		Punch temp;
		for( int i = _punches.size(); i > 0; i++){
			if(_punches.get(i - 1).getId() == id){
				temp = _punches.get(i - 1);
				_punches.remove(i - 1);
				temp.setNeedToRemove(true);
			}
		}
	}

	public Punch getByID(long id){
		Punch temp;
		for( int i = _punches.size(); i > 0; i--){
			if(_punches.get(i - 1).getId() == id){
				temp = _punches.get(i - 1);
				return temp;
			}
		}
		return null;
	}

	public void setByID(long id, Punch newPunch){
		if(printDebug) Log.d(TAG, "SetByID: " + id);
		for( int i = _punches.size(); i > 0; i--){
			if(_punches.get(i - 1).getId() == id){
				_punches.set(i - 1, newPunch);
			}
		}
		sort();
	}

	public void cancelUpdates(){
		_punches = getPunchesForDay(_date);
		_removed.clear();
	}

	public boolean[] checkForMidnight(){
		boolean[] returnValue = new boolean[Defines.MAX_CLOCK_OPT];
		for(int i = 0; i < Defines.MAX_CLOCK_OPT; i++){
			returnValue[i] = false;
		}

		GregorianCalendar cal = new GregorianCalendar();
		if(cal.get(GregorianCalendar.DAY_OF_YEAR) > _date.get(GregorianCalendar.DAY_OF_YEAR) &&
				cal.get(GregorianCalendar.YEAR) >= _date.get(GregorianCalendar.YEAR)){
			//Not today
			long[] hoursForToday = getArrayOfTime();
			for(int i = 0; i < hoursForToday.length; i++){
				if(hoursForToday[i] < 0)
					returnValue[i] = true;
			}
		}
		return returnValue;
	}

	public void sort(){
		Collections.sort(_punches, new Punch.PunchComparator());
		_punches = updateType(_punches);
	}
}
