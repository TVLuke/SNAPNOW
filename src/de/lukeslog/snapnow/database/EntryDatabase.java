package de.lukeslog.snapnow.database;

import de.lukeslog.snapnow.constants.SnapNowConstants;
import de.lukeslog.snapnow.posting.*;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class EntryDatabase 
{

	private static SQLiteDatabase database;
	private static String TAG = SnapNowConstants.TAG;
	
	public static void createDataBase(Context context) 
	{
		Log.i(TAG, "create called (noiseleveldb)");
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
		initialValues.put("type", "photo");
		initialValues.put("date", e.dateAsAString());
		initialValues.put("typeText_text", "");
		initialValues.put("typeText_header", "");
		initialValues.put("typePhoto_path", e.getPath());
		String tags="";
		for(String tag: e.getTagsAsArrayList())
		{
			tags=tags+", "+tag;
		}
		initialValues.put("tags", tags);
		//initialValues.put("submitted", 0);
		database.insert(SnapNowConstants.TABLE_ENTRY, null, initialValues);
	}
	
	public static void put(TextEntry e) 
	{
		Log.i(TAG, "->put this into the database");
		ContentValues initialValues = new ContentValues();
		initialValues.put("type", "text");
		initialValues.put("date", e.dateAsAString());
		initialValues.put("typeText_text", e.getText());
		initialValues.put("typeText_header", e.getHeader());
		initialValues.put("typePhoto_path", "");
		String tags="";
		for(String tag: e.getTagsAsArrayList())
		{
			tags=tags+", "+tag;
		}
		initialValues.put("tags", tags);
		//initialValues.put("submitted", 0);
		database.insert(SnapNowConstants.TABLE_ENTRY, null, initialValues);
	}
	
	
}
