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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import com.kopysoft.chronos.R;
import com.kopysoft.chronos.content.StaticFunctions;
import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.types.Day;
import com.kopysoft.chronos.types.Punch;

import java.util.GregorianCalendar;

public class EnableWidget extends AppWidgetProvider {

    private static String SEND_CLOCK = "com.kopysoft.chronos.service.EnableWidget.CLOCK";
    public static String UPDATE_FROM_APP = "com.kopysoft.chronos.service.EnableWidget.UPDATE";
    private static String TAG = Defines.TAG + " - Widget";
    private static final boolean printDebugMessages = Defines.DEBUG_PRINT;

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        final int N = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i = 0; i < N; i++) {
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
            if (today.getTimeWithBreaks() < 0) {
                views.setImageViewResource(R.id.imageButton, R.drawable.widget_disabled);
                StaticFunctions.setUpAlarm(context,
                        cal.getTimeInMillis() - 1000,
                        (AlarmManager) context.getSystemService(Context.ALARM_SERVICE));
            } else {
                views.setImageViewResource(R.id.imageButton, R.drawable.widget_enabled);
                StaticFunctions.removeAlarm(context,
                        (AlarmManager) context.getSystemService(Context.ALARM_SERVICE));
            }
            views.setOnClickPendingIntent(R.id.imageButton, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);

        GregorianCalendar cal = new GregorianCalendar();
        int todayArray[] = new int[3];
        todayArray[0] = cal.get(GregorianCalendar.YEAR);
        todayArray[1] = cal.get(GregorianCalendar.MONTH);
        todayArray[2] = cal.get(GregorianCalendar.DAY_OF_MONTH);
        Day today = new Day(todayArray, context);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        if (today.getTimeWithBreaks() < 0) {
            views.setImageViewResource(R.id.imageButton, R.drawable.widget_disabled);
            StaticFunctions.setUpAlarm(context,
                    cal.getTimeInMillis() - 1000,
                    (AlarmManager) context.getSystemService(Context.ALARM_SERVICE));

        } else {
            views.setImageViewResource(R.id.imageButton, R.drawable.widget_enabled);
            StaticFunctions.removeAlarm(context,
                    (AlarmManager) context.getSystemService(Context.ALARM_SERVICE));
        }
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(new ComponentName(context, EnableWidget.class), views);

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        GregorianCalendar cal = new GregorianCalendar();
        int todayArray[] = new int[3];
        todayArray[0] = cal.get(GregorianCalendar.YEAR);
        todayArray[1] = cal.get(GregorianCalendar.MONTH);
        todayArray[2] = cal.get(GregorianCalendar.DAY_OF_MONTH);
        Day today = new Day(todayArray, context);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        Intent intentSend = new Intent(context, com.kopysoft.chronos.service.EnableWidget.class);
        intentSend.setAction(SEND_CLOCK);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intentSend, 0);

        if (intent.getAction().compareTo(SEND_CLOCK) == 0) {
            if (printDebugMessages) Log.d(TAG, "Update Icon");
            long i_time = today.getTimeWithBreaks();

            int clockedType = Defines.IN;
            if (today.getTimeWithBreaks() < 0) {
                clockedType = Defines.OUT;
                views.setImageViewResource(R.id.imageButton, R.drawable.widget_enabled);
                i_time += cal.getTimeInMillis();
                StaticFunctions.removeAlarm(context,
                        (AlarmManager) context.getSystemService(Context.ALARM_SERVICE));
            } else {
                StaticFunctions.setUpAlarm(context,
                        cal.getTimeInMillis() - 1000,
                        (AlarmManager) context.getSystemService(Context.ALARM_SERVICE));

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

        } else if (intent.getAction().compareTo(UPDATE_FROM_APP) == 0) {
            if (today.getTimeWithBreaks() < 0) {
                views.setImageViewResource(R.id.imageButton, R.drawable.widget_disabled);
            } else {
                views.setImageViewResource(R.id.imageButton, R.drawable.widget_enabled);
            }
        } else {
            super.onReceive(context, intent);
            return;
        }

        if (printDebugMessages) Log.d(TAG, intent.getAction());
        views.setOnClickPendingIntent(R.id.imageButton, pendingIntent);
        manager.updateAppWidget(new ComponentName(context, EnableWidget.class), views);
    }
}

