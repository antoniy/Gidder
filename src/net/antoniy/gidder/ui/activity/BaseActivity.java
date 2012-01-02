package net.antoniy.gidder.ui.activity;

import net.antoniy.gidder.db.DBHelper;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.Window;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

public abstract class BaseActivity extends OrmLiteBaseActivity<DBHelper> implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		System.setProperty("java.net.preferIPv6Addresses", "false");
//      getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setup();
		initComponents(savedInstanceState);
	}
	
	protected abstract void setup();
	
	protected abstract void initComponents(Bundle savedInstanceState);
	
	@Override
	public void onClick(View v) {
	}
	
}
