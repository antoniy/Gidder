package net.antoniy.gidder.activity;

import java.sql.SQLException;

import net.antoniy.gidder.R;
import net.antoniy.gidder.db.entity.User;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AddUserActivity extends BaseActivity {
	private final static String TAG = AddUserActivity.class.getSimpleName();
	
	private Button addUser;
	private TextView fullnameTextView;
	private TextView emailTextView;
	private TextView usernameTextView;
	private TextView passwordTextView;
//	private Quick
	
	@Override
	protected void setup() {
		setContentView(R.layout.add_user);
	}

	@Override
	protected void initComponents(Bundle savedInstanceState) {
		addUser = (Button) findViewById(R.id.addUserBtnAdd);
		addUser.setOnClickListener(this);
		
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
		}
	}
}
