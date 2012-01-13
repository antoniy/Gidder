package net.antoniy.gidder.ui.activity;

import net.antoniy.gidder.R;
import net.antoniy.gidder.ui.adapter.SlideAdapter;
import net.antoniy.gidder.ui.util.FragmentType;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.IntentAction;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

public class SlideActivity extends FragmentActivity {
	private final static String TAG = SlideActivity.class.getSimpleName();
	
//	private final static FragmentType[] CONTENT = new FragmentType[] { FragmentType.USERS, FragmentType.REPOSITORIES };
	private final static FragmentType[] CONTENT = FragmentType.values();
	
	private SlideAdapter titleAdapter;
	private ViewPager titlePager;
	private PageIndicator titleIndicator;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.slide);
		
		Log.i(TAG, "Creating slide activity...");
		
		titleAdapter = new SlideAdapter(getSupportFragmentManager(), CONTENT);
		
		titlePager = (ViewPager)findViewById(R.id.slidePager);
		titlePager.setAdapter(titleAdapter);
		
		titleIndicator = (TitlePageIndicator)findViewById(R.id.slideIndicator);
		titleIndicator.setViewPager(titlePager);
		titleIndicator.setCurrentItem(titleAdapter.getCount() / 2);
		
		ActionBar actionBar = (ActionBar) findViewById(R.id.slideActionBar);
//		actionBar.setHomeLogo(R.drawable.ic_actionbar_home);
		actionBar.setTitle("Gidder");
        actionBar.setHomeAction(new IntentAction(this, new Intent(this, SlideActivity.class), R.drawable.ic_actionbar_home));
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.addAction(new IntentAction(this, new Intent("net.antoniy.gidder.START_ADD_USER_ACTIVITY"), R.drawable.ic_actionbar_add_user));
        actionBar.addAction(new IntentAction(this, new Intent(this, GidderPreferencesActivity.class), R.drawable.ic_actionbar_settings));
	}
	
	public FragmentType getCurrentFragment() {
		return CONTENT[titlePager.getCurrentItem() % CONTENT.length];
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.slide_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
	    case R.id.slideMenuSettings:
	    	openPreferenceActivity();
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	private void openPreferenceActivity() {
		Intent intent = new Intent(this, GidderPreferencesActivity.class);
		startActivity(intent);
	}
}
