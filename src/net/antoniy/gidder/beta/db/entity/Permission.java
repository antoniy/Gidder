package net.antoniy.gidder.beta.db.entity;

import java.io.Serializable;

import net.antoniy.gidder.beta.db.DBC;

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
	
	@DatabaseField(columnName = DBC.permissions.column_read_only, canBeNull = false, defaultValue = "false")
	private boolean readOnly;
	
	public Permission() {
	}

	public Permission(int id, User user, Repository repository, boolean readOnly) {
		this.id = id;
		this.user = user;
		this.repository = repository;
		this.setReadOnly(readOnly);
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

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	
}
