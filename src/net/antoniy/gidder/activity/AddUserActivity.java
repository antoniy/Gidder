package net.antoniy.gidder.activity;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.antoniy.gidder.R;
import net.antoniy.gidder.db.entity.User;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AddUserActivity extends BaseActivity {
	private final static String TAG = AddUserActivity.class.getSimpleName();
	
	private static final Pattern emailPattern = Pattern.compile( "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
	
	private Button addUserButton;
	private Button cancelButton;
	private TextView fullnameTextView;
	private TextView emailTextView;
	private TextView usernameTextView;
	private TextView passwordTextView;
	
	@Override
	protected void setup() {
		setContentView(R.layout.add_user);
	}

	@Override
	protected void initComponents(Bundle savedInstanceState) {
		addUserButton = (Button) findViewById(R.id.addUserBtnAdd);
		addUserButton.setOnClickListener(this);
		
		cancelButton = (Button) findViewById(R.id.addUserBtnCancel);
		cancelButton.setOnClickListener(this);
		
		fullnameTextView = (TextView) findViewById(R.id.addUserFullname);
		emailTextView = (TextView) findViewById(R.id.addUserEmail);
		usernameTextView = (TextView) findViewById(R.id.addUserUsername);
		passwordTextView = (TextView) findViewById(R.id.addUserPassword);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		if(v.getId() == R.id.addUserBtnAdd) {
			// TODO: Do validation.
//			passwordTextView.setError("Some error message.");
			if(!isFieldsValid()) {
				return;
			}
			
			String fullname = fullnameTextView.getText().toString();
			String email = emailTextView.getText().toString();
			String username = usernameTextView.getText().toString();
			String password = passwordTextView.getText().toString();
			
			try {
				getHelper().getUserDao().create(new User(0, fullname, email, username, password));
			} catch (SQLException e) {
				Log.e(TAG, "Problem when add new user.", e);
			}
			
			finish();
		} else if(v.getId() == R.id.addUserBtnCancel) {
			finish();
		}
	}
	
	private boolean isFieldsValid() {
		boolean isAllFieldsValid = true;
		
		if(!isFullnameValid()) {
			isAllFieldsValid = false;
		}
		
		if(!isEmailValid()) {
			isAllFieldsValid = false;
		}
		
		if(!isUsernameValid()) {
			isAllFieldsValid = false;
		}
		
		if(!isPasswordValid()) {
			isAllFieldsValid = false;
		}
		
		return isAllFieldsValid;
	}
	
	private boolean isTextViewEmpty(TextView tv) {
		String text = tv.getText().toString();
		if("".equals(text.trim())) {
			tv.setError("Field must contain value");
			return true;
		} else {
			return false;
		}
	}
	
	private boolean isFullnameValid() {
		return !isTextViewEmpty(fullnameTextView);
	}
	
	private boolean isPasswordValid() {
		return !isTextViewEmpty(passwordTextView);
	}
	
	private boolean isUsernameValid() {
		return !isTextViewEmpty(usernameTextView);
	}
	
	private boolean isEmailValid() {
		if(isTextViewEmpty(emailTextView)) {
			return false;
		}
		
		String email = emailTextView.getText().toString();
		Matcher matcher = emailPattern.matcher(email);
		if(!matcher.matches()) {
			emailTextView.setError("E-mail address is incorrect");
			return false;
		}
		
		return true;
	}
	
}
