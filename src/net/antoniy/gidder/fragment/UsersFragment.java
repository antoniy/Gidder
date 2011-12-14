package net.antoniy.gidder.fragment;

import java.sql.SQLException;
import java.util.List;

import net.antoniy.gidder.R;
import net.antoniy.gidder.activity.AddUserActivity;
import net.antoniy.gidder.adapter.UsersAdapter;
import net.antoniy.gidder.db.entity.User;
import net.antoniy.gidder.popup.ActionsPopupWindow;
import android.app.Activity;
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

public class UsersFragment extends BaseFragment implements OnClickListener, OnItemLongClickListener {
	private final static String TAG = UsersFragment.class.getSimpleName();
	private final static String INTENT_ACTION_START_ADD_USER = "net.antoniy.gidder.START_ADD_USER_ACTIVITY";
	
	private Button addButton;
	private ListView usersListView;
	private UsersAdapter usersListAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LinearLayout mainContainer = (LinearLayout) inflater.inflate(R.layout.users, null);

		addButton = (Button) mainContainer.findViewById(R.id.usersAddButton);
		addButton.setOnClickListener(this);
		
		usersListView = (ListView) mainContainer.findViewById(R.id.usersListView);
		loadUsersListContent();
		usersListView.setOnItemLongClickListener(this);
//		usersListView.setOnItemClickListener(this);
//		registerForContextMenu(usersListView);
		
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
		} else if(v.getId() == R.id.usersListView) {
			Log.i(TAG, "Single click on users list!");
			
			ActionsPopupWindow popup = new ActionsPopupWindow(v);
			popup.showLikeQuickAction();
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Activity.RESULT_OK) {
			Log.i(TAG, "Refreshing users...");
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
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
//	@Override
//	public boolean onContextItemSelected(MenuItem item) {
//		if(((SlideActivity)getActivity()).getCurrentFragment() != FragmentType.USERS) {
//			return false;
//		}
//		
//		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
//		
//		Log.i(TAG, "Long selection: " + info.position);
//		Log.i(TAG, "Current[user]: " + ((SlideActivity)getActivity()).getCurrentFragment());
//		
//		User user = usersListAdapter.getItem(info.position);
//		
//		switch (item.getItemId()) {
//		case CONTEXT_MENU_ITEM_EDIT:
//			Intent intent = new Intent(getActivity(), AddUserActivity.class);
//			intent.putExtra("userId", user.getId());
//			startActivityForResult(intent, AddUserActivity.REQUEST_CODE_EDIT_USER);
//			break;
//		case CONTEXT_MENU_ITEM_REMOVE:
//			
//			break;
//		}
//		
//		return super.onContextItemSelected(item);
//	}
//
//	@Override
//	protected void onContextMenuEditItemSelected(int position) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	protected void onContextMenuRemoveItemSelected(int position) {
//		// TODO Auto-generated method stub
//		
//	}


	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		Log.i(TAG, "Long click on users list!");
		
		ActionsPopupWindow popup = new ActionsPopupWindow(view);
		popup.showLikeQuickAction();
		return true;
	}
}