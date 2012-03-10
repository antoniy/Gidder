package net.antoniy.gidder.dns;

import android.content.Context;

public class DynamicDNSFactory {

	public static DynamicDNS createDynDNSStrategy(Context context) {
		return new DynDnsStrategy(context);
	}
	
	public static DynamicDNS createNoIpStrategy(Context context) {
		return new NoIpStrategy(context);
	}
}
