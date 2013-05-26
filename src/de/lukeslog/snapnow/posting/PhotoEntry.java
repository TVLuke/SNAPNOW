package de.lukeslog.snapnow.posting;

import java.util.ArrayList;


import de.lukeslog.snapnow.constants.SnapNowConstants;

import android.app.Activity;
import android.util.Log;

public class PhotoEntry extends Entry
{	
	
	String realpath;
	private static String TAG = SnapNowConstants.TAG;
	
	public PhotoEntry(String absolutePath, Activity ctx) 
	{
		super(ctx);
		addTag(Entry.TAG_TYPE, "photo");
		Log.d(TAG, "Asbolute Path:"+absolutePath);
		this.realpath = absolutePath;
	}
	
	//FOR THE DATABASE
	public PhotoEntry(long entryid, boolean uploaded, String date, String header, ArrayList tags, String path)
	{
		super(entryid, uploaded, date, header, tags);
		this.realpath=path;
	}

	public String getPath()
	{
		return realpath;
	}
	
}
