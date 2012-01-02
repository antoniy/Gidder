package net.antoniy.gidder.service;

import java.io.IOException;

import net.antoniy.gidder.db.DBHelper;
import net.antoniy.gidder.ssh.GidderCommandFactory;
import net.antoniy.gidder.ssh.GidderHostKeyProvider;
import net.antoniy.gidder.ssh.NoShell;

import org.apache.sshd.SshServer;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.session.ServerSession;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class SSHDaemonService extends Service implements PasswordAuthenticator {
	private final static String TAG = SSHDaemonService.class.getSimpleName();
	
	private SshServer sshServer;
	private DBHelper dbHelper;
	
	public SSHDaemonService() {
		Log.i(TAG, "Construct SSHDaemonService!");
		
		dbHelper = new DBHelper(this);
		
		sshServer = SshServer.setUpDefaultServer();
		sshServer.setPort(6666);
		sshServer.setKeyPairProvider(new GidderHostKeyProvider(this));
		sshServer.setShellFactory(new NoShell());
		sshServer.setCommandFactory(new GidderCommandFactory(this));
		sshServer.setPasswordAuthenticator(this);
//		sshServer.setSessionFactory(new GidderSessionFactory());
		
//		sshServer.setPasswordAuthenticator(new PasswordAuthenticator() {
//			public boolean authenticate(String username, String password, ServerSession session) {
//				if("test".equals(username) && "123".equals(password)) {
//					return true;
//				}
//				
//				return false;
//			}
//		});
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.i(TAG, "Service BIND!");
		
		return null;
	}
	
	@Override
	public void onDestroy() {
		try {
			sshServer.stop();
			Log.i(TAG, "SSHd stopped!");
		} catch (InterruptedException e) {
			Log.e(TAG, "Problem when stopping SSHd.", e);
		} finally {
			dbHelper.close();
			super.onDestroy();
		}
		
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		try {
			sshServer.start();
			Log.i(TAG, "SSHd started!");
		} catch (IOException e) {
			Log.e(TAG, "Problem when starting SSHd,", e);
		}

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public boolean authenticate(String username, String password, ServerSession session) {
//		session.getIoSession().
//		session.setAttribute(key, value)
		
		return true;
	}

}
