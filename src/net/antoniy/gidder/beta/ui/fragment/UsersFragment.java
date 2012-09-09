package net.antoniy.gidder.beta.ui.fragment;

import java.sql.SQLException;
import java.util.List;

import net.antoniy.gidder.beta.R;
import net.antoniy.gidder.beta.db.entity.User;
import net.antoniy.gidder.beta.ui.activity.AddUserActivity;
import net.antoniy.gidder.beta.ui.adapter.UsersAdapter;
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

public class UsersFragment extends BaseFragment implements OnItemLongClickListener, PopupWindow.OnDismissListener, OnItemClickListener {
	private final static String TAG = UsersFragment.class.getSimpleName();
	
	private final static int USER_DETAILS_REQUEST_CODE = 1; 
	
	private ListView usersListView;
	private UsersAdapter usersListAdapter;
	private TextView noUsersTextView;
	private AlertDialog confirmDialog;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LinearLayout mainContainer = (LinearLayout) inflater.inflate(R.layout.users, null);
		
		noUsersTextView = (TextView) mainContainer.findViewById(R.id.usersNoUsersTextView);
		
		usersListView = (ListView) mainContainer.findViewById(R.id.usersListView);
		loadUsersListContent();
		usersListView.setOnItemLongClickListener(this);
		usersListView.setOnItemClickListener(this);
		
		return mainContainer;
	}

	@Override
	public void onResume() {
		super.onResume();
		
		loadUsersListContent();
	}
	
	@Override
	public void onPause() {
		if(confirmDialog != null) {
			confirmDialog.dismiss();
		}
		
		super.onPause();
	}
	
	private void showUsersList(boolean show) {
		if(show) {
			usersListView.setVisibility(View.VISIBLE);
			noUsersTextView.setVisibility(View.GONE);
		} else {
			usersListView.setVisibility(View.GONE);
			noUsersTextView.setVisibility(View.VISIBLE);
		}
	}
	
	private void loadUsersListContent() {
		List<User> users = null;
		try {
			users = getHelper().getUserDao().queryForAll();
		} catch (SQLException e) {
			Log.e(TAG, "Could not retrieve users.", e);
			return;
		}
		
		showUsersList(users.size() > 0);
		
		usersListAdapter = new UsersAdapter(getActivity(), R.layout.users_item, users);
		usersListView.setAdapter(usersListAdapter);
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
		
		showUsersList(users.size() > 0);
		
		usersListAdapter.setItems(users);
		usersListAdapter.notifyDataSetChanged();
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		actionMode = getSherlockActivity().startActionMode(new UserListActionMode(position));
		
		return true;
	}

	@Override
	public void onDismiss() {
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(C.action.START_USER_DETAILS);
		intent.putExtra("userId", usersListAdapter.getItem(position).getId());
		
		startActivityForResult(intent, USER_DETAILS_REQUEST_CODE);
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
			final User user = usersListAdapter.getItem(position);
			mode.setTitle(user.getFullname());
			
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
										getHelper().getUserDao().deleteById(user.getId());
										updateUsersList();
									} catch (SQLException e) {
										Log.e(TAG, "Problem while deleting user.", e);
									}
						        }
						    }
						};

						AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
						confirmDialog = builder.setMessage("Delete " + user.getFullname() + "?").setPositiveButton("Yes", dialogClickListener)
						    .setNegativeButton("No", null).show();
						
						mode.finish();
						return true;
					}
					
				});
			
			if(!user.isActive()) {
				menu.add("Activate")
	        		.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
	        		.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
						
						@Override
						public boolean onMenuItemClick(MenuItem item) {
							user.setActive(true);
							
							try {
								getHelper().getUserDao().update(user);
								updateUsersList();
							} catch (SQLException e) {
								Log.e(TAG, "Problem while activating user.", e);
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
							user.setActive(false);
							
							try {
								getHelper().getUserDao().update(user);
								updateUsersList();
							} catch (SQLException e) {
								Log.e(TAG, "Problem while deactivating user.", e);
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
					Intent intent = new Intent(getActivity(), AddUserActivity.class);
					intent.putExtra("userId", user.getId());
					startActivityForResult(intent, AddUserActivity.REQUEST_CODE_EDIT_USER);
					
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