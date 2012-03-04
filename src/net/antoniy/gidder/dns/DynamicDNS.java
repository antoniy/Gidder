package net.antoniy.gidder.dns;

public interface DynamicDNS {
	
	public abstract void update(String hostname, String address, String username, String password);

}
