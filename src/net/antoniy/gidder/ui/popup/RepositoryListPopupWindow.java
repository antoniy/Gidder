package net.antoniy.gidder.ui.popup;

import java.util.List;

import net.antoniy.gidder.R;
import net.antoniy.gidder.db.entity.Repository;
import net.antoniy.gidder.ui.adapter.BasePopupListAdapter;
import net.antoniy.gidder.ui.adapter.RepositoriesPopupListAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class RepositoryListPopupWindow extends BasePopupWindow {
	
	public final static int RESULT_UNDEFINED = 0;
	public final static int RESULT_EDIT = 1;
	public final static int RESULT_DELETE = 2;
	public final static int RESULT_ACTIVATE = 3;
	public final static int RESULT_DEACTIVATE = 4;
	
	private int result = RESULT_UNDEFINED;

	private final List<Repository> repositories;

	private final OnPermissionListItemClickListener listener;
	
	public RepositoryListPopupWindow(View anchor, int userId, List<Repository> repositories, OnPermissionListItemClickListener listener, float heightInDp) {
		super(anchor, heightInDp);
		this.repositories = repositories;
		this.listener = listener;
		
		onCreate();
	}

	private void onCreate() {
		// inflate layout
		LayoutInflater inflater = (LayoutInflater) this.anchor.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.popup_list_dialog, null);

		TextView noRepositoriesTextView = (TextView) root.findViewById(R.id.popupListDialogNoRepositories);
		noRepositoriesTextView.setText(R.string.no_repositories);
		
		ListView repositoriesListView = (ListView) root.findViewById(R.id.popupListDialogListView);

		if(repositories.size() > 0) {
			BasePopupListAdapter<Repository> repositoryListAdapter = new RepositoriesPopupListAdapter(anchor.getContext(), repositories, R.drawable.ic_db_pull_small, R.drawable.ic_db_pull_push_small);
			repositoryListAdapter.addOnRepositoryListItemClick(listener);
			repositoryListAdapter.addOnRepositoryListItemClick(new OnPermissionListItemClickListener() {
				@Override
				public void onPermissionItemClick(int entityId, boolean readOnlyPermission) {
					dismiss();
				}
			});
			
			repositoriesListView.setAdapter(repositoryListAdapter);
			
			repositoriesListView.setVisibility(View.VISIBLE);
			noRepositoriesTextView.setVisibility(View.GONE);
		} else {
			repositoriesListView.setVisibility(View.GONE);
			noRepositoriesTextView.setVisibility(View.VISIBLE);
		}
		
		// set the inflated view as what we want to display
		this.setContentView(root);
	}

	public int getResult() {
		return result;
	}
}
