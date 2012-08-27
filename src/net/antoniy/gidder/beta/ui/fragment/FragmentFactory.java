package net.antoniy.gidder.beta.ui.fragment;

import net.antoniy.gidder.beta.ui.util.FragmentType;

public final class FragmentFactory {
	public static BaseFragment createFragment(FragmentType fragmentType) {
		switch (fragmentType) {
		case USERS:
			return new UsersFragment();
		case REPOSITORIES:
			return new RepositoriesFragment();
		}
		
		return null;
	}
}
