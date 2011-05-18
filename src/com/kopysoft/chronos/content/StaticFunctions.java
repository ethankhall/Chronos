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

import java.util.Date;
import java.util.GregorianCalendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;

import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.enums.TimeFormat;
import com.kopysoft.chronos.service.BackgroundUpdate;
import com.kopysoft.chronos.singelton.PreferenceSingelton;
import com.kopysoft.chronos.types.PayPeriod;

public class StaticFunctions {

	/*
	 * Creates a string in the form H:MM:SS when called with a long argument
	 */

	public static String generateTimeString(int hour, int min, int sec, TimeFormat format){

		String returnString = "";
		switch(format){
		case HOUR_MIN:
			returnString = String.format("%d:%02d", hour, min);
			break;

		case HOUR_DECIMAL:
			double temp = (sec + min * 60 + hour * 60 * 60) ;
			temp /= (60 * 60);
			returnString = String.format("%.2f", temp);
			break;

		case HOUR_MIN_SEC:
		default:
			returnString = String.format("%d:%02d:%02d", hour, min, sec);
			break;
		}
		return returnString;
	}

	public static String generateDateString(Context context, long time){
		java.text.DateFormat dateFormat = DateFormat.getTimeFormat(context);
		String returnString = dateFormat.format(new Date(time));
		return returnString;
	}

	public static long correctForClockIn( long i_time){
		long returnValue = i_time / Defines.MS_TO_SECOND;
		if (returnValue < 0) {	//If we are still clocked in
			GregorianCalendar cal = new GregorianCalendar();
			returnValue = returnValue + ( cal.getTimeInMillis() / Defines.MS_TO_SECOND );
		}
		return returnValue;
	}

	public static void setUpAlarm(Context context, long millis, AlarmManager am){
		
		PreferenceSingelton prefs = PreferenceSingelton.getInstance();

		Intent intent = new Intent(context, BackgroundUpdate.class);
		int[] startLunch = getIntFromString(prefs.getStartLunch());
		int[] endLunch = getIntFromString(prefs.getEndLunch());
		intent.putExtra("startLunch", startLunch);
		intent.putExtra("endLunch", endLunch);
		intent.putExtra("autoLunch", prefs.getAutomatic_lunch());
		intent.putExtra("weeksInPP", prefs.getWeeksInPP());
		intent.putExtra("startOfPP", prefs.getStartOfThisPP());
		intent.putExtra("notificationsEnabled", prefs.isNotificationsEnabled());

		// In reality, you would want to have a static variable for the request code instead of 192837
		PendingIntent sender = PendingIntent.getBroadcast(context, 276, intent, 
				PendingIntent.FLAG_UPDATE_CURRENT);

		// Get the AlarmManager service
		am.setInexactRepeating(AlarmManager.RTC, millis, 
				AlarmManager.INTERVAL_FIFTEEN_MINUTES, sender);
	}
	
	public static void removeAlarm(Context context, AlarmManager am){
		Intent intent = new Intent(context, BackgroundUpdate.class);
		
		PendingIntent sender = PendingIntent.getBroadcast(context, 276, intent, 
				PendingIntent.FLAG_UPDATE_CURRENT);

		am.cancel(sender);
	}
	
	public static int[] getIntFromString(String string){
		int[] returnValue = new int[2];
		String[] parcedString = string.split(":");
		try{
			returnValue[0] = Integer.parseInt(parcedString[0]);
			returnValue[1] = Integer.parseInt(parcedString[1]);
		} catch( Exception e){
			returnValue[0] = 11;
			returnValue[1] = 30;
		}

		return returnValue;
	}



	/**
	 * Creates a string based on the input time and the format
	 * 
	 * @param input_time time in milliseconds
	 * @param format format that will be used
	 * @return String with the time and format specified
	 */
	public static String generateTimeString(long input_time, TimeFormat format, 
			boolean correctForClockin) {
		long correctTime = 0;

		if(correctForClockin == true)
			correctTime = correctForClockIn(input_time);
		else
			correctTime = input_time;

		String returnString = "";
		long hour = 0;
		long min = 0;
		long sec = 0;
		sec = (correctTime) % 60;
		min = ((correctTime) / 60 ) % 60;
		hour = (((correctTime) / 60) / 60 ) % 24;

		returnString = generateTimeString((int)hour, (int)min, (int)sec, format);
		return returnString;
	}

	public static TimeFormat TimeFormater( String format ){
		TimeFormat StringFormat;
		switch( Integer.parseInt(format.trim()) ){
		case 1:
			StringFormat = TimeFormat.HOUR_MIN_SEC;
			break;
		case 2:
			StringFormat = TimeFormat.HOUR_MIN;
			break;
		case 3:
			StringFormat = TimeFormat.HOUR_DECIMAL;
			break;
		default: 
			StringFormat = TimeFormat.HOUR_MIN_SEC;
			break;
		}
		return StringFormat;
	}

	public static String generateDollarAmount(long i_time, double payRate){
		String returnValue = "";
		//Convert payRate to dollar amount from $/h to $/s
		double payPerSec = payRate / 60 / 60;
		double temp = (double)i_time * payPerSec;
		returnValue = String.format("$ %02.3f",temp);
		return returnValue;
	}

	public static void printLog(int type, String tag,  String message){
		if(type >= Defines.NOTIFICATION_LEVEL){
			Log.d(tag, message);
		}
	}

	public static void fixMidnight(int[] startOfThisPP, int weeksInPP, Context context){
		//check for midnight		
		startOfThisPP = Chronos.getPP(startOfThisPP, weeksInPP);

		GregorianCalendar cal = new GregorianCalendar(startOfThisPP[0], startOfThisPP[1], startOfThisPP[2]);
		cal.add(GregorianCalendar.DAY_OF_YEAR, 7 * weeksInPP);
		int[] endOfPP = {
				cal.get(GregorianCalendar.YEAR),
				cal.get(GregorianCalendar.MONTH),
				cal.get(GregorianCalendar.DAY_OF_MONTH)
		};

		PayPeriod thisPP = new PayPeriod(startOfThisPP, endOfPP, context);
		thisPP.fixMidights();

	}
}
