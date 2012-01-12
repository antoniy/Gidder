package net.antoniy.gidder.ui.util;

public enum PrefsConstants {

	SSH_PORT("ssh_port", "2222"),
	GIT_REPOSITORIES_DIR("git_repositories_dir", "/sdcard/gidder/repositories"),
	STATUSBAR_NOTIFICATION("statusbar_notification", "true"),
	SSH_SERVICE_STARTUP("ssh_service_startup", "false");
	
	private final String key;
	private final String defaultValue;

	private PrefsConstants(String key, String defaultValue) {
		this.key = key;
		this.defaultValue = defaultValue;
	}

	public String getKey() {
		return key;
	}

	public String getDefaultValue() {
		return defaultValue;
	}
	
}
