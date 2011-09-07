package com.kopysoft.chronos.subActivites;

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

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import com.kopysoft.chronos.R;
import com.kopysoft.chronos.RowHelper.RowHelperEditDay;
import com.kopysoft.chronos.content.StaticFunctions;
import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.enums.TimeFormat;
import com.kopysoft.chronos.singelton.PreferenceSingleton;
import com.kopysoft.chronos.types.Day;
import com.kopysoft.chronos.types.Note;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class ViewDay extends ListActivity {

	private static final String TAG = Defines.TAG + " - VD";
	RowHelperEditDay adapter = null;
	Note currentNote;
	private updateAdapter updateAdapt = null;
    private int gJobNumber;

	int date[] = new int[3];

	TimeFormat StringFormat = TimeFormat.HOUR_MIN_SEC;
	PreferenceSingleton prefs = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_day);
		if (Defines.DEBUG_PRINT) Log.d(TAG, "Creating ViewDay");

		updateAdapt = new updateAdapter();
		updateAdapt.execute(getApplicationContext());
        gJobNumber = getIntent().getExtras().getInt("jobNumber");

	}

	private long getTimeInSeconds(){
		if(adapter != null)
			return adapter.getTime();
		else
			return 0;
	}

	private void updateTime(long time){
		int hour, min, sec;
		sec = (int)( time / 1000 ) % 60;
		min = (int)(time / 1000 / 60 ) % 60;
		hour = (int)(time / 1000 / 60 / 60 );

		if (Defines.DEBUG_PRINT) Log.d(TAG, "Time: " + time);
		TextView tv = (TextView) findViewById(R.id.EditDayTime);

		String newString;
		if ( time < 0 ){
			newString = "--:--:--";
		} else if(time >= Defines.SECONDS_IN_HOUR * 24){
			newString = "--:--:--";
		} else {
			newString = StaticFunctions.generateTimeString(hour, min, sec, StringFormat);
		}
		tv.setText(newString);
	}

	public void callBack(View v){
		switch (v.getId()){
		case R.id.CloseButton:
			okAction();
			break;
		default:
			break;
		}
	}

	private void okAction(){
		setResult(Activity.RESULT_OK);
		finish();
	}

	private class updateAdapter extends AsyncTask<Context, Void, Object> {
		ProgressDialog dialog = null;
		protected void onPreExecute(){
			dialog = ProgressDialog.show(ViewDay.this, "",
				"Generating. Please wait...");
			
			prefs = new PreferenceSingleton();

			//Set string format
			StringFormat = prefs.getPrefEditTime(getApplicationContext());

			date[0] = getIntent().getExtras().getInt("year");
			date[1] = getIntent().getExtras().getInt("month");
			date[2] = getIntent().getExtras().getInt("day");

			updateTime(0);

			//Set title
			GregorianCalendar cal = new GregorianCalendar(date[0], date[1], date[2]);
			String title = Defines.DAYS[( cal.get(Calendar.DAY_OF_WEEK) - 1 ) % 7] + " " +
			Defines.MONTHS[cal.get(Calendar.MONTH) % 12] + ", " + cal.get(Calendar.DAY_OF_MONTH);
			setTitle(title);
		}

		protected void onPostExecute (Object param){

			Object[] passedIn = (Object[]) param;
			Day newDay = (Day) passedIn[0];
			adapter = new RowHelperEditDay(getApplicationContext(), newDay);
			setListAdapter(adapter);
			updateTime(getTimeInSeconds());

			MultiAutoCompleteTextView textView = 
				(MultiAutoCompleteTextView) findViewById(R.id.multiAutoCompleteTextView1);

			String returnString = (String) passedIn[1];
			textView.setText(returnString);	
			try{
				dialog.dismiss();
				dialog = null;
			} catch (Exception e){
				
			}
		}

		@Override
		protected Object doInBackground(Context... param) {
			//chronoSaver.getPunchesForDay(date);
			Day punches = new Day(date, gJobNumber, param[0]);

			registerForContextMenu(getListView());

			currentNote = new Note(date, gJobNumber, param[0]);
			String returnString = currentNote.getNote(false);

            return new Object[]{punches, returnString};
		}

	}

}
