package net.antoniy.gidder.adapter;

import java.util.List;

import net.antoniy.gidder.R;
import net.antoniy.gidder.db.entity.Permission;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UserPermissionsAdapter extends ArrayAdapter<Permission> implements OnClickListener {
	private final static String TAG = UserPermissionsAdapter.class.getSimpleName();
	
	private LayoutInflater inflater;
	private List<Permission> items;

	private final int itemResourceId;
	
	public UserPermissionsAdapter(Context context, int textViewResourceId, List<Permission> objects) {
		super(context, textViewResourceId, objects);

		this.itemResourceId = textViewResourceId;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.items = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v;
		if(convertView == null) {
			v = (LinearLayout) inflater.inflate(itemResourceId, null);
		} else {
			v = convertView;
		}
		
		Permission permission = items.get(position);
		
		TextView repoName = (TextView) v.findViewById(R.id.userPermissionItemName);
		repoName.setText(permission.getRepository().getName());
		
		ImageView pullImage = (ImageView) v.findViewById(R.id.userPermissionItemPull);
		pullImage.setTag(position);
		pullImage.setOnClickListener(this);
		
		if(permission.isAllowPull()) {
			pullImage.setImageResource(R.drawable.pull_checked);
		} else {
			pullImage.setImageResource(R.drawable.pull_unchecked);
		}
		
		ImageView pushImage = (ImageView) v.findViewById(R.id.userPermissionItemPush);
		pushImage.setTag(position);
		pushImage.setOnClickListener(this);
		
		if(permission.isAllowPush()) {
			pushImage.setImageResource(R.drawable.push_checked);
		} else {
			pushImage.setImageResource(R.drawable.push_unchecked);
		}
		
		return v;
	}

	@Override
	public void onClick(View v) {
		Log.i(TAG, "Item clicked!");
		
		Integer pos = (Integer) v.getTag();
		Permission permission = items.get(pos);

		switch (v.getId()) {
		case R.id.userPermissionItemPull:
			permission.setAllowPull(!permission.isAllowPull());
			break;
		case R.id.userPermissionItemPush:
			permission.setAllowPush(!permission.isAllowPush());
			break;
		}
		
		notifyDataSetChanged();
	}
}
