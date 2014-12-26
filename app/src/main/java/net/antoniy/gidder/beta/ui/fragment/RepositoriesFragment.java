package net.antoniy.gidder.beta.ui.fragment;

import java.sql.SQLException;
import java.util.List;

import net.antoniy.gidder.beta.R;
import net.antoniy.gidder.beta.db.entity.Repository;
import net.antoniy.gidder.beta.git.GitRepositoryDao;
import net.antoniy.gidder.beta.ui.activity.AddRepositoryActivity;
import net.antoniy.gidder.beta.ui.adapter.RepositoryAdapter;
import net.antoniy.gidder.beta.ui.util.C;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class RepositoriesFragment extends BaseFragment implements OnItemLongClickListener, OnItemClickListener, PopupWindow.OnDismissListener {
	private final static String TAG = RepositoriesFragment.class.getSimpleName();

	private ListView repositoriesListView;
	private RepositoryAdapter repositoriesListAdapter;
	private TextView noRepositoriesTextView;
	private AlertDialog confirmDialog;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LinearLayout mainContainer = (LinearLayout) inflater.inflate(R.layout.repositories, null);

		noRepositoriesTextView = (TextView) mainContainer.findViewById(R.id.repositoriesNoRepositoriesTextView);
		
		repositoriesListView = (ListView) mainContainer.findViewById(R.id.repositoriesListView);
		loadRepositoriesListContent();
		repositoriesListView.setOnItemLongClickListener(this);
		repositoriesListView.setOnItemClickListener(this);

		return mainContainer;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		loadRepositoriesListContent();
	}
	
	@Override
	public void onPause() {
		if(confirmDialog != null) {
			confirmDialog.dismiss();
		}
		
		super.onPause();
	}

	private void showRepositoryList(boolean show) {
		if(show) {
			repositoriesListView.setVisibility(View.VISIBLE);
			noRepositoriesTextView.setVisibility(View.GONE);
		} else {
			repositoriesListView.setVisibility(View.GONE);
			noRepositoriesTextView.setVisibility(View.VISIBLE);
		}
	}
	
	private void loadRepositoriesListContent() {
		List<Repository> repositories = null;
		try {
			repositories = getHelper().getRepositoryDao().queryForAll();
		} catch (SQLException e) {
			Log.e(TAG, "Could not retrieve repositories.", e);
			return;
		}
		
		showRepositoryList(repositories.size() > 0);

		repositoriesListAdapter = new RepositoryAdapter(getActivity(), R.layout.repositories_item, repositories);
		repositoriesListView.setAdapter(repositoriesListAdapter);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
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
		
		showRepositoryList(repositories.size() > 0);

		repositoriesListAdapter.setItems(repositories);
		repositoriesListAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		actionMode = getSherlockActivity().startActionMode(new UserListActionMode(position));

		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Repository repository = repositoriesListAdapter.getItem(position);

		Intent intent = new Intent(C.action.START_REPOSITORY_DETAILS);
		intent.putExtra("repositoryId", repository.getId());

		startActivityForResult(intent, 0);
	}
	
	@Override
	public void onDismiss() {
	}
	
	private final class UserListActionMode implements ActionMode.Callback {

		private final int position;

		public UserListActionMode(int position) {
			this.position = position;
		}
		
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			mode.finish();
			return true;
		}

		@Override
		public boolean onCreateActionMode(final ActionMode mode, Menu menu) {
			final Repository repository = repositoriesListAdapter.getItem(position);
			mode.setTitle(repository.getName());
			
			menu.add("Delete")
				.setIcon(R.drawable.ic_actionbar_delete)
            	.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
            	.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
					
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
						    @Override
						    public void onClick(DialogInterface dialog, int which) {
						        if(which == DialogInterface.BUTTON_POSITIVE) {
						            try {
						            	new GitRepositoryDao(getActivity()).deleteRepository(repository.getMapping());
										getHelper().getRepositoryDao().deleteById(repository.getId());
										updateRepositoriesList();
									} catch (SQLException e) {
										Log.e(TAG, "Problem while deleting repository.", e);
									}
						        }
						    }
						};

						AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
						confirmDialog = builder.setMessage("Delete " + repository.getName() + "?").setPositiveButton("Yes", dialogClickListener)
						    .setNegativeButton("No", null).show();
						
						mode.finish();
						return true;
					}
					
				});
			
			if(!repository.isActive()) {
				menu.add("Activate")
	        		.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
	        		.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
						
						@Override
						public boolean onMenuItemClick(MenuItem item) {
							repository.setActive(true);
							
							try {
								getHelper().getRepositoryDao().update(repository);
								updateRepositoriesList();
							} catch (SQLException e) {
								Log.e(TAG, "Problem while activating repository.", e);
							}
							
							mode.finish();
							return true;
						}
						
					});
			} else {
				menu.add("Deactivate")
	        		.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
	        		.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
						
						@Override
						public boolean onMenuItemClick(MenuItem item) {
							repository.setActive(false);
							
							try {
								getHelper().getRepositoryDao().update(repository);
								updateRepositoriesList();
							} catch (SQLException e) {
								Log.e(TAG, "Problem while deactivating repository.", e);
							}
							
							mode.finish();
							return true;
						}
						
					});
			}
			
			menu.add("Edit")
			.setIcon(R.drawable.ic_actionbar_edit)
			.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
			.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
				
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					Intent intent = new Intent(C.action.START_ADD_REPOSITORY_ACTIVITY);
					intent.putExtra("repositoryId", repository.getId());
					startActivityForResult(intent, AddRepositoryActivity.REQUEST_CODE_EDIT_REPOSITORY);
					
					mode.finish();
					return true;
				}
				
			});
			
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
		}
	}

}