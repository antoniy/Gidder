package net.antoniy.gidder.ui.adapter;

import java.util.List;

import org.eclipse.jgit.lib.Constants;

import net.antoniy.gidder.R;
import net.antoniy.gidder.db.entity.Repository;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RepositoryAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<Repository> items;
	private final int itemResourceId;
	
	public RepositoryAdapter(Context context, int textViewResourceId, List<Repository> items) {
		this.itemResourceId = textViewResourceId;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.items = items;
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
			v = (LinearLayout) inflater.inflate(itemResourceId, null);
		} else {
			v = convertView;
		}
		
		Repository repository = items.get(position);
		
		TextView repositoryName = (TextView) v.findViewById(R.id.repositoriesItemName);
		repositoryName.setText(repository.getName());
		
		String mapping = "/" + repository.getMapping() + Constants.DOT_GIT_EXT;
		TextView repositoryMapping = (TextView) v.findViewById(R.id.repositoriesItemMapping);
		repositoryMapping.setText(mapping);
		
		TextView repositoryDescription = (TextView) v.findViewById(R.id.repositoriesItemDescription);
		repositoryDescription.setText(repository.getDescription());
		
		int userWithPermissionsCount = repository.getPermissions().size();
		TextView repositoryUserCount = (TextView) v.findViewById(R.id.repositoriesItemUserCount);
		repositoryUserCount.setText(String.valueOf(userWithPermissionsCount));
		
		return v;
	}

	public List<Repository> getItems() {
		return items;
	}

	public void setItems(List<Repository> items) {
		this.items = items;
	}
}
