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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.kopysoft.chronos.content.StaticFunctions;
import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.singelton.ListenerObj;
import com.kopysoft.chronos.types.Day;
import com.kopysoft.chronos.types.Job;
import com.kopysoft.chronos.types.Punch;

import java.util.ArrayList;
import java.util.GregorianCalendar;

public class BackgroundUpdate extends BroadcastReceiver {
    final static String TAG = Defines.TAG + " - BackgroundUpdate";

    @Override
    public void onReceive(Context context, Intent intent) {
        StaticFunctions.printLog(Defines.ALL, TAG, "Background");
        int[] startLunch = intent.getExtras().getIntArray("startLunch");
        int[] endLunch = intent.getExtras().getIntArray("endLunch");
        int[] startOfPP = intent.getExtras().getIntArray("startOfPP");
        int autoLunch = intent.getExtras().getInt("autoLunch");
        int weeksInPP = intent.getExtras().getInt("weeksInPP");

        GregorianCalendar currentCal = new GregorianCalendar();
        StaticFunctions.printLog(Defines.ALL, TAG, "Current Calc: " + currentCal.getTimeInMillis());

        Intent runIntent = new Intent().setClass(context,
                com.kopysoft.chronos.service.NotificationBroadcast.class);

        int[] todayInfo = {currentCal.get(GregorianCalendar.YEAR),
                currentCal.get(GregorianCalendar.MONTH), currentCal.get(GregorianCalendar.DAY_OF_MONTH)};

        ArrayList<Job> jobNumbers = StaticFunctions.getJobNumbers(context);
        for(Job jobNumber : jobNumbers) {

            Day today = new Day(todayInfo, jobNumber.getJobNumber(), context);
            long timeToday = today.getTimeWithBreaks();
            boolean lunchBeenTaken = today.hasLunchBeenTaken(context);

            //Log.d(TAG, "Lunch: " + autoLunch);
            //Log.d(TAG, "TimeToday: " + timeToday);
            //Log.d(TAG, "lunchBeenTaken: " + lunchBeenTaken);

            //check for lunch

            GregorianCalendar outCal = new GregorianCalendar();
            outCal.set(GregorianCalendar.HOUR_OF_DAY, endLunch[0]);
            outCal.set(GregorianCalendar.MINUTE, endLunch[1]);
            outCal.set(GregorianCalendar.SECOND, 0);
            outCal.set(GregorianCalendar.MILLISECOND, 0);

            if(autoLunch >= 2 && !lunchBeenTaken && timeToday != 0 && currentCal.compareTo(outCal) >= 0){
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
                    Punch newPunch;
                    newPunch = new Punch(
                            inCal.getTimeInMillis(),
                            Defines.IN,
                            Defines.NEW_PUNCH,
                            jobNumber.getJobNumber(),
                            Defines.LUNCH_TIME
                    );
                    newPunch.commitToDb(context);

                    newPunch = new Punch(
                            outCal.getTimeInMillis(),
                            Defines.OUT,
                            Defines.NEW_PUNCH,
                            jobNumber.getJobNumber(),
                            Defines.LUNCH_TIME
                    );
                    newPunch.commitToDb(context);

                    today.setHasLunchBeenTaken(context, true);

                    StaticFunctions.printLog(Defines.ALL, TAG, "Start Lunch Calc: " +
                            currentCal.getTimeInMillis());
                    StaticFunctions.printLog(Defines.ALL, TAG, "End Lunch Calc: " +
                            currentCal.getTimeInMillis());

                    ListenerObj.getInstance().fire();
                    try{
                        if(autoLunch == 2){
                            runIntent = NotificationBroadcast.setMessage(runIntent,
                                    "Lunch", "Don't forget to take a lunch!");
                        } else if(autoLunch == 4){
                            runIntent = NotificationBroadcast.setMessage(runIntent,
                                    "Lunch", "I took a lunch for you, FYI.");
                        }
                    }catch(Exception e){
                        //Log.e(TAG, e.getMessage());
                    }
                }


                //midnight
                try{
                    StaticFunctions.fixMidnight(startOfPP, weeksInPP, jobNumber.getJobNumber(), context);
                } catch (Exception e){
                    Log.e(TAG, e.getMessage());
                    throw new RuntimeException();
                }
            }

            //update clock
            try{
                if(timeToday < 0){
                    runIntent = NotificationBroadcast.runUpdate(runIntent, timeToday);
                } else {
                    runIntent = NotificationBroadcast.runUpdate(runIntent, 0);
                }
            }catch(Exception e){
                //Log.e(TAG, e.getMessage());
            }
            context.sendBroadcast(runIntent);

        }
    }

}
