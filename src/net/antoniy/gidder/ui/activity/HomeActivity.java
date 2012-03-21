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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
	private BroadcastReceiver connectivityChangeBroadcastReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();

			if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				if (GidderCommons.isWifiReady(context)) {
					wifiStatusTextView.setText("WiFi connected to");
		        	wifiSSIDTextView.setText(GidderCommons.getWifiSSID(context));
//		        	Log.i(TAG, "[" + getWifiSSID() + "] WiFi is active!");
				} else {
					wifiStatusTextView.setText("WiFi is NOT connected");
		        	wifiSSIDTextView.setText("");
//					Log.i(TAG, "WiFi is NOT active!");
				}
			}
		}
	};
	
	private BroadcastReceiver sshdBroadcastReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			
			if(action.equals(C.action.SSHD_STARTED)) {
				Log.i(TAG, "SSHd started - broadcast received!");
			} else if(action.equals(C.action.SSHD_STOPPED)) {
				Log.i(TAG, "SSHd stopped - broadcast received!");
			}
		}
	};

	@Override
	protected void setup() {
		setContentView(R.layout.home);
	}

	@Override
	protected void initComponents(Bundle savedInstanceState) {
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
        if(isSshServiceRunning) {
        	wirelessImageView.setImageResource(R.drawable.ic_wireless_enabled);
        } else {
        	wirelessImageView.setImageResource(R.drawable.ic_wireless_disabled);
        }
        
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
        } else {
        	wifiStatusTextView.setText("WiFi is NOT connected");
        	wifiSSIDTextView.setText("");
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

				if (!wifi.isConnected()) {
				    return;
				}
				
				startService(intent);
				
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
				boolean isStatusBarNotificationEnabled = prefs.getBoolean(PrefsConstants.STATUSBAR_NOTIFICATION.getKey(), 
						"true".equals(PrefsConstants.STATUSBAR_NOTIFICATION.getDefaultValue()) ? true : false);
				
				if(isStatusBarNotificationEnabled) {
					GidderCommons.makeStatusBarNotification(HomeActivity.this);
				}
				
				startStopButton.setText("Stop");
				wirelessImageView.setImageResource(R.drawable.ic_wireless_enabled);
			} else {
				stopService(intent);
				GidderCommons.stopStatusBarNotification(HomeActivity.this);
				
				startStopButton.setText("Start");
				wirelessImageView.setImageResource(R.drawable.ic_wireless_disabled);
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
