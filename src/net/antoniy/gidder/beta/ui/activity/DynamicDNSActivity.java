package net.antoniy.gidder.beta.ui.activity;

import net.antoniy.gidder.beta.R;
import net.antoniy.gidder.beta.app.GidderApplication;
import net.antoniy.gidder.beta.dns.DynamicDNSManager;
import net.antoniy.gidder.beta.ui.util.C;
import net.antoniy.gidder.beta.ui.util.PrefsConstants;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class DynamicDNSActivity extends BaseActivity implements OnCheckedChangeListener {

	private Spinner providerSpinner;
	private EditText domainEditText;
	private EditText usernameEditText;
	private EditText passwordEditText;
	private CheckBox activateCheckBox;
	private CheckBox showPasswordCheckBox;
	private LinearLayout mainContainer;
	private SharedPreferences prefs;

	@Override
	protected void setup() {
		setContentView(R.layout.dynamic_dns);
	}

	@Override
	protected void initComponents(Bundle savedInstanceState) {
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		boolean isActive = prefs.getBoolean(PrefsConstants.DYNDNS_ACTIVE.getKey(), false);
		int providerIndex = prefs.getInt(PrefsConstants.DYNDNS_PROVIDER_INDEX.getKey(), 0);
		String domain = prefs.getString(PrefsConstants.DYNDNS_DOMAIN.getKey(), "");
		String username = prefs.getString(PrefsConstants.DYNDNS_USERNAME.getKey(), "");
		String password = prefs.getString(PrefsConstants.DYNDNS_PASSWORD.getKey(), "");
		
        providerSpinner = (Spinner) findViewById(R.id.dynamicDnsProvider);
        providerSpinner.setSelection(providerIndex);
        
        domainEditText = (EditText) findViewById(R.id.dynamicDnsDomain);
        domainEditText.setText(domain);
        
        usernameEditText = (EditText) findViewById(R.id.dynamicDnsUsername);
        usernameEditText.setText(username);
        
        passwordEditText = (EditText) findViewById(R.id.dynamicDnsPassword);
        passwordEditText.setText(password);
        
        activateCheckBox = (CheckBox) findViewById(R.id.dynamicDnsActivate);
        activateCheckBox.setChecked(isActive);
        activateCheckBox.setOnCheckedChangeListener(this);
        
        showPasswordCheckBox = (CheckBox) findViewById(R.id.dynamicDnsShowPassword);
        showPasswordCheckBox.setOnCheckedChangeListener(this);
        
        mainContainer = (LinearLayout) findViewById(R.id.dynamicDnsMainContainer);
        if(!isActive) {
        	mainContainer.setVisibility(View.GONE);
        }
	}
	
	@Override
	protected void setupActionBar() {
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem doneMenuItem = menu.add("Done").setIcon(R.drawable.ic_actionbar_accept);
		doneMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		doneMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				processDoneAction();
				return true;
			}
			
		});
		
		MenuItem updateMenuItem = menu.add("Update").setIcon(R.drawable.ic_actionbar_refresh);
		updateMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		updateMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				new DynamicDNSManager(DynamicDNSActivity.this).update();
				((GidderApplication)((Context)DynamicDNSActivity.this).getApplicationContext()).setUpdateDynDnsTime(System.currentTimeMillis());
				return true;
			}
			
		});
		
		MenuItem cancelMenuItem = menu.add("Cancel").setIcon(R.drawable.ic_actionbar_cancel);
		cancelMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		cancelMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				finish();
				return false;
			}
			
		});
		
		return true;
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
	
	private void processDoneAction() {
		if(!activateCheckBox.isChecked()) {
			removeFieldData();
			finish();
			return;
		}
		
		boolean isValid = isFieldDataValid();
		
		if(!isValid) {
			return;
		}
		
		saveFieldData();
		
		finish();
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		int id = buttonView.getId();
		
		if(id == R.id.dynamicDnsActivate) {
			SharedPreferences.Editor prefsEditor = prefs.edit();
			prefsEditor.putBoolean("dynamicDnsActive", isChecked);
			prefsEditor.commit();
			
			if(isChecked) {
				// do activate
				Animation animation = new TranslateAnimation(
		            Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f,
		            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f
		        );
				animation.setDuration(250);
				mainContainer.startAnimation(animation);
				mainContainer.setVisibility(View.VISIBLE);
			} else {
				// do deactivate
				Animation animation = new TranslateAnimation(
		            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f,
		            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f
		        );
				animation.setDuration(250);
				animation.setAnimationListener(new Animation.AnimationListener() {
					
					@Override
					public void onAnimationStart(Animation animation) {
					}
					
					@Override
					public void onAnimationRepeat(Animation animation) {
					}
					
					@Override
					public void onAnimationEnd(Animation animation) {
						mainContainer.setVisibility(View.GONE);
					}
				});
				mainContainer.startAnimation(animation);
			}
		} else if (id == R.id.dynamicDnsShowPassword) {
			if(isChecked) {
				passwordEditText.setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
			} else {
				passwordEditText.setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
			}
		}
	}
	
	private boolean isFieldDataValid() {
		boolean isAllFieldsValid = true;
		
		if(!isDomainValid()) {
			isAllFieldsValid = false;
		}
		
		if(!isUsernameValid()) {
			isAllFieldsValid = false;
		}
		
		if(!isPasswordValid()) {
			isAllFieldsValid = false;
		}
		
		return isAllFieldsValid;
	}
	
	private boolean isEditTextEmpty(EditText tv) {
		String text = tv.getText().toString();
		if("".equals(text.trim())) {
			tv.setError("Field must contain value");
			return true;
		} else {
			return false;
		}
	}
	
	private boolean isDomainValid() {
		return !isEditTextEmpty(domainEditText);
	}
	
	private boolean isPasswordValid() {
		return !isEditTextEmpty(passwordEditText);
	}
	
	private boolean isUsernameValid() {
		return !isEditTextEmpty(usernameEditText);
	}
	
	private void saveFieldData() {
		Editor editPrefs = prefs.edit();
		
		editPrefs.putBoolean(PrefsConstants.DYNDNS_ACTIVE.getKey(), activateCheckBox.isChecked());
		editPrefs.putInt(PrefsConstants.DYNDNS_PROVIDER_INDEX.getKey(), providerSpinner.getSelectedItemPosition());
		editPrefs.putString(PrefsConstants.DYNDNS_DOMAIN.getKey(), domainEditText.getText().toString());
		editPrefs.putString(PrefsConstants.DYNDNS_USERNAME.getKey(), usernameEditText.getText().toString());
		editPrefs.putString(PrefsConstants.DYNDNS_PASSWORD.getKey(), passwordEditText.getText().toString());
		
		editPrefs.commit();
	}
	
	private void removeFieldData() {
		Editor editPrefs = prefs.edit();
		
		editPrefs.putBoolean(PrefsConstants.DYNDNS_ACTIVE.getKey(), false);
		editPrefs.putInt(PrefsConstants.DYNDNS_PROVIDER_INDEX.getKey(), 0);
		editPrefs.putString(PrefsConstants.DYNDNS_DOMAIN.getKey(), "");
		editPrefs.putString(PrefsConstants.DYNDNS_USERNAME.getKey(), "");
		editPrefs.putString(PrefsConstants.DYNDNS_PASSWORD.getKey(), "");
		
		editPrefs.commit();
	}

}
