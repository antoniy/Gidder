package net.antoniy.gidder.ui.activity;

import net.antoniy.gidder.R;
import net.antoniy.gidder.ui.util.C;
import android.content.Intent;
import android.os.Bundle;

public class SplashScreenActivity extends BaseActivity {

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
	               while (waited < 2000) {
	                  sleep(100);
	                  waited += 100;
	               }
	            } catch (InterruptedException e) {
	               // do nothing
	            } finally {
	               finish();
	               Intent i = new Intent(C.action.START_HOME_ACTIVITY);
	               startActivity(i);
	            }
	         }
	      };
	      splashThread.start();
	}

}
