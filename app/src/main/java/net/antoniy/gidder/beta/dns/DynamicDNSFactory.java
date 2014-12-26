package net.antoniy.gidder.beta.dns;

import android.content.Context;
import android.os.Handler;

public class DynamicDNSFactory {

	public static DynamicDNS createDynDNSStrategy(Context context, Handler toastHandler) {
		DynamicDNS dynamicDNS =  new DynDnsStrategy(context);
		dynamicDNS.setToastHandler(toastHandler);
		
		return dynamicDNS;
	}
	
	public static DynamicDNS createNoIpStrategy(Context context, Handler toastHandler) {
		DynamicDNS dynamicDNS = new NoIpStrategy(context);
		dynamicDNS.setToastHandler(toastHandler);
		
		return dynamicDNS;
	}
}
