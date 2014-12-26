package net.antoniy.gidder.beta.ui.activity;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

import net.antoniy.gidder.beta.R;
import net.antoniy.gidder.beta.db.entity.Permission;
import net.antoniy.gidder.beta.db.entity.Repository;
import net.antoniy.gidder.beta.db.entity.User;
import net.antoniy.gidder.beta.ui.adapter.BasePermissionListAdapter;
import net.antoniy.gidder.beta.ui.adapter.BasePopupListAdapter;
import net.antoniy.gidder.beta.ui.adapter.RepositoriesPopupListAdapter;
import net.antoniy.gidder.beta.ui.adapter.RepositoryPermissionListAdapter;
import net.antoniy.gidder.beta.ui.popup.OnPermissionListItemClickListener;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class UserDetailsActivity extends BaseActivity implements OnItemLongClickListener, OnItemClickListener {
	private final static String TAG = UserDetailsActivity.class.getSimpleName();

	private final static int EDIT_USER_REQUEST_CODE = 1;
	
	private int userId;
	private TextView fullnameTextView;
	private TextView emailTextView;
	private TextView usernameTextView;
	private ImageView activateImageView;
	private TextView noPermissionsTextView;
	private ListView permissionsListView;
	private BasePermissionListAdapter permissionsListAdapter;
	private RepositoryListFragment repositoryListFragment;
	
	private OnPermissionListItemClickListener onPermissionListItemClickListener = new OnPermissionListItemClickListener() {
		
		@Override
		public void onPermissionItemClick(int entityId, boolean readOnlyPermission) {
			Log.i(TAG, "RepositoryId: " + entityId + ", Type: " + readOnlyPermission);
			
			User user = new User();
			user.setId(userId);
			
			Repository repository = new Repository();
			repository.setId(entityId);
			
			Permission permission = new Permission(0, user, repository, readOnlyPermission);
			try {
				getHelper().getPermissionDao().create(permission);
			} catch (SQLException e) {
				Log.e(TAG, "Problem creating new user permission.", e);
				Toast.makeText(UserDetailsActivity.this, "Problem creating new user permission.", Toast.LENGTH_SHORT).show();
			}
			
			loadPermissionsListContent();
		}
	};
	
	@Override
	protected void setup() {
		setContentView(R.layout.user_details);

		if(getIntent().getExtras() != null) {
			userId = getIntent().getExtras().getInt("userId", -1);
			
			if(userId <= 0) {
				Toast.makeText(this, "No user ID specified!", Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}

	@Override
	protected void initComponents(Bundle savedInstanceState) {
		fullnameTextView = (TextView) findViewById(R.id.userDetailsName);
		emailTextView = (TextView) findViewById(R.id.userDetailsMail);
		usernameTextView = (TextView) findViewById(R.id.userDetailsUsername);
		activateImageView = (ImageView) findViewById(R.id.userDetailsActive);
		
		noPermissionsTextView = (TextView) findViewById(R.id.userDetailsNoPermissions);
		
		permissionsListView = (ListView) findViewById(R.id.userDetailsPermissions);
		permissionsListView.setOnItemLongClickListener(this);
		permissionsListView.setOnItemClickListener(this);
		
		loadPermissionsListContent();
		populateFieldsWithUserData();
	}
	
	@Override
	protected void onPause() {
		if(repositoryListFragment != null) {
			repositoryListFragment.dismiss();
		}
		
		super.onPause();
	}
	
	@Override
	protected void setupActionBar() {
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
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
			User user = getHelper().getUserDao().queryForId(userId);

			MenuItem activateMenuItem = menu.add(user.isActive() ? "Deactivate" : "Activate");
			activateMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
			activateMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
				
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					User user = null;
					try {
						user = getHelper().getUserDao().queryForId(userId);
						user.setActive(!user.isActive());
						getHelper().getUserDao().update(user);
					} catch (SQLException e) {
						Log.e(TAG, "Error retrieving user with id " + userId, e);
						return true;
					}
					
					item.setTitle(user.isActive() ? "Deactivate" : "Activate");
					populateFieldsWithUserData();
					return true;
				}
				
			});
		} catch (SQLException e) {
			Log.e(TAG, "Error retrieving user with id " + userId, e);
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
					return true;
				}
				
				AlertDialog.Builder builder = new AlertDialog.Builder(UserDetailsActivity.this);
				builder.setMessage("Delete " + user.getFullname() + "?").setPositiveButton("Yes", dialogClickListener)
				    .setNegativeButton("No", null).show();
				return true;
			}
			
		});
		
		MenuItem editMenuItem = menu.add("Edit").setIcon(R.drawable.ic_actionbar_edit);
		editMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		editMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent editUserIntent = new Intent(C.action.START_ADD_USER_ACTIVITY);
				editUserIntent.putExtra("userId", userId);
				
				startActivityForResult(editUserIntent, EDIT_USER_REQUEST_CODE);
				return true;
			}
			
		});
		
		MenuItem addPermissionMenuItem = menu.add("Add Permission").setIcon(R.drawable.ic_actionbar_add_repository);
		addPermissionMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		addPermissionMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				try {
					List<Repository> repositories = getHelper().getRepositoryDao().getAllRepositoriesWithoutPermissionForUserId(userId);
					
					repositoryListFragment = new RepositoryListFragment();
					
					Bundle args = new Bundle(1);
					args.putSerializable("arguments", new RepositoryListArgs(repositories, onPermissionListItemClickListener));
					repositoryListFragment.setArguments(args);
					
					repositoryListFragment.show(getSupportFragmentManager(), "userPermissions");
				} catch (SQLException e) {
					Log.e(TAG, "Couldn't retrieve permissions.", e);
					Toast.makeText(UserDetailsActivity.this, "Couldn't retrieve permissions.", Toast.LENGTH_SHORT).show();
				}
				return true;
			}
			
		});
		
		return true;
	}
	
	private void loadPermissionsListContent() {
		List<Permission> permissions = null;
		try {
			permissions = getHelper().getPermissionDao().getAllByUserId(userId);
		} catch (SQLException e) {
			Log.e(TAG, "Could not retrieve permissions.", e);
			return;
		}
		
		showPermissionsList(permissions.size() > 0);
		
		permissionsListAdapter = new RepositoryPermissionListAdapter(this, permissions, R.drawable.ic_db_pull, R.drawable.ic_db_pull_push);
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
		} else {
			activateImageView.setImageResource(R.drawable.ic_deactivated);
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
		startActionMode(new UserPermissionActionMode(position));
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		onItemClick(parent, view, position, id);
		
		return true;
	}

	private final class UserPermissionActionMode implements ActionMode.Callback {

		private final int position;

		public UserPermissionActionMode(int position) {
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
			mode.setTitle(permission.getRepository().getName());
			
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
							Toast.makeText(UserDetailsActivity.this, "Unable to remove permission!", Toast.LENGTH_SHORT).show();
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
						Intent intent = new Intent(C.action.START_REPOSITORY_DETAILS);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						intent.putExtra("repositoryId", permission.getRepository().getId());
						
						startActivity(intent);
						
						mode.finish();
						return true;
					}
					
				});
			
			if(!permission.isReadOnly()) {
				menu.add("Pull")
	        		.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
	        		.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
						
						@Override
						public boolean onMenuItemClick(MenuItem item) {
							try {
								permission.setReadOnly(true);
								getHelper().getPermissionDao().update(permission);
							} catch (SQLException e) {
								Log.e(TAG, "Unable to remove permission.", e);
								Toast.makeText(UserDetailsActivity.this, "Unable to update permission!", Toast.LENGTH_SHORT).show();
							}
							
							loadPermissionsListContent();
							
							mode.finish();
							return true;
						}
						
					});
			} else {
				menu.add("Pull & Push")
	        		.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
	        		.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
						
						@Override
						public boolean onMenuItemClick(MenuItem item) {
							try {
								permission.setReadOnly(false);
								getHelper().getPermissionDao().update(permission);
							} catch (SQLException e) {
								Log.e(TAG, "Unable to remove permission.", e);
								Toast.makeText(UserDetailsActivity.this, "Unable to update permission!", Toast.LENGTH_SHORT).show();
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
	
	public static class RepositoryListFragment extends BaseDialogFragment {

		private List<Repository> repositories;
		private OnPermissionListItemClickListener listener;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			if(savedInstanceState == null) {
				return;
			}
			
			RepositoryListArgs args = (RepositoryListArgs) savedInstanceState.getSerializable("arguments");
			if(args != null) {
				this.repositories = args.repositories;
				this.listener = args.listener;
			}
		}
		
		@Override
		public void setArguments(Bundle bundle) {
			RepositoryListArgs args = (RepositoryListArgs) bundle.getSerializable("arguments");
			if(args != null) {
				this.repositories = args.repositories;
				this.listener = args.listener;
			}
			
			super.setArguments(bundle);
		}
		
		@Override
		public void onSaveInstanceState(Bundle bundle) {
			bundle.putSerializable("arguments", new RepositoryListArgs(repositories, listener));
			super.onSaveInstanceState(bundle);
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			getDialog().setTitle("Add permission");
			
			ViewGroup root = (ViewGroup) inflater.inflate(R.layout.popup_list_dialog, null);

			TextView noRepositoriesTextView = (TextView) root.findViewById(R.id.popupListDialogNoRepositories);
			noRepositoriesTextView.setText(R.string.no_repositories);
			
			ListView repositoriesListView = (ListView) root.findViewById(R.id.popupListDialogListView);

			if(repositories.size() > 0) {
				BasePopupListAdapter<Repository> repositoryListAdapter = new RepositoriesPopupListAdapter(getActivity(), repositories, R.drawable.ic_db_pull_small, R.drawable.ic_db_pull_push_small);
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
			
			return root;
		}
		
	}
	
	private static class RepositoryListArgs implements Serializable {

		private static final long serialVersionUID = 20120910L;
		
		private final List<Repository> repositories;
		private final OnPermissionListItemClickListener listener;
		
		public RepositoryListArgs(List<Repository> repositories, OnPermissionListItemClickListener listener) {
			this.repositories = repositories;
			this.listener = listener;
		}
	}
	
}
