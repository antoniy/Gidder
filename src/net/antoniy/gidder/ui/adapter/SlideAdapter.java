package net.antoniy.gidder.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import com.viewpagerindicator.TitleProvider;

import net.antoniy.gidder.ui.fragment.BaseFragment;
import net.antoniy.gidder.ui.fragment.FragmentFactory;
import net.antoniy.gidder.ui.fragment.FragmentType;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SlideAdapter extends FragmentPagerAdapter implements TitleProvider {
	private FragmentType[] fragmentTypes;
	private List<BaseFragment> fragments;
	private int count;
	
	public SlideAdapter(FragmentManager fm, FragmentType[] fragmentTypes) {
		super(fm);
		
		this.fragmentTypes = fragmentTypes;
		this.count = fragmentTypes.length;
		
		fragments = new ArrayList<BaseFragment>(fragmentTypes.length);
		for (FragmentType fragmentType : fragmentTypes) {
			fragments.add(FragmentFactory.createFragment(fragmentType));
		}
	}

	@Override
	public Fragment getItem(int position) {
		return fragments.get(position % fragments.size());
//		return FragmentFactory.createFragment(fragmentTypes[position % fragmentTypes.length]);
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
		return fragmentTypes[position % fragmentTypes.length].getTitle();
	}
	
	public List<BaseFragment> getFragments() {
		return fragments;
	}

}
