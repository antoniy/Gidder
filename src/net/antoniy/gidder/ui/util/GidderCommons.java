package net.antoniy.gidder.ui.util;

import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.util.encoders.Hex;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

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

	public static boolean isWifiConnected(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (info.isConnected()) {
			return true;
		} else {
			return false;
		}
	}

	public static String getWifiSSID(Context context) {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		return wifiManager.getConnectionInfo().getSSID();
	}

	public static int convertInet4AddrToInt(byte[] addr) {
		int addrInt = 0;

		byte[] reversedAddr = reverse(addr);
		for (int i = 0; i < reversedAddr.length; i++) {
			addrInt = (addrInt << 8) | (reversedAddr[i] & 0xFF);
		}

		return addrInt;
	}

	public static byte[] convertIntToInet4Addr(int addrInt) {
		byte[] addr = new byte[4];

		for (int i = 0; i < 4; i++) {
			addr[i] = (byte) ((addrInt >> i * 8) & 0xFF);
		}

		return addr;
	}

	public static byte[] reverse(byte[] array) {
		int limit = array.length / 2;
		byte[] reversedArray = new byte[array.length];

		for (int i = 0; i < limit; i++) {
			reversedArray[i] = array[array.length - i - 1];
			reversedArray[reversedArray.length - i - 1] = array[i];
		}

		return reversedArray;
	}

	public static String getCurrentWifiIpAddress(Context context) {
		WifiManager myWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

		WifiInfo myWifiInfo = myWifiManager.getConnectionInfo();
		int ipAddress = myWifiInfo.getIpAddress();

		byte[] addr = GidderCommons.convertIntToInet4Addr(ipAddress);
		StringBuffer addressBuffer = new StringBuffer();
		for (byte b : addr) {
			if (!(addressBuffer.length() == 0)) {
				addressBuffer.append('.');
			}
			addressBuffer.append(String.valueOf(b & 0xff));
		}

		return addressBuffer.toString();
	}

	public static String generateSha1(String data) {
		byte[] dataBytes = data.getBytes();

		SHA1Digest sha1 = new SHA1Digest();
		sha1.reset();
		sha1.update(dataBytes, 0, dataBytes.length);

		int outputSize = sha1.getDigestSize();
		byte[] dataDigest = new byte[outputSize];

		sha1.doFinal(dataDigest, 0);

		String dataSha1 = new String(Hex.encode(dataDigest));

		return dataSha1;
	}

	public static String toCamelCase(String s) {
		String[] parts = s.split("_|\\s+");
		StringBuffer camelCaseString = new StringBuffer();

		boolean isFirst = true;
		for (String part : parts) {
			Log.i("Commons", "Camel case: '" + part + "'");
			camelCaseString.append(toProperCase(part, isFirst));
			isFirst = false;
		}
		
		return camelCaseString.toString();
	}

	private static String toProperCase(String s, boolean firstLetterSmall) {
		if(firstLetterSmall) {
			return s.toLowerCase();
		}
		
		return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
	}
}
