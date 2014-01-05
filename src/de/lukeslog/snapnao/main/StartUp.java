package de.lukeslog.snapnao.main;

import de.lukeslog.snapnao.constants.SnapNowConstants;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StartUp  extends BroadcastReceiver
{

	private final String TAG = SnapNowConstants.TAG;
	
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		try
	    {
	        Intent myIntent = new Intent ( context, SnapNowBackgroundService.class );
	        context.startService(myIntent);
	    }
	    catch ( Exception e )
	    {
	        Log.e(TAG, "Error on autostart");
	    }
		
	}

}
