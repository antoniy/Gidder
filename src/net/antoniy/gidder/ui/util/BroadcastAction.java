package net.antoniy.gidder.ui.util;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.markupartist.android.widget.ActionBar.AbstractAction;

public class BroadcastAction extends AbstractAction {

	private final Context context;
	private final Intent intent;

	public BroadcastAction(Context context, Intent intent, int drawable) {
		super(drawable);
		this.context = context;
		this.intent = intent;
	}

	@Override
	public void performAction(View view) {
		context.sendBroadcast(intent);
	}

}
