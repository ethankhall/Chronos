package com.kopysoft.chronos.subActivites;

import java.util.GregorianCalendar;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.kopysoft.chronos.R;
import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.singelton.ListenerObj;
import com.kopysoft.chronos.types.Punch;

public class AddBreak extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addbreak);
		TimePicker startTime = (TimePicker) findViewById(R.id.startBreak);
		GregorianCalendar cal = new GregorianCalendar();

		startTime.setIs24HourView(DateFormat.is24HourFormat(getApplicationContext()));
		startTime.setCurrentHour(cal.get(GregorianCalendar.HOUR_OF_DAY));
		startTime.setCurrentMinute(cal.get(GregorianCalendar.MINUTE));

		Spinner spinner = (Spinner) findViewById(R.id.spinnerType);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.TimeTitles, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setSelection(0);
	}

	public void callBack(View v){

		TimePicker startTime = (TimePicker) findViewById(R.id.startBreak);
		Spinner spinner = (Spinner) findViewById(R.id.spinnerType);
		Spinner spinnerBreak = (Spinner) findViewById(R.id.spinnerLength);
		int actionReason = 0;
		int length = 0;
		int addto = 0;
		
		GregorianCalendar startBreak = new GregorianCalendar();
		GregorianCalendar endBreak = new GregorianCalendar();
		
		startBreak.set(GregorianCalendar.HOUR_OF_DAY, startTime.getCurrentHour());
		startBreak.set(GregorianCalendar.MINUTE, startTime.getCurrentMinute());
		
		endBreak.setTimeInMillis(startBreak.getTimeInMillis());
		
		if(spinner.getSelectedItemPosition() != Spinner.INVALID_POSITION)
			actionReason = (spinner.getSelectedItemPosition());
		
		if(spinnerBreak.getSelectedItemPosition() != Spinner.INVALID_POSITION)
			length = (spinnerBreak.getSelectedItemPosition());
		
		Log.d(Defines.TAG + " - AB", "Length: " + length);
		
		switch(length){
		case 0:
			addto = 5;
			break;
		case 1:
			addto = 10;
			break;
		case 2:
			addto = 15;
			break;
		case 3:
			addto = 30;
			break;
		case 4:
			addto = 60;
			break;
		case 5:
			addto = 120;
			break;
		default:
			addto = 0;
			break;
		}
		
		endBreak.add(GregorianCalendar.MINUTE, addto);

		if(v.getId() == R.id.OkButton){ 
			//long i_time, int i_type, long i_id, int i_actionReason
			Punch startBreakPunch = new Punch(startBreak.getTimeInMillis(), Defines.IN,
					-1, actionReason);
			startBreakPunch.setNeedToUpdate(true);
			startBreakPunch.commitToDb(getApplicationContext());

			
			Punch endBreakPunch = new Punch(endBreak.getTimeInMillis(), Defines.OUT,
					-1, actionReason);
			endBreakPunch.setNeedToUpdate(true);
			endBreakPunch.commitToDb(getApplicationContext());
			
			ListenerObj.getInstance().fire();
			finish();

		} else if ( v.getId() == R.id.CancelButton){

			finish();
		}
	}

}
