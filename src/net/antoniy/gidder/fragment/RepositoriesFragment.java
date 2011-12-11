package net.antoniy.gidder.fragment;

import net.antoniy.gidder.R;
import net.antoniy.gidder.db.DBC;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class RepositoriesFragment extends BaseFragment implements OnClickListener {
	private final static String TAG = RepositoriesFragment.class.getSimpleName();
	private final static int REQUEST_CODE_ADD_REPOSITORY = 1;
	
	private final static String INTENT_ACTION_START_ADD_REPOSITORY = "net.antoniy.gidder.START_ADD_REPOSITORY_ACTIVITY";
	
	private Button addButton;
	private ListView repositoriesListView;
	private CursorAdapter repositoriesListAdapter;
	private Cursor repositoriesCursor;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LinearLayout mainContainer = (LinearLayout) inflater.inflate(R.layout.repositories, null);

		addButton = (Button) mainContainer.findViewById(R.id.repositoriesAddButton);
		addButton.setOnClickListener(this);
		
		repositoriesListView = (ListView) mainContainer.findViewById(R.id.repositoriesListView);
		loadUsersListContent();
		
		return mainContainer;
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
		
		repositoriesListAdapter = new SimpleCursorAdapter(getActivity(), R.layout.repositories_item, repositoriesCursor, columns, to);
		repositoriesListView.setAdapter(repositoriesListAdapter);
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.repositoriesAddButton) {
			Intent intent = new Intent(INTENT_ACTION_START_ADD_REPOSITORY);
			startActivityForResult(intent, REQUEST_CODE_ADD_REPOSITORY);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_CODE_ADD_REPOSITORY) {
			repositoriesCursor.requery();
			repositoriesListAdapter.notifyDataSetChanged();
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
}
