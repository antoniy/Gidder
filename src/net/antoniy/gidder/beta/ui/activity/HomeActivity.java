package net.antoniy.gidder.beta.ui.activity;

import net.antoniy.gidder.beta.R;
import net.antoniy.gidder.beta.app.SimpleEula;
import net.antoniy.gidder.beta.ui.util.C;
import net.antoniy.gidder.beta.ui.util.GidderCommons;
import net.antoniy.gidder.beta.ui.util.PrefsConstants;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class HomeActivity extends BaseActivity {
//	private final static String TAG = HomeActivity.class.getSimpleName();

	private Button startStopButton;
	private ImageView wirelessImageView;
	private TextView wifiStatusTextView;
	private TextView wifiSSIDTextView;
	private TextView homeServerInfoTextView;
	private SharedPreferences prefs;
	private AlertDialog tutorialDialog;
	private AlertDialog eulaDialog;
	
	private BroadcastReceiver connectivityChangeBroadcastReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();

			if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				if (GidderCommons.isWifiReady(context)) {
					wifiStatusTextView.setText("WiFi connected to");
		        	wifiSSIDTextView.setText(GidderCommons.getWifiSSID(context));
		        	wirelessImageView.setImageResource(R.drawable.ic_wireless_enabled);
		        	startStopButton.setBackgroundResource(R.drawable.blue_btn_selector);
				} else {
					wifiStatusTextView.setText("WiFi is NOT connected");
		        	wifiSSIDTextView.setText("");
		        	wirelessImageView.setImageResource(R.drawable.ic_wireless_disabled);
		        	startStopButton.setBackgroundResource(R.drawable.white_btn_selector);
				}
			}
		}
	};
	
	private BroadcastReceiver sshdBroadcastReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			
			if(action.equals(C.action.SSHD_STARTED)) {
				Animation flyInAnimation = new TranslateAnimation(
						Animation.RELATIVE_TO_SELF, -1.0f,
						Animation.RELATIVE_TO_SELF, 0.0f,
						Animation.RELATIVE_TO_SELF, 0.0f,
						Animation.RELATIVE_TO_SELF, 0.0f);
				flyInAnimation.setDuration(500);
				
				Animation fadeInAnimation = new AlphaAnimation(0.0f, 1.0f);
				fadeInAnimation.setDuration(1000);
				
		        AnimationSet animationSet = new AnimationSet(true);
		        animationSet.addAnimation(flyInAnimation);
		        animationSet.addAnimation(fadeInAnimation);
		        animationSet.setInterpolator(AnimationUtils.loadInterpolator(HomeActivity.this, android.R.anim.overshoot_interpolator));
				
				homeServerInfoTextView.setText(GidderCommons.getCurrentWifiIpAddress(HomeActivity.this) + ":" + 
	        			prefs.getString(PrefsConstants.SSH_PORT.getKey(), PrefsConstants.SSH_PORT.getDefaultValue()));
				
				homeServerInfoTextView.startAnimation(animationSet);
				homeServerInfoTextView.setVisibility(View.VISIBLE);
				startStopButton.setText("Stop");				
			} else if(action.equals(C.action.SSHD_STOPPED)) {
				Animation flyOutAnimation = new TranslateAnimation(
						Animation.RELATIVE_TO_SELF, 0.0f,
						Animation.RELATIVE_TO_SELF, 1.1f,
						Animation.RELATIVE_TO_SELF, 0.0f,
						Animation.RELATIVE_TO_SELF, 0.0f);
		        flyOutAnimation.setDuration(500);
				
		        Animation fadeOutAnimation = new AlphaAnimation(1.0f, 0.0f);
				fadeOutAnimation.setDuration(500);
		        
		        AnimationSet animationSet = new AnimationSet(true);
		        animationSet.addAnimation(fadeOutAnimation);
		        animationSet.addAnimation(flyOutAnimation);
		        animationSet.setInterpolator(AnimationUtils.loadInterpolator(HomeActivity.this, android.R.anim.anticipate_interpolator));
		        
				homeServerInfoTextView.startAnimation(animationSet);
				homeServerInfoTextView.setVisibility(View.INVISIBLE);
				startStopButton.setText("Start");
			}
		}
	};

	@Override
	protected void setup() {
		setContentView(R.layout.home);
	}

	@Override
	protected void initComponents(Bundle savedInstanceState) {
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
        boolean isSshServiceRunning = GidderCommons.isSshServiceRunning(this);
        
        startStopButton = (Button) findViewById(R.id.homeBtnStartStop);
        startStopButton.setOnClickListener(this);
        if(isSshServiceRunning) {
        	startStopButton.setText("Stop");
        } else {
        	startStopButton.setText("Start");
        }
        
        wirelessImageView = (ImageView) findViewById(R.id.homeWirelessImage);
        wifiStatusTextView = (TextView) findViewById(R.id.homeWifiStatus);
        wifiSSIDTextView = (TextView) findViewById(R.id.homeWifiSSID);
        
        if(GidderCommons.isWifiReady(this)) {
        	wifiStatusTextView.setText("WiFi connected to");
        	wifiSSIDTextView.setText(GidderCommons.getWifiSSID(this));
        	wirelessImageView.setImageResource(R.drawable.ic_wireless_enabled);
        	startStopButton.setBackgroundResource(R.drawable.blue_btn_selector);
        } else {
        	wifiStatusTextView.setText("WiFi is NOT connected");
        	wifiSSIDTextView.setText("");
        	wirelessImageView.setImageResource(R.drawable.ic_wireless_disabled);
        	startStopButton.setBackgroundResource(R.drawable.white_btn_selector);
        }
        
        homeServerInfoTextView = (TextView) findViewById(R.id.homeServerInfoTextView);
        
        if(isSshServiceRunning) {
        	homeServerInfoTextView.setVisibility(View.VISIBLE);
        	homeServerInfoTextView.setText(GidderCommons.getCurrentWifiIpAddress(this) + ":" + 
        			prefs.getString(PrefsConstants.SSH_PORT.getKey(), PrefsConstants.SSH_PORT.getDefaultValue()));
        } else {
        	homeServerInfoTextView.setVisibility(View.INVISIBLE);
        }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuItem settingsMenuItem = menu.add("Settings").setIcon(R.drawable.ic_actionbar_settings);
	    settingsMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
	    settingsMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent intent = new Intent(C.action.START_PREFERENCE_ACTIVITY);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				return true;
			}
			
		});
	    
	    MenuItem setupMenuItem = menu.add("Setup");
	    setupMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
	    setupMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent intent = new Intent(C.action.START_SETUP_ACTIVITY);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				return true;
			}
			
		});
	    
	    MenuItem dynamicDnsMenuItem = menu.add("Dynamic DNS").setIcon(R.drawable.ic_actionbar_dns);
	    dynamicDnsMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
	    dynamicDnsMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent intent = new Intent(C.action.START_DYNAMIC_DNS_ACTIVITY);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				return true;
			}
			
		});
	    
        return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		registerReceiver(connectivityChangeBroadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		
		IntentFilter sshdIntentFilter = new IntentFilter();
		sshdIntentFilter.addAction(C.action.SSHD_STARTED);
		sshdIntentFilter.addAction(C.action.SSHD_STOPPED);
		
		registerReceiver(sshdBroadcastReceiver, sshdIntentFilter);
		
		boolean firstrun = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("firstrun", true);
	    if(firstrun) {
	    	tutorialDialog = GidderCommons.showTutorialDialog(this);
	    }

	    eulaDialog = new SimpleEula(this).show();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		unregisterReceiver(connectivityChangeBroadcastReceiver);
		unregisterReceiver(sshdBroadcastReceiver);
		
		if(tutorialDialog != null) {
			tutorialDialog.dismiss();
		}
		
		if(eulaDialog != null) {
			eulaDialog.dismiss();
		}
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();

		if(id == R.id.homeBtnStartStop) {
			boolean isSshServiceRunning = GidderCommons.isSshServiceRunning(HomeActivity.this);
			
			Intent intent = new Intent(C.action.START_SSH_SERVER);
			if(!isSshServiceRunning) {
				if (!GidderCommons.isWifiReady(HomeActivity.this)) {
				    return;
				}
				
				startService(intent);
			} else {
				stopService(intent);
			}
		}
	}
	
}
