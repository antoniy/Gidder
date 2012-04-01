package net.antoniy.gidder.ui.activity;

import java.sql.SQLException;

import net.antoniy.gidder.R;
import net.antoniy.gidder.db.entity.User;
import net.antoniy.gidder.ui.util.C;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.IntentAction;

public class UserDetailsActivity extends BaseActivity {
	private final static String TAG = UserDetailsActivity.class.getSimpleName();

	private int userId;
	private TextView fullnameTextView;
	private TextView emailTextView;
	private TextView activateTextView;
	private ImageView activateImageView;
	
	@Override
	protected void setup() {
		setContentView(R.layout.user_details);

		if(getIntent().getExtras() != null) {
			userId = getIntent().getExtras().getInt("userId", -1);
			Log.i(TAG, "UserID: " + userId);
			
			if(userId <= 0) {
				Toast.makeText(this, "No user ID specified!", Toast.LENGTH_SHORT);
				finish();
			}
		}
	}

	@Override
	protected void initComponents(Bundle savedInstanceState) {
		ActionBar actionBar = (ActionBar) findViewById(R.id.userDetailsActionBar);
		actionBar.setHomeAction(new IntentAction(this, new Intent(C.action.START_SLIDE_ACTIVITY), R.drawable.ic_actionbar_home));
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.addAction(new IntentAction(this, new Intent(C.action.START_PREFERENCE_ACTIVITY), R.drawable.ic_actionbar_settings));
		actionBar.setTitle(R.string.user_details);
		
		fullnameTextView = (TextView) findViewById(R.id.userDetailsName);
		emailTextView = (TextView) findViewById(R.id.userDetailsMail);
		activateTextView = (TextView) findViewById(R.id.userDetailsActiveLabel);
		activateImageView = (ImageView) findViewById(R.id.userDetailsActive);
		populateFieldsWithUserData();
	}
	
	private void populateFieldsWithUserData() {
		User user = null;
		try {
			user = getHelper().getUserDao().queryForId(userId);
		} catch (SQLException e) {
			Log.e(TAG, "Error retrieving user with id " + userId, e);
			return;
		}
		
		fullnameTextView.setText(user.getFullname());
		emailTextView.setText(user.getEmail());
		
		if(user.isActive()) {
			activateImageView.setImageResource(R.drawable.ic_activated);
			activateTextView.setText("active");
		} else {
			activateImageView.setImageResource(R.drawable.ic_deactivated);
			activateTextView.setText("not active");
		}
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
