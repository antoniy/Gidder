package net.antoniy.gidder.beta.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import net.antoniy.gidder.beta.R;
import net.antoniy.gidder.beta.ui.popup.OnPermissionListItemClickListener;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class BasePopupListAdapter<T> extends BaseAdapter implements OnClickListener {

	private LayoutInflater inflater;
	private List<OnPermissionListItemClickListener> listeners;
	private final int resourceIconPullIcon;
	private final int resourceIconPushPullIcon;
	
    public BasePopupListAdapter(Context context, int resourceIconPullIcon, int resourceIconPushPullIcon) {
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.listeners = new ArrayList<OnPermissionListItemClickListener>();
		this.resourceIconPullIcon = resourceIconPullIcon;
		this.resourceIconPushPullIcon = resourceIconPushPullIcon;
	}
    
    protected abstract List<T> getData();
    
    protected abstract void setData(List<T> data);
    
    protected abstract int getDataItemId(int position);
    
    protected abstract String getDataItemName(int position);
	
	@Override
	public int getCount() {
		return getData().size();
	}

	@Override
	public T getItem(int position) {
		return getData().get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v;
		if(convertView == null) {
			v = (LinearLayout) inflater.inflate(R.layout.popup_list_item, null);
		} else {
			v = convertView;
		}
		
		ImageView buttonPull = (ImageView) v.findViewById(R.id.popupListItemBtnPull);
		buttonPull.setImageResource(resourceIconPullIcon);
		buttonPull.setOnClickListener(this);
		buttonPull.setTag(getDataItemId(position));
		
		ImageView buttonPullPush = (ImageView) v.findViewById(R.id.popupListItemBtnPullPush);
		buttonPullPush.setImageResource(resourceIconPushPullIcon);
		buttonPullPush.setOnClickListener(this);
		buttonPullPush.setTag(getDataItemId(position));
		
		TextView itemName = (TextView) v.findViewById(R.id.popupListItemName);
		itemName.setText(getDataItemName(position));
		
		return v;
	}

	public List<T> getItems() {
		return getData();
	}

	public void setItems(List<T> items) {
		setData(items);
	}
	
	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		
		if(id == R.id.popupListItemBtnPull) {
			int itemId = (Integer) v.getTag();
			notifyOnPermissionListItemClickListeners(itemId, true);
		} else if (id == R.id.popupListItemBtnPullPush) {
			int itemId = (Integer) v.getTag();
			notifyOnPermissionListItemClickListeners(itemId, false);
		}
	}
	
	public void addOnRepositoryListItemClick(OnPermissionListItemClickListener listener) {
		listeners.add(listener);
	}
	
	private void notifyOnPermissionListItemClickListeners(int repositoryId, boolean readOnlyPermission) {
		for (OnPermissionListItemClickListener listener : listeners) {
			listener.onPermissionItemClick(repositoryId, readOnlyPermission);
		}
	}
}
