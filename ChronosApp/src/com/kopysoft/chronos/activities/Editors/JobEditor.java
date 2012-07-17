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

package com.kopysoft.chronos.activities.Editors;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
import com.kopysoft.chronos.R;
import com.kopysoft.chronos.content.Chronos;
import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.enums.PayPeriodDuration;
import com.kopysoft.chronos.types.Job;
import org.joda.time.DateTime;

public class JobEditor extends SherlockPreferenceActivity  {

    private static String TAG = Defines.TAG + " - JobEditor";
    public static final int UPDATE_JOB = 3;
    private final boolean enableLog = Defines.DEBUG_PRINT;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if(enableLog) Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.job_editor);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //set the settings into the DB
    @Override
    public void onPause(){

        Chronos chron = new Chronos(this);
        Job thisJob = chron.getAllJobs().get(0);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        try{
            thisJob.setPayRate(Float.valueOf(pref.getString("normal_pay", "7.25")) );
        } catch (NumberFormatException e){
            thisJob.setPayRate(7.25f);
            Log.d(TAG, e.getMessage());
        }

        try{
            thisJob.setOvertimeEnabled(pref.getBoolean("enable_overtime", true));
        } catch (NumberFormatException e){
            thisJob.setOvertimeEnabled(true);
            Log.d(TAG, e.getMessage());
        }

        try{
            thisJob.setOvertime(Float.valueOf(pref.getString("over_time_threshold", "40")) );
        } catch (NumberFormatException e){
            thisJob.setOvertime(40f);
            Log.d(TAG, e.getMessage());
        }

        try{
            thisJob.setDoubletimeThreshold(Float.valueOf(pref.getString("double_time_threshold", "60")) );
        } catch (NumberFormatException e){
            thisJob.setDoubletimeThreshold( 60f );
            Log.d(TAG, e.getMessage());
        }

        try{
            thisJob.setFortyHourWeek(pref.getBoolean("8_or_40_hours", true));
        } catch (NumberFormatException e){
            thisJob.setFortyHourWeek(true);
            Log.d(TAG, e.getMessage());
        }

        String date[] = pref.getString("date", "2011.1.17").split("\\p{Punct}");
        String time[] = pref.getString("time", "00:00").split("\\p{Punct}");
        thisJob.setStartOfPayPeriod(new DateTime(Integer.parseInt(date[0]),
                Integer.parseInt(date[1]),
                Integer.parseInt(date[2]),
                Integer.parseInt(time[0]),
                Integer.parseInt(time[1])
        ));
        switch (Integer.parseInt(pref.getString("len_of_month", "2"))){
            case 1:
                thisJob.setDuration(PayPeriodDuration.ONE_WEEK);
                break;
            case 2:
                thisJob.setDuration(PayPeriodDuration.TWO_WEEKS);
                break;
            case 3:
                thisJob.setDuration(PayPeriodDuration.THREE_WEEKS);
                break;
            case 4:
                thisJob.setDuration(PayPeriodDuration.FOUR_WEEKS);
                break;
            case 5:
                thisJob.setDuration(PayPeriodDuration.FULL_MONTH);
                break;
            case 6:
                thisJob.setDuration(PayPeriodDuration.FIRST_FIFTEENTH);
                break;
            default:
                thisJob.setDuration(PayPeriodDuration.TWO_WEEKS);
                break;
        }
        chron.updateJob(thisJob);
        chron.close();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                setResult(RESULT_OK);
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
