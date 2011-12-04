package net.antoniy.gidder.app;

import android.app.Application;
import android.util.Log;

public class GidderApplication extends Application {
	private final static String TAG = GidderApplication.class.getSimpleName();
	
	@Override
	public void onCreate() {
		Log.i(TAG, "[App] Started!");
		super.onCreate();
	}
	
}
