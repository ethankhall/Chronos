/*******************************************************************************
 * Copyright (c) 2011-2012 Ethan Hall
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 ******************************************************************************/

package com.kopysoft.chronos.lib;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by IntelliJ IDEA.
 * User: ethan
 * Date: 3/15/12
 * Time: 8:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class AlarmClass {
    
    public static final int REPEATING_ALARM = 192937;

    public static void setUpAlarm(Context context, long millis, AlarmManager am){

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        if(pref.getBoolean("NotificationsEnabled", true)){
            Intent intent = new Intent(context, NotificationBroadcast.class);
            intent.putExtra("notificationsEnabled", pref.getBoolean("NotificationsEnabled", true));


            // In reality, you would want to have a static variable for the request code instead of 192837
            PendingIntent sender = PendingIntent.getBroadcast(context,
                    REPEATING_ALARM, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            // Get the AlarmManager service
            am.set(AlarmManager.RTC, millis, sender);
        }
    }
}
