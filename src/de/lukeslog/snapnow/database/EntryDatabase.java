package de.lukeslog.snapnow.database;

import java.util.ArrayList;
import java.util.StringTokenizer;

import de.lukeslog.snapnow.constants.SnapNowConstants;
import de.lukeslog.snapnow.posting.*;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class EntryDatabase 
{

	private static SQLiteDatabase database;
	private static String TAG = SnapNowConstants.TAG;
	
	public static void createDataBase(Context context) 
	{
		Log.i(TAG, "create called EntryDB");
		OpenHelper openHelper = new OpenHelper(context);
		database = openHelper.getWritableDatabase();
	}
	
	public static class OpenHelper extends SQLiteOpenHelper
	{

		OpenHelper(Context context) 
		{
			super(context, SnapNowConstants.DATABASE_NAME, null,  SnapNowConstants.DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db)
		{
			Log.i(TAG, "onCreate for EntryDB");
			db.execSQL(SnapNowConstants.TABLE_ENTRY_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
		{
			Log.i(TAG, "onUpgrade for EntryDB");
			db.execSQL("DROP TABLE IF EXISTS " + SnapNowConstants.TABLE_ENTRY);
			onCreate(db);
		}
	}

	public static void put(PhotoEntry e) 
	{
		Log.i(TAG, "->put this into the database");
		ContentValues initialValues = new ContentValues();
		initialValues.put(SnapNowConstants.TABLE_ENTRY_ENTRYID, e.getId());
		initialValues.put(SnapNowConstants.TABLE_ENTRY_TYPE, SnapNowConstants.ENTRYTYPE_PHOTO);
		initialValues.put(SnapNowConstants.TABLE_ENTRY_DATE, e.getDateAsString());
		initialValues.put(SnapNowConstants.TABLE_ENTRY_TEXT, "");
		initialValues.put(SnapNowConstants.TABLE_ENTRY_HEADER, e.getHeader());
		initialValues.put(SnapNowConstants.TABLE_ENTRY_PATH, e.getPath());
		initialValues.put(SnapNowConstants.TABLE_ENTRY_UPLOADED, 0);
		String tags="";
		for(String tag: e.getTagsAsArrayList())
		{
			tags=tags+", "+tag;
			Log.i(TAG, "tag="+tag);
		}
		initialValues.put(SnapNowConstants.TABLE_ENTRY_TAGS, tags);
		database.insert(SnapNowConstants.TABLE_ENTRY, null, initialValues);
		Log.i(TAG, "ok, this method did what it should... I guess.");
	}
	
	public static void put(TextEntry e) 
	{
		Log.i(TAG, "->put this into the database");
		ContentValues initialValues = new ContentValues();
		initialValues.put("entryid", e.getId());
		initialValues.put("type", "text");
		initialValues.put("thedate", e.getDateAsString());
		initialValues.put("typeText_text", e.getText());
		initialValues.put("header", e.getHeader());
		initialValues.put("typePhoto_path", "");
		initialValues.put("uploaded", "0");
		String tags="";
		for(String tag: e.getTagsAsArrayList())
		{
			tags=tags+", "+tag;
		}
		initialValues.put("tags", tags);
		//initialValues.put("submitted", 0);
		database.insert(SnapNowConstants.TABLE_ENTRY, null, initialValues);
	}
	
	public static PhotoEntry getPhotoEntryById(long entryidx)
	{
		Log.i(TAG, "get by id="+entryidx);
		Cursor c = database.query(SnapNowConstants.TABLE_ENTRY, new String[] {
				SnapNowConstants.TABLE_ENTRY_ENTRYID,
				SnapNowConstants.TABLE_ENTRY_DATE,
				SnapNowConstants.TABLE_ENTRY_HEADER,
				SnapNowConstants.TABLE_ENTRY_UPLOADED,
				SnapNowConstants.TABLE_ENTRY_TAGS,
				SnapNowConstants.TABLE_ENTRY_PATH,
				SnapNowConstants.TABLE_ENTRY_TYPE},
				SnapNowConstants.TABLE_ENTRY_ENTRYID +" = '"+entryidx+"'",
		        null,
		        null,
		        null,
		        null);
		Log.i(TAG, "cursorsize: "+c.getCount());
		PhotoEntry pe = null;
		while (c.moveToNext()) 
		{
			String type = c.getString(c.getColumnIndex(SnapNowConstants.TABLE_ENTRY_TYPE));
			if(type.equals(SnapNowConstants.ENTRYTYPE_PHOTO))
			{
				long entryid = c.getLong(c.getColumnIndex(SnapNowConstants.TABLE_ENTRY_ENTRYID));
				Log.i(TAG, ""+entryid);
				String date = c.getString(c.getColumnIndex(SnapNowConstants.TABLE_ENTRY_DATE));
				Log.i(TAG, ""+date);
				String header = c.getString(c.getColumnIndex(SnapNowConstants.TABLE_ENTRY_HEADER));
				Log.i(TAG, ""+header);
				boolean uploaded =false;
				int u = c.getInt(c.getColumnIndex(SnapNowConstants.TABLE_ENTRY_UPLOADED));
				if(u==1)
				{
					uploaded=true;
				}
				String tags = c.getString(c.getColumnIndex(SnapNowConstants.TABLE_ENTRY_TAGS));
				StringTokenizer tk = new StringTokenizer(tags, ",");
				ArrayList<String> tagslist = new ArrayList<String>();
				while(tk.hasMoreTokens())
				{
					String tag = tk.nextToken();
					tag=tag.replace(" ", "");
					tag=tag.trim();
					tagslist.add(tag);
					Log.i(TAG, ""+tag);
					
				}
				String path = c.getString(c.getColumnIndex(SnapNowConstants.TABLE_ENTRY_PATH));
				pe = new PhotoEntry(entryid, uploaded, date, header, tagslist, path);
			}
		}
		c.close();
		return pe;
	}
	
	public static void setEntryToUploaded(long entryidx)
	{
		Log.i(TAG, "set to uploaded");
		ContentValues args = new ContentValues();
		args.put(SnapNowConstants.TABLE_ENTRY_UPLOADED, 1);
		database.update(SnapNowConstants.TABLE_ENTRY, args, SnapNowConstants.TABLE_ENTRY_ENTRYID+" = "+entryidx, null);
		Log.i(TAG, "did it work? not sure?");
	}
	public static ArrayList<Entry> getNotUploadedEntrys()
	{
		Log.i(TAG, "get unoploaded...");
		Cursor c = database.query(SnapNowConstants.TABLE_ENTRY, new String[] {
				SnapNowConstants.TABLE_ENTRY_ENTRYID,
				SnapNowConstants.TABLE_ENTRY_DATE,
				SnapNowConstants.TABLE_ENTRY_HEADER,
				SnapNowConstants.TABLE_ENTRY_UPLOADED,
				SnapNowConstants.TABLE_ENTRY_TAGS,
				SnapNowConstants.TABLE_ENTRY_PATH,
				SnapNowConstants.TABLE_ENTRY_TYPE},
				SnapNowConstants.TABLE_ENTRY_UPLOADED +" = '0'",
		        null,
		        null,
		        null,
		        null);
		
		Log.i(TAG, "cursorsize: "+c.getCount());
		ArrayList<Entry> entrys= new ArrayList<Entry>();
		while (c.moveToNext()) 
		{
			String type = c.getString(c.getColumnIndex(SnapNowConstants.TABLE_ENTRY_TYPE));
			if(type.equals(SnapNowConstants.ENTRYTYPE_PHOTO))
			{
				long entryid = c.getLong(c.getColumnIndex(SnapNowConstants.TABLE_ENTRY_ENTRYID));
				Log.i(TAG, ""+entryid);
				String date = c.getString(c.getColumnIndex(SnapNowConstants.TABLE_ENTRY_DATE));
				Log.i(TAG, ""+date);
				String header = c.getString(c.getColumnIndex(SnapNowConstants.TABLE_ENTRY_HEADER));
				Log.i(TAG, ""+header);
				boolean uploaded =false;
				int u = c.getInt(c.getColumnIndex(SnapNowConstants.TABLE_ENTRY_UPLOADED));
				if(u==1)
				{
					uploaded=true;
				}
				String tags = c.getString(c.getColumnIndex(SnapNowConstants.TABLE_ENTRY_TAGS));
				StringTokenizer tk = new StringTokenizer(tags, ",");
				ArrayList<String> tagslist = new ArrayList<String>();
				while(tk.hasMoreTokens())
				{
					String tag = tk.nextToken();
					tag=tag.replace(" ", "");
					tag=tag.trim();
					tagslist.add(tag);
					Log.i(TAG, ""+tag);
					
				}
				String path = c.getString(c.getColumnIndex(SnapNowConstants.TABLE_ENTRY_PATH));
				PhotoEntry pe = new PhotoEntry(entryid, uploaded, date, header, tagslist, path);
				entrys.add(pe);
			}
		}
		c.close();
		return entrys;
	}
	
}
