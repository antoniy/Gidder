package net.antoniy.gidder.ui.fragment;

import java.sql.SQLException;
import java.util.List;

import net.antoniy.gidder.R;
import net.antoniy.gidder.db.entity.User;
import net.antoniy.gidder.ui.activity.AddUserActivity;
import net.antoniy.gidder.ui.adapter.UsersAdapter;
import net.antoniy.gidder.ui.quickactions.ActionItem;
import net.antoniy.gidder.ui.quickactions.QuickAction;
import net.antoniy.gidder.ui.util.C;
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
import android.widget.TextView;

public class UsersFragment extends BaseFragment implements OnClickListener, OnItemLongClickListener, QuickAction.OnActionItemClickListener, PopupWindow.OnDismissListener, OnItemClickListener {
	private final static String TAG = UsersFragment.class.getSimpleName();
	
	private final static String INTENT_ACTION_START_ADD_USER = "net.antoniy.gidder.START_ADD_USER_ACTIVITY";
	private final static int USER_DETAILS_REQUEST_CODE = 1; 
	
	private final static int QUICK_ACTION_EDIT = 1;
	private final static int QUICK_ACTION_DELETE = 2;
	private final static int QUICK_ACTION_DEACTIVATE = 3;
	private final static int QUICK_ACTION_ACTIVATE = 4;
	
	private Button addButton;
	private ListView usersListView;
	private UsersAdapter usersListAdapter;
	private int selectedRow;
	private QuickAction quickAction;
	private TextView noUsersTextView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LinearLayout mainContainer = (LinearLayout) inflater.inflate(R.layout.users, null);

		addButton = (Button) mainContainer.findViewById(R.id.usersAddButton);
		addButton.setOnClickListener(this);
		
		noUsersTextView = (TextView) mainContainer.findViewById(R.id.usersNoUsersTextView);
		
		usersListView = (ListView) mainContainer.findViewById(R.id.usersListView);
		loadUsersListContent();
		usersListView.setOnItemLongClickListener(this);
		usersListView.setOnItemClickListener(this);
		
		ActionItem editItem = new ActionItem(1, "Edit", getResources().getDrawable(R.drawable.ic_action_edit));
		ActionItem deleteItem = new ActionItem(2, "Delete", getResources().getDrawable(R.drawable.ic_action_delete));
		
		quickAction = new QuickAction(getActivity());
		quickAction.setOnActionItemClickListener(this);
		quickAction.setOnDismissListener(this);
		
		quickAction.addActionItem(editItem);
		quickAction.addActionItem(deleteItem);
		
		return mainContainer;
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
		
		showUsersList(users.size() > 0);
		
		usersListAdapter.setItems(users);
		usersListAdapter.notifyDataSetChanged();
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		User user = usersListAdapter.getItem(position);
		
		selectedRow = position; //set the selected row
		
		// Delete quick action items (if any of them exists) for activate and deactivate
		quickAction.deleteActionItem(QUICK_ACTION_DEACTIVATE);
		quickAction.deleteActionItem(QUICK_ACTION_ACTIVATE);
		
		ActionItem activateDeactivateItem = null;
		if(user.isActive()) {
			activateDeactivateItem = new ActionItem(QUICK_ACTION_DEACTIVATE, "Deactivate", getResources().getDrawable(R.drawable.ic_action_deactivate));
		} else {
			activateDeactivateItem = new ActionItem(QUICK_ACTION_ACTIVATE, "Activate", getResources().getDrawable(R.drawable.ic_action_activate));
		}
		
		quickAction.addActionItem(activateDeactivateItem);
		quickAction.show(view);
		
		return true;
	}

	@Override
	public void onItemClick(QuickAction source, int pos, int actionId) {
		final User user = usersListAdapter.getItem(selectedRow);
		
		if (actionId == QUICK_ACTION_EDIT) {
			Intent intent = new Intent(getActivity(), AddUserActivity.class);
			intent.putExtra("userId", user.getId());
			startActivityForResult(intent, AddUserActivity.REQUEST_CODE_EDIT_USER);
		} else if(actionId == QUICK_ACTION_DELETE) {
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
		} else if(actionId == QUICK_ACTION_DEACTIVATE) {
			user.setActive(false);
			
			try {
				getHelper().getUserDao().update(user);
				updateUsersList();
			} catch (SQLException e) {
				Log.e(TAG, "Problem while deactivating user.", e);
			}
		} else if(actionId == QUICK_ACTION_ACTIVATE) {
			user.setActive(true);
			
			try {
				getHelper().getUserDao().update(user);
				updateUsersList();
			} catch (SQLException e) {
				Log.e(TAG, "Problem while activating user.", e);
			}
		}
	}

	@Override
	public void onDismiss() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(C.action.START_USER_DETAILS);
		intent.putExtra("userId", usersListAdapter.getItem(position).getId());
		
		startActivityForResult(intent, USER_DETAILS_REQUEST_CODE);
	}
	
}