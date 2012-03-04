package net.antoniy.gidder.receiver;

import net.antoniy.gidder.app.GidderApplication;
import net.antoniy.gidder.ui.util.GidderCommons;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

public class ConnectivityChangeBroadcastReceiver extends BroadcastReceiver {
	private final static String TAG = ConnectivityChangeBroadcastReceiver.class.getSimpleName();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		final String action = intent.getAction();

		if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			if (GidderCommons.isWifiReady(context)) {
	        	Log.i(TAG, "[" + GidderCommons.getWifiSSID(context) + "] WiFi is active!");

	        	GidderApplication application = (GidderApplication)context.getApplicationContext();
	        	long lastDynDnsUpdateTime = application.getUpdateDynDnsTime();
	        	if((System.currentTimeMillis() - lastDynDnsUpdateTime > GidderApplication.UPDATE_DYNDNS_INTERVAL)) {
	        		Log.i(TAG, "Time to update!");
	        		application.setUpdateDynDnsTime(System.currentTimeMillis());
	        	}
			} else {
				Log.i(TAG, "WiFi is NOT active!");
			}
		}
		
	}
	
}
