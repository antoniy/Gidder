package net.antoniy.gidder.beta.ui.adapter;

import java.util.List;

import net.antoniy.gidder.beta.R;
import net.antoniy.gidder.beta.db.entity.Permission;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class BasePermissionListAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	protected List<Permission> items;
	private final int resourceIconPull;
	private final int resourceIconPushPull;

	public BasePermissionListAdapter(Context context, List<Permission> items, int resourceIconPull, int resourceIconPushPull) {
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.items = items;
		this.resourceIconPull = resourceIconPull;
		this.resourceIconPushPull = resourceIconPushPull;
	}
	
	protected abstract String getItemName(int position);
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v;
		if(convertView == null) {
			v = (LinearLayout) inflater.inflate(R.layout.permission_list_item, null);
		} else {
			v = convertView;
		}
		
		Permission permission = items.get(position);
		
		ImageView dbIconImageView = (ImageView) v.findViewById(R.id.permissionListDbIcon);
		TextView allowLabelTextView = (TextView) v.findViewById(R.id.permissionListAllowLabel);
		
		if(permission.isReadOnly()) {
			dbIconImageView.setImageResource(resourceIconPull);
			allowLabelTextView.setText("Allow: pull");
		} else {
			dbIconImageView.setImageResource(resourceIconPushPull);
			allowLabelTextView.setText("Allow: pull/push");
		}
		
		TextView name = (TextView) v.findViewById(R.id.permissionListName);
		name.setText(getItemName(position));
		
		return v;
	}

	public List<Permission> getItems() {
		return items;
	}

	public void setItems(List<Permission> items) {
		this.items = items;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Permission getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return items.get(position).getId();
	}

}
