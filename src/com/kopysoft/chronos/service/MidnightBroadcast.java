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

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.kopysoft.chronos.content.StaticFunctions;
import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.singelton.ListenerObj;
import com.kopysoft.chronos.singelton.PreferenceSingelton;
import com.kopysoft.chronos.types.Day;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MidnightBroadcast extends BroadcastReceiver{
	private static final String TAG = Defines.TAG + " - MB";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if ( Defines.DEBUG_PRINT ) Log.d(TAG, "Midnight Revieved");	
		PreferenceSingelton prefs = new PreferenceSingelton();
		StaticFunctions.fixMidnight(prefs.getStartOfThisPP(context), 
				prefs.getWeeksInPP(context), context);
		ListenerObj.getInstance().fireMidnight();
		
		Intent updateNotification = new Intent();
		updateNotification.setClass(context, NotificationBroadcast.class);
		
		GregorianCalendar cal = new GregorianCalendar();

		int[] dateGiven = new int[3];
		dateGiven[0] = cal.get(Calendar.YEAR);
		dateGiven[1] = cal.get(Calendar.MONTH);
		dateGiven[2] = cal.get(Calendar.DAY_OF_MONTH);
		Day today = new Day(dateGiven, context);
		
		updateNotification = NotificationBroadcast.runUpdate(updateNotification, 
				today.getTimeWithBreaks());
		context.sendBroadcast(updateNotification);
		
	}
}
