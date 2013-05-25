package de.lukeslog.snapnow.posting;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.lukeslog.snapnow.constants.SnapNowConstants;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

public class PhotoEntry extends Entry
{	
	
	//Uri photoURI;
	//File photoFile;
	String realpath;
	private static String TAG = SnapNowConstants.TAG;
	
	//public PhotoEntry(Uri imageURI, Activity ctx)
	//{
	//	super(ctx);
	//	addTag(Entry.TAG_TYPE, "photo");
	//	if(!(imageURI.toString().equals("")))
	//	{
	//		this.photoURI = imageURI;
    //		this.photoFile = new File(photoURI.getPath());
    //		this.realpath = getPath(photoURI, ctx);
	//	}
	//}
	
	public PhotoEntry(String absolutePath, Activity ctx) 
	{
		super(ctx);
		addTag(Entry.TAG_TYPE, "photo");
		Log.d(TAG, "Asbolute Path:"+absolutePath);
		this.realpath = absolutePath;
	}

	public String getPath()
	{
		return realpath;
	}
	
	//private String getPath(Uri uri, Activity a) 
	//{
	//	String[] projection = { MediaStore.Images.Media.DATA };
	//	Cursor cursor = a.managedQuery(uri, projection, null, null, null);
	//	a.startManagingCursor(cursor);
	//	int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	//	cursor.moveToFirst();
	//	return cursor.getString(column_index);
	//}
	
}
