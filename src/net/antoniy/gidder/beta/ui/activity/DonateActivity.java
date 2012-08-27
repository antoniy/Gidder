package net.antoniy.gidder.beta.ui.activity;

import net.antoniy.gidder.beta.R;
import net.antoniy.gidder.beta.ui.util.C;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class DonateActivity extends BaseActivity {
	
	private final static String DONATE_PAYPAL_URL = "https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=antoniy%40chonkov%2enet&lc=US&item_name=Gidder&item_number=Gidder&currency_code=USD&bn=PP%2dDonationsBF%3abtn_donateCC_LG%2egif%3aNonHosted";

//	protected static final int INITIALIZE_SUCCESS = 0;
//	protected static final int INITIALIZE_FAILURE = 1;
//	
//	// The PayPal server to be used - can also be ENV_NONE and ENV_LIVE
//	private static final int server = PayPal.ENV_SANDBOX;
//	
//	// The ID of your application that you received from PayPal
//	private static final String appID = "APP-80W284485P519543T";
//	
//	// This is passed in for the startActivityForResult() android function, the value used is up to you
//	private static final int request = 100;
	
//	private EditText amountEditText;
//	private View mainContainer;
	private Button webButton;
//	private CheckoutButton launchPayPalButton;
//	private ProgressDialog dialog;
	
//	private BroadcastReceiver leaveBroadcastReceiver = new BroadcastReceiver() {
//		
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			String action = intent.getAction();
//			
//			if(action.equals(C.action.LEAVE_DONATE_ACTIVITY)) {
//				Intent intentHome = new Intent(C.action.START_HOME_ACTIVITY);
//				intentHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				
//				startActivity(intentHome);
//				finish();
//			}
//		}
//	};
	
//	private Handler paypalRefreshHandler = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			switch (msg.what) {
//			case INITIALIZE_SUCCESS:
//				paypalSetupButton();
//				break;
//			case INITIALIZE_FAILURE:
//				paypalShowFailure();
//				break;
//			}
//			dialog.dismiss();
//			
//			mainContainer.setVisibility(View.VISIBLE);
////			TranslateAnimation slide = new TranslateAnimation(0f, 0f, 1f, 0f);
//			TranslateAnimation slide = new TranslateAnimation(
//					Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT, 0f, 
//					Animation.RELATIVE_TO_PARENT, 1f, Animation.RELATIVE_TO_PARENT, 0f); 
//		    slide.setDuration(500); 
//		    slide.setFillAfter(true); 
//		    mainContainer.startAnimation(slide);
//		}
//	};

	@Override
	protected void setup() {
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.donate);
	} 

	@Override
	protected void initComponents(Bundle savedInstanceState) {
//		Intent intent = new Intent(C.action.LEAVE_DONATE_ACTIVITY);
//		
//		mainContainer = (View) findViewById(R.id.donateMainContainer);
//		mainContainer.setVisibility(View.INVISIBLE);
		
//		amountEditText = (EditText) findViewById(R.id.donateAmount);
		
		webButton = (Button) findViewById(R.id.donateWebButton);
		webButton.setOnClickListener(this);

//		// Initialize the library. We'll do it in a separate thread because it requires communication with the server
//		// which may take some time depending on the connection strength/speed.
//		Thread libraryInitializationThread = new Thread() {
//			public void run() {
//				initLibrary();
//				
//				// The library is initialized so let's create our CheckoutButton and update the UI.
//				if (PayPal.getInstance().isLibraryInitialized()) {
//					paypalRefreshHandler.sendEmptyMessage(INITIALIZE_SUCCESS);
//				}
//				else {
//					paypalRefreshHandler.sendEmptyMessage(INITIALIZE_FAILURE);
//				}
//			}
//		};
//		libraryInitializationThread.start();
//		
//		dialog = ProgressDialog.show(DonateActivity.this, "", "Initializing PayPal library. Please wait...", true);
	}
	
//	private void paypalSetupButton() {
//		launchPayPalButton = PayPal.getInstance().getCheckoutButton(this, PayPal.BUTTON_278x43, CheckoutButton.TEXT_DONATE);
//		launchPayPalButton.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
//		launchPayPalButton.setOnClickListener(this);
//		((RelativeLayout)findViewById(R.id.donateContent)).addView(launchPayPalButton);
//	}
//	
//	public void paypalShowFailure() {
//		Toast.makeText(DonateActivity.this, "PayPal library initialization failed!", Toast.LENGTH_SHORT).show();
//	}
//	
//	private void initLibrary() {
//		PayPal pp = PayPal.getInstance();
//		// If the library is already initialized, then we don't need to initialize it again.
//		if(pp == null) {
//			// This is the main initialization call that takes in your Context, the Application ID, and the server you would like to connect to.
//			pp = PayPal.initWithAppID(this, appID, server);
//   			
//			// -- These are required settings.
//        	pp.setLanguage("en_US"); // Sets the language for the library.
//        	// --
//        	
//        	// -- These are a few of the optional settings.
//        	// Sets the fees payer. If there are fees for the transaction, this person will pay for them. Possible values are FEEPAYER_SENDER,
//        	// FEEPAYER_PRIMARYRECEIVER, FEEPAYER_EACHRECEIVER, and FEEPAYER_SECONDARYONLY.
//        	pp.setFeesPayer(PayPal.FEEPAYER_SENDER); 
//        	
//        	// Set to true if the transaction will require shipping.
//        	pp.setShippingEnabled(false);
//        	
//        	// Dynamic Amount Calculation allows you to set tax and shipping amounts based on the user's shipping address. Shipping must be
//        	// enabled for Dynamic Amount Calculation. This also requires you to create a class that implements PaymentAdjuster and Serializable.
//        	pp.setDynamicAmountCalculationEnabled(false);
//        	// --
//		}
//	}
//	
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if(requestCode != request) {
//			return;
//		}
//		
//		if(launchPayPalButton != null) {
//			launchPayPalButton.updateButton();
//		}
//	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem continueMenuItem = menu.add("Skip");
		continueMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		continueMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				Intent intent = new Intent(C.action.START_HOME_ACTIVITY);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				
				finish();
				startActivity(intent);
				return true;
			}
			
		});
		
		return true;
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.donateWebButton) {
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(DONATE_PAYPAL_URL));
			startActivity(browserIntent);
//		} else if(v == launchPayPalButton) {
//			String amountString = amountEditText.getText().toString().trim();
//			
//			if("".equals(amountString)) {
//				Toast.makeText(DonateActivity.this, "Please fill the amount box.", Toast.LENGTH_SHORT).show();
//				launchPayPalButton.updateButton();
//				return;
//			}
//			
//			BigDecimal amount = null;
//			try {
//				amount = new BigDecimal(amountString);
//			} catch (NumberFormatException e) {
//				Toast.makeText(DonateActivity.this, "Amount is not valid number.", Toast.LENGTH_SHORT).show();
//				launchPayPalButton.updateButton();
//				return;
//			}
//			
//			if(amount.compareTo(BigDecimal.ZERO) <= 0) {
//				Toast.makeText(DonateActivity.this, "Negative amount. Really? :-)", Toast.LENGTH_SHORT).show();
//				launchPayPalButton.updateButton();
//				return;
//			}
//			
//			PayPalPayment newPayment = new PayPalPayment();
//			newPayment.setSubtotal(amount);
//			newPayment.setCurrencyType(Currency.getInstance("USD"));
////			newPayment.setRecipient("antoniy@chonkov.net");
//			newPayment.setRecipient("antoni_1337973361_biz@gmail.com");
//			newPayment.setMerchantName("Gidder");
//			newPayment.setMemo("Donation for Gidder project");
//			Intent paypalIntent = PayPal.getInstance().checkout(newPayment, DonateActivity.this);
//			startActivityForResult(paypalIntent, request);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == android.R.id.home) {
			Intent intent = new Intent(C.action.START_HOME_ACTIVITY);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			
			finish();
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}
	
//	@Override
//	protected void onResume() {
//		super.onResume();
//		registerReceiver(leaveBroadcastReceiver, new IntentFilter(C.action.LEAVE_DONATE_ACTIVITY));
//	}
//	
//	@Override
//	protected void onPause() {
//		super.onPause();
//		unregisterReceiver(leaveBroadcastReceiver);
//	}

}
