package net.antoniy.gidder.ui.activity;

import java.sql.SQLException;
import java.util.List;

import net.antoniy.gidder.R;
import net.antoniy.gidder.db.entity.Permission;
import net.antoniy.gidder.db.entity.Repository;
import net.antoniy.gidder.db.entity.User;
import net.antoniy.gidder.ui.adapter.UserPermissionsAdapter;
import net.antoniy.gidder.ui.quickactions.ActionItem;
import net.antoniy.gidder.ui.quickactions.OnRepositoryListItemClickListener;
import net.antoniy.gidder.ui.quickactions.QuickAction;
import net.antoniy.gidder.ui.quickactions.RepositoryListPopupWindow;
import net.antoniy.gidder.ui.quickactions.QuickAction.OnActionItemClickListener;
import net.antoniy.gidder.ui.quickactions.QuickAction.OnDismissListener;
import net.antoniy.gidder.ui.util.C;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.IntentAction;

public class UserDetailsActivity extends BaseActivity implements OnItemLongClickListener, OnItemClickListener, OnActionItemClickListener, OnDismissListener, OnRepositoryListItemClickListener {
	private final static String TAG = UserDetailsActivity.class.getSimpleName();

	private final static int EDIT_USER_REQUEST_CODE = 1;
	private final static int QUICK_ACTION_DETAILS = 1;
	private final static int QUICK_ACTION_REMOVE = 2;
	private final static int QUICK_ACTION_PERMISSION_ALL = 3;
	private final static int QUICK_ACTION_PERMISSION_PULL = 4;
	
	private int userId;
	private TextView fullnameTextView;
	private TextView emailTextView;
	private TextView usernameTextView;
	private ImageView activateImageView;
	private TextView noPermissionsTextView;
	private ListView permissionsListView;
	private ImageView userPhotoImageView;
	private UserPermissionsAdapter userPermissionsListAdapter;
	private Button editButton;
	private Button activateButton;
	private Button deleteButton;
	private TextView addButton;
	private QuickAction quickAction;
	private int selectedRow;
	
	@Override
	protected void setup() {
		setContentView(R.layout.user_details);

		if(getIntent().getExtras() != null) {
			userId = getIntent().getExtras().getInt("userId", -1);
			Log.i(TAG, "UserID: " + userId);
			
			if(userId <= 0) {
				Toast.makeText(this, "No user ID specified!", Toast.LENGTH_SHORT);
				finish();
			}
		}
	}

	@Override
	protected void initComponents(Bundle savedInstanceState) {
		ActionBar actionBar = (ActionBar) findViewById(R.id.userDetailsActionBar);
		actionBar.setHomeAction(new IntentAction(this, new Intent(C.action.START_SLIDE_ACTIVITY), R.drawable.ic_actionbar_home));
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.addAction(new IntentAction(this, new Intent(C.action.START_PREFERENCE_ACTIVITY), R.drawable.ic_actionbar_settings));
		actionBar.setTitle(R.string.user_details);
		
		ActionItem editItem = new ActionItem(QUICK_ACTION_DETAILS, "Details", getResources().getDrawable(R.drawable.ic_db_details));
		ActionItem deleteItem = new ActionItem(QUICK_ACTION_REMOVE, "Remove", getResources().getDrawable(R.drawable.ic_db_remove));
		
		quickAction = new QuickAction(this);
		quickAction.setOnActionItemClickListener(this);
		quickAction.setOnDismissListener(this);
		
		quickAction.addActionItem(editItem);
		quickAction.addActionItem(deleteItem);
		
		fullnameTextView = (TextView) findViewById(R.id.userDetailsName);
		emailTextView = (TextView) findViewById(R.id.userDetailsMail);
		usernameTextView = (TextView) findViewById(R.id.userDetailsUsername);
		activateImageView = (ImageView) findViewById(R.id.userDetailsActive);
		userPhotoImageView = (ImageView) findViewById(R.id.userDetailsPhoto);
		
		editButton = (Button) findViewById(R.id.userDetailsBtnEdit);
		editButton.setOnClickListener(this);
		
		activateButton = (Button) findViewById(R.id.userDetailsBtnActivateDeactivate);
		activateButton.setOnClickListener(this);
		
		deleteButton = (Button) findViewById(R.id.userDetailsBtnDelete);
		deleteButton.setOnClickListener(this);
		
		addButton = (TextView) findViewById(R.id.userDetailsPermissionsAddBtn);
		addButton.setOnClickListener(this);
		
		noPermissionsTextView = (TextView) findViewById(R.id.userDetailsNoPermissions);
		
		permissionsListView = (ListView) findViewById(R.id.userDetailsPermissions);
		permissionsListView.setOnItemLongClickListener(this);
		permissionsListView.setOnItemClickListener(this);
		
		loadUserPermissionsListContent();
		populateFieldsWithUserData();
	}
	
	private void loadUserPermissionsListContent() {
		List<Permission> permissions = null;
		try {
			permissions = getHelper().getPermissionDao().getAllByUserId(userId);
		} catch (SQLException e) {
			Log.e(TAG, "Could not retrieve permissions.", e);
			return;
		}
		
		showUserPermissionsList(permissions.size() > 0);
		
		userPermissionsListAdapter = new UserPermissionsAdapter(this, permissions);
		permissionsListView.setAdapter(userPermissionsListAdapter);
	}
	
	private void showUserPermissionsList(boolean show) {
		if(show) {
			permissionsListView.setVisibility(View.VISIBLE);
			noPermissionsTextView.setVisibility(View.GONE);
		} else {
			permissionsListView.setVisibility(View.GONE);
			noPermissionsTextView.setVisibility(View.VISIBLE);
		}
	}
	
	private void populateFieldsWithUserData() {
		User user = null;
		try {
			user = getHelper().getUserDao().queryForId(userId);
		} catch (SQLException e) {
			Log.e(TAG, "Error retrieving user with id " + userId, e);
			return;
		}
		
		fullnameTextView.setText(user.getFullname());
		emailTextView.setText(user.getEmail());
		
		usernameTextView.setText(user.getUsername());
		if(user.isActive()) {
			activateImageView.setImageResource(R.drawable.ic_activated);
			activateButton.setText("Deactivate");
			userPhotoImageView.setImageResource(R.drawable.ic_user_active);
		} else {
			activateImageView.setImageResource(R.drawable.ic_deactivated);
			activateButton.setText("Activate");
			userPhotoImageView.setImageResource(R.drawable.ic_user_inactive);
		}
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		if(v.getId() == R.id.userDetailsBtnEdit) {
			Intent editUserIntent = new Intent(C.action.START_ADD_USER_ACTIVITY);
			editUserIntent.putExtra("userId", userId);
			
			startActivityForResult(editUserIntent, EDIT_USER_REQUEST_CODE);
		} else if(v.getId() == R.id.userDetailsBtnActivateDeactivate) {
			User user = null;
			try {
				user = getHelper().getUserDao().queryForId(userId);
				user.setActive(!user.isActive());
				getHelper().getUserDao().update(user);
			} catch (SQLException e) {
				Log.e(TAG, "Error retrieving user with id " + userId, e);
				return;
			}
			
			populateFieldsWithUserData();
		} else if(v.getId() == R.id.userDetailsBtnDelete) {
			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
			        if(which == DialogInterface.BUTTON_POSITIVE) {
			            try {
							getHelper().getUserDao().deleteById(userId);

							setResult(Activity.RESULT_OK);
							finish();
						} catch (SQLException e) {
							Log.e(TAG, "Problem while deleting user.", e);
						}
			        }
			    }
			};

			User user = null;
			try {
				user = getHelper().getUserDao().queryForId(userId);
			} catch (SQLException e) {
				Log.e(TAG, "Error retrieving user with id " + userId, e);
				return;
			}
			
			AlertDialog.Builder builder = new AlertDialog.Builder(UserDetailsActivity.this);
			builder.setMessage("Delete " + user.getFullname() + "?").setPositiveButton("Yes", dialogClickListener)
			    .setNegativeButton("No", null).show();
		} else if(v.getId() == R.id.userDetailsPermissionsAddBtn) {
			try {
				List<Repository> repositories = getHelper().getRepositoryDao().getAllRepositoriesWithoutPermissionForUserId(userId);
				new RepositoryListPopupWindow(v, userId, repositories, this, 400).showLikeQuickAction();
			} catch (SQLException e) {
				Log.e(TAG, "Couldn't retrieve permissions.", e);
				Toast.makeText(UserDetailsActivity.this, "Couldn't retrieve permissions.", Toast.LENGTH_SHORT);
			}
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == EDIT_USER_REQUEST_CODE) {
			populateFieldsWithUserData();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Permission permission = userPermissionsListAdapter.getItem(position);
		
		selectedRow = position; //set the selected row
		
		// Delete quick action items (if any of them exists) for activate and deactivate
		quickAction.deleteActionItem(QUICK_ACTION_PERMISSION_ALL);
		quickAction.deleteActionItem(QUICK_ACTION_PERMISSION_PULL);
		
		ActionItem activateDeactivateItem = null;
		if(permission.isReadOnly()) {
			activateDeactivateItem = new ActionItem(QUICK_ACTION_PERMISSION_ALL, "Push/Pull", getResources().getDrawable(R.drawable.ic_db_pull_push));
		} else {
			activateDeactivateItem = new ActionItem(QUICK_ACTION_PERMISSION_PULL, "Pull", getResources().getDrawable(R.drawable.ic_db_pull));
		}
		
		quickAction.addActionItem(activateDeactivateItem);
		quickAction.show(view);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		onItemClick(parent, view, position, id);
		return false;
	}

	@Override
	public void onDismiss() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onItemClick(QuickAction source, int pos, int actionId) {
		final Permission permission = userPermissionsListAdapter.getItem(selectedRow);
		
		if (actionId == QUICK_ACTION_DETAILS) {
			// TODO: fill this, after implement repository details activity.
		} else if (actionId == QUICK_ACTION_REMOVE) {
			try {
				getHelper().getPermissionDao().deleteById(permission.getId());
			} catch (SQLException e) {
				Log.e(TAG, "Unable to remove permission.", e);
				Toast.makeText(UserDetailsActivity.this, "Unable to remove permission!", Toast.LENGTH_SHORT).show();
			}
			
			loadUserPermissionsListContent();
		} else if (actionId == QUICK_ACTION_PERMISSION_PULL) {
			try {
				permission.setReadOnly(true);
				getHelper().getPermissionDao().update(permission);
			} catch (SQLException e) {
				Log.e(TAG, "Unable to remove permission.", e);
				Toast.makeText(UserDetailsActivity.this, "Unable to update permission!", Toast.LENGTH_SHORT).show();
			}
			
			loadUserPermissionsListContent();
		} else if (actionId == QUICK_ACTION_PERMISSION_ALL) {
			try {
				permission.setReadOnly(false);
				getHelper().getPermissionDao().update(permission);
			} catch (SQLException e) {
				Log.e(TAG, "Unable to remove permission.", e);
				Toast.makeText(UserDetailsActivity.this, "Unable to update permission!", Toast.LENGTH_SHORT).show();
			}
			
			loadUserPermissionsListContent();
		}
	}

	@Override
	public void onRepositoryPermissionItemClick(int repositoryId, boolean readOnlyPermission) {
		Log.i(TAG, "RepositoryId: " + repositoryId + ", Type: " + readOnlyPermission);
		
		User user = new User();
		user.setId(userId);
		
		Repository repository = new Repository();
		repository.setId(repositoryId);
		
		Permission permission = new Permission(0, user, repository, readOnlyPermission);
		try {
			getHelper().getPermissionDao().create(permission);
		} catch (SQLException e) {
			Log.e(TAG, "Problem creating new user permission.", e);
			Toast.makeText(UserDetailsActivity.this, "Problem creating new user permission.", Toast.LENGTH_SHORT);
		}
		
		loadUserPermissionsListContent();
	}

}
