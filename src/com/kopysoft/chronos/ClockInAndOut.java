package com.kopysoft.chronos;

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
import android.app.AlarmManager;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import com.kopysoft.chronos.RowHelper.RowHelperToday;
import com.kopysoft.chronos.content.StaticFunctions;
import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.enums.TimeFormat;
import com.kopysoft.chronos.service.NotificationBroadcast;
import com.kopysoft.chronos.singelton.ListenerObj;
import com.kopysoft.chronos.singelton.PreferenceSingleton;
import com.kopysoft.chronos.subActivites.selector.EditTime;
import com.kopysoft.chronos.types.Day;
import com.kopysoft.chronos.types.Punch;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ClockInAndOut extends ListActivity{ 

	private static final String TAG = Defines.TAG + " - CIAO";
	private double PAY_RATE = 8.75;
	private boolean forceUpdate = false;

	//private long time= 0;	//Time in seconds

	private RowHelperToday adapter = null;
	private Handler mHandler = new Handler();
	private PreferenceSingleton prefs = null;
    private int gJobNumber;

	private TimeFormat StringFormat = TimeFormat.HOUR_MIN_SEC;

	@Override
	/**
	 * onPause method: Called when another activity comes infront of the this one
	 */
	public void onPause(){
		super.onPause();
		if ( Defines.DEBUG_PRINT ) Log.d(TAG, "Pause");
		mHandler.removeCallbacks(mUpdateTimeTask);	//Remove the callbacks
	}

	@Override
	/**
	 * onResume method: Called when this activity becomes the displayed activity. This activity
	 * 	updates the the time, the data and updates the notification.
	 */
	public void onResume(){
		super.onResume();
		if ( Defines.DEBUG_PRINT ) Log.d(TAG, "onResume");

		//Updates the pay rate and shows or hides the pay info
		updatePayRate();

		long time = adapter.getTimeWithBreaks();

		if (time < 0){
			mHandler.postDelayed(mUpdateTimeTask, 10);
		}

		//updates all the adapters
		updateAdapter(false);

		//Set string format
		if ( Defines.DEBUG_PRINT ) Log.d(TAG, "Time: " + time);
		if ( Defines.DEBUG_PRINT ) Log.d(TAG, "Pay Today: " + generateDollarAmount(time, PAY_RATE));
		if ( Defines.DEBUG_PRINT ) Log.d(TAG, "Current Time: " + generateTimeString(time));


		updateData();
		Intent runIntent = new Intent().setClass(getApplicationContext(), 
				com.kopysoft.chronos.service.NotificationBroadcast.class);
		runIntent = NotificationBroadcast.runUpdate(runIntent, time);
		getApplicationContext().sendBroadcast(runIntent);
	}

	@Override
	/**
	 * onCreate method: Called when the activity is created. Connects to the PreferenceSingleton and
	 * 	the Service. It gets the adapter and sets it all up.
	 * @param savedInstanceState
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if ( Defines.DEBUG_PRINT ) Log.d(TAG, "onCreate");
		setContentView(R.layout.clockinandout);

		prefs = new PreferenceSingleton();

        gJobNumber = getIntent().getExtras().getInt("jobNumber");

		GregorianCalendar cal = new GregorianCalendar();

		int[] dateGiven = new int[3];
		dateGiven[0] = cal.get(Calendar.YEAR);
		dateGiven[1] = cal.get(Calendar.MONTH);
		dateGiven[2] = cal.get(Calendar.DAY_OF_MONTH);
		Day today = new Day(dateGiven, gJobNumber, getApplicationContext());

		adapter = new RowHelperToday(getApplicationContext(), today);
		setListAdapter(adapter);

		long time = adapter.getTimeWithBreaks();

		//Set string format
		StringFormat = prefs.getPrefPunchTime(getApplicationContext());

		//Updates the pay rate and shows or hides the pay info
		updatePayRate();

		if ( Defines.DEBUG_PRINT ) Log.d(TAG, "Pay Today: " + generateDollarAmount(time, PAY_RATE));
		if ( Defines.DEBUG_PRINT ) Log.d(TAG, "Current Time: " + generateTimeString(time));
		updateData();

		ListenerObj.getInstance().addPropertyChangeListener(new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent event) {
				StringFormat = prefs.getPrefPunchTime(getApplicationContext());
				updateData();
				updatePayRate();

				updateAdapter(true);

				long time = adapter.getTimeWithBreaks();

				if (time < 0){
					mHandler.postDelayed(mUpdateTimeTask, 10);
				}

				//updates all the adapters

				updateData();
				Intent runIntent = new Intent().setClass(getApplicationContext(), 
						com.kopysoft.chronos.service.NotificationBroadcast.class);
				runIntent = NotificationBroadcast.runUpdate(runIntent, time);
				getApplicationContext().sendBroadcast(runIntent);
			}
		});

		ListenerObj.getInstance().addMidnightListener(new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent event) {
				GregorianCalendar cal = new GregorianCalendar();

				int[] dateGiven = new int[3];
				dateGiven[0] = cal.get(Calendar.YEAR);
				dateGiven[1] = cal.get(Calendar.MONTH);
				dateGiven[2] = cal.get(Calendar.DAY_OF_MONTH);

				Day today = new Day(dateGiven, gJobNumber, getApplicationContext());
				adapter = new RowHelperToday(getApplicationContext(), today);
				setListAdapter(adapter);

				StringFormat = prefs.getPrefPunchTime(getApplicationContext());	//Set string format
				updatePayRate(); //Updates the pay rate and shows or hides the pay info

				updateData();
			}
		});

        ListenerObj.getInstance().addJobChangeListener(new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent event) {
				gJobNumber = (Integer)event.getNewValue();
			}
		});

		registerForContextMenu(getListView());
	}

	//----------------------------------------------------
	//			Clikcing stuff
	//----------------------------------------------------
	
	/**
	 * 	onActivityResult: when the popup selector calls "OK" or "Cancel"
	 * 
	 * @param requestCode I don't use it.
	 * @param resultCode How the activity was closed, either Activity.RESULT_CANCELED or Activity.RESULT_OK
	 * @param data The Intent that was sent back from the selector.
	 * 
	 * Can either call add a new punch ( if the id pulled from the Intent is -1) or edit the punch based on the id and the info pulled from the (Intent)data.
	 */
	protected void onActivityResult (int requestCode, int resultCode, Intent data){
		if( resultCode == Activity.RESULT_CANCELED){
			//do nothing

		} else if( resultCode == Activity.RESULT_OK){
			long id = data.getExtras().getLong("id");
			long time = data.getExtras().getLong("time");
			int actionReason = data.getExtras().getInt("actionReason");


			if(id == -1){
				//long i_time, int i_type, long i_id, int i_actionReason
				Punch temp = new Punch(time, Defines.IN, id, gJobNumber, actionReason);
				adapter.add(temp);
			} else {
				Punch temp = adapter.getByID(id);
				temp.setAction(actionReason);
				temp.setTime(time);
				adapter.setByID(id, temp);
			}
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		int menuItemIndex = item.getItemId();

		if(menuItemIndex == 0){//edit
			Intent intent = new Intent(getApplicationContext(), EditTime.class);
			Punch temp = adapter.getItem(info.position);
			if(Defines.DEBUG_PRINT) Log.d(TAG, "getID = " + temp.getId());
			intent.putExtra("id", temp.getId());
			intent.putExtra("time", temp.getTime());
			intent.putExtra("type", temp.getType());
			intent.putExtra("actionReason", temp.getAction());
            intent.putExtra("jobNumber", gJobNumber);
			startActivityForResult(intent, 0);

		} else if( menuItemIndex == 1){	//remove
			if (Defines.DEBUG_PRINT) Log.d(TAG, "Position: " + info.id);
			adapter.remove(info.position);
			//adapter.notifyDataSetChanged();
		}

		ListenerObj.getInstance().fire();

		return true;
	}

	//----------------------------------------------------
	//			END Clikcing stuff
	//----------------------------------------------------

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

	private void updatePayRate(){
		//update pay rate
		PAY_RATE = prefs.getPayRate(getApplicationContext());

		//Hide or show the time amount
		//boolean showPay = app_preferences.getBoolean("showPay", true);
		boolean showPay = prefs.getShowPay(getApplicationContext());
		TextView payTitle = (TextView)findViewById(R.id.money_today_text);
		TextView payValue = (TextView)findViewById(R.id.money_today);
		if(!showPay){
			payTitle.setVisibility(View.GONE);
			payValue.setVisibility(View.GONE);
		} else {
			payTitle.setVisibility(View.VISIBLE);
			payValue.setVisibility(View.VISIBLE);
		}
	}

	private void updateAdapter(boolean forceUpdate){
		//if (mPunchList.isEmpty() == true )
		adapter.updateDay(forceUpdate);
	}

	private void updateData(){
		//Log.d(TAG, "Time with breaks: " + adapter.getTimeWithBreaks());
		updateData(adapter.getTimeWithBreaks());
	}

	private void updateData(long input_time){

		//input_time /= Defines.MS_TO_SECOND;
		if( Math.abs(input_time) > 60 * 60 * 24){
			setTimeString("--:--:--");
			setValueString("--:--:--");
		}
		if( input_time >= 0){
			//Log.d(TAG, "in if");
			//Log.d(TAG, "time string: " + generateTimeString(input_time, StringFormat));
			setTimeString(generateTimeString(input_time / Defines.MS_TO_SECOND, StringFormat));
			setValueString(generateDollarAmount(input_time, PAY_RATE));
		} else {
			//Log.d(TAG, "not in if");
			setTimeString("--:--:--");
			setValueString("--:--:--");
		}
		setButtonText();
	}

	public void Callback(View v){
		int type;
		long i_time;
		GregorianCalendar cal = new GregorianCalendar();
		long temp = cal.getTimeInMillis();
		mHandler.removeCallbacks(mUpdateTimeTask);
		//Log.d(TAG, "Callback Happened");

		i_time = adapter.getTimeWithBreaks();
		if( i_time < 0 ){
			i_time = i_time + temp;
			type = Defines.OUT;
		} else {
			i_time = i_time - temp;
			type = Defines.IN;
			mHandler.postDelayed(mUpdateTimeTask, 1000);
		}

		long timeAdd = cal.getTimeInMillis();

		Punch tempPunch = new Punch(timeAdd, type, Defines.NEW_PUNCH, gJobNumber, Defines.REGULAR_TIME);
		tempPunch.commitToDb(getApplicationContext());
		adapter.add(tempPunch);
		adapter.updateDay(false);
		setButtonText();

		Intent runIntent = new Intent().setClass(getApplicationContext(), 
				com.kopysoft.chronos.service.NotificationBroadcast.class);
		runIntent = NotificationBroadcast.runUpdate(runIntent, i_time);
		getApplicationContext().sendBroadcast(runIntent);

	}

	private void setButtonText(){
		Button button = (Button) findViewById(R.id.clock_in_and_out_button);
		long[] times = adapter.getTime();
		boolean clockedIn = false;
		if(times[Defines.REGULAR_TIME] < 0)
			clockedIn = true;
		if(times[Defines.LUNCH_TIME] < 0)
			clockedIn = false;
		if(times[Defines.BREAK_TIME] < 0)
			clockedIn = false;
		if(times[Defines.HOLIDAY_TIME] < 0)
			clockedIn = true;

		if(!clockedIn){
			button.setText("Clock In");
		} else {
			button.setText("Clock Out");
		}
	}

	private void setValueString(String text){
		TextView time = (TextView) findViewById(R.id.money_today);
		time.setText(text);
	}

	private void setTimeString(String text){
		//Log.d(TAG, "input text: " + text);
		TextView time = (TextView) findViewById(R.id.dayTime);
		time.setText(text);
	}

	private String generateDollarAmount(long i_time, double payRate){
		String returnValue;
		//Convert payRate to dollar amount from $/h to $/s
		double payPerSec = payRate / 60 / 60;
		long correctTime = StaticFunctions.correctForClockIn(i_time);
		double temp = (double)correctTime * payPerSec;
		returnValue = String.format("$ %02.2f",temp);
		return returnValue;
	}

	/*
	 * Creates a string in the form H:MM:SS when called
	 */
	private String generateTimeString(long time){
		return generateTimeString(time / Defines.MS_TO_SECOND, TimeFormat.HOUR_MIN_SEC);
	}

	private String generateTimeString(long i_time, TimeFormat type){
		return StaticFunctions.generateTimeString(i_time, type, false);
	}


	private Runnable mUpdateTimeTask = new Runnable(){
		public void run(){
			mHandler.removeCallbacks(mUpdateTimeTask);

			if (forceUpdate){
				forceUpdate = false;

				if (adapter.needToUpdateClock()){
					mHandler.postDelayed(mUpdateTimeTask, 10);
				}

				//updates all the adapters
				updateAdapter(true);
			}
			//Log.d(TAG, "Update: " + adapter.getTimeWithBreaks());
			GregorianCalendar cal = new GregorianCalendar();
			//Log.d(TAG, "Difference: " + (adapter.getTimeWithBreaks() + cal.getTimeInMillis()));

			if(adapter.getTimeWithBreaks() < 0){
				updateData(adapter.getTimeWithBreaks() + cal.getTimeInMillis());
				StaticFunctions.setUpAlarm(getApplicationContext(), cal.getTimeInMillis() + 1000,
						(AlarmManager) getSystemService(ALARM_SERVICE));
			}
			else{
				updateData(adapter.getTimeWithBreaks());
				StaticFunctions.removeAlarm(getApplicationContext(), 
						(AlarmManager) getSystemService(ALARM_SERVICE));
			}

			mHandler.postDelayed(mUpdateTimeTask, 1000);
		}
	};

	

}
