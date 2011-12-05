package net.antoniy.gidder.activity;

import net.antoniy.gidder.R;
import android.content.Intent;
import android.os.Bundle;

public class SplashScreenActivity extends BaseActivity {
	private final static String INTENT_ACTION_START_MAIN_MENU = "net.antoniy.gidder.START_MAIN_MENU_ACTIVITY";
	
	@Override
	protected void setup() {
		setContentView(R.layout.splash_screen);
	}

	@Override
	protected void initComponents(Bundle savedInstanceState) {
		Thread splashThread = new Thread() {
	         @Override
	         public void run() {
	            try {
	               int waited = 0;
	               while (waited < 3000) {
	                  sleep(100);
	                  waited += 100;
	               }
	            } catch (InterruptedException e) {
	               // do nothing
	            } finally {
	               finish();
	               Intent i = new Intent(INTENT_ACTION_START_MAIN_MENU);
	               startActivity(i);
	            }
	         }
	      };
	      splashThread.start();
	}

}
