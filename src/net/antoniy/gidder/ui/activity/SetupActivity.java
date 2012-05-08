package net.antoniy.gidder.ui.activity;

import java.util.ArrayList;
import java.util.List;

import net.antoniy.gidder.R;
import net.antoniy.gidder.ui.fragment.BaseFragment;
import net.antoniy.gidder.ui.fragment.FragmentFactory;
import net.antoniy.gidder.ui.util.C;
import net.antoniy.gidder.ui.util.FragmentType;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

public class SetupActivity extends SherlockFragmentActivity {

    private ViewPager viewPager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		setTheme(com.actionbarsherlock.R.style.Theme_Sherlock_ForceOverflow_DarkActionBar_ForceOverflow);
		setTheme(R.style.Theme_Sherlock);
		super.onCreate(savedInstanceState);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		setContentView(R.layout.setup);
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        viewPager = (ViewPager)findViewById(R.id.pager);

        new SetupAdapter(this, viewPager);

//        if (savedInstanceState != null) {
//            tabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
//        }
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
	
	public static class SetupAdapter extends FragmentPagerAdapter 
			implements ViewPager.OnPageChangeListener, ActionBar.TabListener {

		private List<BaseFragment> fragments;
		private final ViewPager pager;
		private final SherlockFragmentActivity activity;
		
		public SetupAdapter(SherlockFragmentActivity activity, ViewPager pager) {
			super(activity.getSupportFragmentManager());
			this.activity = activity;
			
			this.pager = pager;
			this.pager.setAdapter(this);
			this.pager.setOnPageChangeListener(this);
			
			FragmentType[] fragmentTypes = FragmentType.values();
			fragments = new ArrayList<BaseFragment>(fragmentTypes.length);
			for (FragmentType fragmentType : fragmentTypes) {
				fragments.add(FragmentFactory.createFragment(fragmentType));

				ActionBar.Tab tab = activity.getSupportActionBar().newTab();
				tab.setTabListener(this);
				tab.setText(fragmentType.getTitle());
				activity.getSupportActionBar().addTab(tab);
			}
		}

		@Override
		public Fragment getItem(int position) {
			return fragments.get(position % fragments.size());
//			return FragmentFactory.createFragment(fragmentTypes[position % fragmentTypes.length]);
		}

		@Override
		public int getCount() {
			return fragments.size();
		}

		@Override
		public void onPageScrollStateChanged(int state) {
		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		}

		@Override
		public void onPageSelected(int position) {
			activity.getSupportActionBar().setSelectedNavigationItem(position);
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction transaction) {
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction transaction) {
			pager.setCurrentItem(tab.getPosition());
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction transaction) {
		}
		
	}
}
