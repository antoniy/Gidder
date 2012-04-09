package net.antoniy.gidder.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import net.antoniy.gidder.R;
import net.antoniy.gidder.db.entity.Repository;
import net.antoniy.gidder.ui.quickactions.OnRepositoryListItemClickListener;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RepositoryListAdapter extends BaseAdapter implements OnClickListener {

	private LayoutInflater inflater;
	private List<Repository> items;
	private List<OnRepositoryListItemClickListener> listeners;
	
	public RepositoryListAdapter(Context context, List<Repository> items) {
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.items = items;
		this.listeners = new ArrayList<OnRepositoryListItemClickListener>();
	}
	
	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Repository getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return items.get(position).getId();
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v;
		if(convertView == null) {
			v = (LinearLayout) inflater.inflate(R.layout.repository_list_item, null);
		} else {
			v = convertView;
		}
		
		Repository repository = items.get(position);

		TextView buttonPull = (TextView) v.findViewById(R.id.repositoryListItemBtnPull);
		buttonPull.setOnClickListener(this);
		buttonPull.setTag(repository.getId());
		
		TextView buttonPullPush = (TextView) v.findViewById(R.id.repositoryListItemBtnPullPush);
		buttonPullPush.setOnClickListener(this);
		buttonPullPush.setTag(repository.getId());
		
		TextView repositoryName = (TextView) v.findViewById(R.id.repositoryListItemName);
		repositoryName.setText(repository.getName());
		
		return v;
	}

	public List<Repository> getItems() {
		return items;
	}

	public void setItems(List<Repository> items) {
		this.items = items;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		
		if(id == R.id.repositoryListItemBtnPull) {
			int repositoryId = (Integer) v.getTag();
			notifyOnRepositoryListItemClickListeners(repositoryId, true);
		} else if (id == R.id.repositoryListItemBtnPullPush) {
			int repositoryId = (Integer) v.getTag();
			notifyOnRepositoryListItemClickListeners(repositoryId, false);
		}
	}
	
	public void addOnRepositoryListItemClick(OnRepositoryListItemClickListener listener) {
		listeners.add(listener);
	}
	
	private void notifyOnRepositoryListItemClickListeners(int repositoryId, boolean readOnlyPermission) {
		for (OnRepositoryListItemClickListener listener : listeners) {
			listener.onRepositoryPermissionItemClick(repositoryId, readOnlyPermission);
		}
	}
}
