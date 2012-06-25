package net.antoniy.gidder.ui.activity;

import net.antoniy.gidder.R;
import net.antoniy.gidder.ui.util.C;
import net.antoniy.gidder.ui.util.PrefsConstants;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

public class GidderPreferencesActivity extends SherlockPreferenceActivity implements OnSharedPreferenceChangeListener {

	private EditTextPreference sshPortPreferences;
	private EditTextPreference repositoriesDirectoryPreferences;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setTheme(R.style.Theme_Sherlock);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.preferences);
		
		sshPortPreferences = (EditTextPreference) getPreferenceScreen().findPreference(PrefsConstants.SSH_PORT.getKey());
		repositoriesDirectoryPreferences = (EditTextPreference) getPreferenceScreen().findPreference(PrefsConstants.GIT_REPOSITORIES_DIR.getKey());
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == android.R.id.home) {
			Intent intent = new Intent(C.action.START_HOME_ACTIVITY);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			
			finish();
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
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
		if(key.equals(PrefsConstants.SSH_PORT)) {
			sshPortPreferences.setSummary("SSH server port: " + sharedPreferences.getString(
					PrefsConstants.SSH_PORT.getKey(), PrefsConstants.SSH_PORT.getDefaultValue()));
		} else if(key.equals("ssh_repositories_folder")) {
			repositoriesDirectoryPreferences.setSummary(sharedPreferences.getString(
					PrefsConstants.GIT_REPOSITORIES_DIR.getKey(), PrefsConstants.GIT_REPOSITORIES_DIR.getDefaultValue()));
		}
	}

}
