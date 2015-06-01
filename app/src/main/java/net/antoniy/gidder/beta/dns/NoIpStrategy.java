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

public class NoIpStrategy extends BaseDynamicDNS {
	
	private final static String URL_TEMPLATE = "http://dynupdate.no-ip.com/nic/update?hostname=%s&myip=%s";
	private final static String URL_TEMPLATE_WITHOUT_IP = "http://dynupdate.no-ip.com/nic/update?hostname=%s";

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

	@Override
	protected String getServiceURL(String hostname, String address) {
		if (address == null) {
			return String.format(URL_TEMPLATE_WITHOUT_IP, hostname);
		} else {
			return String.format(URL_TEMPLATE, hostname, address);
		}
	}

	@Override
	protected void handleResponseContent(String content) {
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
	}

	@Override
	protected String getUserAgent() {
		return "Gidder/1.0 antoniy@gmail.com";
	}

}
