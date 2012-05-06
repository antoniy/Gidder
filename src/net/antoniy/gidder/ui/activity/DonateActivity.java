package net.antoniy.gidder.ui.activity;

import net.antoniy.gidder.R;
import net.antoniy.gidder.ui.util.BroadcastAction;
import net.antoniy.gidder.ui.util.C;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.IntentAction;
import com.paypal.android.MEP.CheckoutButton;
import com.paypal.android.MEP.PayPal;

public class DonateActivity extends BaseActivity {

	private EditText amountEditText;
	private PayPal paypal;
	private BroadcastReceiver leaveBroadcastReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			
			if(action.equals(C.action.LEAVE_DONATE_ACTIVITY)) {
				Intent intentHome = new Intent(C.action.START_HOME_ACTIVITY);
				intentHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				
				startActivity(intentHome);
				finish();
			}
		}
	};
	
	@Override
	protected void setup() {
		setContentView(R.layout.donate);
	} 

	@Override
	protected void initComponents(Bundle savedInstanceState) {
		Intent intent = new Intent(C.action.LEAVE_DONATE_ACTIVITY);
		
		ActionBar actionBar = (ActionBar) findViewById(R.id.donateActionBar);
		actionBar.setHomeAction(new BroadcastAction(this, intent, R.drawable.ic_actionbar_home));
        actionBar.addAction(new IntentAction(this, new Intent(C.action.START_PREFERENCE_ACTIVITY), R.drawable.ic_actionbar_settings));
        actionBar.setTitle("Donate");
		
		amountEditText = (EditText) findViewById(R.id.donateAmount);
		
		paypal = PayPal.initWithAppID(this.getBaseContext(), "APP-80W284485P519543T", PayPal.ENV_NONE);

		CheckoutButton launchPayPalButton = paypal.getCheckoutButton(this, PayPal.BUTTON_278x43, CheckoutButton.TEXT_DONATE);
//		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//		params.gravity = Gravity.BOTTOM;
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
//		params.addRule(RelativeLayout.BELOW, R.id.donateAmount);
//		params.topMargin = 20;
//		params.rightMargin = 10;
//		params.leftMargin = 10;
		launchPayPalButton.setLayoutParams(params);
		launchPayPalButton.setOnClickListener(this);
		((RelativeLayout)findViewById(R.id.donateContent)).addView(launchPayPalButton);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(leaveBroadcastReceiver, new IntentFilter(C.action.LEAVE_DONATE_ACTIVITY));
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(leaveBroadcastReceiver);
	}

}
