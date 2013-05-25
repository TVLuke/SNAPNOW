package de.lukeslog.snapnow.posting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import de.lukeslog.snapnow.constants.SnapNowConstants;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public class Entry
{
	private final String TAG = SnapNowConstants.TAG;
	
	public static final String TAG_SNAPNOW="snapnow";
	public static final String TAG_TYPE="type";
	public static final String TAG_DAY="day";
	public static final String TAG_MONTH="month";
	public static final String TAG_HOUR="hour";
	public static final String TAG_YEAR="year";
	
	public static final String TAG_COUNTRY="country";
	public static final String TAG_POSTALCODE="plz";
	public static final String TAG_LOCALITY="locality";
	
	HashMap<String, String> tags;
	HashMap<String, Boolean> uploadServices;
	
	Date d;
	
	boolean uploaded=false;
	long id;
	
	Entry(Context ctx)
	{
		d = new Date();
		id= d.getTime();
		tags = new HashMap<String, String>();
		uploadServices = new HashMap<String, Boolean>();
		addTag(TAG_SNAPNOW, "SNAPNOW");
		//get location
    	LocationManager locationManager = (LocationManager) ctx.getSystemService(ctx.LOCATION_SERVICE);
    	String locationProvider = LocationManager.GPS_PROVIDER;
    	// Or use LocationManager.GPS_PROVIDER
    	Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
    	Geocoder gcd = new Geocoder(ctx, Locale.getDefault());
    	List<Address> addresses;
		try 
		{
			if(lastKnownLocation!=null)
			{
				addresses = gcd.getFromLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), 1);
	        	if (addresses.size() > 0) 
	        	{
	        		addTag(TAG_COUNTRY, addresses.get(0).getCountryName());
	        		addTag(TAG_POSTALCODE, "PLZ"+addresses.get(0).getPostalCode());
	        		addTag(TAG_LOCALITY, addresses.get(0).getLocality());
	        	}
			}
		} 
		catch (Exception e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		int y = d.getYear()+1900;
		addTag(TAG_YEAR, ""+y);
		int month = d.getMonth();
		if(month==0)
		{
			addTag(TAG_MONTH, "january");
		}
		if(month==1)
		{
			addTag(TAG_MONTH, "february");				
		}
		if(month==2)
		{
			addTag(TAG_MONTH, "march");
		}
		if(month==3)
		{
			addTag(TAG_MONTH, "april");				
		}
		if(month==4)
		{
			addTag(TAG_MONTH, "may");
		}
		if(month==5)
		{
			addTag(TAG_MONTH, "june");
		}
		if(month==6)
		{
			addTag(TAG_MONTH, "july");
		}
		if(month==7)
		{
			addTag(TAG_MONTH, "august");				
		}
		if(month==8)
		{
			addTag(TAG_MONTH, "september");				
		}
		if(month==9)
		{
			addTag(TAG_MONTH, "october");
		}
		if(month==10)
		{
			addTag(TAG_MONTH, "november");
		}
		if(month==11)
		{
			addTag(TAG_MONTH, "december");
		}
		int dayoftheweek = d.getDay();
		if(dayoftheweek==0)
		{
			addTag(TAG_DAY, "sunday");
		}
		if(dayoftheweek==1)
		{
			addTag(TAG_DAY, "monday");
		}
		if(dayoftheweek==2)
		{
			addTag(TAG_DAY, "tuesday");
		}
		if(dayoftheweek==3)
		{
			addTag(TAG_DAY, "wednesday");
		}
		if(dayoftheweek==4)
		{
			addTag(TAG_DAY, "thursday");
		}
		if(dayoftheweek==5)
		{
			addTag(TAG_DAY, "friday");
		}
		if(dayoftheweek==6)
		{
			addTag(TAG_DAY, "saturday");				
		}
		int hour = d.getHours();
		if(hour<13)
		{
			addTag(TAG_HOUR, hour+"am");
		}
		else
		{
			addTag(TAG_HOUR, (hour-12)+"pm");
		}
		Set<String> keys = tags.keySet();
		for(String key : keys)
		{
			if(tags.get(key).contains("ü"))
			{
				tags.put(key, tags.get(key).replace("ü", "ue"));
			}
			if(tags.get(key).contains("ö"))
			{
				tags.put(key, tags.get(key).replace("ö", "oe"));
			}
			if(tags.get(key).contains("ä"))
			{
				tags.put(key, tags.get(key).replace("ä", "ae"));
			}
			if(tags.get(key).equals("plznull"))
			{
				tags.remove(key);
			}
			if(tags.get(key).equals("null"))
			{
				tags.remove(key);
			}
		}

	}
	
	public void addTag(String type, String tag)
	{
		if(tag.equals("null") || tag.equals("PLZnull"))
		{
			//this tag is rejected.
		}
		else
		{
			tags.put(type, tag);
		}
	}
	
	public String getTag(String type)
	{
		return tags.get(type);
	}
	
	public ArrayList<String> getTagsAsArrayList()
	{
		Collection<String> tagvalues = tags.values();
		Iterator<String> it1 = tagvalues.iterator();
		ArrayList<String> thetags = new ArrayList<String>();
		while(it1.hasNext())
		{
			thetags.add(it1.next());
		}
		return thetags;
	}
	
	public void upload(UploadService uploadService)
	{
		Log.d(TAG, "entry upload");
		SharedPreferences prefs = uploadService.getSharedPreferences(SnapNowConstants.PREFS, 0);
		boolean tumblrBool = prefs.getBoolean("tumblr", false);
        Log.d(TAG, ""+tumblrBool);
    	boolean twitterBool = prefs.getBoolean("twitter", false);
    	Log.d(TAG, "Twitterbool "+twitterBool);
        boolean sendViaEmail = prefs.getBoolean("sendViaEmail", false);
       	Log.d(TAG, "Send Via Email "+sendViaEmail);
    	//Go through the registered Actors, and ask them to do their thing of they are of a usable type (which in this case, since ... W00t
    	//solution for now...
    	if(tumblrBool)
    	{
    		postToTumblr(uploadService);
    	}
    	if(twitterBool)
    	{
    		
    		postToTwitter(uploadService);
    	}
    	if(sendViaEmail)
    	{
    		postToEmail(uploadService);
    	}
    	if(!tumblrBool && !twitterBool && !sendViaEmail)
    	{
    		postToNothing();
    	}
	}
	
	public boolean isUploaded()
	{
		return uploaded;
	}
	
	public void setUploaded(boolean x)
	{
		uploaded=x;
	}
	
	private void postToTumblr(UploadService uploadService)
	{
			Intent ptt = new Intent(uploadService, PostToTumblrViaMail.class);
			ptt.putExtra("EntryID", id);
			uploadService.startService(ptt);
	}
	
	private void postToTwitter(UploadService uploadService)
	{
       	try 
       	{
       		Intent twitter = new Intent(uploadService, PostToTwitter.class);
       		twitter.putExtra("EntryID", id);
       		twitter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
       		uploadService.startActivity(twitter);
		} 
       	catch (Exception e) 
       	{
			Log.e(TAG, e.toString());
			e.printStackTrace();
		}
   	}
	
	private void postToEmail(UploadService uploadService)
	{
		Intent em = new Intent(uploadService, EmailService.class);
		em.putExtra("EntryID", id);
		uploadService.startService(em);
	}
	
	//if no uploading is activated the post will just be marked as done so as to not upload it later...,
	private void postToNothing()
	{
		uploaded=true;
	}

	public long getId() 
	{
		return id;
	}
	
	public String dateAsAString()
	{
		int y = d.getYear()+1900;
		int month = d.getMonth();
		month++;
		int day = d.getDate();
		
		int hour = d.getHours();
		String shour=""+hour;
		if(hour<10)
		{
			shour="0"+hour;
		}
		int minute = d.getMinutes();
		String sminute=""+minute;
		if(minute<10)
		{
			sminute = "0"+minute;
		}
		String smonth=""+month;
		if(month<10)
		{
			smonth="0"+month;
		}
		String sday=""+day;
		if(day<10)
		{
			sday="0"+day;
		}
		return y+"-"+smonth+"-"+sday+" "+shour+"."+sminute;
	}
	
}
