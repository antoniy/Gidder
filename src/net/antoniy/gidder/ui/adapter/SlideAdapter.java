package net.antoniy.gidder.ui.adapter;

import com.viewpagerindicator.TitleProvider;

import net.antoniy.gidder.ui.fragment.FragmentFactory;
import net.antoniy.gidder.ui.fragment.FragmentType;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SlideAdapter extends FragmentPagerAdapter implements TitleProvider {
	private FragmentType[] content;
	private int count;
	
	public SlideAdapter(FragmentManager fm, FragmentType[] content) {
		super(fm);
		
		this.content = content;
		this.count = content.length;
	}

	@Override
	public Fragment getItem(int position) {
		return FragmentFactory.createFragment(content[position % content.length]);
	}

	@Override
	public int getCount() {
		return count;
	}
	
	public void setCount(int count) {
		if (count > 0 && count <= 10) {
			this.count = count;
			
			notifyDataSetChanged();
		}
	}

	@Override
	public String getTitle(int position) {
		return content[position % content.length].getTitle();
	}

}
