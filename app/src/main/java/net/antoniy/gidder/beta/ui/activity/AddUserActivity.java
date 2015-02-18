package net.antoniy.gidder.beta.ui.activity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.antoniy.gidder.beta.R;
import net.antoniy.gidder.beta.db.entity.User;
import net.antoniy.gidder.beta.ui.util.C;
import net.antoniy.gidder.beta.ui.util.GidderCommons;

import org.apache.commons.validator.routines.EmailValidator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class AddUserActivity extends BaseActivity {
	private final static String TAG = AddUserActivity.class.getSimpleName();
	
	private static final int CONTACT_PICKER = 1;
	
	public final static int REQUEST_CODE_ADD_USER = 1;
	public final static int REQUEST_CODE_EDIT_USER = 2;
	
	private EditText fullnameEditText;
	private EditText emailEditText;
	private EditText usernameEditText;
	private EditText passwordEditText;
    private EditText publickeyEditText;
	private boolean editMode = false;
	private int userId;
	private CheckBox activateCheckox;
	
	@Override
	protected void setup() {
		setContentView(R.layout.add_user);

		if(getIntent().getExtras() != null) {
			userId = getIntent().getExtras().getInt("userId", -1);
			Log.i(TAG, "UserID: " + userId);
			
			if(userId > 0) {
				editMode = true;
			} else {
				editMode = false;
			}
		}
		
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	@Override
	protected void initComponents(Bundle savedInstanceState) {
		fullnameEditText = (EditText) findViewById(R.id.addUserFullname);
		emailEditText = (EditText) findViewById(R.id.addUserEmail);
		usernameEditText = (EditText) findViewById(R.id.addUserUsername);
		passwordEditText = (EditText) findViewById(R.id.addUserPassword);
        publickeyEditText = (EditText) findViewById(R.id.addUserPublickey);
		activateCheckox = (CheckBox) findViewById(R.id.addUserActivate);
		
		if(editMode) {
			populateFieldsWithUserData();
		}
	}
	
	@Override
	protected void setupActionBar() {
		if(editMode) {
        	getSupportActionBar().setTitle(R.string.add_user_edittitle);
        } else {
        	getSupportActionBar().setTitle(R.string.add_user_title);
        }
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == android.R.id.home) {
			Intent intent = new Intent(C.action.START_SETUP_ACTIVITY);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			
			finish();
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem doneMenuItem = menu.add("Save").setIcon(R.drawable.ic_actionbar_accept);
		doneMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		doneMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				processUserAction();
				return true;
			}
			
		});
		
		MenuItem fromContactMenuItem = menu.add("From Contact");
		fromContactMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		fromContactMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult(intent, CONTACT_PICKER);
				return true;
			}
			
		});
		
		MenuItem cancelMenuItem = menu.add("Cancel").setIcon(R.drawable.ic_actionbar_cancel);
		cancelMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		cancelMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				finish();
				return false;
			}
			
		});
		
		return super.onCreateOptionsMenu(menu);
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
        publickeyEditText.setText(user.getPublickey());

		String password = user.getPassword();
		if(password != null && password.length() > 16) {
			passwordEditText.setHint("SHA1: " + password.substring(0, 16) + "...");
		} else {
			passwordEditText.setHint("SHA1: " + password);
		}
		activateCheckox.setChecked(user.isActive());
		
	}
	
	private void processUserAction() {
		if(!isFieldsValid(editMode)) {
			return;
		}
		
		String fullname = fullnameEditText.getText().toString().trim();
		String email = emailEditText.getText().toString().trim();
		String username = usernameEditText.getText().toString().trim();
		String password = passwordEditText.getText().toString().trim();
        String publickey = publickeyEditText.getText().toString().trim();
        Log.i(TAG, "read publickey: " + publickey);
		boolean active = activateCheckox.isChecked();
		
		if(editMode) {
			try {
				User checkUser = getHelper().getUserDao().queryForUsername(usernameEditText.getText().toString().trim());
				if(checkUser != null && checkUser.getId() != userId) {
					Toast.makeText(AddUserActivity.this, "Username already exists.", Toast.LENGTH_SHORT).show();
					return;
				}
				
				checkUser = getHelper().getUserDao().queryForEmail(emailEditText.getText().toString().trim());
				if(checkUser != null && checkUser.getId() != userId) {
					Toast.makeText(AddUserActivity.this, "E-mail already exists.", Toast.LENGTH_SHORT).show();
					return;
				}

                checkUser = getHelper().getUserDao().queryForPublickey(publickeyEditText.getText().toString().trim());
                if(checkUser != null && checkUser.getId() != userId) {
                    Toast.makeText(AddUserActivity.this, "Public Key already exists.", Toast.LENGTH_SHORT).show();
                    return;
                }
			} catch (SQLException e) {
				Log.e(TAG, "SQL problem.", e);
				Toast.makeText(AddUserActivity.this, "Database error.", Toast.LENGTH_SHORT).show();
				return;
			}
			
			try {
				User user = getHelper().getUserDao().queryForId(userId);
				user.setFullname(fullname);
				user.setEmail(email);
				
				if(password != null && !"".equals(password.trim())) {
					user.setPassword(GidderCommons.generateSha1(password));
				}
                if(publickey != null && !"".equals(publickey)) {
                    user.setPublickey(publickey);
                }
				user.setUsername(username);
				user.setActive(active);
				
				getHelper().getUserDao().update(user);
			} catch (SQLException e) {
				Log.e(TAG, "Problem when edit user.", e);
				finish();
				return;
			}
		} else {
			try {
				User checkUser = getHelper().getUserDao().queryForUsername(usernameEditText.getText().toString().trim());
				if(checkUser != null) {
					Toast.makeText(AddUserActivity.this, "Username already exists.", Toast.LENGTH_SHORT).show();
					return;
				}
				
				checkUser = getHelper().getUserDao().queryForEmail(emailEditText.getText().toString().trim());
				if(checkUser != null) {
					Toast.makeText(AddUserActivity.this, "E-mail already exists.", Toast.LENGTH_SHORT).show();
					return;
				}

                checkUser = getHelper().getUserDao().queryForPublickey(publickeyEditText.getText().toString().trim());
                if(checkUser != null && checkUser.getId() != userId) {
                    Toast.makeText(AddUserActivity.this, "Public Key already exists.", Toast.LENGTH_SHORT).show();
                    return;
                }
			} catch (SQLException e) {
				Log.e(TAG, "SQL problem.", e);
				Toast.makeText(AddUserActivity.this, "Database error.", Toast.LENGTH_SHORT).show();
				return;
			}
			
			try {
				getHelper().getUserDao().create(new User(0, fullname, email, username, GidderCommons.generateSha1(password), publickey, active, System.currentTimeMillis()));
			} catch (SQLException e) {
				Log.e(TAG, "Problem when add user.", e);
				finish();
				return;
			}
		}
		
		setResult(RESULT_OK, null);
		finish();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == CONTACT_PICKER) {
			if(resultCode != Activity.RESULT_OK) {
				Log.w(TAG, "Picking a contact failed!");
			}
			
			if(data == null) {
				return;
			}
			
			Uri contactData = data.getData();
			
			if(contactData == null) {
				return;
			}
			
			Cursor c = managedQuery(contactData, null, null, null, null);
			if(c.moveToFirst()) {
				String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
				String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				
				if(name != null && !"".equals(name.trim())) {
					fullnameEditText.setText(name);
					
					String firstWord = name.trim().split("\\s+")[0];
					usernameEditText.setText(GidderCommons.toCamelCase(firstWord));
				}
				
				List<String> emailAddresses = new ArrayList<String>();
				Cursor emails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null);
				while (emails.moveToNext()) {
					emailAddresses.add(emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)));
				}
				emails.close();
				
				if(emailAddresses.size() == 1) {
					emailEditText.setText(emailAddresses.get(0));
				} else if(emailAddresses.size() > 1) {
					
					final String[] items = new String[emailAddresses.size()]; 
					emailAddresses.toArray(items);
					
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
			        builder.setTitle(R.string.dialog_pick_e_mail_address);
			        builder.setIcon(R.drawable.ic_email);
			        builder.setItems(items, new DialogInterface.OnClickListener(){
			            public void onClick(DialogInterface dialogInterface, int item) {
			            	emailEditText.setText(items[item]);
			            }
			        });
			        builder.create().show();
				}
			}
			
			c.deactivate();
		}
	}
	
	private boolean isFieldsValid(boolean doNotValidatePassword) {
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
		
		if(!doNotValidatePassword && !isPasswordValid()) {
			isAllFieldsValid = false;
		}
		
		return isAllFieldsValid;
	}
	
	private boolean isEditTextEmpty(EditText tv) {
		String text = tv.getText().toString();
		if("".equals(text.trim())) {
			tv.startAnimation(AnimationUtils.loadAnimation(AddUserActivity.this, R.anim.shake));
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
		
		if(!EmailValidator.getInstance().isValid(emailEditText.getText().toString().trim())) {
            emailEditText.startAnimation(AnimationUtils.loadAnimation(AddUserActivity.this, R.anim.shake));
            emailEditText.setError("Field must contain a valid E-Mail");
            return false;
        }
        return true;
	}
	
}
