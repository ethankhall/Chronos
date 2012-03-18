package com.kopysoft.chronos.old.note;

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

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.kopysoft.chronos.old.enums.Defines;
import com.kopysoft.chronos.old.types.Note;
import com.kopysoft.chronos.old.R;

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