package net.antoniy.gidder.ui.adapter;

import java.util.List;

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
//	private final Context context;
	
	public RepositoryAdapter(Context context, int textViewResourceId, List<Repository> items) {
//		this.context = context;
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
		
//		String mapping = "/" + repository.getMapping() + Constants.DOT_GIT_EXT;
//		TextView repositoryMapping = (TextView) v.findViewById(R.id.repositoriesItemMapping);
//		repositoryMapping.setText(mapping);
//		
//		TextView repositoryDescription = (TextView) v.findViewById(R.id.repositoriesItemDescription);
//		repositoryDescription.setText(repository.getDescription());
//		
//		int userWithPermissionsCount = repository.getPermissions().size();
//		TextView repositoryUserCount = (TextView) v.findViewById(R.id.repositoriesItemUserCount);
//		repositoryUserCount.setText(String.valueOf(userWithPermissionsCount));
		
		// TODO: Remove next lines - they are just for testing.
////		GitRepositoryDao gitRepositoryDao = new GitRepositoryDao(context);
//		try {
//			Git git = Git.open(new File("/sdcard/gidder/repositories/" + mapping));
////			org.eclipse.jgit.lib.Repository gitRepo = gitRepositoryDao.openRepository(mapping.split("\\.")[0]);
//			List<Ref> branches = git.branchList().call();
//			git.push().setReceivePack(RemoteConfig.DEFAULT_RECEIVE_PACK);
//			for (Ref ref : branches) {
//				Log.i("asd", "Branch: " + ref.getName() + ", Flags: " + ref.isPeeled() + ", " + ref.isSymbolic());
//			}
//			
//		} catch (RepositoryNotFoundException e) {
//			Log.e("asd", "Couldn't open repository with mapping: " + mapping, e);
//		} catch (IOException e) {
//			Log.e("asd", "Couldn't open repository with mapping2: " + mapping, e);
//		}
		
		return v;
	}

	public List<Repository> getItems() {
		return items;
	}

	public void setItems(List<Repository> items) {
		this.items = items;
	}
}
