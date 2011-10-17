package com.kopysoft.chronos.singelton;

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

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.kopysoft.chronos.content.Chronos;
import com.kopysoft.chronos.content.StaticFunctions;
import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.enums.TimeFormat;
import com.kopysoft.chronos.enums.Verbosity;

public class PreferenceSingleton {

    public TimeFormat getPrefViewTime(Context appContext){
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(appContext);
        return StaticFunctions.TimeFormater(app_preferences.getString("viewPrefTime", "1").trim());
    }

    public TimeFormat getPrefPunchTime(Context appContext){
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(appContext);
        return StaticFunctions.TimeFormater(app_preferences.getString("punchPrefTime", "1").trim());
    }

    public TimeFormat getPrefEditTime(Context appContext){
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(appContext);
        return StaticFunctions.TimeFormater(app_preferences.getString("editPrefTime", "1").trim());
    }

    public int getWeeksInPP(Context appContext){
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(appContext);
        try{
            return Integer.parseInt(app_preferences.getString("weeks_in_pp", "2").trim());
        } catch (NumberFormatException e){
            return 2;
        } catch (Exception e){
            return 2;
        }
    }

    public float getOvertimeRate(Context appContext){
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(appContext);
        //overtime calculation
        try{
            return Float.parseFloat(app_preferences.getString("overtime_rate", "1").trim());
        } catch(Exception e){
            return 1;
        }
    }

    public boolean getOvertimeEnable(Context appContext) {
        //get overtime enable
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(appContext);
        return app_preferences.getBoolean("overtime_enable", false);
    }

    public int getOvertimeSetting(Context appContext)	{
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(appContext);
        //get overtime setting
        try{
            return Integer.parseInt(app_preferences.getString("8_or_40_hours", "1").trim());
        } catch (Exception e){
            return 1;
        }
    }

    public double getPayRate(Context appContext){
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(appContext);
        //get pay rate
        try{
            return Double.valueOf(app_preferences.getString("pay_rate","7.25"));
        } catch(NumberFormatException e) {
            return 7.25;
        }
    }

    public String getDate(Context appContext){
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(appContext);
        return app_preferences.getString("date", Defines.INITIAL_START_DATE_OF_PP);
    }

    public int[] getStartOfThisPP(Context appContext){

        int[] dateGiven = Chronos.getDate(getDate(appContext));
        int weeks_in_pp = getWeeksInPP(appContext);
        return Chronos.getPP(dateGiven, weeks_in_pp);
    }


    public int getReportLevel(Context appContext){
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(appContext);
        try{
            String reportLevelString = app_preferences.getString("reportLevel", "2");
            return Integer.parseInt(reportLevelString);
        } catch(Exception e){
            return 2;
        }
    }

    public Verbosity getReportLevelVerbosity(Context appContext){
        if (getReportLevel(appContext) == 1){
            return Verbosity.EVERY_PUNCH;
        } else {
            return Verbosity.ONLY_DAY;
        }
    }

    public int getAutomaticLunch(Context appContext){
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(appContext);
        try{
            String automatic_lunch = app_preferences.getString("automatic_lunch", "1");
            return Integer.parseInt(automatic_lunch);
        } catch(NumberFormatException e){
            return 1;
        }
    }

    public boolean getShowPay(Context appContext){
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(appContext);
        //get show pay
        return app_preferences.getBoolean("showPay", true);
    }

    public String getClockoutForLunch(Context appContext){
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(appContext);
        return app_preferences.getString("clockout_lunch", "11:30");
    }

    public String getClockinForLunch(Context appContext){
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(appContext);
        return app_preferences.getString("clockin_lunch", "12:30");
    }

    public double getRegularTime(Context appContext){
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(appContext);
        int OvertimeSetting = getOvertimeSetting(appContext);
        if(OvertimeSetting == 1){
            try{
                return Double.valueOf(app_preferences.getString("regular_time","8"));
            } catch(NumberFormatException e) {
                return 8;
            }
        } else {
            try{
                return Double.valueOf(app_preferences.getString("regular_time","40"));
            } catch(NumberFormatException e) {
                return 40;
            }
        }
    }

    public double getDoubleTime(Context appContext){
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(appContext);
        int OvertimeSetting = getOvertimeSetting(appContext);
        if(OvertimeSetting == 1){
            try{
                return Double.valueOf(app_preferences.getString("double_time","12"));
            } catch(NumberFormatException e) {
                return 12;
            }
        }else {
            try{
                return Double.valueOf(app_preferences.getString("double_time","60"));
            } catch(NumberFormatException e) {
                return 60;
            }
        }
    }

    public boolean getNotificationEnabled(Context appContext){
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(appContext);
        return app_preferences.getBoolean("NotificationsEnabled", true);
    }
}