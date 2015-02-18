package net.antoniy.gidder.beta.db.entity;

import java.io.Serializable;
import java.util.Collection;

import net.antoniy.gidder.beta.db.DBC;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = DBC.users.table_name)
public class User implements Serializable {
	private static final long serialVersionUID = 20111204L;

	@DatabaseField(columnName = DBC.users.column_id, generatedId = true, canBeNull = false)
	private int id;

	@DatabaseField(columnName = DBC.users.column_fullname, canBeNull = false)
	private String fullname;

	@DatabaseField(columnName = DBC.users.column_email, canBeNull = false, unique = true)
	private String email;

	@DatabaseField(columnName = DBC.users.column_username, canBeNull = false, unique = true)
	private String username;

	@DatabaseField(columnName = DBC.users.column_password, canBeNull = false)
	private String password;

    @DatabaseField(columnName = DBC.users.column_publickey, canBeNull = true, unique = true)
    private String publickey;

	@DatabaseField(columnName = DBC.users.column_active, canBeNull = false, defaultValue = "false")
	private boolean active;

	@DatabaseField(columnName = DBC.users.column_create_date, canBeNull = false)
	private long createDate;

	@ForeignCollectionField(eager = true, orderColumnName = DBC.permissions.column_user_id)
	private Collection<Permission> permissions;

	public User() {
	}

    public User(int id, String fullname, String email, String username,
                String password, String publickey, boolean active, long createDate) {
        this.id = id;
        this.fullname = fullname;
        this.email = email;
        this.username = username;
        this.password = password;
        this.publickey = publickey;
        this.active = active;
        this.createDate = createDate;
    }

    // left in for compatibility
	public User(int id, String fullname, String email, String username,
			String password, boolean active, long createDate) {
		this(id, fullname, email, username, password, null, active, createDate);
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

    public String getPublickey() { return publickey; }

    public void setPublickey(String publickey) { this.publickey = publickey; }

	public Collection<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(Collection<Permission> permissions) {
		this.permissions = permissions;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public long getCreateDate() {
		return createDate;
	}

	public void setCreateDate(long createDate) {
		this.createDate = createDate;
	}

}
