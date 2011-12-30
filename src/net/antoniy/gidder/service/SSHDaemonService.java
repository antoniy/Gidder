package net.antoniy.gidder.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class SSHDaemonService extends Service {
	private final static String TAG = SSHDaemonService.class.getSimpleName();
	
	public SSHDaemonService() {
		Log.i(TAG, "Construct SSHDaemonService!");
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.i(TAG, "Service BIND!");
		
		return null;
	}
	
	@Override
	public void onDestroy() {
		Log.i(TAG, "Service stopped!");
		
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "Service started! flags: " + flags +", startId: " + startId);
		
		return super.onStartCommand(intent, flags, startId);
	}
}
