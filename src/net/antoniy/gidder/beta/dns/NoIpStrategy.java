package net.antoniy.gidder.beta.dns;

import net.antoniy.gidder.beta.ui.util.C;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NoIpStrategy extends DynamicDNS {
	
	private final static String URL_TEMPLATE = "http://dynupdate.no-ip.com/nic/update?hostname=%s&myip=%s";
	
	private final static String RETURN_CODE_GOOD = "good";
	private final static String RETURN_CODE_NOCHG = "nochg";
	private final static String RETURN_CODE_NOHOST = "nohost";
	private final static String RETURN_CODE_BADAUTH = "badauth";
	private final static String RETURN_CODE_BADAGENT = "badagent";
	private final static String RETURN_CODE_DONATOR = "!donator";
	private final static String RETURN_CODE_ABUSE = "abuse";
	private final static String RETURN_CODE_911 = "911";

	private final static String TAG = NoIpStrategy.class.getSimpleName();
	
	public NoIpStrategy(Context context) {
		super(context);
	}

	public void update(String hostname, String address, String username, String password) {
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
		HttpConnectionParams.setSoTimeout(httpParams, 3000);
		
		DefaultHttpClient httpClient = new DefaultHttpClient(httpParams  );
        try {
            httpClient.getCredentialsProvider().setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(username, password));
            

            String url = String.format(URL_TEMPLATE, hostname, address);
            
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("User-Agent", "Gidder/1.0 antoniy@gmail.com");
            
            Log.i(TAG, "executing request " + httpGet.getRequestLine());
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();

            Log.i(TAG, response.getStatusLine().toString());
            if(entity == null) {
            	Log.w(TAG, "Response entity is empty!");
            }

            String content = EntityUtils.toString(entity);
            
            if(content == null || "".equals(content.trim())) {
            	Log.w(TAG, "Content is empty!");
            	return;
            }
            
            content = content.trim();
            
            Log.i(TAG, "Content: " + content);
            
            if(content.startsWith(RETURN_CODE_GOOD)) {
            	makeToast("Dynamic DNS was successfully updated.");
            } else if(content.startsWith(RETURN_CODE_NOCHG)) {
            	makeToast("Dynamic DNS was successfully updated.");
            } else if(content.startsWith(RETURN_CODE_NOHOST)) {
            	makeToast("Dynamic DNS hostname is incorrect.");
            } else if(content.startsWith(RETURN_CODE_BADAUTH)) {
            	makeToast("Dynamic DNS authentication failed.");
            } else if(content.startsWith(RETURN_CODE_BADAGENT)) {
            	Log.e(TAG, "Agent information is not correct!");
            } else if(content.startsWith(RETURN_CODE_DONATOR)) {
            	Log.w(TAG, "Update request include feature that is not available for the user!");
            } else if(content.startsWith(RETURN_CODE_ABUSE)) {
            	makeToast("Dynamic DNS username abuse problem.");
            } else if(content.startsWith(RETURN_CODE_911)) {
            	makeToast("Dynamic DNS provider has fatal problem.");
            }
            	
		} catch (ConnectTimeoutException e) {
			Log.w(TAG, "WiFi is not yet connected! Try again in a minute.", e);
			
			Intent broadcastIntent = new Intent(C.action.UPDATE_DYNAMIC_DNS_ADDRESS);
			broadcastIntent.putExtra("scheduled", true);
			
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, broadcastIntent, 0);
			
			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 60L * 1000L, pendingIntent);
		} catch (Exception e) {
			Log.e(TAG, "Problem updating dynamic DNS.", e);
			makeToast("Problem updating dynamic DNS.");
		} finally {
            httpClient.getConnectionManager().shutdown();
        }
		return;
	}
	
}
