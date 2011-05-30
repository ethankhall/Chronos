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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.kopysoft.chronos.RowHelper.RowHelperPayPeriod;
import com.kopysoft.chronos.content.Chronos;
import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.enums.TimeFormat;
import com.kopysoft.chronos.singelton.PreferenceSingelton;
import com.kopysoft.chronos.types.Day;
import com.kopysoft.chronos.types.PayPeriod;

public class EditPayPeriodOthers extends ListActivity {

	private static final String TAG = Defines.TAG + " - WV";
	Chronos chronoSaver = null;
	RowHelperPayPeriod adapter = null;
	PreferenceSingelton prefs = null;
	
	int weeks_in_pp = 0;
	
	ArrayList<Integer> daysToUpdate = new ArrayList<Integer>();
	TimeFormat StringFormat = TimeFormat.HOUR_MIN_SEC;
	
	int[] date = new int[3];

	@Override
	public void onResume(){
		super.onResume();
		
		chronoSaver = new Chronos(getApplicationContext());	//Connect to content provider
		//StringFormat = Chronos.TimeFormater(app_preferences.getString("viewPrefTime", "1"));
		StringFormat = prefs.getPrefEditTime(getApplicationContext());
		
		GregorianCalendar cal = new GregorianCalendar(date[0], 
				date[1], date[2]);
		cal.add(GregorianCalendar.DAY_OF_YEAR, 7 * weeks_in_pp);
		int[] endOfPP = {
			cal.get(GregorianCalendar.YEAR),
			cal.get(GregorianCalendar.MONTH),
			cal.get(GregorianCalendar.DAY_OF_MONTH)
		};
		
		PayPeriod thisPP = new PayPeriod(date, endOfPP, getApplicationContext());
		for(int i = 0; i < thisPP.size(); i++){
			adapter.updateDay(i, thisPP.get(i));
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_pp_others);

		chronoSaver = new Chronos(getApplicationContext());	//Connect to content provider
		
		prefs = new PreferenceSingelton();

		//String string_weeks_in_pp = app_preferences.getString("weeks_in_pp", "2");
		//weeks_in_pp = Integer.parseInt(string_weeks_in_pp);
		weeks_in_pp = prefs.getWeeksInPP(getApplicationContext());
		
		date[0] = getIntent().getExtras().getInt("year");
		date[1] = getIntent().getExtras().getInt("month");
		date[2] = getIntent().getExtras().getInt("day");
		
		if(Defines.DEBUG_PRINT) Log.d(TAG, "Input Date: " + 
				String.format("(%d/%d/%d)", date[0], date[1], date[2]));

		GregorianCalendar cal = new GregorianCalendar(date[0], date[1], date[2]);
		cal.add(GregorianCalendar.DAY_OF_YEAR, weeks_in_pp * 7);
		int[] endOfThisPP = {cal.get(GregorianCalendar.YEAR), cal.get(GregorianCalendar.MONTH),
				cal.get(GregorianCalendar.DAY_OF_MONTH)};
		
		PayPeriod thisPP = new PayPeriod(date, endOfThisPP, getApplicationContext());
		
		//StringFormat = Chronos.TimeFormater(app_preferences.getString("editPrefTime", "1"));
		StringFormat = prefs.getPrefEditTime(getApplicationContext());

		//Set up the list
		adapter = new RowHelperPayPeriod(getApplicationContext(), thisPP, StringFormat);
		setListAdapter(adapter);

		getListView().setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> a, View v, int position, long id) {
				Day temp = adapter.getItem(position);
				int[] day = temp.getDay();

				Intent intent = new Intent(getApplicationContext(), com.kopysoft.chronos.subActivites.EditDay.class);
				intent.putExtra("year", day[0]);
				intent.putExtra("month", day[1]);
				intent.putExtra("day", day[2]);

				startActivityForResult(intent,0);

			}
		});
		
	}

	protected void onActivityResult (int requestCode, int resultCode, Intent data){
		if ( Defines.DEBUG_PRINT ) Log.d(TAG,"Activity Update");
		if( resultCode == Activity.RESULT_CANCELED){
			if ( Defines.DEBUG_PRINT ) Log.d(TAG,"Activity Update - Canceled");
		} else if( resultCode == Activity.RESULT_OK){
			if ( Defines.DEBUG_PRINT ) Log.d(TAG,"Activity Update - Ok");
		}
	}

	@Override
	public void onPause(){
		super.onPause();
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	public void callback(View v){
		switch(v.getId()){
		case R.id.next:
			nextButton();
			break;
		case R.id.prev:
			prevButton();
			break;
		default:
			break;
		}
	}
	
	private void nextButton(){
		GregorianCalendar cal = new GregorianCalendar(date[0], date[1], date[2]);
		GregorianCalendar newCal = new GregorianCalendar();
		cal.add(Calendar.DAY_OF_YEAR, weeks_in_pp * 7);
		if(newCal.compareTo(cal) == -1){
			return;	//Prevent Time Paradox
		}
		
		date[0] = cal.get(Calendar.YEAR);
		date[1] = cal.get(Calendar.MONTH);
		date[2] = cal.get(Calendar.DAY_OF_MONTH);
		
		cal.add(GregorianCalendar.DAY_OF_YEAR, weeks_in_pp * 7);
		int[] endOfThisPP = {cal.get(GregorianCalendar.YEAR), cal.get(GregorianCalendar.MONTH),
				cal.get(GregorianCalendar.DAY_OF_MONTH)};
		
		PayPeriod thisPP = new PayPeriod(date, endOfThisPP, getApplicationContext());
		for(int i = 0; i < thisPP.size(); i++){
			adapter.updateDay(i, thisPP.get(i));
		}
		adapter.notifyDataSetChanged();
	}
	
	private void prevButton(){
		GregorianCalendar cal = new GregorianCalendar(date[0], date[1], date[2]);
		cal.add(Calendar.DAY_OF_YEAR, weeks_in_pp * -7);
		date[0] = cal.get(Calendar.YEAR);
		date[1] = cal.get(Calendar.MONTH);
		date[2] = cal.get(Calendar.DAY_OF_MONTH);
		
		cal.add(GregorianCalendar.DAY_OF_YEAR, weeks_in_pp * 7);
		int[] endOfThisPP = {cal.get(GregorianCalendar.YEAR), cal.get(GregorianCalendar.MONTH),
				cal.get(GregorianCalendar.DAY_OF_MONTH)};
		
		PayPeriod thisPP = new PayPeriod(date, endOfThisPP, getApplicationContext());
		for(int i = 0; i < thisPP.size(); i++){
			adapter.updateDay(i, thisPP.get(i));
		}
		adapter.notifyDataSetChanged();		
	}


}
