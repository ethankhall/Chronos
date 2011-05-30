package com.kopysoft.chronos.test;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.test.ActivityInstrumentationTestCase2;

import com.kopysoft.chronos.ClockInAndOut;
import com.kopysoft.chronos.content.Chronos;
import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.types.Day;
import com.kopysoft.chronos.types.Punch;

public class ClockInAndOutTest extends ActivityInstrumentationTestCase2<ClockInAndOut> {
	
	private ClockInAndOut mActivity; 

	public ClockInAndOutTest() {
		super("com.kopysoft.chronos", ClockInAndOut.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mActivity = getActivity();
		Chronos chrono = new Chronos(mActivity.getApplicationContext());
		chrono.dropAll();
		GregorianCalendar cal = new GregorianCalendar();

		int[] dateGiven = new int[3];
		dateGiven[0] = cal.get(Calendar.YEAR);
		dateGiven[1] = cal.get(Calendar.MONTH);
		dateGiven[2] = cal.get(Calendar.DAY_OF_MONTH);
		Day today = new Day(dateGiven, mActivity.getApplicationContext());
		
		//Insert new punch
		Punch newPunch = new Punch();
		newPunch.setAction(Defines.REGULAR_TIME);
		newPunch.setTime(cal.getTimeInMillis() - 120000);
		newPunch.setType(Defines.IN);
		today.add(newPunch);
		today.updateDay();
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testUI() {
		// Check the UI for any Null connections
		assertNotNull("dayTime is null",
				mActivity.findViewById(com.kopysoft.chronos.R.id.dayTime));
		assertNotNull("money_today is null",
				mActivity.findViewById(com.kopysoft.chronos.R.id.money_today));
		assertNotNull("money_today_text is null",
				mActivity.findViewById(com.kopysoft.chronos.R.id.money_today_text));
		assertNotNull("clock_in_and_out_button is null",
				mActivity.findViewById(com.kopysoft.chronos.R.id.clock_in_and_out_button));
	}

}
