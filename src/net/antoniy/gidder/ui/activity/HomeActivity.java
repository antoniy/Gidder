package net.antoniy.gidder.ui.activity;

import net.antoniy.gidder.R;
import net.antoniy.gidder.app.GidderApplication;
import net.antoniy.gidder.dns.DynamicDNSManager;
import net.antoniy.gidder.service.SSHDaemonService;
import net.antoniy.gidder.ui.adapter.NavigationAdapter;
import net.antoniy.gidder.ui.adapter.NavigationAdapter.NavigationItem;
import net.antoniy.gidder.ui.util.C;
import net.antoniy.gidder.ui.util.GidderCommons;
import net.antoniy.gidder.ui.util.PrefsConstants;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.AbstractAction;
import com.markupartist.android.widget.ActionBar.IntentAction;

public class HomeActivity extends BaseActivity implements OnItemClickListener {
	private final static String TAG = HomeActivity.class.getSimpleName();

	private Button startStopButton;
	private ImageView wirelessImageView;
	private NavigationAdapter navigationAdapter;
	private GridView navigationGridView; 
	private TextView wifiStatusTextView;
	private TextView wifiSSIDTextView;
	private TextView homeServerInfoTextView;
	private SharedPreferences prefs;
	
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
		
		ActionBar actionBar = (ActionBar) findViewById(R.id.homeActionBar);
		actionBar.setHomeAction(new AbstractAction(R.drawable.ic_actionbar_home) {
			@Override
			public void performAction(View view) {
				// do nothing
			}
		});
        actionBar.addAction(new IntentAction(this, new Intent(C.action.START_PREFERENCE_ACTIVITY), R.drawable.ic_actionbar_settings));
        actionBar.setTitle("Gidder");

        boolean isSshServiceRunning = GidderCommons.isSshServiceRunning(this);
        
        startStopButton = (Button) findViewById(R.id.homeBtnStartStop);
        startStopButton.setOnClickListener(this);
        if(isSshServiceRunning) {
        	startStopButton.setText("Stop");
        } else {
        	startStopButton.setText("Start");
        }
        
        wirelessImageView = (ImageView) findViewById(R.id.homeWirelessImage);
        
        navigationAdapter = new NavigationAdapter(this);
        
        navigationGridView = (GridView) findViewById(R.id.homeNavigationGrid);
        navigationGridView.setAdapter(navigationAdapter);
//        navigationGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        navigationGridView.setOnItemClickListener(this);
        
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
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.home_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
	    case R.id.homeMenuSettings:
	    	Intent intent = new Intent(this, GidderPreferencesActivity.class);
			startActivity(intent);
	        return true;
	    case R.id.homeMenuUpdateDns:
			new DynamicDNSManager(HomeActivity.this).update();
			((GidderApplication)((Context)HomeActivity.this).getApplicationContext()).setUpdateDynDnsTime(System.currentTimeMillis());
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		registerReceiver(connectivityChangeBroadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		
		IntentFilter sshdIntentFilter = new IntentFilter();
		sshdIntentFilter.addAction(C.action.SSHD_STARTED);
		sshdIntentFilter.addAction(C.action.SSHD_STOPPED);
		
		registerReceiver(sshdBroadcastReceiver, sshdIntentFilter);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		unregisterReceiver(connectivityChangeBroadcastReceiver);
		unregisterReceiver(sshdBroadcastReceiver);
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();

		if(id == R.id.homeBtnStartStop) {
			boolean isSshServiceRunning = GidderCommons.isSshServiceRunning(HomeActivity.this);
			
			Intent intent = new Intent(HomeActivity.this, SSHDaemonService.class);
			if(!isSshServiceRunning) {
				ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
				NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

//				if (!wifi.isConnected()) {
				if (!GidderCommons.isWifiReady(HomeActivity.this)) {
				    return;
				}
				
				startService(intent);
				
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
				boolean isStatusBarNotificationEnabled = prefs.getBoolean(PrefsConstants.STATUSBAR_NOTIFICATION.getKey(), 
						"true".equals(PrefsConstants.STATUSBAR_NOTIFICATION.getDefaultValue()) ? true : false);
				
				if(isStatusBarNotificationEnabled) {
					GidderCommons.makeStatusBarNotification(HomeActivity.this);
				}
			} else {
				stopService(intent);
				GidderCommons.stopStatusBarNotification(HomeActivity.this);
			}
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		NavigationItem item = navigationAdapter.getItem(position);
		
		switch(item.getType()) {
		case SETUP: {
			Intent intent = new Intent(C.action.START_SLIDE_ACTIVITY);
			startActivity(intent);
			break;
		}
		case DNS: {
			Intent intent = new Intent(C.action.START_DYNAMIC_DNS_ACTIVITY);
			startActivity(intent);
			break;
		}
		case LOGS: {
			// TODO: ...
			break;
		}
		}
	}
	
}
