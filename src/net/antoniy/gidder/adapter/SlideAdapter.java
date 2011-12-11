package net.antoniy.gidder.adapter;

import com.viewpagerindicator.TitleProvider;

import net.antoniy.gidder.fragment.FragmentFactory;
import net.antoniy.gidder.fragment.FragmentType;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SlideAdapter extends FragmentPagerAdapter implements TitleProvider {
	protected static final FragmentType[] CONTENT = new FragmentType[] { FragmentType.USERS, FragmentType.REPOSITORIES };
	
	private int mCount = CONTENT.length;

	public SlideAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		return FragmentFactory.createFragment(CONTENT[position % CONTENT.length]);
	}

	@Override
	public int getCount() {
		return mCount;
	}
	
	public void setCount(int count) {
		if (count > 0 && count <= 10) {
			mCount = count;
			notifyDataSetChanged();
		}
	}

	@Override
	public String getTitle(int position) {
		return CONTENT[position % CONTENT.length].getTitle();
	}

}
