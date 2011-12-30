package net.antoniy.gidder.activity;

import java.sql.SQLException;

import net.antoniy.gidder.R;
import net.antoniy.gidder.db.entity.Repository;
import net.antoniy.gidder.git.GitRepositoryDao;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AddRepositoryActivity extends BaseActivity {
	private final static String TAG = AddRepositoryActivity.class.getSimpleName();
	
	public final static int REQUEST_CODE_ADD_REPOSITORY = 1;
	public final static int REQUEST_CODE_EDIT_REPOSITORY = 2;
	
	private Button addEditButton;
	private Button cancelButton;
	private EditText nameEditText;
	private EditText mappingEditText;
	private EditText descriptionEditText;
	private boolean editMode = false;
	private int repositoryId;
	private GitRepositoryDao repositoryDao;
	
	@Override
	protected void setup() {
		setContentView(R.layout.add_repository);
		
		if(getIntent().getExtras() != null) {
			repositoryId = getIntent().getExtras().getInt("repositoryId", -1);
			Log.i(TAG, "RepositoryID: " + repositoryId);
			
			if(repositoryId > 0) {
				editMode = true;
			} else {
				editMode = false;
			}
		}
	}

	@Override
	protected void initComponents(Bundle savedInstanceState) {
		repositoryDao = new GitRepositoryDao(this);
		
		TextView titleTextView = (TextView) findViewById(R.id.addRepositoryTitle);
		if(editMode) {
			titleTextView.setText(R.string.add_repository_edittitle);
		} else {
			titleTextView.setText(R.string.add_repository_title);
		}
		
		addEditButton = (Button) findViewById(R.id.addRepositoryBtnAdd);
		addEditButton.setOnClickListener(this);
		if(editMode) {
			addEditButton.setText(R.string.btn_edit);
		} else {
			addEditButton.setText(R.string.btn_add);
		}
		
		cancelButton = (Button) findViewById(R.id.addRepositoryBtnCancel);
		cancelButton.setOnClickListener(this);
		
		nameEditText = (EditText) findViewById(R.id.addRepositoryName);
		mappingEditText = (EditText) findViewById(R.id.addRepositoryMapping);
		descriptionEditText = (EditText) findViewById(R.id.addRepositoryDescription);
		
		if(editMode) {
			populateFieldsWithRepositoryData();
		}
	}
	
	private void populateFieldsWithRepositoryData() {
		Repository repository = null;
		try {
			repository = getHelper().getRepositoryDao().queryForId(repositoryId);
		} catch (SQLException e) {
			Log.e(TAG, "Error retrieving repository with id " + repositoryId, e);
			return;
		}
		
		nameEditText.setText(repository.getName());
		mappingEditText.setText(repository.getMapping());
		descriptionEditText.setText(repository.getDescription());
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		if(v.getId() == R.id.addRepositoryBtnAdd) {
			if(!isFieldsValid()) {
				return;
			}
			
			String name = nameEditText.getText().toString();
			String mapping = mappingEditText.getText().toString();
			String description = descriptionEditText.getText().toString();
			
			try {
				if(editMode) {
					repositoryDao.renameRepository(repositoryId, mapping);

					// TODO: Fix edit of active and create datetime.
					getHelper().getRepositoryDao().update(new Repository(repositoryId, name, mapping, description, true, System.currentTimeMillis()));
				} else {
					getHelper().getRepositoryDao().create(new Repository(0, name, mapping, description, true, System.currentTimeMillis()));
					
					// TODO: create repo WITH LOADING
					repositoryDao.createRepository(mapping);
				}
			} catch (SQLException e) {
				Log.e(TAG, "Problem when add new repository.", e);
				finish();
				return;
			}
			
			setResult(RESULT_OK, null);
			finish();
		} else if(v.getId() == R.id.addRepositoryBtnCancel) {
			finish();
		}
	}
	
	private boolean isFieldsValid() {
		boolean isAllFieldsValid = true;
		
		if(!isNameValid()) {
			isAllFieldsValid = false;
		}
		
		if(!isMappingValid()) {
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
	
	private boolean isNameValid() {
		return !isEditTextEmpty(nameEditText);
	}
	
	private boolean isMappingValid() {
		return !isEditTextEmpty(mappingEditText);
	}
	
}
