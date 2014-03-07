package net.antoniy.gidder.beta.ui.adapter;

import java.util.List;

import net.antoniy.gidder.beta.R;
import net.antoniy.gidder.beta.db.entity.User;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UsersAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<User> items;
	private final int itemResourceId;

	public UsersAdapter(Context context, int textViewResourceId, List<User> items) {
		this.itemResourceId = textViewResourceId;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.items = items;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v;
		if(convertView == null) {
			v = (LinearLayout) inflater.inflate(itemResourceId, null);
		} else {
			v = convertView;
		}
		
		User user = items.get(position);
		
		ImageView userImage = (ImageView) v.findViewById(R.id.usersItemImage);
		if(user.isActive()) {
			userImage.setImageResource(R.drawable.ic_user_active);
		} else {
			userImage.setImageResource(R.drawable.ic_user_inactive);
		}
		
		TextView userFullname = (TextView) v.findViewById(R.id.usersItemFullname);
		userFullname.setText(user.getFullname());
		
		return v;
	}

	public List<User> getItems() {
		return items;
	}

	public void setItems(List<User> items) {
		this.items = items;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public User getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return items.get(position).getId();
	}

}
