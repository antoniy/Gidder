package net.antoniy.gidder.ui.adapter;

import java.util.List;

import net.antoniy.gidder.R;
import net.antoniy.gidder.db.entity.Permission;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UserPermissionsAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<Permission> items;

	public UserPermissionsAdapter(Context context, List<Permission> items) {
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.items = items;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v;
		if(convertView == null) {
			v = (LinearLayout) inflater.inflate(R.layout.user_permissions_item, null);
		} else {
			v = convertView;
		}
		
		Permission permission = items.get(position);
		
		ImageView dbIconImageView = (ImageView) v.findViewById(R.id.userPermissionsDbIcon);
		TextView allowLabelTextView = (TextView) v.findViewById(R.id.userPermissionsAllowLabel);
		
		if(permission.isReadOnly()) {
			dbIconImageView.setImageResource(R.drawable.ic_db_pull);
			allowLabelTextView.setText("Allow: pull");
		} else {
			dbIconImageView.setImageResource(R.drawable.ic_db_pull_push);
			allowLabelTextView.setText("Allow: pull/push");
		}
		
		TextView name = (TextView) v.findViewById(R.id.userPermissionsName);
		name.setText(permission.getRepository().getName());
		
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
