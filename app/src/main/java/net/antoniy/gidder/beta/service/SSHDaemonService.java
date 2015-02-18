package net.antoniy.gidder.beta.service;

import java.io.IOException;
import java.lang.IllegalArgumentException;
import java.lang.Override;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;

import net.antoniy.gidder.beta.R;
import net.antoniy.gidder.beta.db.DBHelper;
import net.antoniy.gidder.beta.db.entity.User;
import net.antoniy.gidder.beta.ssh.GidderCommandFactory;
import net.antoniy.gidder.beta.ssh.GidderHostKeyProvider;
import net.antoniy.gidder.beta.ssh.NoShell;
import net.antoniy.gidder.beta.ui.util.C;
import net.antoniy.gidder.beta.ui.util.GidderCommons;
import net.antoniy.gidder.beta.ui.util.PrefsConstants;
import net.antoniy.gidder.beta.ui.widget.ToggleAppWidgetProvider;

import org.apache.mina.util.Base64;
import org.apache.sshd.SshServer;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.PublickeyAuthenticator;
import org.apache.sshd.server.session.ServerSession;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

public class SSHDaemonService extends Service implements PasswordAuthenticator, PublickeyAuthenticator {
	private final static String TAG = SSHDaemonService.class.getSimpleName();
	
	private SshServer sshServer;
	private DBHelper dbHelper;

	public SSHDaemonService() {
		Log.i(TAG, "Construct SSHDaemonService!");
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
				sendBroadcast(new Intent(C.action.SSHD_STOPPED));
				
				toggleWidgetState(false);
				GidderCommons.stopStatusBarNotification(this);
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
        sshServer.setPublickeyAuthenticator(this);
		
		try {
			sshServer.start();
			sendBroadcast(new Intent(C.action.SSHD_STARTED));
			
			toggleWidgetState(true);
			
			boolean isStatusBarNotificationEnabled = prefs.getBoolean(PrefsConstants.STATUSBAR_NOTIFICATION.getKey(), 
					"true".equals(PrefsConstants.STATUSBAR_NOTIFICATION.getDefaultValue()) ? true : false);
			
			if(isStatusBarNotificationEnabled) {
				GidderCommons.makeStatusBarNotification(this);
			}
			
			Log.i(TAG, "SSHd started!");
		} catch (IOException e) {
			Log.e(TAG, "Problem when starting SSHd.", e);
		}

		return START_STICKY;
	}
	
	private void toggleWidgetState(boolean runningState) {
		RemoteViews widgetViews = new RemoteViews(getPackageName(), R.layout.toggle_widget);
		widgetViews.setImageViewResource(R.id.toggleWidgetButton, runningState ? R.drawable.ic_widget_active : R.drawable.ic_widget_inactive);
		
		ComponentName widget = new ComponentName(this, ToggleAppWidgetProvider.class);
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
		appWidgetManager.updateAppWidget(widget, widgetViews);
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
			
			String passwordSha1 = GidderCommons.generateSha1(password);
			Log.i(TAG, "Password SHA1: " + passwordSha1);
			
			if(passwordSha1.equals(user.getPassword())) {
				return true;
			}
		} catch (SQLException e) {
			Log.e(TAG, "Problem while retrieving user from database.", e);
			return false;
		}
		
		return false;
	}

    @Override
    public boolean authenticate(String username, PublicKey key, ServerSession session) {
        if(key == null) {
            return false;
        }

        if(key instanceof RSAPublicKey) {
            try {
                User user = dbHelper.getUserDao().queryForUsernameAndActive(username);
                if(user == null) {
                    return false;
                }

                if(user.getPublickey() == null || "".equals(user.getPublickey().trim())) {
                    return false;
                }

                PublicKey knownkey = decodePublicKey(user.getPublickey());
                return ((RSAPublicKey) knownkey).getModulus().equals(((RSAPublicKey) key).getModulus());

            } catch (SQLException e) {
                Log.e(TAG, "Problem while retrieving user form database.", e);
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Problem while decoding the public key." , e);
            } catch (NoSuchAlgorithmException e) {
                Log.e(TAG, "Problem while decoding the public key." , e);
            } catch (InvalidKeySpecException e) {
                Log.e(TAG, "Problem while decoding the public key." , e);
            }
        }

        return false;
    }

    private byte[] bytes;
    private int pos;

    private PublicKey decodePublicKey(String keystring) throws IllegalArgumentException, NoSuchAlgorithmException, InvalidKeySpecException {
        bytes = null;
        pos = 0;

        for (String part : keystring.split(" ")) {
            if (part.startsWith("AAAA")) {
                bytes = Base64.decodeBase64(part.getBytes());
                break;
            }
        }

        if (bytes == null) {
            throw new IllegalArgumentException("No Base64 part to decode.");
        }

        String type = decodeType();
        if (type.equals("ssh-rsa")) {
            BigInteger e = decodeBigInt();
            BigInteger m = decodeBigInt();
            RSAPublicKeySpec spec = new RSAPublicKeySpec(m, e);
            return KeyFactory.getInstance("RSA").generatePublic(spec);
        } else {
            throw new IllegalArgumentException("Unknown type: " + type);
        }
    }

    private String decodeType() {
        int len = decodeInt();
        String type = new String(bytes, pos, len);
        pos += len;
        return type;
    }

    private int decodeInt() {
        return ((bytes[pos++] & 0xFF) << 24) | ((bytes[pos++] & 0xFF) << 16) | ((bytes[pos++] & 0xFF) << 8) | (bytes[pos++] & 0xFF);
    }

    private BigInteger decodeBigInt() {
        int len = decodeInt();
        byte[] bigIntBytes = new byte[len];
        System.arraycopy(bytes, pos, bigIntBytes, 0, len);
        pos += len;
        return new BigInteger(bigIntBytes);
    }

}
