/*******************************************************************************
 * Copyright (c) 2011 Ethan Hall
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

package com.kopysoft.chronos.types;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.kopysoft.chronos.enums.PayPeriodDuration;
import org.joda.time.DateTime;

@DatabaseTable(tableName = "jobs")
public class Job {

    public final static String JOB_FIELD_NAME = "job_id";

    @DatabaseField( columnName = JOB_FIELD_NAME, generatedId = true)
    int id = -1;
    @DatabaseField(defaultValue = "", canBeNull = false)
    String jobName;
    @DatabaseField(defaultValue = "7.25", canBeNull = false)
    float payRate;
    @DatabaseField
    boolean overTimeEnabled = true; //Should overtime be used for this job
    @DatabaseField(defaultValue = "40")
    float overTime;    //Start overtime at...
    @DatabaseField(defaultValue = "60")
    float doubleTime;  //Start double time at...
    @DatabaseField(dataType= DataType.SERIALIZABLE)
    DateTime startOfPayPeriod;
    @DatabaseField(dataType= DataType.SERIALIZABLE)
    PayPeriodDuration payPeriodDuration = PayPeriodDuration.TWO_WEEKS;



    /**
     * Normal constructor for the object Job. By default the overtime is calculated.
     *  To change the overtime setting use the {@link #setOvertimeEnabled(boolean)}.
     *  The default overtime value will be set to 40, and the double time value will be
     *  set to 60. To change this see their respective methods.
     * @param iJobName Name the user want to call this job.
     * @param iPayRate Amount the user gets paid.
     * @param iStartOfPayPeriod Start of the pay period.
     * @param ppd   Set the duration of the pay period.
     */
    public Job(String iJobName, float iPayRate,
               DateTime iStartOfPayPeriod, PayPeriodDuration ppd){

        jobName = iJobName;
        payRate = iPayRate;
        startOfPayPeriod = iStartOfPayPeriod;
        payPeriodDuration = ppd;
        doubleTime = 60;
        overTime = 40;

    }
    
    public void setOvertime(float in){
        overTime = in;
    }

    public float getOvertime(){
        return overTime;
    }

    public float getDoubleTime(){
        return doubleTime;
    }

    public boolean isOverTimeEnabled(){
        return overTimeEnabled;
    }

    public void setPayRate(float in){
        payRate = in;
    }

    /**
     * Gets the pay rate for this job.
     * @return float containing the pay rate
     */
    public float getPayRate(){
        return payRate;
    }
    /**
     * Constructor required by ormlite
     */
    public Job(){ }

    /**
     * Sets the double time threshold to value. This will allow the data to be
     *  changed in the database.
     *
     * @param value Value to set the overtime threshold to.
     */
    public void setDoubletimeThreshold(float value){
        doubleTime = value;
    }

    /**
     * Sets the overtime threshold to value. This will allow the data to be
     *  changed in the database.
     *
     * @param value Value to set the overtime threshold to.
     */
    public void setOvertimeThreshold(float value){
        overTime = value;
    }

    /**
     * Sets the overtime setting to the parameter "setting"
     *
     * @param setting TRUE to enable overtime calculations, otherwise false.
     */
    public void setOvertimeEnabled(boolean setting){
        overTimeEnabled = setting;
    }

    /**
     * Gets the ID of this job, this is linked to the database and should not change.
     *
     * @return integer of the ID located in the database
     */
    public int getID(){
        return id;
    }

    /**
     * Gets the start of the Pay Period (initial one)
     * @return DateMidnight of the start of the pay period
     */
    public DateTime getStartOfPayPeriod(){
        return startOfPayPeriod;
    }

    /**
     * Gets the duration of the Pay Period
     * @return PayPeriodDuration of the duration of the pay period
     */
    public PayPeriodDuration getDuration(){
        return payPeriodDuration;
    }


    /**
     * Sets the default job in the preferences to this job
     * @param context Application Context
     */
    public void setDefault(Context context){
        SharedPreferences app_preferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putInt("DefaultJobNumber", id);
        editor.commit();
    }

}
