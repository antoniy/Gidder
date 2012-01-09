package net.antoniy.gidder.db.dao;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;

import net.antoniy.gidder.db.DBC;
import net.antoniy.gidder.db.entity.Permission;

public class PermissionDao extends BaseDao<Permission, Integer> {

	public PermissionDao(Dao<Permission, Integer> dao) {
		super(dao);
	}

	public Permission queryForUserAndRepository(int userId, int repositoryId) throws SQLException {
		List<Permission> permissions = dao.queryBuilder().where().
				eq(DBC.permissions.column_user_id, userId).and().
				eq(DBC.permissions.column_repository_id, repositoryId).query();
		
		if(permissions.size() > 0) {
			return permissions.get(0);
		}
		
		return null;
	}
}
