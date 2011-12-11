package net.antoniy.gidder.fragment;

import android.support.v4.app.Fragment;

public final class FragmentFactory {
	public static Fragment createFragment(FragmentType fragmentType) {
		switch (fragmentType) {
		case USERS:
			return new UsersFragment();
		case REPOSITORIES:
//			return new TestFragment("Repositories"); 
			return new RepositoriesFragment();
		}
		
		return null;
	}
}
