package net.antoniy.gidder.activity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.j256.ormlite.stmt.QueryBuilder;

import net.antoniy.gidder.R;
import net.antoniy.gidder.adapter.UserPermissionsAdapter;
import net.antoniy.gidder.db.DBC;
import net.antoniy.gidder.db.entity.Permission;
import net.antoniy.gidder.db.entity.Repository;
import net.antoniy.gidder.db.entity.User;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class AddUserActivity extends BaseActivity {
	private final static String TAG = AddUserActivity.class.getSimpleName();
	private static final Pattern emailPattern = Pattern.compile( "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
	
	public final static int REQUEST_CODE_ADD_USER = 1;
	public final static int REQUEST_CODE_EDIT_USER = 2;
	
	private Button addUserButton;
	private Button cancelButton;
	private EditText fullnameEditText;
	private EditText emailEditText;
	private EditText usernameEditText;
	private EditText passwordEditText;
	private ListView permissionsListView;
	private UserPermissionsAdapter permissionAdapter;
	private boolean editMode = false;
	private int userId;
	
	@Override
	protected void setup() {
		setContentView(R.layout.add_user);
	}

	@Override
	protected void initComponents(Bundle savedInstanceState) {
		addUserButton = (Button) findViewById(R.id.addUserBtnAdd);
		addUserButton.setOnClickListener(this);
		
		cancelButton = (Button) findViewById(R.id.addUserBtnCancel);
		cancelButton.setOnClickListener(this);
		
		fullnameEditText = (EditText) findViewById(R.id.addUserFullname);
		emailEditText = (EditText) findViewById(R.id.addUserEmail);
		usernameEditText = (EditText) findViewById(R.id.addUserUsername);
		passwordEditText = (EditText) findViewById(R.id.addUserPassword);
		
		permissionsListView = (ListView) findViewById(R.id.addUserPermissions);
		loadPermissionsListContent();

		if(getIntent().getExtras() != null) {
			userId = getIntent().getExtras().getInt("userId", -1);
			Log.i(TAG, "UserID: " + userId);
			
			if(userId > 0) {
				editMode = true;
				populateFieldsWithUserData();
			} else {
				editMode = false;
			}
		}
	}
	
	private void populateFieldsWithUserData() {
		User user = null;
		try {
			user = getHelper().getUserDao().queryForId(userId);
		} catch (SQLException e) {
			Log.e(TAG, "Error retrieving user with id " + userId, e);
			return;
		}
		
		fullnameEditText.setText(user.getFullname());
		emailEditText.setText(user.getEmail());
		usernameEditText.setText(user.getUsername());
		passwordEditText.setText(user.getPassword());
	}
	
	private void loadPermissionsListContent() {
		List<Repository> repositories = null;
		try {
//			User user = getHelper().getUserDao().queryForId(userId);
//			
//			QueryBuilder<Permission, Integer> queryBuilder = getHelper().getPermissionDao().queryBuilder();
//			List<Permission> permissions = queryBuilder.where().eq(DBC.permissions.column_user_id, user.getId()).query();

			repositories = getHelper().getRepositoryDao().queryForAll();
		} catch (SQLException e) {
			Log.e(TAG, "Could not retrieve repositories.", e);
			return;
		}

		// if we add new user
		List<Permission> permissions = new ArrayList<Permission>(repositories.size());
		for (Repository repository : repositories) {
			permissions.add(new Permission(0, null, repository, false, false));
		}
		
		permissionAdapter = new UserPermissionsAdapter(this, R.layout.user_permission_item, permissions);
		permissionsListView.setAdapter(permissionAdapter);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		if(v.getId() == R.id.addUserBtnAdd) {
			if(!isFieldsValid()) {
				return;
			}
			
			String fullname = fullnameEditText.getText().toString();
			String email = emailEditText.getText().toString();
			String username = usernameEditText.getText().toString();
			String password = passwordEditText.getText().toString();
			
			try {
				getHelper().getUserDao().create(new User(0, fullname, email, username, password));
			} catch (SQLException e) {
				Log.e(TAG, "Problem when add new user.", e);
				finish();
				return;
			}
			
			setResult(RESULT_OK, null);
			finish();
		} else if(v.getId() == R.id.addUserBtnCancel) {
			finish();
		}
	}
	
	private boolean isFieldsValid() {
		boolean isAllFieldsValid = true;
		
		if(!isFullnameValid()) {
			isAllFieldsValid = false;
		}
		
		if(!isEmailValid()) {
			isAllFieldsValid = false;
		}
		
		if(!isUsernameValid()) {
			isAllFieldsValid = false;
		}
		
		if(!isPasswordValid()) {
			isAllFieldsValid = false;
		}
		
		return isAllFieldsValid;
	}
	
	private boolean isEditTextEmpty(EditText tv) {
		String text = tv.getText().toString();
		if("".equals(text.trim())) {
			tv.setError("Field must contain value");
			return true;
		} else {
			return false;
		}
	}
	
	private boolean isFullnameValid() {
		return !isEditTextEmpty(fullnameEditText);
	}
	
	private boolean isPasswordValid() {
		return !isEditTextEmpty(passwordEditText);
	}
	
	private boolean isUsernameValid() {
		return !isEditTextEmpty(usernameEditText);
	}
	
	private boolean isEmailValid() {
		if(isEditTextEmpty(emailEditText)) {
			return false;
		}
		
		String email = emailEditText.getText().toString();
		Matcher matcher = emailPattern.matcher(email);
		if(!matcher.matches()) {
			emailEditText.setError("E-mail address is incorrect");
			return false;
		}
		
		return true;
	}
	
}
