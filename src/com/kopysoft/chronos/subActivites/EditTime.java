package com.kopysoft.chronos.subActivites;

import java.util.GregorianCalendar;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.kopysoft.chronos.R;
import com.kopysoft.chronos.content.Chronos;
import com.kopysoft.chronos.enums.Defines;

public class EditTime extends Activity {

	private static final String TAG = Defines.TAG + " - ET";
	Cursor mCursor = null;
	Chronos chronoSaver = null;
	long id;
	GregorianCalendar cal = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edittime);
		if(Defines.DEBUG_PRINT) Log.d(TAG, "Creating EditTime");

		id = getIntent().getExtras().getLong("id");
		long time = getIntent().getExtras().getLong("time");
		int actionReason = getIntent().getExtras().getInt("actionReason");
		int getType = getIntent().getExtras().getInt("type");

		int[] timeSet = new int[2];
		cal = new GregorianCalendar();
		cal.setTimeInMillis(time);
		timeSet[0] = cal.get(GregorianCalendar.HOUR_OF_DAY);
		timeSet[1] = cal.get(GregorianCalendar.MINUTE);

		Log.d(TAG, "Input:" + time + "\tHour: " + timeSet[0] + "\tMin: " + timeSet[1]);

		TimePicker timePick = (TimePicker) findViewById(R.id.TimePicker01);
		timePick.setCurrentHour(timeSet[0]);
		timePick.setCurrentMinute(timeSet[1]);
		timePick.setIs24HourView(DateFormat.is24HourFormat(getApplicationContext()));
		
		ToggleButton toggle = (ToggleButton)findViewById(R.id.toggleButtonClock);
		if ( getType == Defines.IN ){
			toggle.setChecked(true);
		}else{
			toggle.setChecked(false);
		}

		Spinner spinner = (Spinner) findViewById(R.id.spinnerType);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.TimeTitles, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setSelection(actionReason);
	}
	
	public void callBack(View v){
		
		TimePicker timePick = (TimePicker) findViewById(R.id.TimePicker01);
		ToggleButton toggle = (ToggleButton)findViewById(R.id.toggleButtonClock);
		Spinner spinner = (Spinner) findViewById(R.id.spinnerType);
		
		int hour = timePick.getCurrentHour();
		int min = timePick.getCurrentMinute();
		cal.set(GregorianCalendar.HOUR_OF_DAY, hour);
		cal.set(GregorianCalendar.MINUTE, min);
		cal.set(GregorianCalendar.SECOND, 0);
		long time = cal.getTimeInMillis();
		//if(Defines.DEBUG_PRINT) Log.d(TAG, "Return Time:" + cal.getTimeInMillis() + "\tHour: " + hour + "\tMin: " + min);
		Log.d(TAG, "Return Time:" + cal.getTimeInMillis() + "\tHour: " + hour + "\tMin: " + min);
		
		int type = 0;
		int actionReason = 0;
		if (toggle.isChecked()) {
			type = ( Defines.IN );
		} else {
			type = ( Defines.OUT );
		}
		
		if(spinner.getSelectedItemPosition() != Spinner.INVALID_POSITION)
			actionReason = (spinner.getSelectedItemPosition());
		
		Log.d(TAG, "ID: " + id);
		
		if(v.getId() == R.id.OkButton){ 
			Intent returnIntent = new Intent();
			returnIntent.putExtra("id", id);
			returnIntent.putExtra("time", time);
			returnIntent.putExtra("type", type);
			returnIntent.putExtra("actionReason", actionReason);
			setResult(Activity.RESULT_OK, returnIntent);
			finish();
			
		} else if ( v.getId() == R.id.CancelButton){
			Intent returnIntent = new Intent();
			returnIntent.putExtra("id", id);
			setResult(Activity.RESULT_CANCELED, returnIntent);
			finish();
		}
	}

}
