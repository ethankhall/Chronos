package com.kopysoft.chronos.note;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.kopysoft.chronos.R;
import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.types.Note;

public class ViewNote extends Activity {
	
	private static final String TAG = Defines.TAG + " - EN";
	
	int date[] = new int[3];
	//Chronos chronoSaver = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_note);
		
		date[0] = getIntent().getExtras().getInt("year");
		date[1] = getIntent().getExtras().getInt("month");
		date[2] = getIntent().getExtras().getInt("day");

		//Set title
		GregorianCalendar cal = new GregorianCalendar(date[0], date[1], date[2]);
		String title = Defines.DAYS[( cal.get(Calendar.DAY_OF_WEEK) - 1 ) % 7] + " " +
		Defines.MONTHS[cal.get(Calendar.MONTH) % 12] + ", " + cal.get(Calendar.DAY_OF_MONTH);
		
		//set title
		setTitle("Note: " + title);
		
		Log.d(TAG, "Starting EditNote");
				
		Note newNote = new Note(date, getApplicationContext());
		String returnString = newNote.getNote(false);
		
		if (returnString == ""){
			finish();
		} else if (returnString.length() == 0) {
			finish();
		}
		
		TextView textView = 
			(TextView) findViewById(R.id.multiAutoCompleteTextView1);
		
		textView.setText(returnString);
	}
}
