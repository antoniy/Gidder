package net.antoniy.gidder.dns;

public class DynamicDNSFactory {

	public static DynamicDNS createDynDNSStrategy(String hostname) {
		return new DynDNSStrategy(hostname);
	}
	
	public static DynamicDNS createNoIpStrategy(String hostname) {
		return new NoIpStrategy(hostname);
	}
}
