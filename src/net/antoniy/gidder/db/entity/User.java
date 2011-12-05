package net.antoniy.gidder.db.entity;

import java.io.Serializable;
import java.util.Collection;

import net.antoniy.gidder.db.DBC;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = DBC.users.table_name)
public class User implements Serializable {
	private static final long serialVersionUID = 20111204L;

	@DatabaseField(
			columnName = DBC.users.column_id, 
			generatedId = true, 
			canBeNull = false
		)
	private int id;
	
	@DatabaseField(columnName = DBC.users.column_fullname, canBeNull = false)
	private String fullname;
	
	@DatabaseField(columnName = DBC.users.column_email, canBeNull = false, unique = true)
	private String email;
	
	@DatabaseField(columnName = DBC.users.column_username, canBeNull = false, unique = true)
	private String username;
	
	@DatabaseField(columnName = DBC.users.column_password, canBeNull = false)
	private String password;
	
	@ForeignCollectionField(eager = true, orderColumnName = DBC.permissions.column_user_id)
	private Collection<Permission> permissions;
	
	public User() {
	}

	public User(int id, String fullname, String email, String username, String password) {
		this.id = id;
		this.fullname = fullname;
		this.email = email;
		this.username = username;
		this.password = password;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Collection<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(Collection<Permission> permissions) {
		this.permissions = permissions;
	}
	
}
