package com.kopysoft.chronos.RowHelper;

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
