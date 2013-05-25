package de.lukeslog.snapnow.posting;

import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import de.lukeslog.snapnow.constants.SnapNowConstants;
import de.lukeslog.snapnow.main.OAuthConstants;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

public class PostToTumblrX extends Service
{

	private Context context;
	
	private final String TAG = SnapNowConstants.TAG;
	static SharedPreferences prefs;
	public final String API_URL = "http://api.tumblr.com/v2/blog/";
	
	private static final String REQUEST_TOKEN_URL = "http://www.tumblr.com/oauth/request_token";
    private static final String ACCESS_TOKEN_URL = "http://www.tumblr.com/oauth/access_token";
    private static final String AUTH_URL = "http://www.tumblr.com/oauth/authorize";

    // Taken from Tumblr app registration
    private static final String CONSUMER_KEY = OAuthConstants.TUMBR_CONSUMERKEY;
    private static final String CONSUMER_SECRET = OAuthConstants.TUMBR_SECRETKEY;
    
    private static String OAUTH_TOKEN="";
    private static String OAUTH_SECRET="";

    private static final String CALLBACK_URL = "snapnow://snapnow.com/ok";
    
    static CommonsHttpOAuthProvider provider;
    static CommonsHttpOAuthConsumer consumer;
	
	@Override
	public IBinder onBind(Intent arg0) 
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() 
	{
		super.onCreate();
	 	//Log.i(TAG, "SNAPNOW Service Läuft");
	    context=this;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		super.onStartCommand(intent, flags, startId);
		//Log.i(TAG, "Received start id " + startId + ": " + intent);
		return Service.START_NOT_STICKY;
		
		//requestOAuthData();
	}
	
    @Override
    public void onDestroy() 
    {
        super.onDestroy();
    }
    
    private boolean requestOAuthData()
    {
    	//if we already have the tokens can directly upload the picture. However, we have to get them if this is the first time the app is used.
    	if(!(OAUTH_TOKEN.equals("")) && !(OAUTH_SECRET.equals(""))) 
    	{
    		consumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
            provider = new CommonsHttpOAuthProvider(REQUEST_TOKEN_URL, ACCESS_TOKEN_URL, AUTH_URL);
            
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("https://www.tumblr.com/oauth/access_token");

            try 
            {
	            //List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	            //nameValuePairs.add(new BasicNameValuePair("email", Username));
	            //nameValuePairs.add(new BasicNameValuePair("password", Password));
	            //httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	            HttpResponse response = httpclient.execute(httppost);
	            if (response.getStatusLine().getStatusCode() != 200) 
	            {
	                return false;
	            }
	            return true;
            }
            catch(Exception e)
            {
            }
        }
		return false;
    }
}
