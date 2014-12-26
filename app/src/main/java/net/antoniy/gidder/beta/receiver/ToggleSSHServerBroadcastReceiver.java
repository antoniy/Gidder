package net.antoniy.gidder.beta.receiver;

import net.antoniy.gidder.beta.ui.util.C;
import net.antoniy.gidder.beta.ui.util.GidderCommons;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class ToggleSSHServerBroadcastReceiver extends BroadcastReceiver {
	private final static String TAG = ToggleSSHServerBroadcastReceiver.class.getSimpleName();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		final String action = intent.getAction();

		if (action.equals(C.action.TOGGLE_SSH_SERVER)) {
			if(GidderCommons.isSshServiceRunning(context)) {
				context.stopService(new Intent(C.action.START_SSH_SERVER));
				Log.i(TAG, "Broadcast - stop service!");
			} else if (!GidderCommons.isWifiReady(context)) {
				Toast.makeText(context, "WiFi is NOT connected!", Toast.LENGTH_SHORT).show();
				Log.i(TAG, "Broadcast failed - wifi is NOT connected!");
			} else {
				context.startService(new Intent(C.action.START_SSH_SERVER));
				Log.i(TAG, "Broadcast - start service!");
			}
		}
		
	}
	
}
