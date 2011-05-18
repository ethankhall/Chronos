package com.kopysoft.chronos.types;

import android.app.Application;
import android.content.Context;

public class AppContext extends Application {
	private static Context context;

	public void onCreate(){
		AppContext.context=getApplicationContext();
	}

	public static Context getAppContext(){
		return context;
	}

}
