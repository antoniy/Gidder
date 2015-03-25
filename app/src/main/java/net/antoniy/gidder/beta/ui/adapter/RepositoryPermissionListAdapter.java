package net.antoniy.gidder.beta.ui.adapter;

import android.content.Context;

import net.antoniy.gidder.beta.db.entity.Permission;

import java.util.List;

public class RepositoryPermissionListAdapter extends BasePermissionListAdapter {

	public RepositoryPermissionListAdapter(Context context, List<Permission> items, int resourceIconPull, int resourceIconPushPull) {
		super(context, items, resourceIconPull, resourceIconPushPull);
	}

	@Override
	protected String getItemName(int position) {
		return items.get(position).getRepository().getName();
	}

}
