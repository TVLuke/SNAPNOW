package de.lukeslog.snapnow.posting;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import de.lukeslog.snapnow.R;
import de.lukeslog.snapnow.constants.SnapNowConstants;
import de.lukeslog.snapnow.main.OAuthConstants;
import de.lukeslog.snapnow.main.SnapNowActivity;
import de.lukeslog.snapnow.main.SnapNowBackgroundService;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

public class PostToTwitter extends Activity
{
	private final String TAG = SnapNowConstants.TAG;
	static SharedPreferences prefs;
	public static String TWITTER_OAUTH_CONSUMER_KEY=OAuthConstants.TWITTER_OAUTH_CONSUMER_KEY;
	public static String TWITTER_OAUTH_CONSUMER_SECRET=OAuthConstants.TWITTER_OAUTH_CONSUMER_SECRET;
	public static String TWITTER_OAUTH_ACCESS_TOKEN=OAuthConstants.TWITTER_OAUTH_ACCESS_TOKEN;
	public static String TWITTER_OAUTH_ACCESS_TOKEN_SECRET=OAuthConstants.TWITTER_OAUTH_ACCESS_TOKEN_SECRET;
	
	Twitter twitter;
	Activity context;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
		prefs = getSharedPreferences(SnapNowConstants.PREFS, 0);
        final String x = prefs.getString("twitteroauthtolken", "");
		if(x.equals(""))
        {
        	setContentView(R.layout.twitterauth);
        }
		else
		{
        	setContentView(R.layout.empty);
		}
		Long entryId = (Long) getIntent().getExtras().get("EntryID");
		final Entry entry = SnapNowBackgroundService.getEntryById(entryId);
		if(entry!=null)
		{
			if(entry instanceof PhotoEntry)
			{
				String image = ((PhotoEntry) entry).getPath();
		        final File file = new File(image);
		        Log.d(TAG, file.getAbsolutePath());
		        Log.d(TAG, "file exists? "+file.exists());
		        context = this;
				Thread t = new Thread(new Runnable()
				{
					@Override
					public void run()
					{
					     
						twitter = new TwitterFactory().getInstance();
						Log.d(TAG, "Twitterkram geht los");
						//thanks to http://consultingblogs.emc.com/nileeshabojjawar/archive/2010/03/18/twitter4j-oauth-generating-the-access-token.aspx
						twitter.setOAuthConsumer(TWITTER_OAUTH_CONSUMER_KEY, TWITTER_OAUTH_CONSUMER_SECRET);
						try 
						{
							final RequestToken requestToken = twitter.getOAuthRequestToken();
							if(x.equals(""))
					        {
					            try 
					            {
					    			authorize(requestToken);
					    		} 
					            catch (Exception e) 
					            {
					    			// TODO Auto-generated catch block
					    			e.printStackTrace();
					    		}
					            Button photoButton = (Button) context.findViewById(R.id.button1);
					            photoButton.setOnClickListener(new View.OnClickListener() 
					            {
				
					                @Override
					                public void onClick(View v) 
					                {
					                		EditText pp = (EditText) findViewById(R.id.twitterpin);
					                		String pin = pp.getEditableText().toString();
					                		postauthorization(pin, file, "#SnapNOW", requestToken);
					                }
					            });
					        }
					        else
					        {
					            try 
					            {
					            	String tags="";
					            	for(String tag: entry.getTagsAsArrayList())
					            	{
					            		tags=tags+" #"+tag;
					            		if(tags.length()>100)
					            		{
					            			break;
					            		}
					            	}
					            	postToTwitter(file, tags);
					    		} 
					            catch (Exception e) 
					            {
					    			// TODO Auto-generated catch block
					    			e.printStackTrace();
					    		}
					        }
						}
						catch (TwitterException e1)
						{
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						PostToTwitter.this.finish();
					}
				});
				t.start();
			}
		}
        
	}

	public void authorize(final RequestToken requestToken) throws Exception  
	{
		Log.d(TAG, "authorize");
		try
		{
			
					prefs = getSharedPreferences(SnapNowConstants.PREFS, 0);
			        String x = prefs.getString("twitteroauthtolken", "");
			        if(x.equals(""))
			        {
			        	Log.d(TAG, "x is nicht");
						Log.d(TAG, "Open the following URL and grant access to your account:");
						Log.d(TAG, requestToken.getAuthorizationURL());
						runOnUiThread(new Runnable() 
						{
						    public void run() 
						    {
						    	WebView wv = (WebView) findViewById(R.id.twitterview);
								wv.loadUrl(requestToken.getAuthorizationURL());
						    }
						});
						startActivityForResult(new Intent("android.intent.action.VIEW", Uri.parse(requestToken.getAuthorizationURL())), 5234);
						Log.d(TAG, "Enter the PIN(if aviailable) or just hit enter.[PIN]:");
			        }
			        else
			        {
			        	Log.d(TAG, "x war schon gesetzt, hier sollte nichts passieren");
			        }
		}
		catch(Exception e)
		{
			Log.e(TAG, e.toString());
		}
	}
	
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	 {
		 Log.d(TAG, "Got Back from where I cam from.");
	 }
	
	private void postauthorization(final String pin, final File file, final String text, final RequestToken requestToken)
	{
		Log.d(TAG, "postauthorization");
		Thread t = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				AccessToken accessToken= null;
				try
				{
					 if(pin.length() > 0)
			         {
			           accessToken = twitter.getOAuthAccessToken(requestToken, pin);
			         }
			         else
			         {
			           accessToken = twitter.getOAuthAccessToken();
			         }
					 prefs = getSharedPreferences(SnapNowConstants.PREFS, 0);
					 Editor edit =prefs.edit();
					 edit.putString("twitteroauthtolken", accessToken.getToken());
					 edit.putString("twitteroauthtolkensecret", accessToken.getTokenSecret());
					 edit.commit();
					 System.out.println(twitter.verifyCredentials().getId());
					 StatusUpdate status = new StatusUpdate(text);
				     Log.d(TAG, file.getAbsolutePath());
				     Log.d(TAG, "file exists? "+file.exists());
				     status.setMedia(file);
				     twitter.updateStatus(status);
				}
				catch(Exception e)
				{
					Log.e(TAG, e.toString());
				}
			}
		});
		t.start();
	}
	
	private void postToTwitter(final File file, final String text)
	{
		Log.d(TAG, "posttotwitter");
				try
				{
					final String x = prefs.getString("twitteroauthtolken", "");
					final String y = prefs.getString("twitteroauthtolkensecret", "");
					Log.d(TAG, "->r"+x+y);
					AccessToken accessToken = new AccessToken(x,y);
					Log.d(TAG, "->");
					twitter.setOAuthAccessToken(accessToken);
					Log.d(TAG, "ok, twittershouldbeset");
					System.out.println(twitter.verifyCredentials().getId());
					StatusUpdate status = new StatusUpdate(text);
					Log.d(TAG, "adding file");
				    status.setMedia(file);
				    twitter.updateStatus(status);
				}
				catch(Exception e)
				{
					Log.e(TAG, e.toString());
				}
				PostToTwitter.this.finish();
	}
}
