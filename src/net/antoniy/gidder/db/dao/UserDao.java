package net.antoniy.gidder.db.dao;

import java.sql.SQLException;
import java.util.List;

import net.antoniy.gidder.db.DBC;
import net.antoniy.gidder.db.entity.User;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.SelectArg;

public class UserDao extends BaseDao<User, Integer>{

	public UserDao(Dao<User, Integer> dao) {
		super(dao);
	}
	
	public User queryForUsername(String username) throws SQLException {
		SelectArg usernameArg = new SelectArg(username);
		
		List<User> users = dao.queryBuilder().where().eq(DBC.users.column_username, usernameArg).query();
		
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
}
