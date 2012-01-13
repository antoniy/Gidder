package net.antoniy.gidder.ui.activity;

import java.sql.SQLException;
import java.util.List;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.IntentAction;

import net.antoniy.gidder.R;
import net.antoniy.gidder.db.entity.User;
import net.antoniy.gidder.ui.adapter.RepositoryPermissionsAdapter;
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
		
		ActionBar actionBar = (ActionBar) findViewById(R.id.repositoryPermissionsActionBar);
        actionBar.setHomeAction(new IntentAction(this, new Intent(this, SlideActivity.class), R.drawable.ic_actionbar_home));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.addAction(new IntentAction(this, new Intent(this, GidderPreferencesActivity.class), R.drawable.ic_actionbar_settings));
       	actionBar.setTitle("Repository permissions");
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.repositoryPermissionsDone) {
			setResult(RESULT_OK);
			finish();
		}
	}
}
