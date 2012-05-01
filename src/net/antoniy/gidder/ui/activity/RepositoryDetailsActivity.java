package net.antoniy.gidder.ui.activity;

import java.sql.SQLException;
import java.util.List;

import net.antoniy.gidder.R;
import net.antoniy.gidder.db.entity.Permission;
import net.antoniy.gidder.db.entity.Repository;
import net.antoniy.gidder.db.entity.User;
import net.antoniy.gidder.ui.adapter.BasePermissionListAdapter;
import net.antoniy.gidder.ui.adapter.UserPermissionListAdapter;
import net.antoniy.gidder.ui.popup.OnPermissionListItemClickListener;
import net.antoniy.gidder.ui.popup.UserListPopupWindow;
import net.antoniy.gidder.ui.quickactions.ActionItem;
import net.antoniy.gidder.ui.quickactions.QuickAction;
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

public class RepositoryDetailsActivity extends BaseActivity implements OnItemLongClickListener, OnItemClickListener, OnActionItemClickListener, OnDismissListener, OnPermissionListItemClickListener {
	private final static String TAG = RepositoryDetailsActivity.class.getSimpleName();

	private final static int EDIT_REPOSITORY_REQUEST_CODE = 1;
	private final static int QUICK_ACTION_DETAILS = 1;
	private final static int QUICK_ACTION_REMOVE = 2;
	private final static int QUICK_ACTION_PERMISSION_ALL = 3;
	private final static int QUICK_ACTION_PERMISSION_PULL = 4;
	
	private int repositoryId;
	private TextView nameTextView;
	private TextView descriptionTextView;
	private TextView mappingTextView;
	private ImageView activateImageView;
	private TextView noPermissionsTextView;
	private ListView permissionsListView;
	private ImageView repositoryPhotoImageView;
	private BasePermissionListAdapter userPermissionsListAdapter;
	private Button editButton;
	private Button activateButton;
	private Button deleteButton;
	private TextView addButton;
	private QuickAction quickAction;
	private int selectedRow;
	
	@Override
	protected void setup() {
		setContentView(R.layout.repository_details);

		if(getIntent().getExtras() != null) {
			repositoryId = getIntent().getExtras().getInt("repositoryId", -1);
			
			if(repositoryId <= 0) {
				Toast.makeText(this, "No repository ID specified!", Toast.LENGTH_SHORT);
				finish();
			}
		}
	}

	@Override
	protected void initComponents(Bundle savedInstanceState) {
		ActionBar actionBar = (ActionBar) findViewById(R.id.repositoryDetailsActionBar);
		actionBar.setHomeAction(new IntentAction(this, new Intent(C.action.START_SLIDE_ACTIVITY), R.drawable.ic_actionbar_home));
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.addAction(new IntentAction(this, new Intent(C.action.START_PREFERENCE_ACTIVITY), R.drawable.ic_actionbar_settings));
		actionBar.setTitle(R.string.repository_details);
		
		ActionItem editItem = new ActionItem(QUICK_ACTION_DETAILS, "Details", getResources().getDrawable(R.drawable.ic_db_details));
		ActionItem deleteItem = new ActionItem(QUICK_ACTION_REMOVE, "Remove", getResources().getDrawable(R.drawable.ic_db_remove));
		
		quickAction = new QuickAction(this);
		quickAction.setOnActionItemClickListener(this);
		quickAction.setOnDismissListener(this);
		
		quickAction.addActionItem(editItem);
		quickAction.addActionItem(deleteItem);
		
		nameTextView = (TextView) findViewById(R.id.repositoryDetailsName);
		descriptionTextView = (TextView) findViewById(R.id.repositoryDetailsDescription);
		mappingTextView = (TextView) findViewById(R.id.repositoryDetailsMapping);
		activateImageView = (ImageView) findViewById(R.id.repositoryDetailsActive);
		repositoryPhotoImageView = (ImageView) findViewById(R.id.repositoryDetailsPhoto);
		
		editButton = (Button) findViewById(R.id.repositoryDetailsBtnEdit);
		editButton.setOnClickListener(this);
		
		activateButton = (Button) findViewById(R.id.repositoryDetailsBtnActivateDeactivate);
		activateButton.setOnClickListener(this);
		
		deleteButton = (Button) findViewById(R.id.repositoryDetailsBtnDelete);
		deleteButton.setOnClickListener(this);
		
		addButton = (TextView) findViewById(R.id.repositoryDetailsPermissionsAddBtn);
		addButton.setOnClickListener(this);
		
		noPermissionsTextView = (TextView) findViewById(R.id.repositoryDetailsNoPermissions);
		
		permissionsListView = (ListView) findViewById(R.id.repositoryDetailsPermissions);
		permissionsListView.setOnItemLongClickListener(this);
		permissionsListView.setOnItemClickListener(this);
		
		loadUserPermissionsListContent();
		populateFieldsWithUserData();
	}
	
	private void loadUserPermissionsListContent() {
		List<Permission> permissions = null;
		try {
			permissions = getHelper().getPermissionDao().getAllByRepositoryId(repositoryId);
		} catch (SQLException e) {
			Log.e(TAG, "Could not retrieve permissions.", e);
			return;
		}
		
		showUserPermissionsList(permissions.size() > 0);
		
		userPermissionsListAdapter = new UserPermissionListAdapter(this, permissions, R.drawable.ic_db_pull, R.drawable.ic_db_pull_push);
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
		Repository repository = null;
		try {
			repository = getHelper().getRepositoryDao().queryForId(repositoryId);
		} catch (SQLException e) {
			Log.e(TAG, "Error retrieving repository with id " + repositoryId, e);
			return;
		}
		
		nameTextView.setText(repository.getName());
		descriptionTextView.setText(repository.getDescription());
		mappingTextView.setText("/" + repository.getMapping() + ".git");
		
		if(repository.isActive()) {
			activateImageView.setImageResource(R.drawable.ic_activated);
			activateButton.setText("Deactivate");
			repositoryPhotoImageView.setImageResource(R.drawable.ic_user_active);
		} else {
			activateImageView.setImageResource(R.drawable.ic_deactivated);
			activateButton.setText("Activate");
			repositoryPhotoImageView.setImageResource(R.drawable.ic_user_inactive);
		}
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		if(v.getId() == R.id.repositoryDetailsBtnEdit) {
			Intent editUserIntent = new Intent(C.action.START_ADD_REPOSITORY_ACTIVITY);
			editUserIntent.putExtra("repositoryId", repositoryId);
			
			startActivityForResult(editUserIntent, EDIT_REPOSITORY_REQUEST_CODE);
		} else if(v.getId() == R.id.repositoryDetailsBtnActivateDeactivate) {
			Repository repository = null;
			try {
				repository = getHelper().getRepositoryDao().queryForId(repositoryId);
				repository.setActive(!repository.isActive());
				getHelper().getRepositoryDao().update(repository);
			} catch (SQLException e) {
				Log.e(TAG, "Error retrieving repository with id " + repositoryId, e);
				return;
			}
			
			populateFieldsWithUserData();
		} else if(v.getId() == R.id.repositoryDetailsBtnDelete) {
			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
			        if(which == DialogInterface.BUTTON_POSITIVE) {
			            try {
							getHelper().getRepositoryDao().deleteById(repositoryId);

							setResult(Activity.RESULT_OK);
							finish();
						} catch (SQLException e) {
							Log.e(TAG, "Problem while deleting repository.", e);
						}
			        }
			    }
			};

			Repository repository = null;
			try {
				repository = getHelper().getRepositoryDao().queryForId(repositoryId);
			} catch (SQLException e) {
				Log.e(TAG, "Error retrieving repository with id " + repositoryId, e);
				return;
			}
			
			AlertDialog.Builder builder = new AlertDialog.Builder(RepositoryDetailsActivity.this);
			builder.setMessage("Delete " + repository.getName() + "?").setPositiveButton("Yes", dialogClickListener)
			    .setNegativeButton("No", null).show();
		} else if(v.getId() == R.id.repositoryDetailsPermissionsAddBtn) {
			try {
				List<User> users = getHelper().getUserDao().getAllUsersWithoutPermissionForRepositoryId(repositoryId);
				new UserListPopupWindow(v, repositoryId, users, this, 400).showLikeQuickAction();
			} catch (SQLException e) {
				Log.e(TAG, "Couldn't retrieve permissions.", e);
				Toast.makeText(RepositoryDetailsActivity.this, "Couldn't retrieve permissions.", Toast.LENGTH_SHORT);
			}
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == EDIT_REPOSITORY_REQUEST_CODE) {
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
	}

	@Override
	public void onItemClick(QuickAction source, int pos, int actionId) {
		final Permission permission = userPermissionsListAdapter.getItem(selectedRow);
		
		if (actionId == QUICK_ACTION_DETAILS) {
			Intent intent = new Intent(C.action.START_USER_DETAILS);
			intent.putExtra("userId", permission.getUser().getId());
			
			startActivity(intent);
		} else if (actionId == QUICK_ACTION_REMOVE) {
			try {
				getHelper().getPermissionDao().deleteById(permission.getId());
			} catch (SQLException e) {
				Log.e(TAG, "Unable to remove permission.", e);
				Toast.makeText(RepositoryDetailsActivity.this, "Unable to remove permission!", Toast.LENGTH_SHORT).show();
			}
			
			loadUserPermissionsListContent();
		} else if (actionId == QUICK_ACTION_PERMISSION_PULL) {
			try {
				permission.setReadOnly(true);
				getHelper().getPermissionDao().update(permission);
			} catch (SQLException e) {
				Log.e(TAG, "Unable to remove permission.", e);
				Toast.makeText(RepositoryDetailsActivity.this, "Unable to update permission!", Toast.LENGTH_SHORT).show();
			}
			
			loadUserPermissionsListContent();
		} else if (actionId == QUICK_ACTION_PERMISSION_ALL) {
			try {
				permission.setReadOnly(false);
				getHelper().getPermissionDao().update(permission);
			} catch (SQLException e) {
				Log.e(TAG, "Unable to remove permission.", e);
				Toast.makeText(RepositoryDetailsActivity.this, "Unable to update permission!", Toast.LENGTH_SHORT).show();
			}
			
			loadUserPermissionsListContent();
		}
	}

	@Override
	public void onPermissionItemClick(int entityId, boolean readOnlyPermission) {
		Log.i(TAG, "UserId: " + entityId + ", Type: " + readOnlyPermission);
		
		User user = new User();
		user.setId(entityId);
		
		Repository repository = new Repository();
		repository.setId(repositoryId);
		
		Permission permission = new Permission(0, user, repository, readOnlyPermission);
		try {
			getHelper().getPermissionDao().create(permission);
		} catch (SQLException e) {
			Log.e(TAG, "Problem creating new repository permission.", e);
			Toast.makeText(RepositoryDetailsActivity.this, "Problem creating new repository permission.", Toast.LENGTH_SHORT);
		}
		
		loadUserPermissionsListContent();
	}

}
