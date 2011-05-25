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

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kopysoft.chronos.content.StaticFunctions;
import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.types.Day;
import com.kopysoft.chronos.types.Punch;

public class RowHelperEditDay extends BaseAdapter {

	Context gContext = null;
	Day gPunchArray = null;
	private static final String TAG = Defines.TAG + " - RH_ED";

	public RowHelperEditDay(Context context, Day punchArray) {
		gContext = context;
		gPunchArray = punchArray;
	}

	public int getCount() {
		return gPunchArray.getSize();
	}
	
	public void add(Punch newPunch){
		gPunchArray.add(newPunch);
		gPunchArray.sort();
	}
	
	public Punch getByID( long ID ){
		return gPunchArray.getByID(ID);
	}
	
	public void setByID( long ID, Punch newPunch){
		gPunchArray.setByID(ID, newPunch);
	}
	
	public void setByPos( int location, Punch newPunch){
		gPunchArray.set(location, newPunch);
	}


	public Punch getItem(int arg0) {
		return gPunchArray.get(arg0);
	}

	public long getItemId(int arg0) {
		return gPunchArray.get(arg0).getId();
	}
	
	public void remove(int arg0){
		gPunchArray.remove(arg0);
		notifyDataSetChanged();
	}
	
	public void commit(){
		gPunchArray.updateDay();
		notifyDataSetChanged();
	}
	
	public void sort(){
		gPunchArray.sort();
		notifyDataSetChanged();
	}
	
	public void removeById(int arg0){
		gPunchArray.removeByID(arg0);
	}

	public void updateDay(){
		notifyDataSetChanged();
	}
	
	public void cancelUpdates(){
		gPunchArray.cancelUpdates();
	}
	
	public long getTime(){
		return gPunchArray.getTimeWithBreaks();
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		Punch temp = gPunchArray.get(position);
		long time = temp.getTime();
		if(Defines.DEBUG_PRINT) Log.d(TAG, "ID: " + temp.getId() + "\tAction: " + temp.getAction());

		if(Defines.DEBUG_PRINT) Log.d(TAG, "Position: " + position);

		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(time);

		if(Defines.DEBUG_PRINT) Log.d(TAG, "Day of Week: " + cal.get(Calendar.DAY_OF_WEEK));

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
