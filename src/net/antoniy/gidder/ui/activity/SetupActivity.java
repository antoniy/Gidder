package net.antoniy.gidder.ui.activity;

import java.util.ArrayList;
import java.util.List;

import net.antoniy.gidder.R;
import net.antoniy.gidder.ui.fragment.BaseFragment;
import net.antoniy.gidder.ui.fragment.FragmentFactory;
import net.antoniy.gidder.ui.util.FragmentType;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabWidget;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class SetupActivity extends SherlockFragmentActivity {

	private TabHost tabHost;
    private ViewPager  viewPager;
//    private SetupAdapter setupAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		setTheme(com.actionbarsherlock.R.style.Theme_Sherlock_ForceOverflow_DarkActionBar_ForceOverflow);
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.setup);
        tabHost = (TabHost)findViewById(android.R.id.tabhost);
        tabHost.setup();

        viewPager = (ViewPager)findViewById(R.id.pager);

//        SetupAdapter setupAdapter = 
        new SetupAdapter(this, tabHost, viewPager);

        if (savedInstanceState != null) {
            tabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
        }
	}
	
	static class DummyTabFactory implements TabHost.TabContentFactory {
        private final Context context;

        public DummyTabFactory(Context context) {
            this.context = context;
        }

        @Override
        public View createTabContent(String tag) {
            View v = new View(context);
            v.setMinimumWidth(0);
            v.setMinimumHeight(0);
            return v;
        }
    }
	
	public static class SetupAdapter extends FragmentPagerAdapter 
			implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {

		private List<BaseFragment> fragments;
		private final TabHost tabHost;
		private final ViewPager pager;
		private final Context context;
		
		public SetupAdapter(FragmentActivity activity, TabHost tabHost, ViewPager pager) {
			super(activity.getSupportFragmentManager());
			this.context = activity;
			
			this.tabHost = tabHost;
			this.tabHost.setOnTabChangedListener(this);
			this.pager = pager;
			this.pager.setAdapter(this);
			this.pager.setOnPageChangeListener(this);
			
			FragmentType[] fragmentTypes = FragmentType.values();
			fragments = new ArrayList<BaseFragment>(fragmentTypes.length);
			for (FragmentType fragmentType : fragmentTypes) {
				fragments.add(FragmentFactory.createFragment(fragmentType));
				tabHost.addTab(tabHost.newTabSpec(fragmentType.getTitle())
						.setIndicator(fragmentType.getTitle())
						.setContent(new DummyTabFactory(context)));
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
			// Unfortunately when TabHost changes the current tab, it kindly
            // also takes care of putting focus on it when not in touch mode.
            // The jerk.
            // This hack tries to prevent this from pulling focus out of our
            // ViewPager.
            TabWidget widget = tabHost.getTabWidget();
            int oldFocusability = widget.getDescendantFocusability();
            widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            tabHost.setCurrentTab(position);
            widget.setDescendantFocusability(oldFocusability);
		}

		@Override
		public void onTabChanged(String tabId) {
			int position = tabHost.getCurrentTab();
            pager.setCurrentItem(position);
		}
		
	}
}
