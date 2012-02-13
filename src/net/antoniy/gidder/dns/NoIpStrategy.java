package net.antoniy.gidder.dns;

public class NoIpStrategy extends DynamicDNS {

	private final static String TAG = NoIpStrategy.class.getSimpleName();
	
	public NoIpStrategy(String hostname) {
		super(hostname);
	}

	public void update(String address, String username, String password) {
//		DefaultHttpClient httpclient = new DefaultHttpClient();
//        try {
//            httpclient.getCredentialsProvider().setCredentials(AuthScope.ANY,
//                    //new AuthScope("localhost", 443),
//                    new UsernamePasswordCredentials("test", "test"));
//
//            HttpGet httpget = new HttpGet(url);
//
//            Log.i(TAG, "executing request" + httpget.getRequestLine());
//            HttpResponse response = httpclient.execute(httpget);
//            HttpEntity entity = response.getEntity();
//
////            System.out.println("----------------------------------------");
//            Log.i(TAG, response.getStatusLine().toString());
////            System.out.println(response.getStatusLine());
//            if (entity != null) {
//                System.out.println("Response content length: " + entity.getContentLength());
//            }
////            EntityUtils.consume(entity);
//        } catch (ClientProtocolException e) {
//        	Log.e(TAG, e.getLocalizedMessage(), e);
////			e.printStackTrace();
//		} catch (IOException e) {
//			Log.e(TAG, e.getLocalizedMessage(), e);
////			e.printStackTrace();
//		} finally {
//            // When HttpClient instance is no longer needed,
//            // shut down the connection manager to ensure
//            // immediate deallocation of all system resources
//            httpclient.getConnectionManager().shutdown();
//        }
		return;
	}
}
