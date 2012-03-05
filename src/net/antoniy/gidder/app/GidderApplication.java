package net.antoniy.gidder.app;

import net.antoniy.gidder.ui.util.C;
import android.app.Application;
import android.content.Intent;
import android.util.Log;

public class GidderApplication extends Application {
	private final static String TAG = GidderApplication.class.getSimpleName();
	
//	public final static long UPDATE_DYNDNS_INTERVAL = 10L * 60L * 1000L;
	public final static long UPDATE_DYNDNS_INTERVAL = 2L * 60L * 1000L;
	
	private long updateDynDnsTime = 0;
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "[App] Started!");
		
		Intent intent = new Intent(C.action.UPDATE_DYNAMIC_DNS_ADDRESS);
		sendBroadcast(intent);
	}
	
	public void setUpdateDynDnsTime(long updateDynDnsTime) {
		this.updateDynDnsTime = updateDynDnsTime;
	}
	
	public long getUpdateDynDnsTime() {
		return updateDynDnsTime;
	}
	
}
