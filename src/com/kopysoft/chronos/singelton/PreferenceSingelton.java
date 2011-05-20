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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.kopysoft.chronos.content.Chronos;
import com.kopysoft.chronos.content.StaticFunctions;
import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.enums.TimeFormat;
import com.kopysoft.chronos.enums.Verbosity;

public class PreferenceSingelton {

	private static PreferenceSingelton instance = null;
	
	SharedPreferences app_preferences = null;
	
	//Set the formats
	private TimeFormat ViewStringFormat = TimeFormat.HOUR_MIN_SEC;	
	private int weeks_in_pp = 0;
	private float overtimeRate = 1;
	private boolean overtimeEnable = false;
	private int overtimeSetting = 0;
	private double payRate = 8.75;
	//int[] dateGiven;
	private int[] startOfThisPP;
	private int reportLevel;
	private int automatic_lunch = 1;
	private boolean showPay;
	private boolean NotificationsEnabled;
	private String ppStart;
	private TimeFormat punchStringFormat = TimeFormat.HOUR_MIN_SEC;
	private TimeFormat editStringFormat = TimeFormat.HOUR_MIN_SEC;
	private final PropertyChangeSupport pcs = new PropertyChangeSupport( this );
	private String startLunch;
	private String endLunch;
	private long lastCal = 0;
	
	protected PreferenceSingelton(){
	}
	
	public static PreferenceSingelton getInstance(){
		if(instance == null){
			instance = new PreferenceSingelton();
		} 
		return instance;
	}
	
	public void updatePreferences(Context appContext){
		SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(appContext);
		
		setViewStringFormat(StaticFunctions.TimeFormater(app_preferences.getString("viewPrefTime", "1").trim()));
		setPunchStringFormat(StaticFunctions.TimeFormater(app_preferences.getString("punchPrefTime", "1").trim()));
		setEditStringFormat(StaticFunctions.TimeFormater(app_preferences.getString("editPrefTime", "1").trim()));

		//get weeks in pp
		try{
			setWeeksInPP(Integer.parseInt(app_preferences.getString("weeks_in_pp", "2").trim()));
		} catch (NumberFormatException e){
			setWeeksInPP(2);
		}

		//overtime calculation
		try{
			setOvertimeRate(Float.parseFloat(app_preferences.getString("overtime_rate", "1").trim()));
		} catch(NumberFormatException e){
			setOvertimeRate(1);
		}

		//get overtime enable
		setOvertimeEnable(app_preferences.getBoolean("overtime_enable", false));
		
		//get overtime setting
		try{
			setOvertimeSetting(Integer.parseInt(app_preferences.getString("8_or40_hours", "1").trim()));
		} catch (NumberFormatException e){
			setOvertimeSetting(1);
		}
		
		//get pay rate
		try{
			setPayRate(Double.valueOf(app_preferences.getString("pay_rate","7.25")));
		} catch(NumberFormatException e) {
			setPayRate(7.25); 
		}	
		
		setPpStart(app_preferences.getString("date", Defines.INITIAL_START_DATE_OF_PP));
		int[] dateGiven = Chronos.getDate(ppStart);
		setStartOfThisPP(Chronos.getPP(dateGiven, weeks_in_pp));
		
		try{
			String reportLevelString = app_preferences.getString("reportLevel", "2");
			setReportLevel(Integer.parseInt(reportLevelString));
		} catch(NumberFormatException e){
			setReportLevel(2);
		}
		
		try{
			String automatic_lunch = app_preferences.getString("automatic_lunch", "1");
			setAutomatic_lunch(Integer.parseInt(automatic_lunch));
		} catch(NumberFormatException e){
			setAutomatic_lunch(1);
		}
		
		//get show pay
		setShowPay(app_preferences.getBoolean("showPay", true));
		
		setStartLunch(app_preferences.getString("clockout_lunch", "11:30"));
		setEndLunch(app_preferences.getString("clockin_lunch", "12:30"));
		
		setNotificationsEnabled(app_preferences.getBoolean("NotificationsEnabled", true));
		this.pcs.firePropertyChange( "prefs", "old", "new" );
	}
	
	public void addPropertyChangeListener( PropertyChangeListener listener )
    {
        this.pcs.addPropertyChangeListener( listener );
    }

    public void removePropertyChangeListener( PropertyChangeListener listener )
    {
        this.pcs.removePropertyChangeListener( listener );
    }
	
	public void setEditStringFormat(TimeFormat timeFormater) {
		this.editStringFormat = timeFormater;
	}
	
	public TimeFormat getEditStringFormat() {
		return this.editStringFormat;
	}

	public void setPunchStringFormat(TimeFormat timeFormater) {
		this.punchStringFormat = timeFormater;
	}
	
	public TimeFormat getPunchStringFormat() {
		return this.punchStringFormat;
	}

	public String getPpStart() {
		return ppStart;
	}

	public void setPpStart(String ppStart) {
		this.ppStart = ppStart;
	}

	public int getWeeksInPP(){
		return weeks_in_pp;
	}
	
	public void setWeeksInPP( int setValue){
		this.weeks_in_pp = setValue;
	}


	public TimeFormat getViewStringFormat() {
		return ViewStringFormat;
	}
	
	public void setViewStringFormat(TimeFormat setvalue) {
		this.ViewStringFormat = setvalue;
	}

	public void setOvertimeRate(float overtimeRate) {
		this.overtimeRate = overtimeRate;
	}

	public float getOvertimeRate() {
		return overtimeRate;
	}

	public void setOvertimeEnable(boolean overtimeEnable) {
		this.overtimeEnable = overtimeEnable;
	}

	public boolean isOvertimeEnable() {
		return overtimeEnable;
	}

	public void setStartOfThisPP(int[] startOfThisPP) {
		this.startOfThisPP = startOfThisPP;
	}

	public int[] getStartOfThisPP() {
		return startOfThisPP;
	}

	public void setPayRate(double payRate) {
		this.payRate = payRate;
	}

	public double getPayRate() {
		return payRate;
	}

	public void setOvertimeSetting(int overtimeSetting) {
		this.overtimeSetting = overtimeSetting;
	}

	public int getOvertimeSetting() {
		return overtimeSetting;
	}

	public void setReportLevel(int reportLevel) {
		this.reportLevel = reportLevel;
	}

	public int getReportLevel() {
		return reportLevel;
	}
	
	public Verbosity getReportLevelVerbosity(){
		if (reportLevel == 1){
			return Verbosity.EVERY_PUNCH;
		} else {
			return Verbosity.ONLY_DAY;
		}
	}

	public void setShowPay(boolean showPay) {
		this.showPay = showPay;
	}

	public boolean isShowPay() {
		return showPay;
	}

	public void setNotificationsEnabled(boolean notificationsEnabled) {
		NotificationsEnabled = notificationsEnabled;
	}

	public boolean isNotificationsEnabled() {
		return NotificationsEnabled;
	}

	public void setAutomatic_lunch(int automatic_lunch) {
		this.automatic_lunch = automatic_lunch;
	}

	public int getAutomatic_lunch() {
		return automatic_lunch;
	}

	public void setStartLunch(String startLunch) {
		this.startLunch = startLunch;
	}

	public String getStartLunch() {
		return startLunch;
	}

	public void setEndLunch(String endLunch) {
		this.endLunch = endLunch;
	}

	public String getEndLunch() {
		return endLunch;
	}

	public void setLastCal(long lastCal) {
		this.lastCal = lastCal;
	}

	public long getLastCal() {
		return lastCal;
	}
}
