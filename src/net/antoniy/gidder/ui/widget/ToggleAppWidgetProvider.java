package net.antoniy.gidder.ui.widget;

import net.antoniy.gidder.R;
import net.antoniy.gidder.ui.util.C;
import net.antoniy.gidder.ui.util.GidderCommons;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class ToggleAppWidgetProvider extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		final int length = appWidgetIds.length;
		
		for (int i = 0; i < length; i++) {
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(C.action.TOGGLE_SSH_SERVER), PendingIntent.FLAG_UPDATE_CURRENT);
			
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.toggle_widget);
			views.setOnClickPendingIntent(R.id.toggleWidgetButton, pendingIntent);
			
			if(GidderCommons.isSshServiceRunning(context)) {
				views.setImageViewResource(R.id.toggleWidgetButton, R.drawable.ic_widget_active);
			} else {
				views.setImageViewResource(R.id.toggleWidgetButton, R.drawable.ic_widget_inactive);
			}
			
	
			appWidgetManager.updateAppWidget(appWidgetIds[i], views);
		}
	}
	
}
