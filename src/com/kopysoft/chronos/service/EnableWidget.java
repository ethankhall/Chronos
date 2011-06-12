package com.kopysoft.chronos.service;

import java.util.GregorianCalendar;

import com.kopysoft.chronos.R;
import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.types.Day;
import com.kopysoft.chronos.types.Punch;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class EnableWidget extends AppWidgetProvider {
	
	private static String SEND_CLOCK = "com.kopysoft.chronos.service.EnableWidget.CLOCK";
	public static String UPDATE_FROM_APP = "com.kopysoft.chronos.service.EnableWidget.UPDATE";
	private static String TAG = Defines.TAG + " - Widget";
	private static final boolean printDebugMessages = true;

	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

		final int N = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];
            
            //Intent intent = new Intent(context, com.kopysoft.MorseMessenger.EnableWidget.class);
            Intent intent = new Intent(context, com.kopysoft.chronos.service.EnableWidget.class);
            intent.setAction(SEND_CLOCK);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
            int todayArray[] = new int[3];
            
            GregorianCalendar cal = new GregorianCalendar();
            todayArray[0] = cal.get(GregorianCalendar.YEAR);
            todayArray[1] = cal.get(GregorianCalendar.MONTH);
            todayArray[2] = cal.get(GregorianCalendar.DAY_OF_MONTH);
			Day today = new Day(todayArray, context);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            if(today.getTimeWithBreaks() < 0){
            	views.setImageViewResource(R.id.imageButton, R.drawable.widget_disabled);
            } else {
            	views.setImageViewResource(R.id.imageButton, R.drawable.widget_enabled);
            }
            views.setOnClickPendingIntent(R.id.imageButton, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }        
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		GregorianCalendar cal = new GregorianCalendar();
		int todayArray[] = new int[3];
		todayArray[0] = cal.get(GregorianCalendar.YEAR);
		todayArray[1] = cal.get(GregorianCalendar.MONTH);
		todayArray[2] = cal.get(GregorianCalendar.DAY_OF_MONTH);
		Day today = new Day(todayArray, context);
		AppWidgetManager manager = AppWidgetManager.getInstance(context);
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
		
		if(intent.getAction().compareTo(SEND_CLOCK) == 0){
			if(printDebugMessages) Log.d(TAG, "Update Icon");
			long i_time = today.getTimeWithBreaks();
			
			int clockedType = Defines.IN;
			if(today.getTimeWithBreaks() < 0){
				clockedType = Defines.OUT;
				views.setImageViewResource(R.id.imageButton, R.drawable.widget_enabled);
				i_time += cal.getTimeInMillis();
            } else {
            	views.setImageViewResource(R.id.imageButton, R.drawable.widget_disabled);
            	i_time -= cal.getTimeInMillis();
            }
			
			Punch newPunch = new Punch(cal.getTimeInMillis(), clockedType, -1, Defines.REGULAR_TIME);
			newPunch.commitToDb(context);
			
			//Send intent for the notification bar
			Intent runIntent = new Intent().setClass(context, 
					com.kopysoft.chronos.service.NotificationBroadcast.class);
			runIntent = NotificationBroadcast.runUpdate(runIntent, i_time);
			context.sendBroadcast(runIntent);
			//End intent
			
		} else if(intent.getAction().compareTo(UPDATE_FROM_APP) == 0){
			if(today.getTimeWithBreaks() < 0){
				views.setImageViewResource(R.id.imageButton, R.drawable.widget_disabled);
            } else {
            	views.setImageViewResource(R.id.imageButton, R.drawable.widget_enabled);
            }
		}
		
		if(printDebugMessages) Log.d(TAG,intent.getAction());
		manager.updateAppWidget(new ComponentName(context, EnableWidget.class), views);
	}
}

