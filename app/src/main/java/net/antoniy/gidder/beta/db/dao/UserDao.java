package net.antoniy.gidder.beta.db.dao;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import net.antoniy.gidder.beta.db.DBC;
import net.antoniy.gidder.beta.db.DBHelper;
import net.antoniy.gidder.beta.db.entity.Permission;
import net.antoniy.gidder.beta.db.entity.User;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.SelectArg;

public class UserDao extends BaseDao<DBHelper, User, Integer>{

	public UserDao(DBHelper dbHelper, Dao<User, Integer> dao) {
		super(dbHelper, dao);
	}
	
	public User queryForUsername(String username) throws SQLException {
		SelectArg usernameArg = new SelectArg(username);
		
		List<User> users = dao.queryBuilder().where().eq(DBC.users.column_username, usernameArg).query();
		
		if(users.size() > 0) {
			return users.get(0);
		}
		
		return null;
	}
	
	public User queryForEmail(String email) throws SQLException {
		SelectArg usernameArg = new SelectArg(email);
		
		List<User> users = dao.queryBuilder().where().eq(DBC.users.column_email, usernameArg).query();
		
		if(users.size() > 0) {
			return users.get(0);
		}
		
		return null;
	}
	
	public User queryForUsernameAndActive(String username) throws SQLException {
		SelectArg usernameArg = new SelectArg(username);
		
		List<User> users = dao.queryBuilder().where().eq(DBC.users.column_active, true).and().eq(DBC.users.column_username, usernameArg).query();
		
		if(users.size() > 0) {
			return users.get(0);
		}
		
		return null;
	}

    public User queryForPublickey(String publickey) throws SQLException {
        SelectArg publickeyArg = new SelectArg(publickey);

        List<User> users = dao.queryBuilder().where().eq(DBC.users.column_publickey, publickeyArg).query();

        if(users.size() > 0) {
            return users.get(0);
        }

        return null;
    }
	
	@Override
	public int deleteById(Integer id) throws SQLException {
		dbHelper.getPermissionDao().deleteByUserId(id);
		
		return super.deleteById(id);
	}
	
	public List<User> getAllUsersWithoutPermissionForRepositoryId(int repositoryId) throws SQLException {
		List<User> users = queryForAll();
		List<Permission> permissions = dbHelper.getPermissionDao().getAllByRepositoryId(repositoryId);
		
		for (Permission permission : permissions) {
			Iterator<User> iter = users.iterator();
			while(iter.hasNext()) {
				User user = iter.next();
				if(user.getId() == permission.getUser().getId()) {
					users.remove(user);
					break;
				}
			}
		}
		
		return users;
	}
}
