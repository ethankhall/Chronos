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

import java.util.ArrayList;
import java.util.GregorianCalendar;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.kopysoft.chronos.content.Chronos;
import com.kopysoft.chronos.enums.Defines;

public class PayPeriod {
	ArrayList<Day> _days = new ArrayList<Day>();
	GregorianCalendar _start = null;
	GregorianCalendar _end = null;
	private static final String TAG = Defines.TAG + " - PayPeriod";

	public PayPeriod(int[] start, int[] end, Context context){
		//Configure start and end times 
		_start = new GregorianCalendar(start[0], start[1], start[2]);
		_end = new GregorianCalendar(end[0], end[1], end[2]);
		
		ArrayList<Punch> returnValue = new ArrayList<Punch>();
		Chronos chrono = new Chronos(AppContext.getAppContext());
		SQLiteDatabase db = chrono.getReadableDatabase();

		Cursor cursor = db.query(Chronos.TABLE_NAME_CLOCK, 
				new String[] { "_id", "punch_type", "time", "actionReason" }, 
				"( time >= ? AND time <= ? )", 
				new String[] {Long.toString(_start.getTimeInMillis()), 
					Long.toString(_end.getTimeInMillis() + 24 * 60 * 60 * 1000 -1) }, 
				null, null, "time ASC ");	//Get all time punches between today at midnight and midnight

		final int colType = cursor.getColumnIndex("punch_type");
		final int colTime = cursor.getColumnIndex("time");
		final int colId = cursor.getColumnIndex("_id");
		final int colAR = cursor.getColumnIndex("actionReason");

		int punch, id, actionReason;
		long time;
		if (cursor.moveToFirst()) {
			do {
				punch = (int)cursor.getLong(colType);
				time = cursor.getLong(colTime);
				id = (int)cursor.getLong(colId);
				actionReason = (int)cursor.getLong(colAR);
				returnValue.add(new Punch(time, punch, id, actionReason));

			} while (cursor.moveToNext());
			cursor.close();
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		db.close();
		
		GregorianCalendar temp = new GregorianCalendar(start[0], start[1], start[2]);

		long difference = _end.getTimeInMillis() - _start.getTimeInMillis();
		difference = difference / 1000 / 60 / 60 / 24;
		
		ArrayList<Punch> ArrayOfDayPunches = null;
		int[] tempDayInfo = new int[3];
		long tempTime;
		for(int i = 0; i < difference; i++){
			tempDayInfo[0] = temp.get(GregorianCalendar.YEAR);
			tempDayInfo[1] = temp.get(GregorianCalendar.MONTH);
			tempDayInfo[2] = temp.get(GregorianCalendar.DAY_OF_MONTH);
			ArrayOfDayPunches = new ArrayList<Punch>();
			tempTime = temp.getTimeInMillis();
			
			Log.d(TAG, "tempTime: " + tempTime);
			Log.d(TAG, "end tempTime: " + (tempTime + 24*60*60*1000));
			for(int indexArray = 0; indexArray < returnValue.size(); indexArray++){
				Punch tempPunch = returnValue.get(indexArray);
				//Log.d(TAG, "Punch Time: " + tempPunch.getTime());
				
				if(tempPunch.getTime() > tempTime && tempPunch.getTime() < tempTime + 24*60*60*1000){
					ArrayOfDayPunches.add(tempPunch);
					Log.d(TAG, "Instert!");
				}
			}
			
			Log.d(TAG, "Size: " + ArrayOfDayPunches.size());
			
			Note todayNote = new Note(tempDayInfo, context);
			
			Day instartDay = new Day(tempDayInfo, ArrayOfDayPunches, todayNote);
			
			_days.add(instartDay);
			temp.add(GregorianCalendar.DAY_OF_YEAR, 1);
		}
	}

	public void fixMidights(){
		boolean prevNeedFix = false;

		for(int i = 0; i < _days.size(); i++){
			boolean[] needFix = _days.get(i).checkForMidnight();

			if(needFix[Defines.REGULAR_TIME] == true){
				if(prevNeedFix == false){
					Day temp = _days.get(i);
					int[] dayInfo= temp.getDay();
					GregorianCalendar cal = new GregorianCalendar(dayInfo[0], dayInfo[1], dayInfo[2]);
					cal.add(GregorianCalendar.DAY_OF_YEAR, 1);
					Punch quickFix = new Punch(cal.getTimeInMillis() - 1000, 
							Defines.OUT, -1, Defines.REGULAR_TIME);
					quickFix.setNeedToUpdate(true);
					temp.add(quickFix);
					_days.get(i).updateDay();

					quickFix = new Punch(cal.getTimeInMillis() + 1000, 
							Defines.IN, -1, Defines.REGULAR_TIME);
					quickFix.setNeedToUpdate(true);
					
					if(i + 1 < _days.size()){
						_days.get(i+1).add(quickFix);
						_days.get(i+1).updateDay();
					}

				} else {
					Day temp = _days.get(i);
					int[] dayInfo= temp.getDay();
					GregorianCalendar cal = new GregorianCalendar(dayInfo[0], dayInfo[1], dayInfo[2]);
					cal.add(GregorianCalendar.DAY_OF_YEAR, 1);
					Punch quickFix = new Punch(cal.getTimeInMillis() - 1000, 
							Defines.OUT, -1, Defines.REGULAR_TIME);
					quickFix.setNeedToUpdate(true);
					temp.add(quickFix);
					_days.get(i).updateDay();
				}
			}
			prevNeedFix = needFix[Defines.REGULAR_TIME];

			//all others
			for(int j = 1; j < Defines.MAX_CLOCK_OPT; j++){
				if(needFix[j] == true){
					Day temp = _days.get(i);
					int[] dayInfo= temp.getDay();
					GregorianCalendar cal = new GregorianCalendar(dayInfo[0], dayInfo[1], dayInfo[2]);
					cal.add(GregorianCalendar.DAY_OF_YEAR, 1);
					Punch quickFix = new Punch(cal.getTimeInMillis() - 1000, 
							Defines.OUT, -1, j);
					quickFix.setNeedToUpdate(true);
					temp.add(quickFix);
					_days.get(i).updateDay();
				}
			}
		}
	}

	public long getTimeForDay(int index){
		return _days.get(index).getTimeWithBreaks();
	}

	public Note getNoteForDay(int index){
		GregorianCalendar cal = _start;
		cal.add(GregorianCalendar.DAY_OF_YEAR, index);
		int[] tempDayInfo = new int[3];
		tempDayInfo[0] = cal.get(GregorianCalendar.YEAR);
		tempDayInfo[1] = cal.get(GregorianCalendar.MONTH);
		tempDayInfo[2] = cal.get(GregorianCalendar.DAY_OF_MONTH);
		return new Note(tempDayInfo, AppContext.getAppContext());
	}

	public int size(){
		return _days.size();
	}

	public Day get(int index){
		return _days.get(index);
	}

	public void set(int index, Day replaceDay){
		_days.set(index, replaceDay);
	}
	
	public void add(Day newDay){
		_days.add(newDay);
	}
}
