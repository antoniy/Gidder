package net.antoniy.gidder.ui.activity;

import net.antoniy.gidder.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class UserDetailsActivity extends BaseActivity {
	private final static String TAG = UserDetailsActivity.class.getSimpleName();
	
//	private static final Pattern emailPattern = Pattern.compile( "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
//	private static final int CONTACT_PICKER = 1;
	
//	public final static int REQUEST_CODE_ADD_USER = 1;
//	public final static int REQUEST_CODE_EDIT_USER = 2;
	
//	private Button addEditButton;
//	private Button cancelButton;
//	private EditText fullnameEditText;
//	private EditText emailEditText;
//	private EditText usernameEditText;
//	private EditText passwordEditText;
//	private boolean editMode = false;
//	private int userId;
//	private CheckBox activateCheckox;
//	private TextView contactPicker;
	
	@Override
	protected void setup() {
		setContentView(R.layout.user_details);

//		if(getIntent().getExtras() != null) {
//			userId = getIntent().getExtras().getInt("userId", -1);
//			Log.i(TAG, "UserID: " + userId);
//			
//			if(userId > 0) {
//				editMode = true;
//			} else {
//				editMode = false;
//			}
//		}
	}

	@Override
	protected void initComponents(Bundle savedInstanceState) {
//		contactPicker = (TextView) findViewById(R.id.add_user_contacts);
//		contactPicker.setOnClickListener(this);
//		
//		addEditButton = (Button) findViewById(R.id.addUserBtnAddEdit);
//		addEditButton.setOnClickListener(this);
//		if(editMode) {
//			addEditButton.setText(R.string.btn_save);
//		} else {
//			addEditButton.setText(R.string.btn_add);
//		}
//		
//		cancelButton = (Button) findViewById(R.id.addUserBtnCancel);
//		cancelButton.setOnClickListener(this);
//		
//		fullnameEditText = (EditText) findViewById(R.id.addUserFullname);
//		emailEditText = (EditText) findViewById(R.id.addUserEmail);
//		usernameEditText = (EditText) findViewById(R.id.addUserUsername);
//		passwordEditText = (EditText) findViewById(R.id.addUserPassword);
//		activateCheckox = (CheckBox) findViewById(R.id.addUserActivate);
//		
//		ActionBar actionBar = (ActionBar) findViewById(R.id.addUserActionBar);
//        actionBar.setHomeAction(new IntentAction(this, new Intent(C.action.START_SLIDE_ACTIVITY), R.drawable.ic_actionbar_home));
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.addAction(new IntentAction(this, new Intent(C.action.START_PREFERENCE_ACTIVITY), R.drawable.ic_actionbar_settings));
//
//        if(editMode) {
//        	actionBar.setTitle(R.string.add_user_edittitle);
//        } else {
//        	actionBar.setTitle(R.string.add_user_title);
//        }
//		
//		if(editMode) {
//			populateFieldsWithUserData();
//		}
	}
	
	private void populateFieldsWithUserData() {
//		User user = null;
//		try {
//			user = getHelper().getUserDao().queryForId(userId);
//		} catch (SQLException e) {
//			Log.e(TAG, "Error retrieving user with id " + userId, e);
//			return;
//		}
//		
//		fullnameEditText.setText(user.getFullname());
//		emailEditText.setText(user.getEmail());
//		usernameEditText.setText(user.getUsername());
//		passwordEditText.setHint("SHA1: " + user.getPassword());
//		activateCheckox.setChecked(user.isActive());
		
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		
//		if(v.getId() == R.id.addUserBtnAddEdit) {
//			if(!isFieldsValid(editMode)) {
//				return;
//			}
//			
//			String fullname = fullnameEditText.getText().toString();
//			String email = emailEditText.getText().toString();
//			String username = usernameEditText.getText().toString();
//			String password = passwordEditText.getText().toString();
//			boolean active = activateCheckox.isChecked();
//			
//			try {
//				if(editMode) {
//					User user = getHelper().getUserDao().queryForId(userId);
//					user.setFullname(fullname);
//					user.setEmail(email);
//					
//					if(password != null && !"".equals(password.trim())) {
//						user.setPassword(GidderCommons.generateSha1(password));
//					}
//					user.setUsername(username);
//					user.setActive(active);
//					
//					getHelper().getUserDao().update(user);
//				} else {
//					getHelper().getUserDao().create(new User(0, fullname, email, username, GidderCommons.generateSha1(password), active, System.currentTimeMillis()));
//				}
//			} catch (SQLException e) {
//				Log.e(TAG, "Problem when add new user.", e);
//				finish();
//				return;
//			}
//			
//			setResult(RESULT_OK, null);
//			finish();
//		} else if(v.getId() == R.id.addUserBtnCancel) {
//			finish();
//		} else if(v.getId() == R.id.add_user_contacts) {
//			Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
//			startActivityForResult(intent, CONTACT_PICKER);
//		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
//		if(requestCode == CONTACT_PICKER) {
//			if(resultCode != Activity.RESULT_OK) {
//				Log.w(TAG, "Picking a contact failed!");
//			}
//			
//			if(data == null) {
//				return;
//			}
//			
//			Uri contactData = data.getData();
//			
//			if(contactData == null) {
//				return;
//			}
//			
//			Cursor c = managedQuery(contactData, null, null, null, null);
//			if(c.moveToFirst()) {
//				String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
//				String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//				
//				if(name != null && !"".equals(name.trim())) {
//					fullnameEditText.setText(name);
//					
//					String firstWord = name.trim().split("\\s+")[0];
//					usernameEditText.setText(GidderCommons.toCamelCase(firstWord));
//				}
//				
//				List<String> emailAddresses = new ArrayList<String>();
//				Cursor emails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null);
//				while (emails.moveToNext()) {
//					emailAddresses.add(emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)));
//				}
//				emails.close();
//				
//				if(emailAddresses.size() == 1) {
//					emailEditText.setText(emailAddresses.get(0));
//				} else if(emailAddresses.size() > 1) {
//					
//					final String[] items = new String[emailAddresses.size()]; 
//					emailAddresses.toArray(items);
//					
//					AlertDialog.Builder builder = new AlertDialog.Builder(this);
//			        builder.setTitle(R.string.dialog_pick_e_mail_address);
//			        builder.setIcon(R.drawable.ic_email);
//			        builder.setItems(items, new DialogInterface.OnClickListener(){
//			            public void onClick(DialogInterface dialogInterface, int item) {
//			            	emailEditText.setText(items[item]);
//			            }
//			        });
//			        builder.create().show();
//				}
//			}
//			
//			c.deactivate();
//		}
	}
	
}
