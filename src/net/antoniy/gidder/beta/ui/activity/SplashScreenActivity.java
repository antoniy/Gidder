package net.antoniy.gidder.beta.ui.activity;

import net.antoniy.gidder.beta.R;
import net.antoniy.gidder.beta.ui.util.C;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.actionbarsherlock.view.Window;

public class SplashScreenActivity extends BaseActivity {

	private Handler handler;
	private Runnable timeoutRunnable = new Runnable() {
        public void run() {
        	Intent intent = new Intent(C.action.START_HOME_ACTIVITY);

        	SplashScreenActivity.this.startActivity(intent);
        	SplashScreenActivity.this.finish();
        }
    };
	
	@Override
	protected void setup() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash_screen);
	}

	@Override
	protected void initComponents(Bundle savedInstanceState) {
		handler = new Handler();
		handler.postDelayed(timeoutRunnable, 3L * 1000L);
		
		View donateButton = findViewById(R.id.splashScreenDonateButton);
		donateButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		
		if(v.getId() == R.id.splashScreenDonateButton) {
			handler.removeCallbacks(timeoutRunnable);
			finish();
			startActivity(new Intent(C.action.START_DONATE_ACTIVITY));
		}
	}
}
