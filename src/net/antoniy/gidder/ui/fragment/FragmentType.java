package net.antoniy.gidder.ui.fragment;

public enum FragmentType {
	
	USERS("Users"),
	REPOSITORIES("Repositories"),
	SETTINGS("Settings");
	
	private String title;

	private FragmentType(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return title;
	}
}
