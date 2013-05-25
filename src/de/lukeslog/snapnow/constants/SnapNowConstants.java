package de.lukeslog.snapnow.constants;

public class SnapNowConstants 
{

	public static final String TAG = "SNAPNOW";
	public static final String PREFS = "SnapNowPrefs";
	public static final int SNAPALARM = 5394; 
	
	//public static int RANDOMNUMBER=1440;
	public static int RANDOMNUMBER=10;
	
	public static String VERSION="0.0.2";
	
	public static String DATABASE_NAME = "SnapNowDatabase";
	public static final int DATABASE_VERSION=2;
	
	public static final String TABLE_ENTRY="ssquedb"; 
	public static final String TABLE_ENTRY_CREATE =
			"CREATE TABLE IF NOT EXISTS " + TABLE_ENTRY + 
			" (_id integer primary key autoincrement, " +
			"type text, " +
			"tags text, " +
			"thedate text, " +
			"typeText_text text, " +
			"typeText_header text, " +
			"typePhoto_path text, " +
			"uploaded integer"
			+ ";";
	
}
