package net.antoniy.gidder.app;

import net.antoniy.gidder.dns.DynamicDNSManager;
import net.antoniy.gidder.ui.util.GidderCommons;
import android.app.Application;
import android.util.Log;

public class GidderApplication extends Application {
	private final static String TAG = GidderApplication.class.getSimpleName();
	
	public final static long UPDATE_DYNDNS_INTERVAL = 10L * 60L * 1000L;
	
	private long updateDynDnsTime = 0;
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "[App] Started!");
		
    	if((System.currentTimeMillis() - updateDynDnsTime > GidderApplication.UPDATE_DYNDNS_INTERVAL) 
    			&& GidderCommons.isWifiConnected(this)) {

    		Log.i(TAG, "Update DynDNS on start!");
    		new DynamicDNSManager(this).update();
    		updateDynDnsTime = System.currentTimeMillis();
    	}
		
	}
	
	public void setUpdateDynDnsTime(long updateDynDnsTime) {
		this.updateDynDnsTime = updateDynDnsTime;
	}
	
	public long getUpdateDynDnsTime() {
		return updateDynDnsTime;
	}
	
}
