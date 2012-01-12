package net.antoniy.gidder.service;

import java.io.IOException;
import java.sql.SQLException;

import net.antoniy.gidder.db.DBHelper;
import net.antoniy.gidder.db.entity.User;
import net.antoniy.gidder.ssh.GidderCommandFactory;
import net.antoniy.gidder.ssh.GidderHostKeyProvider;
import net.antoniy.gidder.ssh.NoShell;
import net.antoniy.gidder.ui.util.PrefsConstants;

import org.apache.sshd.SshServer;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.session.ServerSession;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class SSHDaemonService extends Service implements PasswordAuthenticator {
	private final static String TAG = SSHDaemonService.class.getSimpleName();
	
	private SshServer sshServer;
	private DBHelper dbHelper;

	public SSHDaemonService() {
		Log.i(TAG, "Construct SSHDaemonService!");
		
//		dbHelper = new DBHelper(this);
//		
//		sshServer = SshServer.setUpDefaultServer();
//		sshServer.setPort(6666);
//		sshServer.setKeyPairProvider(new GidderHostKeyProvider(this));
//		sshServer.setShellFactory(new NoShell());
//		sshServer.setCommandFactory(new GidderCommandFactory(this));
//		sshServer.setPasswordAuthenticator(this);
	}
	
	@Override
	public void onCreate() {
		Log.i(TAG, "SSHd onCreate!");
		
		super.onCreate();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.i(TAG, "Service BIND!");
		
		return null;
	}
	
	@Override
	public void onDestroy() {
		try {
			if(sshServer != null) {
				sshServer.stop(true);
			}
			Log.i(TAG, "SSHd stopped!");
		} catch (InterruptedException e) {
			Log.e(TAG, "Problem when stopping SSHd.", e);
		} finally {
			if(dbHelper != null) {
				dbHelper.close();
			}
		}
		
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		dbHelper = new DBHelper(this);
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SSHDaemonService.this);
		String sshPort = prefs.getString(PrefsConstants.SSH_PORT.getKey(), PrefsConstants.SSH_PORT.getDefaultValue());
		
		sshServer = SshServer.setUpDefaultServer();
		sshServer.setPort(Integer.parseInt(sshPort));
		sshServer.setKeyPairProvider(new GidderHostKeyProvider(this));
		sshServer.setShellFactory(new NoShell());
		sshServer.setCommandFactory(new GidderCommandFactory(this));
		sshServer.setPasswordAuthenticator(this);
		
		try {
			sshServer.start();
			Log.i(TAG, "SSHd started!");
		} catch (IOException e) {
			Log.e(TAG, "Problem when starting SSHd.", e);
		}

		return START_STICKY;
	}

	@Override
	public boolean authenticate(String username, String password, ServerSession session) {
		if(password == null || "".equals(password.trim())) {
			return false;
		}
		
		// Query for user by username
		try {
			User user = dbHelper.getUserDao().queryForUsernameAndActive(username);
			
			if(user == null) {
				return false;
			}
			
			if(password.equals(user.getPassword())) {
				return true;
			}
		} catch (SQLException e) {
			Log.e(TAG, "Problem while retrieving user from database.", e);
			return false;
		}
		
		return false;
	}

}
