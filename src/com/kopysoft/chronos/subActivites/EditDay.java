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

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.kopysoft.chronos.R;
import com.kopysoft.chronos.RowHelper.RowHelperEditDay;
import com.kopysoft.chronos.content.Chronos;
import com.kopysoft.chronos.content.StaticFunctions;
import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.enums.TimeFormat;
import com.kopysoft.chronos.singelton.ListenerObj;
import com.kopysoft.chronos.singelton.PreferenceSingelton;
import com.kopysoft.chronos.types.Day;
import com.kopysoft.chronos.types.Note;
import com.kopysoft.chronos.types.Punch;

public class EditDay extends ListActivity {

	private static final String TAG = Defines.TAG + " - ED";
	Chronos chronoSaver = null;
	RowHelperEditDay adapter = null;
	Note currentNote;
	private updateAdapter updateAdapt = null; 

	int date[] = new int[3];

	TimeFormat StringFormat = TimeFormat.HOUR_MIN_SEC;
	PreferenceSingelton prefs = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editday);
		if (Defines.DEBUG_PRINT) Log.d(TAG, "Creating EditDay");

		updateAdapt = new updateAdapter();
		updateAdapt.execute(getApplicationContext());

	}

	private long getTimeInSeconds(){
		if(adapter != null)
			return adapter.getTime();
		else 
			return 0;
	}

	protected void onActivityResult (int requestCode, int resultCode, Intent data){
		if( resultCode == Activity.RESULT_CANCELED){
			//do nothing

		} else if( resultCode == Activity.RESULT_OK){
			long id = data.getExtras().getLong("id");
			long time = data.getExtras().getLong("time");
			int actionReason = data.getExtras().getInt("actionReason");
			int getType = data.getExtras().getInt("type");
			Log.d(TAG, "ID: " + id);

			if(requestCode == 1){
				//long i_time, int i_type, long i_id, int i_actionReason
				Punch temp = new Punch(time, getType, -1, actionReason);
				temp.setNeedToUpdate(true);
				adapter.add(temp);
			} else if(requestCode == 2) {
				int position = data.getExtras().getInt("position");
				Punch temp = adapter.getItem(position);
				temp.setAction(actionReason);
				temp.setTime(time);
				temp.setType(getType);
				adapter.setByPos(position, temp);
				
			} else {
				Punch temp = adapter.getByID(id);
				temp.setAction(actionReason);
				temp.setTime(time);
				temp.setType(getType);
				adapter.setByID(id, temp);
			}

			updateTime(getTimeInSeconds());
			adapter.sort();
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		int menuItemIndex = item.getItemId();

		if(menuItemIndex == 0){//edit
			Intent intent = new Intent(getApplicationContext(), com.kopysoft.chronos.subActivites.EditTime.class);
			Punch temp = adapter.getItem(info.position);
			if(Defines.DEBUG_PRINT) Log.d(TAG, "getID = " + temp.getId());
			intent.putExtra("id", temp.getId());
			intent.putExtra("time", temp.getTime());
			intent.putExtra("type", temp.getType());
			intent.putExtra("actionReason", temp.getAction());
			if(temp.getId() == -1){
				intent.putExtra("position", info.position);
				startActivityForResult(intent, 2);
			} else {
				startActivityForResult(intent, 0);
			}
			temp = null;

		} else if( menuItemIndex == 1){	//remove
			if (Defines.DEBUG_PRINT) Log.d(TAG, "Position: " + info.id);
			adapter.remove(info.position);
			//adapter.notifyDataSetChanged();
		}
		updateTime(getTimeInSeconds());

		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		//AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
		menu.setHeaderTitle("Action");
		String[] menuItems = getResources().getStringArray(R.array.menu);
		for (int i = 0; i<menuItems.length; i++) {
			menu.add(Menu.NONE, i, i, menuItems[i]);
		}
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
		} else {
			newString = StaticFunctions.generateTimeString(hour, min, sec, StringFormat);
		}
		tv.setText(newString);
	}

	public void callBack(View v){
		switch (v.getId()){
		case R.id.OkButton:
			okAction();
			break;
		case R.id.CancelButton:
			cancelActions();
			break;
		case R.id.addPunch:
			insertAction();
			break;
		default:
			break;
		}
	}

	private void insertAction(){
		Intent intent = new Intent(getApplicationContext(), com.kopysoft.chronos.subActivites.EditTime.class);
		GregorianCalendar cal = new GregorianCalendar();
		cal.set(Calendar.YEAR, date[0]);
		cal.set(Calendar.MONTH, date[1]);
		cal.set(Calendar.DAY_OF_MONTH, date[2]);
		if(Defines.DEBUG_PRINT) Log.d(TAG, "time for insert: " + cal.getTimeInMillis());

		int type = 0;

		if(getTimeInSeconds() < 0)
		{
			//temp = new Punch(cal.getTimeInMillis(), Defines.OUT, -1, Defines.REGULAR_TIME);
			type = Defines.OUT;
		} else {
			//temp = new Punch(cal.getTimeInMillis(), Defines.IN, -1, Defines.REGULAR_TIME);
			type = Defines.IN;
		}

		intent.putExtra("id", (long)-1);
		intent.putExtra("time", cal.getTimeInMillis());
		intent.putExtra("type", type);
		intent.putExtra("actionReason", Defines.REGULAR_TIME);

		startActivityForResult(intent, 1);
	}

	private void okAction(){
		setResult(Activity.RESULT_OK);

		MultiAutoCompleteTextView textView = 
			(MultiAutoCompleteTextView) findViewById(R.id.multiAutoCompleteTextView1);
		String returnNote = textView.getText().toString();

		//chronoSaver.editNote(date, returnNote);
		adapter.commit();
		currentNote.setNote(returnNote);
		currentNote.update();
		ListenerObj.getInstance().fire();
		finish();

	}

	private void cancelActions(){
		try{
			adapter.cancelUpdates();
		} catch (Exception e){
			Log.e(TAG, "Error: Cancel Actions");
		}
		setResult(Activity.RESULT_CANCELED);
		finish();
	}

	private class updateAdapter extends AsyncTask<Context, Void, Object> {
		ProgressDialog dialog = null;
		protected void onPreExecute(){
			dialog = ProgressDialog.show(EditDay.this, "",
				"Generating. Please wait...");
			
			prefs = PreferenceSingelton.getInstance();
			prefs.updatePreferences(getApplicationContext());

			//Set string format
			StringFormat = prefs.getEditStringFormat();

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
			registerForContextMenu(getListView());

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
			Day punches = new Day(date, param[0]);

			registerForContextMenu(getListView());

			currentNote = new Note(date, param[0]);
			String returnString = currentNote.getNote(false);

			Object[] returnArray = {punches, returnString};

			return returnArray;
		}

	}

}
