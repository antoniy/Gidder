package net.antoniy.gidder.beta.ui.util;

public enum FragmentType {
	
	USERS("Users"),
	REPOSITORIES("Repositories");
	
	private String title;

	private FragmentType(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return title;
	}
}
