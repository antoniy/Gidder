package net.antoniy.gidder.ui.activity;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import net.antoniy.gidder.R;
import net.antoniy.gidder.ui.util.PrefsConstants;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class DynamicDNSActivity extends BaseActivity implements OnCheckedChangeListener {

	private final static String TAG = DynamicDNSActivity.class.getSimpleName();
	
	private Spinner providerSpinner;
	private EditText domainEditText;
	private EditText usernameEditText;
	private EditText passwordEditText;
	private CheckBox activateCheckBox;
	private CheckBox showPasswordCheckBox;
	private LinearLayout mainContainer;
	private Button saveButton;
	private SharedPreferences prefs;
	private View cancelButton;

	@Override
	protected void setup() {
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
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

//        AnimationSet set = new AnimationSet(true);
//
//        Animation animation = new AlphaAnimation(0.0f, 1.0f);
//        animation.setDuration(250);
//        set.addAnimation(animation);
//
//        animation = new TranslateAnimation(
//            Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f,
//            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f
//        );
//        animation.setDuration(150);
//        set.addAnimation(animation);
//
//        LayoutAnimationController controller =
//            new LayoutAnimationController(set, 0.25f);
//        mainContainer.setLayoutAnimation(controller);
        
        saveButton = (Button) findViewById(R.id.dynamicDnsSaveButton);
        saveButton.setOnClickListener(this);
        
        cancelButton = (Button) findViewById(R.id.dynamicDnsCancelButton);
        cancelButton.setOnClickListener(this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Update").setIcon(R.drawable.ic_action_refresh)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		
		return true;
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		
		if(id == R.id.dynamicDnsSaveButton) {
			boolean isValid = isFieldDataValid();
			
			if(!isValid) {
				return;
			}
			
			saveFieldData();
			
			finish();
		} else if(id == R.id.dynamicDnsCancelButton) {
			finish();
		}
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
//				int[] location = new int[2];
//				doneButton.getLocationInWindow(location);
//				Log.i(TAG, "Location: " + location[0] +", " + location[1]);
//				
//				Animation buttonAnimation = new TranslateAnimation(
//		            Animation.ABSOLUTE, location[0], Animation.ABSOLUTE, -doneButton.getWidth(),
//		            Animation.ABSOLUTE, location[1], Animation.ABSOLUTE, location[1]
//		        );
//				buttonAnimation.setDuration(1250);
//				doneButton.startAnimation(buttonAnimation);
				
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

}
