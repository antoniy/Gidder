package net.antoniy.gidder.ui.activity;

import java.sql.SQLException;
import java.util.List;

import net.antoniy.gidder.R;
import net.antoniy.gidder.db.entity.Permission;
import net.antoniy.gidder.db.entity.Repository;
import net.antoniy.gidder.db.entity.User;
import net.antoniy.gidder.ui.adapter.BasePermissionListAdapter;
import net.antoniy.gidder.ui.adapter.BasePopupListAdapter;
import net.antoniy.gidder.ui.adapter.UserPermissionListAdapter;
import net.antoniy.gidder.ui.adapter.UsersPopupListAdapter;
import net.antoniy.gidder.ui.popup.OnPermissionListItemClickListener;
import net.antoniy.gidder.ui.util.C;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class RepositoryDetailsActivity extends BaseActivity implements OnItemLongClickListener, OnItemClickListener, OnPermissionListItemClickListener {
	private final static String TAG = RepositoryDetailsActivity.class.getSimpleName();

	private final static int EDIT_REPOSITORY_REQUEST_CODE = 1;
	
	private int repositoryId;
	private TextView nameTextView;
	private TextView descriptionTextView;
	private TextView mappingTextView;
	private ImageView activateImageView;
	private TextView noPermissionsTextView;
	private ListView permissionsListView;
	private ImageView repositoryPhotoImageView;
	private BasePermissionListAdapter permissionsListAdapter;
	
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
	protected void setupActionBar() {
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
	}

	@Override
	protected void initComponents(Bundle savedInstanceState) {
		nameTextView = (TextView) findViewById(R.id.repositoryDetailsName);
		descriptionTextView = (TextView) findViewById(R.id.repositoryDetailsDescription);
		mappingTextView = (TextView) findViewById(R.id.repositoryDetailsMapping);
		activateImageView = (ImageView) findViewById(R.id.repositoryDetailsActive);
		repositoryPhotoImageView = (ImageView) findViewById(R.id.repositoryDetailsPhoto);
		
		noPermissionsTextView = (TextView) findViewById(R.id.repositoryDetailsNoPermissions);
		
		permissionsListView = (ListView) findViewById(R.id.repositoryDetailsPermissions);
		permissionsListView.setOnItemLongClickListener(this);
		permissionsListView.setOnItemClickListener(this);
		
		loadPermissionsListContent();
		populateFieldsWithUserData();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == android.R.id.home) {
			Intent intent = new Intent(C.action.START_SETUP_ACTIVITY);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			
			finish();
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		try {
			Repository repository = getHelper().getRepositoryDao().queryForId(repositoryId);
			
			MenuItem activateMenuItem = menu.add(repository.isActive() ? "Deactivate" : "Activate");
			activateMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
			activateMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
				
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					Repository repository = null;
					
					try {
						repository = getHelper().getRepositoryDao().queryForId(repositoryId);
						repository.setActive(!repository.isActive());
						getHelper().getRepositoryDao().update(repository);
					} catch (SQLException e) {
						Log.e(TAG, "Error retrieving repository with id " + repositoryId, e);
						return true;
					}
					
					item.setTitle(repository.isActive() ? "Deactivate" : "Activate");
					populateFieldsWithUserData();
					return true;
				}
				
			});
		} catch (SQLException e) {
			Log.e(TAG, "Error retrieving repository with id " + repositoryId, e);
		}
		
		MenuItem deleteMenuItem = menu.add("Delete").setIcon(R.drawable.ic_actionbar_delete);
		deleteMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		deleteMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
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
					return true;
				}
				
				AlertDialog.Builder builder = new AlertDialog.Builder(RepositoryDetailsActivity.this);
				builder.setMessage("Delete " + repository.getName() + "?").setPositiveButton("Yes", dialogClickListener)
				    .setNegativeButton("No", null).show();
				return true;
			}
			
		});
		
		MenuItem editMenuItem = menu.add("Edit").setIcon(R.drawable.ic_actionbar_edit);
		editMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		editMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent editUserIntent = new Intent(C.action.START_ADD_REPOSITORY_ACTIVITY);
				editUserIntent.putExtra("repositoryId", repositoryId);
				
				startActivityForResult(editUserIntent, EDIT_REPOSITORY_REQUEST_CODE);
				return true;
			}
			
		});
		
		MenuItem addPermissionMenuItem = menu.add("Add Permission").setIcon(R.drawable.ic_actionbar_add_user);
		addPermissionMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		addPermissionMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				try {
					List<User> users = getHelper().getUserDao().getAllUsersWithoutPermissionForRepositoryId(repositoryId);
					UserListFragment userListFragment = new UserListFragment(users, RepositoryDetailsActivity.this);
					userListFragment.show(getSupportFragmentManager(), "repositoryPermissions");
				} catch (SQLException e) {
					Log.e(TAG, "Couldn't retrieve permissions.", e);
					Toast.makeText(RepositoryDetailsActivity.this, "Couldn't retrieve permissions.", Toast.LENGTH_SHORT);
				}
				return true;
			}
			
		});
		
		return true;
	}
	
	private void loadPermissionsListContent() {
		List<Permission> permissions = null;
		try {
			permissions = getHelper().getPermissionDao().getAllByRepositoryId(repositoryId);
		} catch (SQLException e) {
			Log.e(TAG, "Could not retrieve permissions.", e);
			return;
		}
		
		showPermissionsList(permissions.size() > 0);
		
		permissionsListAdapter = new UserPermissionListAdapter(this, permissions, R.drawable.ic_db_pull, R.drawable.ic_db_pull_push);
		permissionsListView.setAdapter(permissionsListAdapter);
	}
	
	private void showPermissionsList(boolean show) {
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
			repositoryPhotoImageView.setImageResource(R.drawable.ic_user_active);
		} else {
			activateImageView.setImageResource(R.drawable.ic_deactivated);
			repositoryPhotoImageView.setImageResource(R.drawable.ic_user_inactive);
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
		startActionMode(new RepositoryPermissionActionMode(position));
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		onItemClick(parent, view, position, id);
		return true;
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
		
		loadPermissionsListContent();
	}
	
	private final class RepositoryPermissionActionMode implements ActionMode.Callback {

		private final int position;

		public RepositoryPermissionActionMode(int position) {
			this.position = position;
		}
		
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			mode.finish();
			return true;
		}

		@Override
		public boolean onCreateActionMode(final ActionMode mode, Menu menu) {
			final Permission permission = permissionsListAdapter.getItem(position);
			mode.setTitle(permission.getUser().getFullname());
			
			menu.add("Remove")
				.setIcon(R.drawable.ic_actionbar_delete)
				.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
				.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
					
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						try {
							getHelper().getPermissionDao().deleteById(permission.getId());
						} catch (SQLException e) {
							Log.e(TAG, "Unable to remove permission.", e);
							Toast.makeText(RepositoryDetailsActivity.this, "Unable to remove permission!", Toast.LENGTH_SHORT).show();
						}
						
						loadPermissionsListContent();
						
						mode.finish();
						return true;
					}
					
				});
			
			menu.add("Details")
            	.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
            	.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
					
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						Intent intent = new Intent(C.action.START_USER_DETAILS);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						intent.putExtra("userId", permission.getUser().getId());
						
						startActivity(intent);
						
						mode.finish();
						return true;
					}
					
				});
			
			if(!permission.isReadOnly()) {
				menu.add("Pull")
					.setIcon(R.drawable.ic_actionbar_pull)
	        		.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
	        		.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
						
						@Override
						public boolean onMenuItemClick(MenuItem item) {
							try {
								permission.setReadOnly(true);
								getHelper().getPermissionDao().update(permission);
							} catch (SQLException e) {
								Log.e(TAG, "Unable to remove permission.", e);
								Toast.makeText(RepositoryDetailsActivity.this, "Unable to update permission!", Toast.LENGTH_SHORT).show();
							}
							
							loadPermissionsListContent();
							
							mode.finish();
							return true;
						}
						
					});
			} else {
				menu.add("Pull & Push")
					.setIcon(R.drawable.ic_actionbar_pull_push)
	        		.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
	        		.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
						
						@Override
						public boolean onMenuItemClick(MenuItem item) {
							try {
								permission.setReadOnly(false);
								getHelper().getPermissionDao().update(permission);
							} catch (SQLException e) {
								Log.e(TAG, "Unable to remove permission.", e);
								Toast.makeText(RepositoryDetailsActivity.this, "Unable to update permission!", Toast.LENGTH_SHORT).show();
							}
							
							loadPermissionsListContent();
							
							mode.finish();
							return true;
						}
						
					});
			}
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}
		
	}

	public static class UserListFragment extends BaseDialogFragment {

		private final List<User> users;
		private final OnPermissionListItemClickListener listener;

		public UserListFragment(List<User> users, OnPermissionListItemClickListener listener) {
			this.users = users;
			this.listener = listener;
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			getDialog().setTitle("Add permission");
			
			ViewGroup root = (ViewGroup) inflater.inflate(R.layout.popup_list_dialog, null);

			TextView noUsersTextView = (TextView) root.findViewById(R.id.popupListDialogNoRepositories);
			noUsersTextView.setText(R.string.no_users);
			
			ListView usersListView = (ListView) root.findViewById(R.id.popupListDialogListView);

			if(users.size() > 0) {
				BasePopupListAdapter<User> userListAdapter = new UsersPopupListAdapter(getActivity(), users, R.drawable.ic_db_pull_small, R.drawable.ic_db_pull_push_small);
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
			
			return root;
		}
	}
	
}
