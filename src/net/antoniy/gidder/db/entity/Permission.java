package net.antoniy.gidder.db.entity;

import java.io.Serializable;

import net.antoniy.gidder.db.DBC;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = DBC.permissions.table_name)
public class Permission implements Serializable {
	private static final long serialVersionUID = 20111205L;

	@DatabaseField(columnName = DBC.permissions.column_id, generatedId = true, canBeNull = false)
	private int id;
	
	@DatabaseField(
			columnName = DBC.permissions.column_user_id, 
			foreign = true, 
			foreignAutoRefresh = true, 
			foreignAutoCreate = true, 
			uniqueCombo = true,
			canBeNull = false
		)
	private User user;
	
	@DatabaseField(
			columnName = DBC.permissions.column_repository_id, 
			foreign = true, 
			foreignAutoRefresh = true, 
			foreignAutoCreate = true,
			uniqueCombo = true,
			canBeNull = false
		)
	private Repository repository;
	
	@DatabaseField(columnName = DBC.permissions.column_allow_pull, canBeNull = false, defaultValue = "false")
	private boolean allowPull;
	
	@DatabaseField(columnName = DBC.permissions.column_allow_push, canBeNull = false, defaultValue = "false")
	private boolean allowPush;
	
	public Permission() {
	}

	public Permission(int id, User user, Repository repository, boolean allowPull, boolean allowPush) {
		this.id = id;
		this.user = user;
		this.repository = repository;
		this.allowPull = allowPull;
		this.allowPush = allowPush;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Repository getRepository() {
		return repository;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	public boolean isAllowPull() {
		return allowPull;
	}

	public void setAllowPull(boolean allowPull) {
		this.allowPull = allowPull;
	}

	public boolean isAllowPush() {
		return allowPush;
	}

	public void setAllowPush(boolean allowPush) {
		this.allowPush = allowPush;
	}
	
}
