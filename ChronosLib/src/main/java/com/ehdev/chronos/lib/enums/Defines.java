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

package com.ehdev.chronos.lib.enums;

public class Defines {
	
	public static final int REPEATING_ALARM = 1;
	public static final int MIDNIGHT_ALARM = 2;

    public static final int NEW_PUNCH = -1;
	
	public static final int IN 	= 0;
	public static final int OUT = 1;
	public static final int MS_TO_SECOND = 1000;
	public static final boolean DEBUG_PRINT = false; 
	
	public static final String TAG = "Chronos";
	public static final String[] DAYS = { "Sunday", "Monday", "Tuesday", "Wednesday",
		"Thursday", "Friday", "Saturday" };
	public static final String[] DAYSABBV = { "Sun", "Mon", "Tue", "Wed",
		"Thu", "Fri", "Sat" };
	public static final String[] MONTHS = { "Jan", "Feb", "March", "April", "May", "June", "July", 
		"Aug", "Sept", "Oct", "Nov", "Dec"
	};
	public static final int CLOSE_NOTIFICATION = 0;
	public static final int CREATE_NOTIFICATION = 1;
	public static final int UPDATE_NOTIFICATION = 2;
	
	public static final String INITIAL_START_DATE_OF_PP = "2011.1.17";
	
	public static final String FILENAME = "ChronosPrefs";
	
	public static final int OVERTIME_8HOUR = 1;
	public static final int OVERTIME_40HOUR = 2;
	
	public static final int SECONDS_IN_HOUR = 3600 * MS_TO_SECOND;
	public static final int NOTIFICATION_LEVEL = 5;
	//public static final int NOTIFICATION_LEVEL = 1;
	
	public static final int ERROR = 4;
	public static final int IMPORTANT = 3;
	public static final int MOST = 2;
	public static final int ALL = 1;

	public static final int REGULAR_TIME 	= 0;
	public static final int LUNCH_TIME 		= 1;
	public static final int BREAK_TIME 		= 2;
	public static final int TRAVEL_TIME 		= 3;
	public static final int ADMINISTRATIVE_TIME = 4;
	public static final int SICK_TIME 		= 5;
	public static final int PERSONAL_TIME 	= 6;
	public static final int OTHER_TIME 		= 7;
	public static final int HOLIDAY_TIME 	= 8;
	public static final int MAX_CLOCK_OPT 	= 9;
	
}
