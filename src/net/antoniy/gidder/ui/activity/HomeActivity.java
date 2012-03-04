package net.antoniy.gidder.ui.activity;

import net.antoniy.gidder.R;
import net.antoniy.gidder.dns.DynamicDNSManager;
import net.antoniy.gidder.service.SSHDaemonService;
import net.antoniy.gidder.ui.adapter.NavigationAdapter;
import net.antoniy.gidder.ui.adapter.NavigationAdapter.NavigationItem;
import net.antoniy.gidder.ui.util.C;
import net.antoniy.gidder.ui.util.GidderCommons;
import net.antoniy.gidder.ui.util.PrefsConstants;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
	private final static int SSH_STARTED_NOTIFICATION_ID = 1;

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

        boolean isSshServiceRunning = isSshServiceRunning();
        
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
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		registerReceiver(connectivityChangeBroadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		unregisterReceiver(connectivityChangeBroadcastReceiver);
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();

		if(id == R.id.homeBtnStartStop) {
			boolean isSshServiceRunning = isSshServiceRunning();
			
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
					makeStatusBarNotification();
				}
				
				startStopButton.setText("Stop");
				wirelessImageView.setImageResource(R.drawable.ic_wireless_enabled);
			} else {
				stopService(intent);
				stopStatusBarNotification();
				
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
	
	private void makeStatusBarNotification() {
		Notification notification = new Notification(R.drawable.ic_launcher, "SSH server started!", System.currentTimeMillis());
		notification.defaults |= Notification.DEFAULT_SOUND;
//		notification.defaults |= Notification.DEFAULT_VIBRATE;
//		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
		
		Intent notificationIntent = new Intent(C.action.START_HOME_ACTIVITY);
		PendingIntent contentIntent = PendingIntent.getActivity(HomeActivity.this, 1, notificationIntent, 0);

//		notification.setLatestEventInfo(getActivity(), "Gidder", "SSH server is running", contentIntent);
		notification.setLatestEventInfo(HomeActivity.this, "SSH server is running", GidderCommons.getCurrentWifiIpAddress(HomeActivity.this) + ":6666", contentIntent);
		
		NotificationManager notificationManager = (NotificationManager) HomeActivity.this.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(SSH_STARTED_NOTIFICATION_ID, notification);
	}
	
	private void stopStatusBarNotification() {
		NotificationManager notificationManager = (NotificationManager) HomeActivity.this.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(SSH_STARTED_NOTIFICATION_ID);
	}

	private boolean isSshServiceRunning() {
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (SSHDaemonService.class.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
}
