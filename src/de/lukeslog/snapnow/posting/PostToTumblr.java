package de.lukeslog.snapnow.posting;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

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

import de.lukeslog.snapnow.R;
import de.lukeslog.snapnow.R.layout;
import de.lukeslog.snapnow.constants.SnapNowConstants;
import de.lukeslog.snapnow.main.OAuthConstants;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

public class PostToTumblr extends Activity
{
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
			
    Activity context;
    
    String blogname = "snapnowandroid";
    
    private static ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
    
    static CommonsHttpOAuthProvider provider;
    static CommonsHttpOAuthConsumer consumer;
    
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty);
        context =this;
        prefs = getSharedPreferences(SnapNowConstants.PREFS, 0);
        OAUTH_TOKEN = prefs.getString("tumblroauthtolken", "");
        OAUTH_SECRET = prefs.getString("tumbrouthsecret", "");

        Log.d(TAG, "postToTumblr onCreate()");
		// To get the oauth token after the user has granted permissions
		Thread t = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				if(OAUTH_TOKEN.equals(""))
				{
		            
			        Log.d(TAG, "no verifier yet");
			        Uri uri = context.getIntent().getData();
			        if (uri != null && uri.toString().startsWith(CALLBACK_URL)) 
				    {  
				    	Log.d(TAG, "uri!=null");
			             
			            String verifier = uri.getQueryParameter("oauth_verifier");  

			            Log.d(TAG, "verifier"+verifier);
			            try 
			            {
			            	provider.setOAuth10a(true);
							provider.retrieveAccessToken(consumer, verifier);
							Log.d(TAG, "try");
						} 
			            catch (OAuthMessageSignerException e) 
			            {
			            	Log.e(TAG, e.toString());
							e.printStackTrace();
						} 
			            catch (OAuthNotAuthorizedException e) 
			            {
			            	Log.e(TAG, e.toString());
							e.printStackTrace();
						} 
			            catch (OAuthExpectationFailedException e) 
			            {
			            	Log.e(TAG, e.toString());
							e.printStackTrace();
						} 
			            catch (OAuthCommunicationException e) 
			            {
			            	Log.e(TAG, e.toString());
							e.printStackTrace();
						}
			            Log.d(TAG, "get");
			            OAUTH_TOKEN = consumer.getToken();
			            OAUTH_SECRET = consumer.getTokenSecret();
			            Log.d(TAG, OAUTH_TOKEN);
			            Log.d(TAG, OAUTH_SECRET);
			            
			            Editor edit = prefs.edit();
			            edit.putString("tumblroauthtolken",  OAUTH_TOKEN);
			            edit.putString("tumbrouthsecret", OAUTH_SECRET);
			            edit.commit();
			            
			            Log.d(TAG, "put into shared prefs");
						if(bitmaps.size()>0)
						{
							Log.d(TAG, bitmaps.size()+"");
							for(int i=0; i<bitmaps.size(); i++)
							{
								uploadToTumblr(bitmaps.get(i));
								//createText();
							}
						}
						bitmaps.clear();
				    } 
			        else 
			        {
			            Log.d(TAG, "uri==null");
			
			            consumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY,
			                    CONSUMER_SECRET);
			
			            // It uses this signature by default
			            // consumer.setMessageSigner(new HmacSha1MessageSigner());
			
			            provider = new CommonsHttpOAuthProvider(
			                    REQUEST_TOKEN_URL,
			                    ACCESS_TOKEN_URL,
			                    AUTH_URL);
			

			            
			            String authUrl;
			            try 
			            {
			                authUrl = provider.retrieveRequestToken(consumer, CALLBACK_URL);
			                Log.d(TAG, "Auth url:" + authUrl);
			
			                startActivity(new Intent("android.intent.action.VIEW", Uri.parse(authUrl)));
			
			            } 
			            catch (OAuthMessageSignerException e) 
			            {
			            	Log.e(TAG, e.toString());
			                e.printStackTrace();
			            } 
			            catch (OAuthNotAuthorizedException e) 
			            {
			            	Log.e(TAG, e.toString());
			                e.printStackTrace();
			            } 
			            catch (OAuthExpectationFailedException e) 
			            {
			            	Log.e(TAG, e.toString());
			                e.printStackTrace();
			            } 
			            catch (OAuthCommunicationException e) 
			            {
			            	Log.e(TAG, e.toString());
			                e.printStackTrace();
			            }
			        }
				}
				if(bitmaps.size()>0)
				{
					Log.d(TAG, bitmaps.size()+"");
					for(int i=0; i<bitmaps.size(); i++)
					{
						uploadToTumblr(bitmaps.get(i));
						//createText();
					}
				}
				bitmaps.clear();
			}
		});
		t.start();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		Log.d(TAG, "onResume");
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		Log.d(TAG, "onPaue");
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		Log.d(TAG, "onDestroy");
	}
	
	@Override
	protected void onNewIntent(Intent intent) 
	{
		super.onNewIntent(intent);
	    Uri uri = intent.getData();
	    

	}
	
	public static void postToTumblr(final Bitmap photo)
	{
		if(OAUTH_TOKEN.equals(""))
		{
			//has not ben authroized yet, store the pic for now
			bitmaps.add(photo);
		}
	}

	private void createText()
	{
		if(!OAUTH_TOKEN.equals(""))
		{
			Log.d(TAG, "Tumbr token"+OAUTH_TOKEN);
			Log.d(TAG, "Tumbr secxret"+OAUTH_SECRET);
			Log.d(TAG, "posttotumbr");
			
			String caption = "SNAPNOW";
			
			HttpContext context = new BasicHttpContext();
		    HttpPost request = new HttpPost("http://api.tumblr.com/v2/blog/" + blogname + ".tumblr.com/post");
			
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			
			nameValuePairs.add(new BasicNameValuePair("type", "text")); 
			nameValuePairs.add(new BasicNameValuePair("body", "this is just a test")); 	
			
			try 
			{
				request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			} 
			catch (UnsupportedEncodingException e1) 
			{
				Log.e(TAG, e1.toString());
				e1.printStackTrace();
			}
			
		    if (consumer == null)
		    {
		    	consumer = new CommonsHttpOAuthConsumer(OAuthConstants.TUMBR_CONSUMERKEY, OAuthConstants.TUMBR_SECRETKEY);
		    }
			if (OAUTH_TOKEN == null || OAUTH_SECRET == null)
			{
				//throw new LoginErrorException(LoginErrorException.NOT_LOGGED_IN);
				Log.e(TAG, "Not logged in error");
			}
			consumer.setTokenWithSecret(OAUTH_TOKEN, OAUTH_SECRET);

			try 
			{
			    consumer.sign(request);
			} 
			catch (OAuthMessageSignerException e) 
			{
			   
			} 
			catch (OAuthExpectationFailedException e) 
			{
			} 
			catch (OAuthCommunicationException e) 
			{
			}
		    HttpClient client = new DefaultHttpClient();
		    //finally execute this request
		    try 
		    {
				HttpResponse response = client.execute(request, context);
				HttpEntity responseEntity = response.getEntity(); 
				if (responseEntity != null) 
				{ 
					Log.d(TAG, "responseEntety!=null");
				    try 
				    {
						Log.d(TAG, EntityUtils.toString(responseEntity));
					} 
				    catch (ParseException e) 
				    {
				    	e.printStackTrace();
						Log.e(TAG, e.toString());
					} 
				    catch (IOException e) 
				    {
						e.printStackTrace();
						Log.e(TAG, e.toString());
					} // gives me {"meta":{"status":401,"msg":"Not Authorized"},"response":[]} when I try to upload a photo
				}
				else
				{
					Log.d(TAG, "responseEntety==null");
				}
			} 
		    catch (ClientProtocolException e) 
		    {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		    catch (IOException e) 
		    {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		PostToTumblr.this.finish();
	}
	
	private void uploadToTumblr(Bitmap photo)
	{
		if(!OAUTH_TOKEN.equals(""))
		{
			Log.d(TAG, "Tumbr token"+OAUTH_TOKEN);
			Log.d(TAG, "Tumbr secxret"+OAUTH_SECRET);
			Log.d(TAG, "posttotumbr");
			
			String caption = "SNAPNOW";
			
		
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
			byte[] bytes = stream.toByteArray();
			
			String text ="SNAP";
			
			
		    HttpContext context = new BasicHttpContext();
		    HttpPost request = new HttpPost("http://api.tumblr.com/v2/blog/" + blogname + ".tumblr.com/post");

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2); 
			String enc = "UTF-8"; 
	
			try 
			{
				nameValuePairs.add(new BasicNameValuePair(URLEncoder.encode("type", enc), URLEncoder.encode("photo", enc)));
				nameValuePairs.add(new BasicNameValuePair(URLEncoder.encode("caption", enc), URLEncoder.encode(text, enc))); 
				nameValuePairs.add(new BasicNameValuePair("data", Base64.encodeToString(bytes, Base64.URL_SAFE))); 
				//String s = new String(bytes);
				//String s = Base64.encodeToString(bytes, Base64.URL_SAFE);
				//AAAAAAAAAAAAAAAAAAAAA
				//http://www.coderanch.com/t/526487/java/java/Java-Byte-Hex-String
			    final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
			    char[] hexChars = new char[bytes.length * 3];
			    int v;
			    for ( int j = 0; j < bytes.length; j++ ) 
			    {
			        v = bytes[j] & 0xFF;
			        hexChars[j * 3] = '%';
			        hexChars[j * 3 + 1] = hexArray[v >>> 4];
			        hexChars[j * 3 + 2] = hexArray[v & 0x0F];
			    }
			    //String s="";
			    Log.d(TAG, ""+hexChars.length);
			    for(int i=0; i<hexChars.length; i++)
			    {
			    	//Log.d(TAG, ""+hexChars[i]);
			    	//s=s.concat(String.valueOf(hexChars[i]));
			    	if(i>200 && i<500)
			    	{
			    		System.out.print(hexChars[i]);
			    	}
			   }
			    System.out.println();
			    String s = new String(hexChars);		    	
			    s = URLEncoder.encode(s, enc);
				if(s.length()>500)
				{
					String s2 = s.substring(200,500);
					Log.d(TAG, s2);
				}
				else
				{
					String s2 = s.substring(0,s.length()-5);
					Log.d(TAG, s2);
				}
				nameValuePairs.add(new BasicNameValuePair(URLEncoder.encode("data", enc), s)); 
			} 
			catch (UnsupportedEncodingException e2) 
			{
				Log.e(TAG, e2.toString());
				e2.printStackTrace();
			} 
			try 
			{
				request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			} 
			catch (UnsupportedEncodingException e1) 
			{
				Log.e(TAG, e1.toString());
				e1.printStackTrace();
			}
			
		    if (consumer == null)
		    {
		    	consumer = new CommonsHttpOAuthConsumer(OAuthConstants.TUMBR_CONSUMERKEY, OAuthConstants.TUMBR_SECRETKEY);
		    }
			if (OAUTH_TOKEN == null || OAUTH_SECRET == null)
			{
				//throw new LoginErrorException(LoginErrorException.NOT_LOGGED_IN);
				Log.e(TAG, "Not logged in error");
			}
			consumer.setTokenWithSecret(OAUTH_TOKEN, OAUTH_SECRET);

			    try 
			    {
			        consumer.sign(request);
			    } 
			    catch (OAuthMessageSignerException e) 
			    {
			       
			    } 
			    catch (OAuthExpectationFailedException e) 
			    {

			    } 
			    catch (OAuthCommunicationException e) 
			    {
			    }

			    HttpClient client = new DefaultHttpClient();

			    //finally execute this request
			    try 
			    {
					HttpResponse response = client.execute(request, context);
					HttpEntity responseEntity = response.getEntity(); 
					if (responseEntity != null) 
					{ 
						Log.d(TAG, "responseEntety!=null");
					    try 
					    {
							Log.d(TAG, EntityUtils.toString(responseEntity));
						} 
					    catch (ParseException e) 
					    {
							e.printStackTrace();
							Log.e(TAG, e.toString());
						} 
					    catch (IOException e) 
					    {
							e.printStackTrace();
							Log.e(TAG, e.toString());
						} 
					}
					else
					{
						Log.d(TAG, "responseEntety==null");
					}
				} 
			    catch (ClientProtocolException e) 
			    {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			    catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			
			
			//thge old stuff
			    /*
			int size = photo.getRowBytes() *photo.getHeight();
			ByteBuffer b = ByteBuffer.allocate(size);
			photo.copyPixelsToBuffer(b);
			byte[] bytes = new byte[size];
			Log.d(TAG, "posttotumbr "+size);
			try 
			{
			    b.get(bytes, 0, bytes.length);
			}
			catch (BufferUnderflowException e)
			{
			    //always happens
			}
			String text ="SNAP";

			HttpPost hpost = new HttpPost("http://api.tumblr.com/v2/blog/" + blogName + ".tumblr.com/post");
			Log.d(TAG, "post httppost");
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2); 
			nameValuePairs.add(new BasicNameValuePair("type", "photo")); 
			nameValuePairs.add(new BasicNameValuePair("body", "this is just a test")); 
			
			//nameValuePairs.add(new BasicNameValuePair("caption", text)); 
			//instead of "data" I've tried also "data[0]" or "data%5B0%5D"
			//variants (I've tried every line separately) - bitmap = byte[]
			//nameValuePairs.add(new BasicNameValuePair("data", new String(bytes))); 
			//nameValuePairs.add(new BasicNameValuePair("data", Base64.encodeToString(bytes, Base64.URL_SAFE))); 
			try 
			{
				hpost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				Log.d(TAG, "->");
			} 
			catch (UnsupportedEncodingException e) 
			{
				e.printStackTrace();
				Log.e(TAG, "FAIL");
				Log.e(TAG, e.toString());
			}
			CommonsHttpOAuthConsumer consumer = new CommonsHttpOAuthConsumer(OAuthConstants.TUMBR_CONSUMERKEY, OAuthConstants.TUMBR_SECRETKEY); 
			consumer.setTokenWithSecret(OAUTH_TOKEN, OAUTH_SECRET); 
			Log.d(TAG, "xyz");
			try 
			{
				consumer.sign(hpost);
			} 
			catch (OAuthMessageSignerException e) 
			{
				e.printStackTrace();
				Log.e(TAG, e.toString());
			} 
			catch (OAuthExpectationFailedException e) 
			{
				e.printStackTrace();
				Log.e(TAG, e.toString());
			} 
			catch (OAuthCommunicationException e) 
			{
				e.printStackTrace();
				Log.e(TAG, e.toString());
			} 
			Log.d(TAG, "ert");
			DefaultHttpClient client = new DefaultHttpClient(); 
			try 
			{
				HttpResponse response = client.execute(hpost);
				HttpEntity responseEntity = response.getEntity(); 
				if (responseEntity != null) 
				{ 
					Log.d(TAG, "responseEntety!=null");
				    try 
				    {
						Log.d(TAG, EntityUtils.toString(responseEntity));
					} 
				    catch (ParseException e) 
				    {
						e.printStackTrace();
						Log.e(TAG, e.toString());
					} 
				    catch (IOException e) 
				    {
						e.printStackTrace();
						Log.e(TAG, e.toString());
					} // gives me {"meta":{"status":401,"msg":"Not Authorized"},"response":[]}
				}
				else
				{
					Log.d(TAG, "responseEntety==null");
				}
			} 
			catch (ClientProtocolException e) 
			{
				e.printStackTrace();
				Log.e(TAG, e.toString());
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
				Log.e(TAG, e.toString());
			} */
			//PostToTumblr.this.finish();
			}

		else
		{
			Log.d(TAG, "upload imposble... Toklen not set");
		}
		PostToTumblr.this.finish();
	}
}
