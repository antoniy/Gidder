package net.antoniy.gidder.ui.popup;

import java.util.List;

import net.antoniy.gidder.R;
import net.antoniy.gidder.db.entity.User;
import net.antoniy.gidder.ui.adapter.BasePopupListAdapter;
import net.antoniy.gidder.ui.adapter.UsersPopupListAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class UserListPopupWindow extends BasePopupWindow {
	
	public final static int RESULT_UNDEFINED = 0;
	public final static int RESULT_EDIT = 1;
	public final static int RESULT_DELETE = 2;
	public final static int RESULT_ACTIVATE = 3;
	public final static int RESULT_DEACTIVATE = 4;
	
	private int result = RESULT_UNDEFINED;

	private final List<User> users;

	private final OnPermissionListItemClickListener listener;
	
	public UserListPopupWindow(View anchor, int userId, List<User> users, OnPermissionListItemClickListener listener, float heightInDp) {
		super(anchor, heightInDp);
		this.users = users;
		this.listener = listener;
		
		onCreate();
	}

	private void onCreate() {
		// inflate layout
		LayoutInflater inflater = (LayoutInflater) this.anchor.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.popup_list_dialog, null);

		TextView noUsersTextView = (TextView) root.findViewById(R.id.popupListDialogNoRepositories);
		noUsersTextView.setText(R.string.no_users);
		
		ListView usersListView = (ListView) root.findViewById(R.id.popupListDialogListView);

		if(users.size() > 0) {
			BasePopupListAdapter<User> userListAdapter = new UsersPopupListAdapter(anchor.getContext(), users, R.drawable.ic_db_pull_small, R.drawable.ic_db_pull_push_small);
			userListAdapter.addOnRepositoryListItemClick(listener);
			userListAdapter.addOnRepositoryListItemClick(new OnPermissionListItemClickListener() {
				@Override
				public void onPermissionItemClick(int entityId, boolean readOnlyPermission) {
					dismiss();
				}
			});
			
			usersListView.setAdapter(userListAdapter);
			
			usersListView.setVisibility(View.VISIBLE);
			noUsersTextView.setVisibility(View.GONE);
		} else {
			usersListView.setVisibility(View.GONE);
			noUsersTextView.setVisibility(View.VISIBLE);
		}
		
		// set the inflated view as what we want to display
		this.setContentView(root);
	}

	public int getResult() {
		return result;
	}
}
