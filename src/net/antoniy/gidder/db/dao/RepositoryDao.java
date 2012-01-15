package net.antoniy.gidder.db.dao;

import java.sql.SQLException;
import java.util.List;

import net.antoniy.gidder.db.DBC;
import net.antoniy.gidder.db.DBHelper;
import net.antoniy.gidder.db.entity.Repository;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.SelectArg;

public class RepositoryDao extends BaseDao<DBHelper, Repository, Integer> {

	public RepositoryDao(DBHelper dbHelper, Dao<Repository, Integer> dao) {
		super(dbHelper, dao);
	}

	public Repository queryForMapping(String mapping) throws SQLException {
		SelectArg usernameArg = new SelectArg(mapping);
		
		List<Repository> repositories = dao.queryBuilder().where().eq(DBC.repositories.column_mapping, usernameArg).query();
		
		if(repositories.size() > 0) {
			return repositories.get(0);
		}
		
		return null;
	}
	
	@Override
	public int deleteById(Integer id) throws SQLException {
		dbHelper.getPermissionDao().deleteByRepositoryId(id);
		
		return super.deleteById(id);
	}
}
