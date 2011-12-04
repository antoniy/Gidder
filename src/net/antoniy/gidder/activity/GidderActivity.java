package net.antoniy.gidder.activity;

import java.io.IOException;

import net.antoniy.gidder.R;
import net.antoniy.gidder.adapter.MainMenuAdapter;
import net.antoniy.gidder.adapter.MainMenuAdapter.MainMenuItem;
import net.antoniy.gidder.ssh.GidderCommandFactory;
import net.antoniy.gidder.ssh.GidderHostKeyProvider;
import net.antoniy.gidder.ssh.NoShell;

import org.apache.sshd.SshServer;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.session.ServerSession;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class GidderActivity extends BaseActivity implements OnItemClickListener {
	private final static String TAG = GidderActivity.class.getSimpleName();
	private final static String INTENT_ACTION_START_USERS = "net.antoniy.gidder.START_USERS_ACTIVITY";
	
	private SshServer sshServer;
	
	@Override
	protected void setup() {
		setContentView(R.layout.main);
	}
	
	@Override
	protected void initComponents(Bundle savedInstanceState) {
//    	Button button = (Button) findViewById(R.id.mainUsersButton);
//    	button.setOnClickListener(this);
    	
    	GridView mainMenuGridView = (GridView) findViewById(R.id.mainGridView);
    	mainMenuGridView.setAdapter(new MainMenuAdapter(this));
    	mainMenuGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
    	mainMenuGridView.setOnItemClickListener(this);
    	
//    	try {
//			getHelper().getUserDao().create(new User(0, "Antoniy Chonkov", "antoniy@gmail.com", "antoniy", "asd123"));
//		} catch (SQLException e) {
//			Log.e(TAG, "Error inserting user", e);
//		}
//    	
//    	QueryBuilder<User, Integer> builder = getHelper().getUserDao().queryBuilder();
//    	try {
//			Cursor cursor = ((AndroidCompiledStatement)getHelper().getUserDao().queryBuilder().prepare().compile(null, StatementType.SELECT)).getCursor();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
    	
//    	getHelper().getUserDao()
//    	PreparedQuery<User>
    }
    
    @Override
    protected void onPause() {
    	try {
			sshServer.stop();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			super.onPause();
		}
    }
    
    protected void onResume() {
    	sshServer = SshServer.setUpDefaultServer();
		sshServer.setPort(6666);
		sshServer.setKeyPairProvider(new GidderHostKeyProvider(this));
		sshServer.setShellFactory(new NoShell());
		sshServer.setCommandFactory(new GidderCommandFactory(this));
		sshServer.setPasswordAuthenticator(new PasswordAuthenticator() {
			public boolean authenticate(String username, String password, ServerSession session) {
				if("test".equals(username) && "123".equals(password)) {
					return true;
				}
				
				return false;
			}
		});
		
		try {
			sshServer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		super.onResume();
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(TAG, "RequestCode: " + requestCode + ", ResultCode: " + resultCode);
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.i(TAG, "Position: " + position);
		
		MainMenuItem item = (MainMenuItem) parent.getItemAtPosition(position);
		Log.i(TAG, "Title: " + item.getTitle());
		
		switch(item.getType()) {
		case USERS:
			Intent intent = new Intent(INTENT_ACTION_START_USERS);
			startActivityForResult(intent, 0);
			break;
		}
	}

}