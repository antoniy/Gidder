package net.antoniy.gidder.activity;

import java.sql.SQLException;

import net.antoniy.gidder.R;
import net.antoniy.gidder.db.entity.Repository;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddRepositoryActivity extends BaseActivity {
	private final static String TAG = AddRepositoryActivity.class.getSimpleName();
	
	private Button addRepositoryButton;
	private Button cancelButton;
	private EditText nameEditText;
	private EditText mappingEditText;
	private EditText descriptionEditText;
	
	@Override
	protected void setup() {
		setContentView(R.layout.add_repository);
	}

	@Override
	protected void initComponents(Bundle savedInstanceState) {
		addRepositoryButton = (Button) findViewById(R.id.addRepositoryBtnAdd);
		addRepositoryButton.setOnClickListener(this);
		
		cancelButton = (Button) findViewById(R.id.addRepositoryBtnCancel);
		cancelButton.setOnClickListener(this);
		
		nameEditText = (EditText) findViewById(R.id.addRepositoryName);
		mappingEditText = (EditText) findViewById(R.id.addRepositoryMapping);
		descriptionEditText = (EditText) findViewById(R.id.addRepositoryDescription);
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
				getHelper().getRepositoryDao().create(new Repository(0, name, mapping, description));
			} catch (SQLException e) {
				Log.e(TAG, "Problem when add new repository.", e);
			}
			
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
