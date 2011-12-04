package net.antoniy.gidder.activity;

import net.antoniy.gidder.R;
import net.antoniy.gidder.db.DBC;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class UsersActivity extends BaseActivity implements OnClickListener {
	private final static String TAG = UsersActivity.class.getSimpleName();
	private final static int REQUEST_CODE_ADD_USER = 1;
	
	private final static String INTENT_ACTION_START_ADD_USER = "net.antoniy.gidder.START_ADD_USER_ACTIVITY";
	
	private Button doneButton;
	private Button addButton;
	private ListView usersListView;
	private CursorAdapter usersListAdapter;
	private Cursor usersCursor;
	
	@Override
	protected void setup() {
		setContentView(R.layout.users);
	}
	
	
	@Override
	protected void initComponents(Bundle savedInstanceState) {
		addButton = (Button) findViewById(R.id.usersAddButton);
		addButton.setOnClickListener(this);
		
		doneButton = (Button) findViewById(R.id.usersDoneButton);
		doneButton.setOnClickListener(this);
		
		usersListView = (ListView) findViewById(R.id.usersListView);
		loadUsersListContent();
	}
	
	private void loadUsersListContent() {
		String[] columns = new String[] {DBC.users.column_fullname, DBC.users.column_email, DBC.users.column_username};
		int[] to = new int[] {R.id.usersItemFullname, R.id.usersItemEmail, R.id.usersItemUsername};
		
		usersCursor = getHelper().getReadableDatabase().query(DBC.users.table_name, new String[] {DBC.users.column_id, DBC.users.column_fullname, DBC.users.column_email, DBC.users.column_username}, null, null, null, null, null);
		
		Log.i(TAG, "Num of rows retrieved: " + usersCursor.getCount());
		
		usersListAdapter = new SimpleCursorAdapter(this, R.layout.users_item, usersCursor, columns, to);
		usersListView.setAdapter(usersListAdapter);
//		usersListAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.usersDoneButton) {
			setResult(Activity.RESULT_OK, null);
			finish();
		} else if(v.getId() == R.id.usersAddButton) {
			Intent intent = new Intent(INTENT_ACTION_START_ADD_USER);
			startActivityForResult(intent, REQUEST_CODE_ADD_USER);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_CODE_ADD_USER) {
			usersCursor.requery();
			usersListAdapter.notifyDataSetChanged();
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
}