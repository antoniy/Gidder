package net.antoniy.gidder.beta.ui.adapter;

import android.content.Context;

import net.antoniy.gidder.beta.db.entity.Repository;

import java.util.List;

public class RepositoriesPopupListAdapter extends BasePopupListAdapter<Repository> {

	private List<Repository> repositories;

	public RepositoriesPopupListAdapter(Context context, List<Repository> repositories, int resourceIconPullIcon, int resourceIconPushPullIcon) {
		super(context, resourceIconPullIcon, resourceIconPushPullIcon);
		this.repositories = repositories;
	}

	@Override
	protected List<Repository> getData() {
		return repositories;
	}

	@Override
	protected void setData(List<Repository> data) {
		this.repositories = data;
	}

	@Override
	protected int getDataItemId(int position) {
		return repositories.get(position).getId();
	}

	@Override
	protected String getDataItemName(int position) {
		return repositories.get(position).getName();
	}

}