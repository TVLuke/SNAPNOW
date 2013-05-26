package de.lukeslog.snapnow.posting;

import java.util.ArrayList;
import java.util.StringTokenizer;

import de.lukeslog.snapnow.constants.SnapNowConstants;
import de.lukeslog.snapnow.main.SnapNowBackgroundService;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

public class EmailService extends Service
{
	private final String TAG = SnapNowConstants.TAG;
	static SharedPreferences prefs;
	
	Service context;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() 
	{
		super.onCreate();
	 	Log.i(TAG, "PostToTumblrVia Mail onCreate()");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		super.onStartCommand(intent, flags, startId);
		Log.i(TAG, "PostToTumblrVia Mail onStart()");
		//Log.i(TAG, "Received start id " + startId + ": " + intent);
		context=this;

		prefs = getSharedPreferences(SnapNowConstants.PREFS, 0);

		String emailadresses = prefs.getString("emailadresses", "");
		StringTokenizer tk = new StringTokenizer(emailadresses, ","); 
		
		long entryId = intent.getLongExtra("EntryID", 0l);
		final Entry entry = SnapNowBackgroundService.getEntryById(entryId);
		while(tk.hasMoreTokens())
		{
			String stringTarget = tk.nextToken();
			stringTarget = stringTarget.replace(" ", "");
			
			if(entry!=null)
			{
				if(entry instanceof PhotoEntry)
				{
					String image = ((PhotoEntry) entry).getPath();
					if(!(image.equals("")))
					{
						Log.i(TAG, "path="+image);
						String gmailaccString= prefs.getString("gmailacc", "");
					    String gmailpswString= prefs.getString("gmailpsw", "");
					    Log.i(TAG, "gmailacc="+gmailaccString);
					    Log.i(TAG, "newmail");
						final BackgroundMail m = new BackgroundMail(gmailaccString, gmailpswString);
						Log.i(TAG, "setTo");
						String t[] = new String[1];
						t[0]= stringTarget;
						m.setTo(t);
						Log.i(TAG, "Set From");
						m.setFrom(gmailaccString);
						Log.i(TAG, "setSubject");
						m.setSubject(entry.getHeader());
						Log.i(TAG, "setBody");
						String body="";
						ArrayList<String> tags = entry.getTagsAsArrayList();
						for(String tag: tags)
						{
							body = body+" #"+tag;
						}
						m.setBody(body);
						try 
						{
							Log.i(TAG, "add Atachment");
							m.addAttachment(image);
						} 
						catch (Exception e) 
						{
				        	Log.e(TAG, e.toString());
							e.printStackTrace();
						}
						Thread tt = new Thread(new Runnable()
						{
							@Override
							public void run()
							{
								try 
								{
									Log.i(TAG, "send?");
									m.send();
									
								} 
								catch (Exception e) 
								{
									// TODO Auto-generated catch block
									e.printStackTrace();
								}	
							}
							
						});
						tt.start(); 
						SnapNowBackgroundService.uploaded(entry);
					}
					else
					{
						Log.e(TAG, "imageString is not there");
					}

				}
				else if(entry instanceof TextEntry)
				{
					Log.d(TAG, "textentry");
					String gmailaccString= prefs.getString("gmailacc", "");
				    String gmailpswString= prefs.getString("gmailpsw", "");
				    Log.i(TAG, "gmailacc="+gmailaccString);
				    Log.i(TAG, "newmail");
					final BackgroundMail m = new BackgroundMail(gmailaccString, gmailpswString);
					Log.i(TAG, "setTo");
					String t[] = new String[1];
					t[0]= stringTarget;
					m.setTo(t);
					Log.i(TAG, "Set From");
					m.setFrom(gmailaccString);
					Log.i(TAG, "setSubject");
					m.setSubject(((TextEntry) entry).getHeader());
					Log.i(TAG, "setBody");
					String body = ((TextEntry) entry).getText();
					ArrayList<String> tags = entry.getTagsAsArrayList();
					for(String tag: tags)
					{
						body = body+" #"+tag;
					}
					m.setBody(body);
					Thread tt = new Thread(new Runnable()
					{
						@Override
						public void run()
						{
							try 
							{
								Log.i(TAG, "send?");
								m.send();
							} 
							catch (Exception e) 
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}	
						}
						
					});
					tt.start(); 
					SnapNowBackgroundService.uploaded(entry);
				}
			}
		}
		return Service.START_NOT_STICKY;
	}
	

    @Override
    public void onDestroy() 
    {
        super.onDestroy();
    }
}
