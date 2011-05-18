package com.kopysoft.chronos.singelton;

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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.kopysoft.chronos.enums.Defines;
import com.kopysoft.chronos.service.IAndroidService;

public class ServiceSingleton {
	private static final String TAG = Defines.TAG + " - Main";
	
	private Context mContext = null;
	private static boolean connected = false;

	//Service Stuff
	public static IAndroidService remoteService;
	private static boolean started = false;
	private static RemoteServiceConnection conn = null;
	
	//ClockAction
	private static boolean type = false;
	private static long time = 0;

	public void setContext(Context context){
		mContext = context;
	}

	public ServiceSingleton( ){
	}

	public void stopService() {
		if (!started) {
			return;
		} else {
			Intent i = new Intent().setClass(mContext, com.kopysoft.chronos.service.ChronoService.class);
			mContext.stopService(i);
			started = false;
			if(Defines.DEBUG_PRINT) Log.d( TAG, "stopService()" );
		}
	}

	public void bindService() {
		if(conn == null) {
			conn = new RemoteServiceConnection();
			Intent i = new Intent().setClass(mContext, com.kopysoft.chronos.service.ChronoService.class);
			mContext.bindService(i, conn, Context.BIND_AUTO_CREATE);
			if(Defines.DEBUG_PRINT) Log.d( TAG, "bindService()" );
		} else {
			return;
		}
	}

	public void releaseService() {
		if(conn != null) {
			mContext.unbindService(conn);
			conn = null;
			if(Defines.DEBUG_PRINT) Log.d( TAG, "releaseService()" );
		} else {
			return;
		}
	}

	public void setClockAction(boolean i_type, long i_time) {
		if(Defines.DEBUG_PRINT) Log.d(TAG, "Connected: " + connected);
		type = i_type;
		time = i_time;
		if(conn == null) {
			return;
		} else if ( connected == true ){
			try {
				remoteService.setClockAction(i_type, i_time);
			} catch (RemoteException re) {
				Log.e( TAG, "RemoteException" );
			}
		}
	}
	
	public void runUpdate() {
		if(Defines.DEBUG_PRINT) Log.d(TAG, "Connected: " + connected);
		if(conn == null) {
			return;
		} else if ( connected == true ){
			try {
				remoteService.runUpdate();
			} catch (RemoteException re) {
				Log.e( TAG, "RemoteException" );
			}
		}
	}	
	
	public void setNotification(boolean notification){
		if(conn == null) {
			return;
		} else if ( connected == true ){
			try {
				remoteService.setNotification(notification);
			} catch (RemoteException re) {
				Log.e( TAG, "RemoteException" );
			}
		}
	}
	
	public void setTextNotification(String title, String message){
		if(conn == null) {
			return;
		} else if ( connected == true ){
			try {
				remoteService.setTextNotification(title, message);
			} catch (RemoteException re) {
				Log.e( TAG, "RemoteException" );
			}
		} else {
			Log.e( TAG, "Connection Failed" );
		}
	}

	public void startService(){
		if (started) {
			return;
		} else {
			Intent i = new Intent().setClass(mContext, com.kopysoft.chronos.service.ChronoService.class);
			mContext.startService(i);
			started = true;
			if(Defines.DEBUG_PRINT) Log.d( TAG, "startService()" );
		}

	}
	
	public boolean getConnectedStatus(){
		return connected;
	}

	class RemoteServiceConnection implements ServiceConnection {
		public void onServiceConnected(ComponentName className, 
				IBinder boundService ) {
			remoteService = IAndroidService.Stub.asInterface((IBinder)boundService);
			connected = true;
			if(type == true){
				setClockAction(type, time);
			}
			if(Defines.DEBUG_PRINT) Log.d( TAG, "onServiceConnected()" );
		}

		public void onServiceDisconnected(ComponentName className) {
			remoteService = null;
			connected = false;
			if(Defines.DEBUG_PRINT) Log.d( TAG, "onServiceDisconnected" );
		}
	};

}
