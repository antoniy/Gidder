package net.antoniy.gidder.ui.adapter;

import java.sql.SQLException;
import java.util.List;

import net.antoniy.gidder.R;
import net.antoniy.gidder.db.entity.Permission;
import net.antoniy.gidder.db.entity.Repository;
import net.antoniy.gidder.db.entity.User;
import net.antoniy.gidder.ui.activity.BaseActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RepositoryPermissionsAdapter extends BaseExpandableListAdapter implements OnClickListener {
	private final static String TAG = RepositoryPermissionsAdapter.class.getSimpleName();
	
	private final List<User> users;
	private final int repositoryId;
	private final LayoutInflater inflater;

	private final BaseActivity context;

	public RepositoryPermissionsAdapter(BaseActivity context, List<User> users, int repositoryId) {
		this.context = context;
		this.users = users;
		this.repositoryId = repositoryId;
		this.inflater = (LayoutInflater) context.getSystemService(BaseActivity.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public Permission getChild(int groupPosition, int childPosition) {
		User user = getGroup(groupPosition);
		
		if(user == null) {
			return null;
		}
		
		return extractPermission(user);
	}
	
	private Permission extractPermission(User user) {
		for (Permission permission : user.getPermissions()) {
			if(permission.getRepository().getId() == repositoryId) {
				return permission;
			}
		}
		
		// Create new empty permission instance
		Repository repository = new Repository(repositoryId, null, null, null, false, 0);
		Permission permission = new Permission(0, user, repository, false, false);
		
		return permission;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		Permission permission = getChild(groupPosition, childPosition);
		
		if(permission == null) {
			return 0;
		}
		
		return permission.getId();
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		User user = getGroup(groupPosition);
		if(user == null) {
			Log.e(TAG, "No user for group position: " + groupPosition);
			return null;
		}
		
		View v;
		if(convertView != null) {
			v = convertView;
		} else {
			v = inflater.inflate(R.layout.repository_permissions_child_item, null);
		}
		
		Permission permission = getChild(groupPosition, childPosition);

		ImageView pullImageView = (ImageView) v.findViewById(R.id.repositoryPermissionsPullImage);
		pullImageView.setOnClickListener(this);
		pullImageView.setTag(user);
		if(permission.isAllowPull()) {
			pullImageView.setImageResource(R.drawable.ic_pull_checked);
		} else {
			pullImageView.setImageResource(R.drawable.ic_pull_unchecked);
		}
		
		ImageView pushImageView = (ImageView) v.findViewById(R.id.repositoryPermissionsPushImage);
		pushImageView.setOnClickListener(this);
		pushImageView.setTag(user);
		if(permission.isAllowPush()) {
			pushImageView.setImageResource(R.drawable.ic_push_checked);
		} else {
			pushImageView.setImageResource(R.drawable.ic_push_unchecked);
		}
		
		return v;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return 1;
	}

	@Override
	public User getGroup(int groupPosition) {
		if(groupPosition > users.size()) {
			return null;
		}
		
		return users.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return users.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		User user = getGroup(groupPosition);
		
		if(user == null) {
			return 0;
		}
		
		return user.getId();
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		User user = getGroup(groupPosition);
		if(user == null) {
			Log.e(TAG, "No user for group position: " + groupPosition);
			return null;
		}
		
		View v;
		if(convertView != null) {
			v = convertView;
		} else {
			v = inflater.inflate(R.layout.repository_permissions_group_item, null);
		}
		
		TextView fullname = (TextView) v.findViewById(R.id.repositoryPermissionGroupItemFullname);
		fullname.setText(user.getFullname());
		
		return v;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	@Override
	public void onClick(View v) {
		User user = (User) v.getTag();
		Permission permission = extractPermission(user);
		
		if(permission == null) {
			Log.e(TAG, "Permission instance should not be null!");
			return;
		}
		
		if(v.getId() == R.id.repositoryPermissionsPullImage) {
			permission.setAllowPull(!permission.isAllowPull());
		} else if(v.getId() == R.id.repositoryPermissionsPushImage) {
			permission.setAllowPush(!permission.isAllowPush());
		}
			
		// Check if there already is a persisted permission instance
		if(permission.getId() > 0) {
			// If permission instance is persisted check if we need to update it or remove it.
			if(permission.isAllowPull() || permission.isAllowPush()) {
				try {
					context.getHelper().getPermissionDao().update(permission);
				} catch (SQLException e) {
					Log.e(TAG, "Cannot update permission instance.", e);
					return;
				}
				Log.i(TAG, "Updated!");
			} else {
				try {
					context.getHelper().getPermissionDao().deleteById(permission.getId());
				} catch (SQLException e) {
					Log.e(TAG, "Cannot delete permission instance.", e);
					return;
				}
				permission.setId(0);
				Log.i(TAG, "Removed!");
			}
		} else {
			
			// If the permission instance is not persisted till now check if we need to persist it
			if(permission.isAllowPull() || permission.isAllowPush()) {
				// This actually persists the new permission instance automatically.
				user.getPermissions().add(permission);
				
				Log.i(TAG, "Persisted!");
			}
		}
		
		notifyDataSetChanged();
	}

}
