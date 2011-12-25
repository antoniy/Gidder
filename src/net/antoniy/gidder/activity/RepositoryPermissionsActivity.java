package net.antoniy.gidder.activity;

import java.sql.SQLException;
import java.util.List;

import net.antoniy.gidder.R;
import net.antoniy.gidder.adapter.RepositoryPermissionsAdapter;
import net.antoniy.gidder.db.entity.User;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;

public class RepositoryPermissionsActivity extends BaseActivity {
	private final static String TAG = RepositoryPermissionsActivity.class.getSimpleName();
	
	private ExpandableListView listView;
	private RepositoryPermissionsAdapter adapter;
	private int repositoryId;
	
	@Override
	protected void setup() { 
		Intent intent = getIntent();
		repositoryId = intent.getExtras().getInt("repositoryId");

		setContentView(R.layout.repository_permissions);
	}

	@Override
	protected void initComponents(Bundle savedInstanceState) {
		List<User> users = null;
		try {
			users = getHelper().getUserDao().queryForAll();
		} catch (SQLException e) {
			Log.e(TAG, "Problem while retrieving users.", e);
			return;
		}
		adapter = new RepositoryPermissionsAdapter(this, users, repositoryId);

		listView = (ExpandableListView) findViewById(R.id.repositoryPermissionsListView);
		listView.setAdapter(adapter);
		
		Button doneButton = (Button) findViewById(R.id.repositoryPermissionsDone);
		doneButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.repositoryPermissionsDone) {
			finish();
		}
	}
}
