package com.kopysoft.chronos.service;

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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.kopysoft.chronos.content.StaticFunctions;
import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.singelton.ListenerObj;
import com.kopysoft.chronos.types.Day;
import com.kopysoft.chronos.types.Punch;

public class BackgroundUpdate extends BroadcastReceiver {
	final static String TAG = Defines.TAG + " - BackgroundUpdate";

	@Override
	public void onReceive(Context context, Intent intent) {
		StaticFunctions.printLog(Defines.ALL, TAG, "Background");
		int[] startLunch = intent.getExtras().getIntArray("startLunch");
		int[] endLunch = intent.getExtras().getIntArray("endLunch");
		int autoLunch = intent.getExtras().getInt("autoLunch");
		int weeksInPP = intent.getExtras().getInt("weeksInPP");
		int[] startOfPP = intent.getExtras().getIntArray("startOfPP");
		boolean notificationsEnabled = intent.getExtras().getBoolean("notificationsEnabled", true);

		GregorianCalendar currentCal = new GregorianCalendar();
		StaticFunctions.printLog(Defines.ALL, TAG, "Current Calc: " + currentCal.getTimeInMillis());

		Intent i = new Intent().setClass(context, com.kopysoft.chronos.service.ChronoService.class);
		IBinder tempIBinder = peekService(context, i);
		IAndroidService remoteService = IAndroidService.Stub.asInterface(tempIBinder);

		int[] todayInfo = {currentCal.get(GregorianCalendar.YEAR), 
				currentCal.get(GregorianCalendar.MONTH), currentCal.get(GregorianCalendar.DAY_OF_MONTH)};

		Day today = new Day(todayInfo, context);
		long timeToday = today.getTimeWithBreaks();
		boolean lunchBeenTaken = today.hasLunchBeenTaken(context);
		
		//Log.d(TAG, "PayRate: " + prefs.getPayRate());
		//Log.d(TAG, "Lunch: " + prefs.getAutomatic_lunch());
		//Log.d(TAG, "TimeToday: " + timeToday);

		//check for lunch
		if(autoLunch >= 2 && !lunchBeenTaken && timeToday < 0){
			GregorianCalendar lunchTime = new GregorianCalendar();
			lunchTime.setTimeInMillis(currentCal.getTimeInMillis());
			lunchTime.set(GregorianCalendar.HOUR, endLunch[0]);
			lunchTime.set(GregorianCalendar.MINUTE, endLunch[1]);
			
			long times[] = today.getArrayOfTime();
			//Log.d(TAG, "Lunch Time Array: " + times[Defines.LUNCH_TIME]);

			StaticFunctions.printLog(Defines.ALL, TAG, "Lunch Time: " + lunchTime.getTimeInMillis());
			if(lunchTime.compareTo(currentCal) > 0 && times[Defines.LUNCH_TIME] == 0){
				GregorianCalendar inCal = new GregorianCalendar();
				inCal.set(GregorianCalendar.HOUR_OF_DAY, startLunch[0]);
				inCal.set(GregorianCalendar.MINUTE, startLunch[1]);
				inCal.set(GregorianCalendar.SECOND, 0);
				inCal.set(GregorianCalendar.MILLISECOND, 0);
				Punch newPunch = null;
				newPunch = new Punch(inCal.getTimeInMillis(), Defines.IN, -1, Defines.LUNCH_TIME);
				newPunch.commitToDb(context);

				GregorianCalendar outCal = new GregorianCalendar();
				outCal.set(GregorianCalendar.HOUR_OF_DAY, endLunch[0]);
				outCal.set(GregorianCalendar.MINUTE, endLunch[1]);
				outCal.set(GregorianCalendar.SECOND, 0);
				outCal.set(GregorianCalendar.MILLISECOND, 0);
				newPunch = new Punch(outCal.getTimeInMillis(), Defines.OUT, -1, Defines.LUNCH_TIME);
				newPunch.commitToDb(context);

				today.setHasLunchBeenTaken(context, true);
				
				StaticFunctions.printLog(Defines.ALL, TAG, "Start Lunch Calc: " + 
						currentCal.getTimeInMillis());
				StaticFunctions.printLog(Defines.ALL, TAG, "End Lunch Calc: " + 
						currentCal.getTimeInMillis());
				
				ListenerObj.getInstance().fire();
				try{
					if(autoLunch == 2){
						remoteService.setTextNotification("Lunch", "Don't forget to take a lunch!");
					} else if(autoLunch == 4){
						remoteService.setTextNotification("Lunch", "I took a lunch for you, FYI.");
					}
				}catch(Exception e){
					//Log.e(TAG, e.getMessage());
				}
			}
		}

		//midnight
		try{
		StaticFunctions.fixMidnight(startOfPP, weeksInPP, context);
		} catch (Exception e){
			Log.e(TAG, e.getMessage());
			throw new RuntimeException();
		}
		
		//update clock
		try{
			if(notificationsEnabled == true && timeToday < 0){
				remoteService.setClockAction(true, timeToday);
			} else {
				remoteService.setClockAction(false, timeToday);
			}
		}catch(Exception e){
			//Log.e(TAG, e.getMessage());
		}



	}

}
