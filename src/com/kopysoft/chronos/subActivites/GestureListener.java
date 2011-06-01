package com.kopysoft.chronos.subActivites;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.singelton.ListenerObj;

public class GestureListener extends GestureDetector.SimpleOnGestureListener{
	
	private static final String TAG = Defines.TAG + " - GestureL";
	
	private static final int SWIPE_MIN_DISTANCE = 60;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;

	@Override
	public boolean onSingleTapUp(MotionEvent ev) {
		//Log.d("onSingleTapUp",ev.toString());
		return true;
	}

	@Override
	public void onShowPress(MotionEvent ev) {
		//Log.d("onShowPress",ev.toString());
	}

	@Override
	public void onLongPress(MotionEvent ev) {
		//Log.d("onLongPress",ev.toString());
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		//Log.d("onScroll",e1.toString());
		return true;
	}

	@Override
	public boolean onDown(MotionEvent ev) {
		//Log.d("onDownd",ev.toString());
		return true;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		if(Defines.DEBUG_PRINT) Log.d(TAG, "onFling");
		try {
			if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
				return false;
			// right to left swipe
			if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				//Toast.makeText(ClockInAndOut.this, "Left Swipe", Toast.LENGTH_SHORT).show();
				ListenerObj.getInstance().fireFlingLeft();
				return true;
			}  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				//Toast.makeText(ClockInAndOut.this, "Right Swipe", Toast.LENGTH_SHORT).show();
				ListenerObj.getInstance().fireFlingRight();
				return true;
			}
		} catch (Exception e) {
			// nothing
		}
		return false;
	}
}



