package net.antoniy.gidder.db.dao;

import java.sql.SQLException;
import java.util.List;

import net.antoniy.gidder.db.DBC;
import net.antoniy.gidder.db.entity.Repository;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.SelectArg;

public class RepositoryDao extends BaseDao<Repository, Integer> {

	public RepositoryDao(Dao<Repository, Integer> dao) {
		super(dao);
	}

	public Repository queryForMapping(String mapping) throws SQLException {
		SelectArg usernameArg = new SelectArg(mapping);
		
		List<Repository> repositories = dao.queryBuilder().where().eq(DBC.repositories.column_mapping, usernameArg).query();
		
		if(repositories.size() > 0) {
			return repositories.get(0);
		}
		
		return null;
	}
}
