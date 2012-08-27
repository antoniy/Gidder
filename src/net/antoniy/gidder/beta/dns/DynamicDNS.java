package net.antoniy.gidder.beta.dns;

public interface DynamicDNS {
	
	public abstract void update(String hostname, String address, String username, String password);

}
