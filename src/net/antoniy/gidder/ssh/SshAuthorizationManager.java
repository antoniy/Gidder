package net.antoniy.gidder.ssh;

import java.sql.SQLException;
import java.util.List;

import net.antoniy.gidder.db.DBC;
import net.antoniy.gidder.db.DBHelper;
import net.antoniy.gidder.db.entity.Permission;
import net.antoniy.gidder.db.entity.Repository;
import net.antoniy.gidder.db.entity.User;
import net.antoniy.gidder.exception.SshAuthorizationException;
import android.content.Context;

import com.j256.ormlite.stmt.QueryBuilder;

public class SshAuthorizationManager {
	private final static String TAG = SshAuthorizationManager.class.getSimpleName();
	
	private DBHelper dbHelper;

	public SshAuthorizationManager(Context context) {
		dbHelper = new DBHelper(context);
	}
	
	public boolean hasRepositoryPullPermission(String username, String repositoryMapping) throws SshAuthorizationException {
		return hasRepositoryPermission(username, repositoryMapping, true);
	}
	
	public boolean hasRepositoryPushPermission(String username, String repositoryMapping) throws SshAuthorizationException {
		return hasRepositoryPermission(username, repositoryMapping, false);
	}
		
	private boolean hasRepositoryPermission(String username, String repositoryMapping, boolean checkPull) throws SshAuthorizationException {
		try {
			// Query for user by username
			List<User> users = dbHelper.getUserDao().queryForEq(DBC.users.column_username, username);
			if(users.size() != 1) {
				throw new SshAuthorizationException("There should exactly one record in the database for username: " + username);
			}
			User user = users.get(0);
			
			// Query for repository with specified mapping
			List<Repository> repositories = dbHelper.getRepositoryDao().queryForEq(DBC.repositories.column_mapping, repositoryMapping);
			if(repositories.size() != 1) {
				throw new SshAuthorizationException("There should exactly one record in the database for repository mapping: " + repositoryMapping);
			}
			Repository repository = repositories.get(0);
			
			// Query for permission with specified user and repository ids
			QueryBuilder<Permission, Integer> queryBuilder = dbHelper.getPermissionDao().queryBuilder();
			List<Permission> permissions = queryBuilder.where().
					eq(DBC.permissions.column_user_id, user.getId()).and().
					eq(DBC.permissions.column_repository_id, repository.getId()).query();
			
			// If there is no permission record in the database - there is no positive permission set for the user.
			if(permissions.size() == 0) {
				return false;
			}
			Permission permission = permissions.get(0);
			
			// Check for pull or push
			if(checkPull) {
				return permission.isAllowPull();
			} else {
				return permission.isAllowPush();
			}
		} catch (SQLException e) {
			throw new SshAuthorizationException("I/O problem while quering the database.", e);
		}
	}
}
