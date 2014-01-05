package de.lukeslog.snapnao.actors;

import de.lukeslog.snapnao.constants.SnapNowConstants;
import de.lukeslog.snapnao.main.SnapNowActivity;
import de.lukeslog.snapnow.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class NotificationActor extends Service {

	private final String TAG = SnapNowConstants.TAG;
	private Context context;
	public static boolean cancel=false;
	
	@Override
	public IBinder onBind(Intent intent) 
	{
		return null;
	}
	
	@Override
	public void onCreate() 
	{
		Log.d(TAG, "NotificationActor");
		super.onCreate();
	}

	private void notification(final int number) 
	{
		new Thread(new Runnable() 
		{
			public void run() 
			{
				for(int i=0; i<number; i++)
				{
					if(!cancel)
					{
						if(number%3==0)
						{
							NotificationActor.this.notify(true);
						}
						else
						{
							NotificationActor.this.notify(true);
						}
						try 
						{
							Thread.sleep(2000);
						} 
						catch (InterruptedException e) 
						{
							e.printStackTrace();
						}
					}
				}
				cancel=true;
			}
		}).start();
	}
	
	private void notify(boolean sound)
	{
		//TODO: how many types of notification are there?
		//Chose one at random
    	//Log.i(TAG, "onNotification()");
    	final NotificationManager mNotMan = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		final Notification notfication = new Notification(
				R.drawable.snapcolor, "SNAPNAO",
				System.currentTimeMillis());
		// on can puit the countdown here!
		Intent settingsIntent = new Intent(this, SnapNowActivity.class);
		final PendingIntent pIntent = PendingIntent.getActivity(this, 0,
				settingsIntent, 0);
		notfication.setLatestEventInfo(context.getApplicationContext(),
				"SNAPNAO", getResources().getString(R.string.whatareyoulookingat), pIntent);
		notfication.defaults |= Notification.DEFAULT_VIBRATE;
	    notfication.vibrate = new long[]{500,500,500,500};
	    if(sound)
	    {
	    	notfication.defaults |= Notification.DEFAULT_SOUND;
	    }
		mNotMan.notify(48, notfication);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		super.onStartCommand(intent, flags, startId);
		//Log.i(TAG, "Received start id " + startId + ": " + intent);
		cancel=false;
		context=this;
		notification(230);
		
		return Service.START_NOT_STICKY;
	}
	
    @Override
    public void onDestroy() 
    {
        super.onDestroy();
        cancel=true;
    }

}
