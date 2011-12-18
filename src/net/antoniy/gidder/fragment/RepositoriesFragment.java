package net.antoniy.gidder.fragment;

import java.sql.SQLException;
import java.util.List;

import net.antoniy.gidder.R;
import net.antoniy.gidder.activity.AddRepositoryActivity;
import net.antoniy.gidder.adapter.RepositoryAdapter;
import net.antoniy.gidder.db.entity.Repository;
import net.antoniy.gidder.popup.RepositoryActionsPopupWindow;
import net.antoniy.gidder.popup.OnActionItemClickListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

public class RepositoriesFragment extends BaseFragment implements OnClickListener, OnItemLongClickListener, OnActionItemClickListener {
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
		repositoriesListView.setOnItemLongClickListener(this);
		
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
			updateRepositoriesList();
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void updateRepositoriesList() {
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

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		RepositoryActionsPopupWindow popup = new RepositoryActionsPopupWindow(view, position);
		popup.showLikeQuickAction();
		popup.addOnActionItemClickListener(this);
		
		return true;
	}

	@Override
	public void onActionItemClick(View v, int position, int resultCode) {
		if(resultCode == RepositoryActionsPopupWindow.RESULT_EDIT) {
			Repository repository = repositoriesListAdapter.getItem(position);
			
			Intent intent = new Intent(getActivity(), AddRepositoryActivity.class);
			intent.putExtra("repositoryId", repository.getId());
			startActivityForResult(intent, AddRepositoryActivity.REQUEST_CODE_EDIT_REPOSITORY);
		} else if(resultCode == RepositoryActionsPopupWindow.RESULT_DELETE) {
			final Repository repository = repositoriesListAdapter.getItem(position);
			
			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
			        if(which == DialogInterface.BUTTON_POSITIVE) {
			            try {
							getHelper().getRepositoryDao().deleteById(repository.getId());
							updateRepositoriesList();
						} catch (SQLException e) {
							Log.e(TAG, "Problem while deleting repository.", e);
						}
			        }
			    }
			};

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage("Delete " + repository.getName() + "?").setPositiveButton("Yes", dialogClickListener)
			    .setNegativeButton("No", null).show();
		}
	}
}