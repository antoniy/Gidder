package net.antoniy.gidder.beta.ui.util;

import android.os.Environment;

public enum PrefsConstants {

	SSH_PORT("ssh_port", "2222"),
	GIT_REPOSITORIES_DIR("git_repositories_dir", Environment.getExternalStorageDirectory().getPath() + "/gidder/repositories"),
	STATUSBAR_NOTIFICATION("statusbar_notification", "true"),
	SSH_SERVICE_STARTUP("ssh_service_startup", "false"),
	
	DYNDNS_ACTIVE("dyndns_active", ""),
	DYNDNS_PROVIDER_INDEX("dyndns_provider_index", ""),
	DYNDNS_DOMAIN("dyndns_domain", ""),
	DYNDNS_PASSWORD("dyndns_username", ""),
	DYNDNS_USERNAME("dyndns_password", ""),
	
	AUTOSTART_ON_WIFI_ON("autostart_on_wifi_on", "false"),
	AUTOSTOP_ON_WIFI_OFF("autostop_on_wifi_off", "false"),
	
	HELP("help_tutorial", "");
	
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
