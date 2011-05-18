package com.kopysoft.chronos.RowHelper;

import java.util.GregorianCalendar;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kopysoft.chronos.content.StaticFunctions;
import com.kopysoft.chronos.types.Day;
import com.kopysoft.chronos.types.Punch;

public class RowHelperToday extends BaseAdapter {

	Context gContext = null;
	Day workingDay = null;
	//@SuppressWarnings("unused")
	//private static final String TAG = Defines.TAG + " - RH_TODAY";

	public RowHelperToday(Context context, Day punchArray) {
		gContext = context;
		workingDay = punchArray;
	}

	public int getCount() {
		//workingDay.updateDay();
		return workingDay.getSize();
	}
	
	public boolean needToUpdateClock(){
		return workingDay.needToUpdateClock();
	}

	public Punch getItem(int arg0) {
		return workingDay.get(arg0);
	}
	
	public void add(Punch arg0){
		workingDay.add(arg0);
		workingDay.updateDay();
		workingDay.sort();
	}

	public long getItemId(int arg0) {
		return arg0;
	}

	public void updateDay(boolean update){
		if(update == true){
			//Log.d(TAG, "updateDay");
			workingDay.updateDay();
		}
		notifyDataSetChanged();
	}
	
	public void remove(int id){
		workingDay.remove(id);
		workingDay.updateDay();
		notifyDataSetChanged();		
	}
	
	public void sort(){
		workingDay.sort();
	}
	
	public Punch getByID( long ID ){
		return workingDay.getByID(ID);
	}
	
	public void setByID( long ID, Punch newPunch){
		workingDay.setByID(ID, newPunch);
	}
	
	public long[] getTime(){
		return workingDay.getArrayOfTime();
	}
	
	public long getTimeWithBreaks(){
		return workingDay.getTimeWithBreaks();
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		Punch temp = workingDay.get(position);
		long time = temp.getTime();

		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(time);

		String left = "";
		String right = "";
		Row2 returnValue = null;

		if(convertView == null){
			returnValue = new Row2(gContext);
		} else {
			returnValue = (Row2) convertView;
		}

		TextView leftView = returnValue.left();
		TextView rightView = returnValue.right();

		left = StaticFunctions.generateDateString(gContext, time);
		right = temp.generateTypeString(gContext);

		leftView.setText(left);
		rightView.setText(right);

		return returnValue;
	}
}
