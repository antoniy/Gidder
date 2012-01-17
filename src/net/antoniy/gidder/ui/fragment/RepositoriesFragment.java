package net.antoniy.gidder.ui.fragment;

import java.sql.SQLException;
import java.util.List;

import net.antoniy.gidder.R;
import net.antoniy.gidder.db.entity.Repository;
import net.antoniy.gidder.git.GitRepositoryDao;
import net.antoniy.gidder.ui.activity.AddRepositoryActivity;
import net.antoniy.gidder.ui.activity.RepositoryPermissionsActivity;
import net.antoniy.gidder.ui.adapter.RepositoryAdapter;
import net.antoniy.gidder.ui.quickactions.ActionItem;
import net.antoniy.gidder.ui.quickactions.QuickAction;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

public class RepositoriesFragment extends BaseFragment implements OnClickListener, OnItemLongClickListener, OnItemClickListener, QuickAction.OnActionItemClickListener, PopupWindow.OnDismissListener {
	private final static String TAG = RepositoriesFragment.class.getSimpleName();
	private final static String INTENT_ACTION_START_ADD_REPOSITORY = "net.antoniy.gidder.START_ADD_REPOSITORY_ACTIVITY";

	private final static int QUICK_ACTION_EDIT = 1;
	private final static int QUICK_ACTION_DELETE = 2;
	
	private Button addButton;
	private ListView repositoriesListView;
	private RepositoryAdapter repositoriesListAdapter;
	private QuickAction quickAction;
	private int selectedRow;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LinearLayout mainContainer = (LinearLayout) inflater.inflate(R.layout.repositories, null);

		addButton = (Button) mainContainer.findViewById(R.id.repositoriesAddButton);
		addButton.setOnClickListener(this);

		repositoriesListView = (ListView) mainContainer.findViewById(R.id.repositoriesListView);
		loadRepositoriesListContent();
		repositoriesListView.setOnItemLongClickListener(this);
		repositoriesListView.setOnItemClickListener(this);

		ActionItem editItem = new ActionItem(1, "Edit", getResources().getDrawable(R.drawable.ic_action_edit));
		ActionItem deleteItem = new ActionItem(2, "Delete", getResources().getDrawable(R.drawable.ic_action_delete));
		
		quickAction = new QuickAction(getActivity());
		quickAction.setOnActionItemClickListener(this);
		quickAction.setOnDismissListener(this);
		
		quickAction.addActionItem(editItem);
		quickAction.addActionItem(deleteItem);
		
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
		if (v.getId() == R.id.repositoriesAddButton) {
			Intent intent = new Intent(INTENT_ACTION_START_ADD_REPOSITORY);
			startActivityForResult(intent, AddRepositoryActivity.REQUEST_CODE_ADD_REPOSITORY);
		}
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

		repositoriesListAdapter.setItems(repositories);
		repositoriesListAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		selectedRow = position;
		quickAction.show(view);

		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Repository repository = repositoriesListAdapter.getItem(position);

		Intent intent = new Intent(getActivity(), RepositoryPermissionsActivity.class);
		intent.putExtra("repositoryId", repository.getId());

		startActivityForResult(intent, 0);
	}
	
	@Override
	public void onItemClick(QuickAction source, int pos, int actionId) {
		final Repository repository = repositoriesListAdapter.getItem(selectedRow);
		
		if(actionId == QUICK_ACTION_EDIT) {
			Intent intent = new Intent(getActivity(), AddRepositoryActivity.class);
			intent.putExtra("repositoryId", repository.getId());
			startActivityForResult(intent, AddRepositoryActivity.REQUEST_CODE_EDIT_REPOSITORY);
		} else if(actionId == QUICK_ACTION_DELETE) {
			
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
			builder.setMessage("Delete " + repository.getName() + "?").setPositiveButton("Yes", dialogClickListener)
			    .setNegativeButton("No", null).show();
		}
	}

	@Override
	public void onDismiss() {
		// TODO Auto-generated method stub
	}

}