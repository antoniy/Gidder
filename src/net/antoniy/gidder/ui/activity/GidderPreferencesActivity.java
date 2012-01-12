package net.antoniy.gidder.ui.activity;

import net.antoniy.gidder.R;
import net.antoniy.gidder.ui.util.PrefsConstants;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.view.Window;

public class GidderPreferencesActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	private EditTextPreference sshPortPreferences;
	private EditTextPreference repositoriesDirectoryPreferences;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.preferences);
		
		sshPortPreferences = (EditTextPreference) getPreferenceScreen().findPreference(PrefsConstants.SSH_PORT.getKey());
		repositoriesDirectoryPreferences = (EditTextPreference) getPreferenceScreen().findPreference(PrefsConstants.GIT_REPOSITORIES_DIR.getKey());
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		SharedPreferences prefs = getPreferenceScreen().getSharedPreferences();
		
		sshPortPreferences.setSummary("SSH server port: " + prefs.getString(PrefsConstants.SSH_PORT.getKey(), PrefsConstants.SSH_PORT.getDefaultValue()));
		repositoriesDirectoryPreferences.setSummary(prefs.getString(PrefsConstants.GIT_REPOSITORIES_DIR.getKey(), PrefsConstants.SSH_PORT.getDefaultValue()));
		
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if(key.equals("ssh_port")) {
			sshPortPreferences.setSummary("SSH server port: " + sharedPreferences.getString(
					PrefsConstants.SSH_PORT.getKey(), PrefsConstants.SSH_PORT.getDefaultValue()));
		} else if(key.equals("ssh_repositories_folder")) {
			repositoriesDirectoryPreferences.setSummary(sharedPreferences.getString(
					PrefsConstants.GIT_REPOSITORIES_DIR.getKey(), PrefsConstants.GIT_REPOSITORIES_DIR.getDefaultValue()));
		}
	}

}
