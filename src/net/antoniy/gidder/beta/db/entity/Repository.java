package net.antoniy.gidder.beta.db.entity;

import java.io.Serializable;
import java.util.Collection;

import net.antoniy.gidder.beta.db.DBC;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = DBC.repositories.table_name)
public class Repository implements Serializable {
	private static final long serialVersionUID = 20111204L;

	@DatabaseField(columnName = DBC.repositories.column_id, generatedId = true, canBeNull = false)
	private int id;
	
	@DatabaseField(columnName = DBC.repositories.column_name, canBeNull = false)
	private String name;
	
	@DatabaseField(columnName = DBC.repositories.column_mapping, canBeNull = false, unique = true)
	private String mapping;
	
	@DatabaseField(columnName = DBC.repositories.column_description, canBeNull = true)
	private String description;
	
	@DatabaseField(columnName = DBC.repositories.column_active, canBeNull = false, defaultValue = "false")
	private boolean active;
	
	@DatabaseField(columnName = DBC.repositories.column_create_date, canBeNull = false)
	private long createDate;
	
	@ForeignCollectionField(eager = true, orderColumnName = DBC.permissions.column_repository_id)
	private Collection<Permission> permissions;
	
	public Repository() {
	}

	public Repository(int id, String name, String mapping, String description,
			boolean active, long createDate) {
		super();
		this.id = id;
		this.name = name;
		this.mapping = mapping;
		this.description = description;
		this.active = active;
		this.createDate = createDate;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMapping() {
		return mapping;
	}

	public void setMapping(String mapping) {
		this.mapping = mapping;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

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
