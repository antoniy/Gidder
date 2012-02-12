package net.antoniy.gidder.dns;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class DynDNSStrategy {
	private final static String TAG = DynDNSStrategy.class.getSimpleName();

	public DynDNSStrategy() {
		// TODO Auto-generated constructor stub
	}
	
	public String update(String url, String username, String password) {
		DefaultHttpClient httpclient = new DefaultHttpClient();
        try {
            httpclient.getCredentialsProvider().setCredentials(AuthScope.ANY,
                    //new AuthScope("localhost", 443),
                    new UsernamePasswordCredentials("test", "test"));

            HttpGet httpget = new HttpGet(url);

            Log.i(TAG, "executing request" + httpget.getRequestLine());
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();

//            System.out.println("----------------------------------------");
            Log.i(TAG, response.getStatusLine().toString());
//            System.out.println(response.getStatusLine());
            if (entity != null) {
                System.out.println("Response content length: " + entity.getContentLength());
            }
//            EntityUtils.consume(entity);
        } catch (ClientProtocolException e) {
        	Log.e(TAG, e.getLocalizedMessage(), e);
//			e.printStackTrace();
		} catch (IOException e) {
			Log.e(TAG, e.getLocalizedMessage(), e);
//			e.printStackTrace();
		} finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }
		return null;
	}
}
