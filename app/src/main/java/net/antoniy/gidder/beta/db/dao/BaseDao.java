package net.antoniy.gidder.beta.db.dao;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;

public class BaseDao<H extends OrmLiteSqliteOpenHelper, T, ID> {
	protected final Dao<T, ID> dao;
	protected final H dbHelper;

	public BaseDao(H dbHelper, Dao<T, ID> dao) {
		this.dbHelper = dbHelper;
		this.dao = dao;
	}

	public T queryForId(ID id) throws SQLException {
		return dao.queryForId(id);
	}
	
	public int create(T entity) throws SQLException {
		return dao.create(entity);
	}
	
	public int update(T entity) throws SQLException {
		return dao.update(entity);
	}
	
	public List<T> queryForAll() throws SQLException {
		return dao.queryForAll();
	}
	
	public int deleteById(ID id) throws SQLException {
		return dao.deleteById(id);
	}
}
