package net.antoniy.gidder.ui.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

public abstract class GidderCommons {
	
	public static boolean isWifiReady(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		String ssid = getWifiSSID(context);
		
		if (info.isConnected() && ssid != null && !"".equals(ssid)) {
			return true;
		} else {
			return false;
		}
	}
	
	public static String getWifiSSID(Context context) {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		return wifiManager.getConnectionInfo().getSSID();
	}
}
