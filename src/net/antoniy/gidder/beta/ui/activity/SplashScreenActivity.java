package net.antoniy.gidder.beta.ui.activity;

import net.antoniy.gidder.beta.R;
import net.antoniy.gidder.beta.ui.util.C;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.actionbarsherlock.view.Window;

public class SplashScreenActivity extends BaseActivity {

//	private final static int REQUEST_MOVE_ON = 1;
//	private PayPal paypal;
//	private PendingIntent pendingIntent;
//	
//	private BroadcastReceiver timeoutBroadcastReceiver = new BroadcastReceiver() {
//		
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			final String action = intent.getAction();
//
//			if (action.equals(C.action.SPLASH_SCREEN_TIMEOUT)) {
//				finish();
//				startActivity(new Intent(C.action.START_HOME_ACTIVITY));
//			}
//		}
//	};
	
	@Override
	protected void setup() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash_screen);
	}

	@Override
	protected void initComponents(Bundle savedInstanceState) {
		new Handler().postDelayed(new Runnable() {
	        public void run() {
	        	Intent intent = new Intent(C.action.START_HOME_ACTIVITY);

	        	SplashScreenActivity.this.startActivity(intent);
	        	SplashScreenActivity.this.finish();
	        }
	    }, 3L * 1000L);
		
//		Intent intent = new Intent(C.action.SPLASH_SCREEN_TIMEOUT);
//		pendingIntent = PendingIntent.getBroadcast(this, REQUEST_MOVE_ON, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//		
//		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//		alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 3L * 1000L, pendingIntent);
		
		View donateButton = findViewById(R.id.splashScreenDonateButton);
		donateButton.setOnClickListener(this);
		
//		paypal = PayPal.initWithAppID(this.getBaseContext(), "APP-80W284485P519543T", PayPal.ENV_NONE);
//
//		CheckoutButton launchPayPalButton = paypal.getCheckoutButton(this, PayPal.BUTTON_278x43, CheckoutButton.TEXT_DONATE);
//		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
////		params.gravity = Gravity.BOTTOM;
////		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
////		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
////		params.bottomMargin = 10;
//		launchPayPalButton.setLayoutParams(params);
//		launchPayPalButton.setOnClickListener(this);
//		((LinearLayout)findViewById(R.id.spashScreenDonateContainer)).addView(launchPayPalButton);
	}

//	@Override
//	protected void onResume() {
//		super.onResume();
//		registerReceiver(timeoutBroadcastReceiver, new IntentFilter(C.action.SPLASH_SCREEN_TIMEOUT));
//	}
//	
//	@Override
//	protected void onPause() {
//		super.onPause();
//		unregisterReceiver(timeoutBroadcastReceiver);
//	}
	
	@Override
	public void onClick(View v) {
		
		if(v.getId() == R.id.splashScreenDonateButton) {
			finish();
			startActivity(new Intent(C.action.START_DONATE_ACTIVITY));
		}
		
//		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//		alarmManager.cancel(pendingIntent);
//		
////		PayPal paypal = PayPal.initWithAppID(this.getBaseContext(), "APP-80W284485P519543T", PayPal.ENV_NONE);
//		
//		PayPalPayment newPayment = new PayPalPayment();
//		newPayment.setSubtotal(new BigDecimal(1));
//		newPayment.setCurrencyType(Currency.getInstance("USD"));
//		newPayment.setRecipient("antoniy@chonkov.net");
//		newPayment.setMerchantName("Gidder");
//		Intent paypalIntent = paypal.checkout(newPayment, SplashScreenActivity.this);
//		startActivityForResult(paypalIntent, 1);
	}
}
