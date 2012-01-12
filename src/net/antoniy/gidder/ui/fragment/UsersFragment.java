package net.antoniy.gidder.ui.fragment;

import java.sql.SQLException;
import java.util.List;

import net.antoniy.gidder.R;
import net.antoniy.gidder.db.entity.User;
import net.antoniy.gidder.ui.activity.AddUserActivity;
import net.antoniy.gidder.ui.adapter.UsersAdapter;
import net.antoniy.gidder.ui.popup.OnActionItemClickListener;
import net.antoniy.gidder.ui.popup.UserActionsPopupWindow;
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

public class UsersFragment extends BaseFragment implements OnClickListener, OnItemLongClickListener, OnActionItemClickListener {
	private final static String TAG = UsersFragment.class.getSimpleName();
	private final static String INTENT_ACTION_START_ADD_USER = "net.antoniy.gidder.START_ADD_USER_ACTIVITY";
	
	private Button addButton;
	private ListView usersListView;
	private UsersAdapter usersListAdapter;
	private UserActionsPopupWindow popup;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LinearLayout mainContainer = (LinearLayout) inflater.inflate(R.layout.users, null);

		addButton = (Button) mainContainer.findViewById(R.id.usersAddButton);
		addButton.setOnClickListener(this);
		
		usersListView = (ListView) mainContainer.findViewById(R.id.usersListView);
		loadUsersListContent();
		usersListView.setOnItemLongClickListener(this);
		
		return mainContainer;
	}
	
	private void loadUsersListContent() {
		List<User> users = null;
		try {
			users = getHelper().getUserDao().queryForAll();
		} catch (SQLException e) {
			Log.e(TAG, "Could not retrieve users.", e);
			return;
		}
		
		usersListAdapter = new UsersAdapter(getActivity(), R.layout.users_item, users);
		usersListView.setAdapter(usersListAdapter);
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.usersAddButton) {
			Intent intent = new Intent(INTENT_ACTION_START_ADD_USER);
			startActivityForResult(intent, AddUserActivity.REQUEST_CODE_ADD_USER);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Activity.RESULT_OK) {
			Log.i(TAG, "Refreshing users...");
			updateUsersList();
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void updateUsersList() {
		List<User> users = null;
		try {
			users = getHelper().getUserDao().queryForAll();
		} catch (SQLException e) {
			Log.e(TAG, "Could not retrieve users.", e);
			return;
		}
		
		usersListAdapter.setItems(users);
		usersListAdapter.notifyDataSetChanged();
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		User user = usersListAdapter.getItem(position);
		
		popup = new UserActionsPopupWindow(view, position, user.isActive());
		popup.showLikeQuickAction(0, 40);
		popup.addOnActionItemClickListener(this);

		return true;
	}
	
//	@Override
//	public void onConfigurationChanged(Configuration newConfig) {
//		// We need this because when the popup is opened and the screen orientation 
//		// changes - the popup window leaks and we got an exception.
//		if(popup != null) {
//			popup.dismiss();
//		}
//
//		super.onConfigurationChanged(newConfig);
//	}

	@Override
	public void onActionItemClick(View v, int position, int resultCode) {
		if(resultCode == UserActionsPopupWindow.RESULT_EDIT) {
			User user = usersListAdapter.getItem(position);
			
			Intent intent = new Intent(getActivity(), AddUserActivity.class);
			intent.putExtra("userId", user.getId());
			startActivityForResult(intent, AddUserActivity.REQUEST_CODE_EDIT_USER);
		} else if(resultCode == UserActionsPopupWindow.RESULT_DELETE) {
			final User user = usersListAdapter.getItem(position);
			
			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
			        if(which == DialogInterface.BUTTON_POSITIVE) {
			            try {
							getHelper().getUserDao().deleteById(user.getId());
							updateUsersList();
						} catch (SQLException e) {
							Log.e(TAG, "Problem while deleting user.", e);
						}
			        }
			    }
			};

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage("Delete " + user.getFullname() + "?").setPositiveButton("Yes", dialogClickListener)
			    .setNegativeButton("No", null).show();
		} else if(resultCode == UserActionsPopupWindow.RESULT_DEACTIVATE) {
			User user = usersListAdapter.getItem(position);
			user.setActive(false);
			
			try {
				getHelper().getUserDao().update(user);
				updateUsersList();
			} catch (SQLException e) {
				Log.e(TAG, "Problem while deactivating user.", e);
			}
		} else if(resultCode == UserActionsPopupWindow.RESULT_ACTIVATE) {
			User user = usersListAdapter.getItem(position);
			user.setActive(true);
			
			try {
				getHelper().getUserDao().update(user);
				updateUsersList();
			} catch (SQLException e) {
				Log.e(TAG, "Problem while activating user.", e);
			}
		}
	}
}