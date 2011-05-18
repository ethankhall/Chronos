package com.kopysoft.chronos.service;

import java.util.GregorianCalendar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.kopysoft.chronos.R;
import com.kopysoft.chronos.mainUI;
import com.kopysoft.chronos.content.StaticFunctions;
import com.kopysoft.chronos.enums.Defines;

public class ChronoService extends Service {

	private static final String TAG = Defines.TAG + " - Service";
	IAndroidService mService = null;
	public final static int NOTIFY_ME_ID = 62051;

	//GregorianCalendar currentCal = null;

	//timing data
	private static boolean running = false; 
	private static long time = 0;

	//update notification
	//private Handler serviceHandler;
	//private Task updateTask = new Task();
	NotificationManager mNotificationManager = null;
	boolean notificationEnabled = true;

	@Override
	public IBinder onBind(Intent arg0) {
		if(Defines.DEBUG_PRINT) Log.d(TAG, "onBind()");
		return myRemoteService;
	}

	private IAndroidService.Stub myRemoteService = new IAndroidService.Stub() {
		public void setClockAction(boolean i_type, long i_time) {
			running = i_type;
			time = i_time;
			if(Defines.DEBUG_PRINT) Log.d(TAG, "Init Time: " + time );
			if( running == false){
				removeTimeNotification();
			}
			update();
		}	//End setClockAction

		public void setNotification(boolean notification) {
			notificationEnabled = notification;
			update();
		}

		public void runUpdate() throws RemoteException {
			update();			
		}

		public void setTextNotification(String title, String message) throws RemoteException {
			StaticFunctions.printLog(Defines.ALL, TAG, "LogOut");
			setNotificationString(title, message);	
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		if(Defines.DEBUG_PRINT) Log.d(TAG, "onCreate()");
	}

	@Override
	public void onDestroy() {
		removeTimeNotification();
		if(Defines.DEBUG_PRINT) Log.d(TAG,"onDestroy()");
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		if(Defines.DEBUG_PRINT) Log.d(TAG, "onStart()");
		createNotification();
	}

	private String getTimeString(){
		String returnString;
		int hour = 0;
		int min = 0;
		long temp = time;
		if ( temp < 0 ){
			GregorianCalendar cal = new GregorianCalendar();
			temp = temp + cal.getTimeInMillis();
		}

		//Do calculations
		hour = (int)(( temp / 1000) / 60) /60;
		min = (int)(( temp / 1000) / 60) % 60;
		if( hour == 0 ){
			returnString = String.format("About %d minutes", min);
		} else {
			returnString = String.format("About %d hours and %d min", hour, min);
		}
		return returnString;
	}

	private void createNotification(){
		mNotificationManager = 
			(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
	}

	private void removeTimeNotification(){
		if (mNotificationManager != null)
			mNotificationManager.cancel( NOTIFY_ME_ID );
		if(Defines.DEBUG_PRINT) Log.d(TAG,"Remove Notification");
	}

	private void updateNotificationTime(String pushIn){

		notificationEnabled = true;
		Notification notifyDetails = 
			new Notification(R.drawable.ic_status_bar,
					"Clocked In",System.currentTimeMillis());

		PendingIntent myIntent = 
			PendingIntent.getActivity(this, 0, new Intent(this, mainUI.class), 0);

		notifyDetails.setLatestEventInfo(this, 
				"Clocked In", pushIn, myIntent);

		notifyDetails.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_ONLY_ALERT_ONCE;
		mNotificationManager.notify( NOTIFY_ME_ID , notifyDetails);
		if(Defines.DEBUG_PRINT) Log.d(TAG,"Sucessfully Changed Time");
	}
	
	private void setNotificationString(String title, String message){
		Notification notifyDetails = 
			new Notification(R.drawable.ic_status_bar,
					title,System.currentTimeMillis());
		
		PendingIntent myIntent = 
			PendingIntent.getActivity(this, 0, new Intent(this, mainUI.class), 0);

		notifyDetails.setLatestEventInfo(this, 
				title, message, myIntent);
		
		notifyDetails.flags |= Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONLY_ALERT_ONCE;

		mNotificationManager.notify( NOTIFY_ME_ID + 1 , notifyDetails);
	}

	public void update(){

		if(Defines.DEBUG_PRINT) Log.d(TAG, "Service Run");
		GregorianCalendar cal = new GregorianCalendar();
		
		long temp = time + cal.getTimeInMillis();


		if(notificationEnabled == true){
			if(temp < 0 || temp > 24 * 60 * 60 * 1000)	//Error Conditions
			{
				removeTimeNotification();
				running = false;
				Log.w(TAG, "Time was set incorrectly, stopping notification. Time: " + time +
						" Working Time: " + temp);
				notificationEnabled = false;
			} else {

				String post = getTimeString();
				updateNotificationTime(post);
				if(Defines.DEBUG_PRINT) Log.d(TAG, "String to put: " + post);
			}
		} else {
			removeTimeNotification();
		}
	}
}
