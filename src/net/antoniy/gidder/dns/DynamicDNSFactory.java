package net.antoniy.gidder.dns;

import android.content.Context;

public class DynamicDNSFactory {

	public static DynamicDNS createDynDNSStrategy() {
		return new DynDNSStrategy();
	}
	
	public static DynamicDNS createNoIpStrategy(Context context) {
		return new NoIpStrategy(context);
	}
}
