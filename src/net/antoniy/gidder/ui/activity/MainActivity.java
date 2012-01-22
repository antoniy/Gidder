package net.antoniy.gidder.ui.activity;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.AbstractAction;
import com.markupartist.android.widget.ActionBar.IntentAction;

import net.antoniy.gidder.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends BaseActivity {
	
	private final static String TAG = MainActivity.class.getSimpleName(); 

	@Override
	protected void setup() {
		setContentView(R.layout.main);
	}

	@Override
	protected void initComponents(Bundle savedInstanceState) {
		ActionBar actionBar = (ActionBar) findViewById(R.id.mainActionBar);
		actionBar.setHomeAction(new IntentAction(this, new Intent(this, SlideActivity.class), R.drawable.ic_actionbar_home));
		actionBar.setHomeAction(new AbstractAction(R.drawable.ic_gidder) {
			@Override
			public void performAction(View view) {
				// do nothing
			}
		});
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setHomeLogo(R.drawable.ic_launcher);
        actionBar.addAction(new IntentAction(this, new Intent(this, GidderPreferencesActivity.class), R.drawable.ic_actionbar_settings));
        actionBar.setTitle("Gidder");
	}

}
