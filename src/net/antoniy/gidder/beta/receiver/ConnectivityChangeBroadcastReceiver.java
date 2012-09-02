package net.antoniy.gidder.beta.receiver;

import net.antoniy.gidder.beta.app.GidderApplication;
import net.antoniy.gidder.beta.dns.DynamicDNSManager;
import net.antoniy.gidder.beta.service.SSHDaemonService;
import net.antoniy.gidder.beta.ui.util.GidderCommons;
import net.antoniy.gidder.beta.ui.util.PrefsConstants;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.util.Log;

public class ConnectivityChangeBroadcastReceiver extends BroadcastReceiver {
	private final static String TAG = ConnectivityChangeBroadcastReceiver.class.getSimpleName();
	
	@Override
	public void onReceive(final Context context, Intent intent) {
		final String action = intent.getAction();

		if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
			boolean autostartOnWifiOn = prefs.getBoolean(PrefsConstants.AUTOSTART_ON_WIFI_ON.getKey(), false);
			boolean autostopOnWifiOff = prefs.getBoolean(PrefsConstants.AUTOSTOP_ON_WIFI_OFF.getKey(), false);
			
			if (GidderCommons.isWifiReady(context)) {
	        	Log.i(TAG, "[" + GidderCommons.getWifiSSID(context) + "] WiFi is active!");

	        	final GidderApplication application = (GidderApplication)context.getApplicationContext();
	        	long lastDynDnsUpdateTime = application.getUpdateDynDnsTime();
	        	if((System.currentTimeMillis() - lastDynDnsUpdateTime > GidderApplication.UPDATE_DYNDNS_INTERVAL)) {
					new DynamicDNSManager(context).update();
	        		application.setUpdateDynDnsTime(System.currentTimeMillis());
	        	}
	        	
	        	if(autostartOnWifiOn && !GidderCommons.isSshServiceRunning(context)) {
	        		context.startService(new Intent(context, SSHDaemonService.class));
	        		GidderCommons.makeStatusBarNotification(context);
	        	}
			} else {
				Log.i(TAG, "WiFi is NOT active!");
				Intent startServiceIntent = new Intent(context, SSHDaemonService.class);
				if(autostopOnWifiOff && GidderCommons.isSshServiceRunning(context)) {
					context.stopService(startServiceIntent);
					GidderCommons.stopStatusBarNotification(context);
				}
			}
		}
		
	}
	
}
