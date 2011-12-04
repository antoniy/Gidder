package net.antoniy.gidder.adapter;

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

public class MainMenuAdapter extends BaseAdapter {

	private Context context;
	private List<MainMenuItem> model;
	
	public MainMenuAdapter(Context context) {
		this.context = context;
		
		model = new ArrayList<MainMenuItem>();
		model.add(new MainMenuItem("Users", R.drawable.users_selector, MenuItemType.USERS));
		model.add(new MainMenuItem("Repositories", R.drawable.repositories_selector, MenuItemType.REPOSITORIES));
		model.add(new MainMenuItem("Permissions", R.drawable.permissions_selector, MenuItemType.PERMISSIONS));
		model.add(new MainMenuItem("Settings", R.drawable.settings_selector, MenuItemType.SETTINGS));
	}

	@Override
	public int getCount() {
		return model.size();
	}

	@Override
	public Object getItem(int position) {
		return model.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v;
		if(convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = (LinearLayout) inflater.inflate(R.layout.main_menu_item, null);
		} else {
			v = convertView;
		}
		
		ImageView itemImage = (ImageView) v.findViewById(R.id.mainMenuItemImg);
		itemImage.setImageResource(model.get(position).getImage());
		
		TextView itemText = (TextView) v.findViewById(R.id.mainMenuItemText);
		itemText.setText(model.get(position).getTitle());
		
		return v;
	}
	
	public enum MenuItemType {
		USERS, REPOSITORIES, PERMISSIONS, SETTINGS;
	}

	public class MainMenuItem {
		private String title;
		private int image;
		private MenuItemType type;
		
		public MainMenuItem(String title, int image, MenuItemType type) {
			this.image = image;
			this.title = title;
			this.type = type;
		}

		public int getImage() {
			return image;
		}

		public void setImage(int image) {
			this.image = image;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public MenuItemType getType() {
			return type;
		}

		public void setType(MenuItemType type) {
			this.type = type;
		}
		
	}
}
