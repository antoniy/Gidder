package net.antoniy.gidder.ui.activity;

import java.sql.SQLException;
import java.util.List;

import net.antoniy.gidder.R;
import net.antoniy.gidder.db.entity.Permission;
import net.antoniy.gidder.db.entity.User;
import net.antoniy.gidder.ui.adapter.UserPermissionsAdapter;
import net.antoniy.gidder.ui.util.C;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.IntentAction;

public class UserDetailsActivity extends BaseActivity implements OnItemLongClickListener, OnItemClickListener {
	private final static String TAG = UserDetailsActivity.class.getSimpleName();

	private final static int EDIT_USER_REQUEST_CODE = 1;
	
	private int userId;
	private TextView fullnameTextView;
	private TextView emailTextView;
	private TextView activateTextView;
	private ImageView activateImageView;
	private TextView noPermissionsTextView;
	private ListView permissionsListView;
	private UserPermissionsAdapter userPermissionsListAdapter;
	private Button editButton;
	private Button activateButton;
	private Button deleteButton;
	
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
		
		editButton = (Button) findViewById(R.id.userDetailsBtnEdit);
		editButton.setOnClickListener(this);
		
		activateButton = (Button) findViewById(R.id.userDetailsBtnActivateDeactivate);
		activateButton.setOnClickListener(this);
		
		deleteButton = (Button) findViewById(R.id.userDetailsBtnDelete);
		deleteButton.setOnClickListener(this);
		
		noPermissionsTextView = (TextView) findViewById(R.id.userDetailsNoPermissions);
		
		permissionsListView = (ListView) findViewById(R.id.userDetailsPermissions);
		permissionsListView.setOnItemLongClickListener(this);
		permissionsListView.setOnItemClickListener(this);
		
		loadUserPermissionsListContent();
		populateFieldsWithUserData();
	}
	
	private void loadUserPermissionsListContent() {
		List<Permission> permissions = null;
		try {
			permissions = getHelper().getPermissionDao().getAllByUserId(userId);
		} catch (SQLException e) {
			Log.e(TAG, "Could not retrieve permissions.", e);
			return;
		}
		
		showUserPermissionsList(permissions.size() > 0);
		
		userPermissionsListAdapter = new UserPermissionsAdapter(this, permissions);
		permissionsListView.setAdapter(userPermissionsListAdapter);
	}
	
	private void showUserPermissionsList(boolean show) {
		if(show) {
			permissionsListView.setVisibility(View.VISIBLE);
			noPermissionsTextView.setVisibility(View.GONE);
		} else {
			permissionsListView.setVisibility(View.GONE);
			noPermissionsTextView.setVisibility(View.VISIBLE);
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
		
		fullnameTextView.setText(user.getFullname());
		emailTextView.setText(user.getEmail());
		
		if(user.isActive()) {
			activateImageView.setImageResource(R.drawable.ic_activated);
			activateTextView.setText("active");
			activateButton.setText("Deactivate");
		} else {
			activateImageView.setImageResource(R.drawable.ic_deactivated);
			activateTextView.setText("not active");
			activateButton.setText("Activate");
		}
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		if(v.getId() == R.id.userDetailsBtnEdit) {
			Intent editUserIntent = new Intent(C.action.START_ADD_USER_ACTIVITY);
			editUserIntent.putExtra("userId", userId);
			
			startActivityForResult(editUserIntent, EDIT_USER_REQUEST_CODE);
		} else if(v.getId() == R.id.userDetailsBtnActivateDeactivate) {
			User user = null;
			try {
				user = getHelper().getUserDao().queryForId(userId);
				user.setActive(!user.isActive());
				getHelper().getUserDao().update(user);
			} catch (SQLException e) {
				Log.e(TAG, "Error retrieving user with id " + userId, e);
				return;
			}
			
			populateFieldsWithUserData();
		} else if(v.getId() == R.id.userDetailsBtnDelete) {
			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
			        if(which == DialogInterface.BUTTON_POSITIVE) {
			            try {
							getHelper().getUserDao().deleteById(userId);

							setResult(Activity.RESULT_OK);
							finish();
						} catch (SQLException e) {
							Log.e(TAG, "Problem while deleting user.", e);
						}
			        }
			    }
			};

			User user = null;
			try {
				user = getHelper().getUserDao().queryForId(userId);
			} catch (SQLException e) {
				Log.e(TAG, "Error retrieving user with id " + userId, e);
				return;
			}
			
			AlertDialog.Builder builder = new AlertDialog.Builder(UserDetailsActivity.this);
			builder.setMessage("Delete " + user.getFullname() + "?").setPositiveButton("Yes", dialogClickListener)
			    .setNegativeButton("No", null).show();
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == EDIT_USER_REQUEST_CODE) {
			populateFieldsWithUserData();
		}
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
