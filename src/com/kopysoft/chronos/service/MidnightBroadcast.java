package com.kopysoft.chronos.service;

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
