package net.antoniy.gidder.beta.ui.widget;

import net.antoniy.gidder.beta.R;
import net.antoniy.gidder.beta.ui.util.C;
import net.antoniy.gidder.beta.ui.util.GidderCommons;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.RemoteViews.RemoteView;

public class ToggleAppWidgetProvider extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		context.startService(new Intent(context, MyUpdateService.class));
	}
	
	public static class MyUpdateService extends Service {
		
		@Override
		public int onStartCommand(Intent intent, int flags, int startId) {
			// Update the widget
			buildRemoteView(this);

			// No more updates so stop the service and free resources
			stopSelf();
			
			return Service.START_NOT_STICKY;
		}
		
		public void buildRemoteView(Context context) {
			RemoteViews updateView = null;

			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(C.action.TOGGLE_SSH_SERVER), PendingIntent.FLAG_UPDATE_CURRENT);
			
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.toggle_widget);
			views.setOnClickPendingIntent(R.id.toggleWidgetButton, pendingIntent);
			
			if(GidderCommons.isSshServiceRunning(context)) {
				views.setImageViewResource(R.id.toggleWidgetButton, R.drawable.ic_widget_active);
			} else {
				views.setImageViewResource(R.id.toggleWidgetButton, R.drawable.ic_widget_inactive);
			}

			ComponentName myWidget = new ComponentName(this, ToggleAppWidgetProvider.class);
			AppWidgetManager manager = AppWidgetManager.getInstance(this);
			manager.updateAppWidget(myWidget, updateView);
		}

		@Override
		public void onConfigurationChanged(Configuration newConfig) {
			int oldOrientation = this.getResources().getConfiguration().orientation;

			if (newConfig.orientation != oldOrientation) {
				// Update the widget
				buildRemoteView(this);
			}
		}

		@Override
		public IBinder onBind(Intent intent) {
			return null;
		}
	}

}
