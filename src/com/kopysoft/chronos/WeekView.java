package com.kopysoft.chronos;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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

public class WeekView extends ListActivity {

	private static final String TAG = Defines.TAG + " - WV";
	private RowHelperPayPeriod adapter = null;
	private PreferenceSingelton prefs = null;
	//SharedPreferences app_preferences = null;
	private updateAdapter updateAdapt = null; 

	private int weeks_in_pp = 0;
	
	private TimeFormat StringFormat = TimeFormat.HOUR_MIN_SEC;

	//private Handler mHandler = new Handler();

	@Override
	public void onResume(){
		super.onResume();

		if ( Defines.DEBUG_PRINT ) Log.d(TAG, "Resume");
				
		//weeks_in_pp = prefs.getWeeksInPP();
		//startOfThisPP = prefs.getStartOfThisPP();
		StringFormat = prefs.getEditStringFormat();
		
		adapter.setFormat(StringFormat);		
		updateAdapt = new updateAdapter();
		updateAdapt.execute(getApplicationContext());
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.week_view);
		
		prefs = PreferenceSingelton.getInstance();
		ProgressDialog dialog = ProgressDialog.show(WeekView.this, "",
			"Generating. Please wait...");
		
		StringFormat = prefs.getEditStringFormat();
		weeks_in_pp = prefs.getWeeksInPP();
		int[] startOfThisPP = prefs.getStartOfThisPP();
		GregorianCalendar cal = new GregorianCalendar(startOfThisPP[0], startOfThisPP[1], startOfThisPP[2]);
		cal.add(GregorianCalendar.DAY_OF_YEAR, weeks_in_pp * 7);
		int[] endOfThisPP = {cal.get(GregorianCalendar.YEAR), cal.get(GregorianCalendar.MONTH),
				cal.get(GregorianCalendar.DAY_OF_MONTH)};
		PayPeriod thisPP = new PayPeriod(startOfThisPP, endOfThisPP, getApplicationContext());
		

		//Set up the list
		adapter = new RowHelperPayPeriod(getApplicationContext(), thisPP, StringFormat);
		setListAdapter(adapter);
		
		prefs.addPropertyChangeListener(new PropertyChangeListener(){

			public void propertyChange(PropertyChangeEvent event) {
				
				StringFormat = prefs.getEditStringFormat();
				
				weeks_in_pp = prefs.getWeeksInPP();
				int[] startOfThisPP2 = prefs.getStartOfThisPP();
				
				adapter.setFormat(StringFormat);		
				GregorianCalendar cal = new GregorianCalendar(startOfThisPP2[0], 
						startOfThisPP2[1], startOfThisPP2[2]);
				cal.add(GregorianCalendar.DAY_OF_YEAR, 7 * weeks_in_pp);
				int[] endOfPP = {
					cal.get(GregorianCalendar.YEAR),
					cal.get(GregorianCalendar.MONTH),
					cal.get(GregorianCalendar.DAY_OF_MONTH)
				};
				
				PayPeriod thisPP = new PayPeriod(startOfThisPP2, endOfPP, getApplicationContext());
				for(int i = 0; i < thisPP.size(); i++){
					adapter.updateDay(i, thisPP.get(i));
				}
				adapter.notifyDataSetChanged();

				
			}
			
		});

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
		
		dialog.cancel();
		
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
		if(updateAdapt != null){
			updateAdapt.cancel(true);
		}
		updateAdapt = null;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	private class updateAdapter extends AsyncTask<Context, Object, Void> {
		int[] endOfThisPP = new int[3];
		int[] startOfThisPP = new int[3];

		protected void onPreExecute(){
			PreferenceSingelton prefs = PreferenceSingelton.getInstance();
				
			int[] dateHold = prefs.getStartOfThisPP();
			int weeks_in_pp = prefs.getWeeksInPP();
			startOfThisPP =  Chronos.getPP(dateHold, weeks_in_pp);

			GregorianCalendar cal = new GregorianCalendar(startOfThisPP[0], 
					startOfThisPP[1], startOfThisPP[2]);
			
			cal.add(GregorianCalendar.DAY_OF_YEAR, weeks_in_pp * 7);
			endOfThisPP[0] = cal.get(GregorianCalendar.YEAR);
			endOfThisPP[1] = cal.get(GregorianCalendar.MONTH);
			endOfThisPP[2] = cal.get(GregorianCalendar.DAY_OF_MONTH);
		}

		protected void onProgressUpdate(Object... progress) {
			Log.d(TAG, "Day Punches: " + ((Day)progress[1]).getSize());
			adapter.updateDay((Integer)progress[0], (Day)progress[1]);
		}

		protected void onPostExecute (Void param){
			adapter.notifyDataSetChanged();
		}

		@Override
		protected Void doInBackground(Context... param) {
			PayPeriod thisPP = new PayPeriod(startOfThisPP, endOfThisPP, (Context)param[0]);
			
			for(int i = 0; i < thisPP.size(); i++){
				publishProgress((Object)i, (Object)thisPP.get(i));
			}
			return null;
		}

	}


}
