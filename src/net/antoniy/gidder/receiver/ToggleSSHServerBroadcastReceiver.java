package net.antoniy.gidder.receiver;

import net.antoniy.gidder.ui.util.C;
import net.antoniy.gidder.ui.util.GidderCommons;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ToggleSSHServerBroadcastReceiver extends BroadcastReceiver {
	private final static String TAG = ToggleSSHServerBroadcastReceiver.class.getSimpleName();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		final String action = intent.getAction();

		if (action.equals(C.action.TOGGLE_SSH_SERVER)) {
			if(GidderCommons.isSshServiceRunning(context)) {
				context.stopService(new Intent(C.action.START_SSH_SERVER));
				Log.i(TAG, "Broadcast - stop service!");
			} else {
				context.startService(new Intent(C.action.START_SSH_SERVER));
				Log.i(TAG, "Broadcast - start service!");
			}
		}
		
	}
	
}
