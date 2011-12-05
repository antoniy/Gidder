package net.antoniy.gidder.activity;

import net.antoniy.gidder.R;
import net.antoniy.gidder.db.DBC;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class RepositoriesActivity extends BaseActivity {
	private final static String TAG = RepositoriesActivity.class.getSimpleName();
	private final static int REQUEST_CODE_ADD_REPOSITORY = 1;
	
	private final static String INTENT_ACTION_START_ADD_REPOSITORY = "net.antoniy.gidder.START_ADD_REPOSITORY_ACTIVITY";
	
	private Button doneButton;
	private Button addButton;
	private ListView repositoriesListView;
	private CursorAdapter repositoriesListAdapter;
	private Cursor repositoriesCursor;
	
	@Override
	protected void setup() {
		setContentView(R.layout.repositories);
	}

	@Override
	protected void initComponents(Bundle savedInstanceState) {
		addButton = (Button) findViewById(R.id.repositoriesAddButton);
		addButton.setOnClickListener(this);
		
		doneButton = (Button) findViewById(R.id.repositoriesDoneButton);
		doneButton.setOnClickListener(this);
		
		repositoriesListView = (ListView) findViewById(R.id.repositoriesListView);
		loadUsersListContent();
	}
	
	private void loadUsersListContent() {
		String[] columns = new String[] {DBC.repositories.column_name, DBC.repositories.column_mapping, DBC.repositories.column_description};
		int[] to = new int[] {R.id.repositoriesItemName, R.id.repositoriesItemMapping, R.id.repositoriesItemDescription};
		
		repositoriesCursor = getHelper().getReadableDatabase().query(
				DBC.repositories.table_name, 
				new String[] {
						DBC.repositories.column_id, 
						DBC.repositories.column_name, 
						DBC.repositories.column_mapping, 
						DBC.repositories.column_description
					}, 
				null, null, null, null, null);
		
		Log.i(TAG, "Num of rows retrieved: " + repositoriesCursor.getCount());
		
		repositoriesListAdapter = new SimpleCursorAdapter(this, R.layout.repositories_item, repositoriesCursor, columns, to);
		repositoriesListView.setAdapter(repositoriesListAdapter);
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.repositoriesDoneButton) {
			setResult(Activity.RESULT_OK, null);
			finish();
		} else if(v.getId() == R.id.repositoriesAddButton) {
			Intent intent = new Intent(INTENT_ACTION_START_ADD_REPOSITORY);
			startActivityForResult(intent, REQUEST_CODE_ADD_REPOSITORY);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_CODE_ADD_REPOSITORY) {
			repositoriesCursor.requery();
			repositoriesListAdapter.notifyDataSetChanged();
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
}
