package com.kopysoft.chronos.RowHelper;

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
import com.kopysoft.chronos.enums.TimeFormat;
import com.kopysoft.chronos.types.Day;
import com.kopysoft.chronos.types.PayPeriod;

public class RowHelperPastView extends BaseAdapter {

	Context gContext = null;
	PayPeriod gDayArray = null;
	private static final String TAG = Defines.TAG + " - RH_PP";
	TimeFormat mFormatType;

	public RowHelperPastView(Context context, PayPeriod payPeriod, TimeFormat formatType) {
		gContext = context;
		gDayArray = payPeriod;
		mFormatType = formatType;
	}

	public void setFormat( TimeFormat formatType ){
		mFormatType = formatType;
	}

	public int getCount() {
		return gDayArray.size();
	}

	public Day getItem(int arg0) {
		return gDayArray.get(arg0);
	}

	public long getItemId(int arg0) {
		return arg0;
	}

	public void updateAll(){
		for(int i = 0; i < gDayArray.size(); i++){
			gDayArray.get(i).updateDay();
		}
	}

	public void updateDay( int poition, Day replace){
		gDayArray.set(poition, replace);
		//notifyDataSetChanged();
	}
	
	public void addDay(Day addDay){
		gDayArray.add(addDay);
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		Day temp = gDayArray.get(position);
		int[] dateInfo = temp.getDay();

		if(Defines.DEBUG_PRINT) Log.d(TAG, "Position: " + position + "\tY: " + dateInfo[0] + 
				"\tM: " + dateInfo[1] + "\tD: " + dateInfo[2] + "\tTime: " + temp.getSeconds());

		GregorianCalendar cal = new GregorianCalendar(dateInfo[0], dateInfo[1], dateInfo[2]);

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

		left = Defines.DAYS[( cal.get(Calendar.DAY_OF_WEEK) - 1 ) % 7] + " " +
		Defines.MONTHS[cal.get(Calendar.MONTH)] + ", " + cal.get(Calendar.DAY_OF_MONTH); 		

		if (temp.getSeconds() < 0){
			right = "--:--:--";
		} else {
			right = StaticFunctions.generateTimeString(temp.getSeconds(), mFormatType, false);
		}

		leftView.setText(left);
		rightView.setText(right);

		return returnValue;
	}
}
