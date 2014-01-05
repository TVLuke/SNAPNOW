package de.lukeslog.snapnao.main;

import de.lukeslog.snapnao.constants.SnapNowConstants;
import de.lukeslog.snapnow.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class NotificationService extends Service 
{
	private final String TAG = SnapNowConstants.TAG;
	@Override
	public IBinder onBind(Intent arg0) 
	{
		return null;
	}
	
	@Override
	public void onCreate() 
	{
		super.onCreate();
	 	Log.i(TAG, "SNAPNOW Service Läuft");
		if(!SnapNowBackgroundService.blackout)
		{
			Notification note=new Notification(R.drawable.snapgrey, "SNAPNOW is running", System.currentTimeMillis());
			 Intent i=new Intent(this, SnapNowActivity.class);
	
			 i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
					 Intent.FLAG_ACTIVITY_SINGLE_TOP);
	
			PendingIntent pi=PendingIntent.getActivity(this, 0,
	              i, 0);
	
			note.setLatestEventInfo(this, "SNAPNOW",
					"...running",
					pi);
			note.flags|=Notification.FLAG_AUTO_CANCEL;
			startForeground(1337, note);
		}
		else
		{
			Notification note=new Notification(R.drawable.bkackoutlogo, "SNAPNOW is running", System.currentTimeMillis());
			 Intent i=new Intent(this, SnapNowActivity.class);
	
			 i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
					 Intent.FLAG_ACTIVITY_SINGLE_TOP);
	
			PendingIntent pi=PendingIntent.getActivity(this, 0,
	              i, 0);
	
			note.setLatestEventInfo(this, "SNAPNOW",
					"...running",
					pi);
			note.flags|=Notification.FLAG_AUTO_CANCEL;
			startForeground(1337, note);
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		super.onStartCommand(intent, flags, startId);
		Log.i(TAG, "Received start id " + startId + ": " + intent);
		return Service.START_STICKY;
	}
	
    @Override
    public void onDestroy() 
    {
        super.onDestroy();
    	//AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
    	//if(intent!=null)
    	//{
	    //	PendingIntent p = PendingIntent.getService(this, SnapNowConstants.SNAPALARM, intent, PendingIntent.FLAG_NO_CREATE);
	    //	am.cancel(p);
    	//}
    }
}
