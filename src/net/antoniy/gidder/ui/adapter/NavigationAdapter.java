package net.antoniy.gidder.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import net.antoniy.gidder.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NavigationAdapter extends BaseAdapter {
	private final Context context;
	private List<NavigationItem> model;

	public NavigationAdapter(final Context context) {
		this.context = context;

		model = new ArrayList<NavigationAdapter.NavigationItem>();
		model.add(new NavigationItem(NavigationItemType.SETUP, "Setup", R.drawable.ic_nav_setup));
		model.add(new NavigationItem(NavigationItemType.DNS, "DNS", R.drawable.ic_nav_setup));
		model.add(new NavigationItem(NavigationItemType.LOGS, "Logs", R.drawable.ic_nav_setup));
	}

	@Override
	public int getCount() {
		return model.size();
	}

	@Override
	public NavigationItem getItem(int position) {
		return model.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View v;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = (LinearLayout) inflater.inflate(R.layout.navigation_item, null);
		} else {
			v = convertView;
		}

		ImageView itemImage = (ImageView) v.findViewById(R.id.navigationItemImage);
		itemImage.setImageResource(model.get(position).getDrawableId());

		TextView itemText = (TextView) v.findViewById(R.id.navigationItemTitle);
		itemText.setText(model.get(position).getTitle());

		return v;
	}
	
	public enum NavigationItemType {
		SETUP, DNS, LOGS;
	}

	public class NavigationItem {
		private final int drawableId;
		private final String title;
		private final NavigationItemType type;

		NavigationItem(final NavigationItemType type, final String title, final int drawableId) {
			this.drawableId = drawableId;
			this.title = title;
			this.type = type;
		}

		public int getDrawableId() {
			return drawableId;
		}

		public String getTitle() {
			return title;
		}

		public NavigationItemType getType() {
			return type;
		}

	}
}
