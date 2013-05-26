package de.lukeslog.snapnow.posting;

import java.util.ArrayList;

import de.lukeslog.snapnow.constants.SnapNowConstants;
import de.lukeslog.snapnow.main.SnapNowBackgroundService;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

public class UploadService extends Service
{

	private final String TAG = SnapNowConstants.TAG;
	static SharedPreferences prefs;
	
	Service context;
	
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
	 	Log.i(TAG, " onCreate()");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		super.onStartCommand(intent, flags, startId);
		ArrayList<Entry> entrys = SnapNowBackgroundService.getUnuplodedEntrys();
		Log.d(TAG, "Entrysize="+entrys.size());
		for (int i=entrys.size()-1; i>=0; i--)
		{
			Entry entry = entrys.get(i);
			Log.d(TAG, "x");
			entry.upload(this);
	        prefs = getSharedPreferences(SnapNowConstants.PREFS, 0);
		}
		return Service.START_NOT_STICKY;
	}
}
