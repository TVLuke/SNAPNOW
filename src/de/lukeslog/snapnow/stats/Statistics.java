package de.lukeslog.snapnow.stats;

import java.util.Date;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.IBinder;
import android.util.Log;
import de.lukeslog.snapnow.R;
import de.lukeslog.snapnow.constants.SnapNowConstants;

public class Statistics 
{

	public static long laststamp =0;
	public static long avDifBetweenStamps=0;
	public static int stamps=0;
	
	public static int month =0;
	public static int year=0;
	// shared preferences
	public static SharedPreferences prefs;
	public final static String PREFS = SnapNowConstants.PREFS;
	private final static String TAG = SnapNowConstants.TAG;
	
	public static void randomNumber(Context context, int number)
	{
		prefs = context.getSharedPreferences(PREFS, 0);
		long f = prefs.getLong("firststart", 0);
		long lasstamp = prefs.getLong("lasstamp", 0);
		int x_number = prefs.getInt("occurance"+number, 0); //times this number has been drawn
		int x = prefs.getInt("occurance", 0); //times any number has been drwan
		long tsl = prefs.getLong("timesincelastalert_long", 0);
		Date d = new Date();
		//now get some values done...
		month=d.getMonth();
		year=d.getYear()+1900;
		Editor editor = prefs.edit();
		x++;
		x_number++;
		if(laststamp==0)
		{
			laststamp=d.getTime();
		}
		else
		{
			long difsum= avDifBetweenStamps*stamps;
			long difnow = d.getTime()-laststamp;
			laststamp=d.getTime();
			stamps++;
			difsum=difsum+difnow;
			avDifBetweenStamps = difsum/stamps;
			Log.d(TAG, "now: "+difnow);
			Log.d(TAG, "av: "+avDifBetweenStamps);
			tsl=tsl+difnow;
			editor.putLong("timesincelastalert_long", tsl);
			if(difnow<120000)
			{
				long mseconds= prefs.getLong("mseconds", 0);
				long msecondsmonth= prefs.getLong("mseconds_"+month+"_"+year, 0);
				mseconds=mseconds+difnow;
				msecondsmonth=msecondsmonth+difnow;
				editor.putLong("mseconds", mseconds);
				editor.putLong("mseconds_"+month+"_"+year, msecondsmonth);
			}
			editor.putLong("lasttamp", laststamp);
		}
		if(f==0)
		{
			//this is the first time this app runs
			editor.putLong("firststart", d.getTime());
		}
		editor.putInt("occurance"+number, x_number);
		editor.putInt("occurance", x);
		editor.commit();
		//reset for the average every 1.000.000 calls, if the environment changes, the averag has to change to. 
		// This happens every 17 days (ca.)
		if(stamps>1000000)
		{
			stamps=0;
			avDifBetweenStamps=0;
		}
	}
	
	public static long actualAverageTimeBetweenServiceCalls()
	{
		return avDifBetweenStamps;
	}
	
	public static long runtimeInSeconds(Context context)
	{
		prefs = context.getSharedPreferences(PREFS, 0);
		long mseconds= prefs.getLong("mseconds", 0);
		long seconds = mseconds/1000;
		return seconds;
	}
	
	public static long[] runtimeInSecondsMonth(Context context, long timestamp, boolean monthend)
	{
		prefs = context.getSharedPreferences(PREFS, 0);
		Date d = new Date(timestamp); 
		int month = d.getMonth();
		int year = d.getYear()+1900;
		long mseconds= prefs.getLong("mseconds_"+month+"_"+year, 0);
		long seconds = mseconds/1000;
		long secondsthismonth=0;

		int daysthismonth = d.getDate()-1;
		if(monthend)
		{
			daysthismonth = daysthismonth+5;
		}
		secondsthismonth = daysthismonth*24*60*60;
		int hourofday = d.getHours();
		secondsthismonth=secondsthismonth+hourofday*60*60;
		int minuteofhour=d.getMinutes();
		secondsthismonth=secondsthismonth+minuteofhour*60;
		int secondofminute = d.getSeconds();
		secondsthismonth=secondsthismonth+secondofminute;
		long [] result = new long[2];
		result[0]=seconds;
		result[1]=secondsthismonth;
		return result;
	}
	
	public static long sumofallerts(Context context)
	{
		prefs = context.getSharedPreferences(PREFS, 0);
		long alerts= prefs.getLong("alerts", 0);
		return alerts;
	}
	
	public static long sumofallertsMonth(Context context, long timestamp)
	{
		Date d = new Date(timestamp); 
		int month = d.getMonth();
		int year = d.getYear()+1900;
		prefs = context.getSharedPreferences(PREFS, 0);
		long alerts= prefs.getLong("alerts_"+month+"_"+year, 0);
		return alerts;
	}
	public static long sumoffails(Context context)
	{
		prefs = context.getSharedPreferences(PREFS, 0);
		long alerts= prefs.getLong("fails", 0);
		return alerts;
	}
	
	public static long sumoffailsMonth(Context context, long timestamp)
	{
		Date d = new Date(timestamp); 
		int month = d.getMonth();
		int year = d.getYear()+1900;
		prefs = context.getSharedPreferences(PREFS, 0);
		long alerts= prefs.getLong("fails_"+month+"_"+year, 0);
		return alerts;
	}
	
	public static long timesincelastalert(Context context)
	{
		prefs = context.getSharedPreferences(PREFS, 0);
		long tsl = prefs.getLong("timesincelastalert_long", 0);
		tsl=tsl/1000;
		return tsl;
	}
	

	public static long getTimeOfFirstActivation(Context context)
	{
		prefs = context.getSharedPreferences(PREFS, 0);
		long f = prefs.getLong("firststart", 0);
		return f;
	}
	/**
	 * An alert has gone off
	 * 
	 * @param context
	 */
	public static void alert(Context context)
	{
		prefs = context.getSharedPreferences(PREFS, 0);
		Editor editor = prefs.edit();
		long alerts= prefs.getLong("alerts", 0);
		long alertsmonth = prefs.getLong("alerts_"+month+"_"+year, 0);
		long tsl = prefs.getLong("timesincelastalert_long", 0);
		tsl=0;
		editor.putLong("timesincelastalert_long", tsl);
		alerts++;
		alertsmonth++;
		editor.putLong("alerts", alerts);
		editor.putLong("alerts_"+month+"_"+year, alertsmonth);
		editor.commit();
	}

	/**
	 * An Alert had gone of but was missed.
	 * @param context
	 */
	public static void fail(Context context)
	{
		prefs = context.getSharedPreferences(PREFS, 0);
		Editor editor = prefs.edit();
		long alerts= prefs.getLong("fails", 0);
		long alertsmonth = prefs.getLong("fails_"+month+"_"+year, 0);
		alerts++;
		alertsmonth++;
		editor.putLong("fails", alerts);
		editor.putLong("fails_"+month+"_"+year, alertsmonth);
		editor.commit();
	}
	
	public static String[] generateRightTimeUnit(long difvalue, Context c)
	{
		//Log.d(TAG, "generate Right time unit for "+difvalue+" seconds");
        String seconds = c.getResources().getString(R.string.seconds);
        String minutes = c.getResources().getString(R.string.minutes);
        String hours = c.getResources().getString(R.string.hours);
        String days = c.getResources().getString(R.string.days);
		String difunit=seconds;
		long difvalue2=0;
   	    if(difvalue>=60 && difvalue<60*60)
   	    {
   	    	difvalue2=difvalue/60;
   	    	difunit=minutes;
   	    }
   	    else if(difvalue>=60*60 && difvalue<24*60*60)
   	    {
   	    	difvalue2=difvalue/(60*60);
   	    	difunit=hours;
   	    }
   	    else if(difvalue>=24*60*60)
   	    {
   	    	difvalue2=difvalue/(24*60*60);
   	    	difunit=days;
   	    }
		//Log.d(TAG, "generate Right time unit for "+difvalue2+" "+difunit+"");
   	    String [] result = {""+difvalue2, difunit};
   	    return result;
	}
}
