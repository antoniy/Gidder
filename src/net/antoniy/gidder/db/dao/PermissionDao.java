package net.antoniy.gidder.db.dao;

import java.sql.SQLException;
import java.util.List;

import net.antoniy.gidder.db.DBC;
import net.antoniy.gidder.db.DBHelper;
import net.antoniy.gidder.db.entity.Permission;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedDelete;

public class PermissionDao extends BaseDao<DBHelper, Permission, Integer> {

	public PermissionDao(DBHelper dbHelper, Dao<Permission, Integer> dao) {
		super(dbHelper, dao);
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
	
	public int deleteByRepositoryId(Integer repositoryId) throws SQLException {
		DeleteBuilder<Permission, Integer> deleteBuilder = dao.deleteBuilder();
		deleteBuilder.where().eq(DBC.permissions.column_repository_id, repositoryId);
		
		PreparedDelete<Permission> preparedDelete = deleteBuilder.prepare();

		return dao.delete(preparedDelete);
	}
	
	public int deleteByUserId(Integer userId) throws SQLException {
		DeleteBuilder<Permission, Integer> deleteBuilder = dao.deleteBuilder();
		deleteBuilder.where().eq(DBC.permissions.column_user_id, userId);
		
		PreparedDelete<Permission> preparedDelete = deleteBuilder.prepare();

		return dao.delete(preparedDelete);
	}
}
