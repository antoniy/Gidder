package net.antoniy.gidder.dns;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class NoIpStrategy implements DynamicDNS {
	
	private final static String URL_TEMPLATE = "http://dynupdate.no-ip.com/nic/update?hostname=%s&myip=%s";
//	private final static String URL_TEMPLATE = "http://dynupdate.no-ip.com/nic/update";
	
	private final static String RETURN_CODE_GOOD = "good";
	private final static String RETURN_CODE_NOCHG = "nochg";
	private final static String RETURN_CODE_NOHOST = "nohost";
	private final static String RETURN_CODE_BADAUTH = "badauth";
	private final static String RETURN_CODE_BADAGENT = "badagent";
	private final static String RETURN_CODE_DONATOR = "!donator";
	private final static String RETURN_CODE_ABUSE = "abuse";
	private final static String RETURN_CODE_911 = "911";

	private final static String TAG = NoIpStrategy.class.getSimpleName();
	private final Context context;
	
	public NoIpStrategy(Context context) {
		this.context = context;
	}

	public void update(String hostname, String address, String username, String password) {
		DefaultHttpClient httpClient = new DefaultHttpClient();
        try {
            httpClient.getCredentialsProvider().setCredentials(AuthScope.ANY,
                    //new AuthScope("localhost", 443),
                    new UsernamePasswordCredentials(username, password));

            String url = String.format(URL_TEMPLATE, hostname, address);
            
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("User-Agent", "Gidder/1.0 antoniy@gmail.com");
            
//            HttpParams params = new BasicHttpParams();
//            params.setParameter("hostname", hostname);
//            params.setParameter("address", address);
//            
//            httpGet.setParams(params);

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
            }
            
            content = content.trim();
            
            Log.i(TAG, "Content: " + content);
            
            if(content.startsWith(RETURN_CODE_GOOD)) {
//            	Toast.makeText(context.getApplicationContext(), "Dynamic DNS was successfully updated.", Toast.LENGTH_SHORT).show();
            	runToast("Dynamic DNS was successfully updated.");
            } else if(content.startsWith(RETURN_CODE_NOCHG)) {
//            	Toast.makeText(context.getApplicationContext(), "Dynamic DNS was successfully updated.", Toast.LENGTH_SHORT).show();
            	runToast("Dynamic DNS was successfully updated.");
            } else if(content.startsWith(RETURN_CODE_NOHOST)) {
            	Toast.makeText(context, "Dynamic DNS hostname is incorrect.", Toast.LENGTH_SHORT).show();
            } else if(content.startsWith(RETURN_CODE_BADAUTH)) {
            	Toast.makeText(context, "Dynamic DNS authentication failed.", Toast.LENGTH_SHORT).show();
            } else if(content.startsWith(RETURN_CODE_BADAGENT)) {
            	Log.e(TAG, "Agent information is not correct!");
            } else if(content.startsWith(RETURN_CODE_DONATOR)) {
            	Log.w(TAG, "Update request include feature that is not available for the user!");
            } else if(content.startsWith(RETURN_CODE_ABUSE)) {
            	Toast.makeText(context, "Dynamic DNS username abuse problem.", Toast.LENGTH_SHORT).show();
            } else if(content.startsWith(RETURN_CODE_911)) {
            	Toast.makeText(context, "Dynamic DNS provider has fatal problem.", Toast.LENGTH_SHORT).show();
            }
            	
        } catch (ClientProtocolException e) {
        	Log.e(TAG, e.getLocalizedMessage(), e);
		} catch (IOException e) {
			Log.e(TAG, e.getLocalizedMessage(), e);
		} finally {
            httpClient.getConnectionManager().shutdown();
        }
		return;
	}
	
	private void runToast(final String text) {
		((Activity)context).runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Toast.makeText(context.getApplicationContext(), text, Toast.LENGTH_SHORT).show();
			}
		});
	}
	
}
