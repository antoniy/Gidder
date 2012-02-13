package net.antoniy.gidder.dns;

public abstract class DynamicDNS {
	protected final String hostname;

	public DynamicDNS(String hostname) {
		this.hostname = hostname;
	}
	
	public abstract void update(String address, String username, String password);

}
