package com.kopysoft.chronos.note;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.MultiAutoCompleteTextView;

import com.kopysoft.chronos.R;
import com.kopysoft.chronos.content.StaticFunctions;
import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.types.Note;

public class EditNote extends Activity {
	
	private static final String TAG = Defines.TAG + " - EN";
	
	int date[] = new int[3];
	//Chronos chronoSaver = null;
	Note newNote;
	
	public void onDestroy(){
		super.onDestroy();
		newNote.update();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_note);
		
		date[0] = getIntent().getExtras().getInt("year");
		date[1] = getIntent().getExtras().getInt("month");
		date[2] = getIntent().getExtras().getInt("day");

		//Set title
		GregorianCalendar cal = new GregorianCalendar(date[0], date[1], date[2]);
		String title = Defines.DAYS[( cal.get(Calendar.DAY_OF_WEEK) - 1 ) % 7] + " " +
		Defines.MONTHS[cal.get(Calendar.MONTH) % 12] + ", " + cal.get(Calendar.DAY_OF_MONTH);
		
		//set title
		setTitle("Note: " + title);
		
		StaticFunctions.printLog(Defines.ALL, TAG, "Starting EditNote");
		
		newNote = new Note(date, getApplicationContext());
		String returnString = newNote.getNote(false);
		
		MultiAutoCompleteTextView textView = 
			(MultiAutoCompleteTextView) findViewById(R.id.multiAutoCompleteTextView1);
		
		textView.setText(returnString);
	}

	public void callBack(View v){
		switch (v.getId()){
		case R.id.OkButton:
			okAction();
			finish();
			break;
		case R.id.CancelButton:
			finish();
			//cancelActions();
			break;
		default:
			break;
		}
		return;
	}
	
	private void okAction(){
		MultiAutoCompleteTextView textView = 
			(MultiAutoCompleteTextView) findViewById(R.id.multiAutoCompleteTextView1);
		String returnNote = textView.getText().toString();
		
		newNote.setNote(returnNote);
		//newNote.update();
	}
}
