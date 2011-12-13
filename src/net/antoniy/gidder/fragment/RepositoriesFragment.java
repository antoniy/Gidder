package net.antoniy.gidder.fragment;

import java.sql.SQLException;
import java.util.List;

import net.antoniy.gidder.R;
import net.antoniy.gidder.activity.AddRepositoryActivity;
import net.antoniy.gidder.activity.SlideActivity;
import net.antoniy.gidder.adapter.RepositoryAdapter;
import net.antoniy.gidder.db.entity.Repository;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

public class RepositoriesFragment extends ContextMenuFragment implements OnClickListener {
	private final static String TAG = RepositoriesFragment.class.getSimpleName();
	private final static String INTENT_ACTION_START_ADD_REPOSITORY = "net.antoniy.gidder.START_ADD_REPOSITORY_ACTIVITY";
	
	private Button addButton;
	private ListView repositoriesListView;
	private RepositoryAdapter repositoriesListAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LinearLayout mainContainer = (LinearLayout) inflater.inflate(R.layout.repositories, null);

		addButton = (Button) mainContainer.findViewById(R.id.repositoriesAddButton);
		addButton.setOnClickListener(this);
		
		repositoriesListView = (ListView) mainContainer.findViewById(R.id.repositoriesListView);
		loadRepositoriesListContent();
		registerForContextMenu(repositoriesListView);
		
		return mainContainer;
	}
	
	private void loadRepositoriesListContent() {
		List<Repository> repositories = null;
		try {
			repositories = getHelper().getRepositoryDao().queryForAll();
		} catch (SQLException e) {
			Log.e(TAG, "Could not retrieve repositories.", e);
			return;
		}
		
		repositoriesListAdapter = new RepositoryAdapter(getActivity(), R.layout.repositories_item, repositories);
		repositoriesListView.setAdapter(repositoriesListAdapter);
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.repositoriesAddButton) {
			Intent intent = new Intent(INTENT_ACTION_START_ADD_REPOSITORY);
			startActivityForResult(intent, AddRepositoryActivity.REQUEST_CODE_ADD_REPOSITORY);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Activity.RESULT_OK) {
			Log.i(TAG, "Refreshing repositories...");
			List<Repository> repositories = null;
			try {
				repositories = getHelper().getRepositoryDao().queryForAll();
			} catch (SQLException e) {
				Log.e(TAG, "Could not retrieve repositories.", e);
				return;
			}
			
			repositoriesListAdapter.setItems(repositories);
			repositoriesListAdapter.notifyDataSetChanged();
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if(((SlideActivity)getActivity()).getCurrentFragment() != FragmentType.REPOSITORIES) {
			return false;
		}
		
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		
		Log.i(TAG, "Long selection: " + info.position);
		
		Repository repository = repositoriesListAdapter.getItem(info.position);
		
		switch (item.getItemId()) {
		case CONTEXT_MENU_ITEM_EDIT:
			Intent intent = new Intent(getActivity(), AddRepositoryActivity.class);
			intent.putExtra("repositoryId", repository.getId());
			startActivityForResult(intent, AddRepositoryActivity.REQUEST_CODE_EDIT_REPOSITORY);
			break;
		case CONTEXT_MENU_ITEM_REMOVE:
			
			break;
		}
		
		return super.onContextItemSelected(item);
	}

	@Override
	protected void onContextMenuEditItemSelected(int position) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onContextMenuRemoveItemSelected(int position) {
		// TODO Auto-generated method stub
		
	}
}