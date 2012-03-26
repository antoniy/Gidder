package net.antoniy.gidder.ui.activity;

import java.sql.SQLException;

import org.eclipse.jgit.errors.RepositoryNotFoundException;

import net.antoniy.gidder.R;
import net.antoniy.gidder.db.entity.Repository;
import net.antoniy.gidder.git.GitRepositoryDao;
import net.antoniy.gidder.ui.util.C;
import net.antoniy.gidder.ui.util.GidderCommons;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.IntentAction;

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

	private ActionBar actionBar;
	
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
		
		addEditButton = (Button) findViewById(R.id.addRepositoryBtnAdd);
		addEditButton.setOnClickListener(this);
		if(editMode) {
			addEditButton.setText(R.string.btn_save);
		} else {
			addEditButton.setText(R.string.btn_add);
		}
		
		cancelButton = (Button) findViewById(R.id.addRepositoryBtnCancel);
		cancelButton.setOnClickListener(this);
		
		nameEditText = (EditText) findViewById(R.id.addRepositoryName);
		nameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus && nameEditText.getText().length() > 0 && mappingEditText.getText().length() == 0) {
					mappingEditText.setText(GidderCommons.toCamelCase(nameEditText.getText().toString()));
				}
			}
		});
		
		mappingEditText = (EditText) findViewById(R.id.addRepositoryMapping);
		descriptionEditText = (EditText) findViewById(R.id.addRepositoryDescription);
		
		actionBar = (ActionBar) findViewById(R.id.addRepositoryActionBar);
        actionBar.setHomeAction(new IntentAction(this, new Intent(C.action.START_SLIDE_ACTIVITY), R.drawable.ic_actionbar_home));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.addAction(new IntentAction(this, new Intent(C.action.START_PREFERENCE_ACTIVITY), R.drawable.ic_actionbar_settings));

        if(editMode) {
        	actionBar.setTitle(R.string.add_repository_edittitle);
        } else {
        	actionBar.setTitle(R.string.add_repository_title);
        }
        
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
				actionBar.setProgressBarVisibility(View.VISIBLE);
				if(editMode) {
					repositoryDao.renameRepository(repositoryId, mapping);

					// TODO: Fix edit of active and create datetime.
					getHelper().getRepositoryDao().update(new Repository(repositoryId, name, mapping, description, true, System.currentTimeMillis()));
				} else {
					getHelper().getRepositoryDao().create(new Repository(0, name, mapping, description, true, System.currentTimeMillis()));
					
					// TODO: create repo WITH LOADING
					try {
						repositoryDao.createRepository(mapping);
					} catch (RepositoryNotFoundException e) {
						Log.e(TAG, "Problem while creating repository.", e);
						// TODO: Make some toast message or something.
					}
				}
				actionBar.setProgressBarVisibility(View.GONE);
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
