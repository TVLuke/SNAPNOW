package de.lukeslog.snapnow.constants;

public class SnapNowConstants 
{

	public static final String TAG = "SNAPNOW";
	public static final String PREFS = "SnapNowPrefs";
	public static final int SNAPALARM = 5394; 
	
	public static int RANDOMNUMBER=1440;
	//public static int RANDOMNUMBER=3;
	
	public static final String ENTRYTYPE_PHOTO ="photo";
	public static final String ENTRYTYPE_TEXT = "text";
	
	public static String VERSION="0.92";
	
	public static String DATABASE_NAME = "SnapNowDatabase";
	public static final int DATABASE_VERSION=8;
	
	public static final String TABLE_ENTRY="ssquedb"; 
	
	public static final String TABLE_ENTRY_ID = "_id";
	public static final String TABLE_ENTRY_ENTRYID = "entryid";
	public static final String TABLE_ENTRY_TYPE = "type";
	public static final String TABLE_ENTRY_TAGS = "tags";
	public static final String TABLE_ENTRY_DATE = "thedate";
	public static final String TABLE_ENTRY_TEXT = "typeText_text";
	public static final String TABLE_ENTRY_HEADER = "header";
	public static final String TABLE_ENTRY_PATH = "typePhoto_path";
	public static final String TABLE_ENTRY_UPLOADED = "uploaded";
	
	public static final String TABLE_ENTRY_CREATE =
			"CREATE TABLE IF NOT EXISTS " + TABLE_ENTRY + 
			" ("+TABLE_ENTRY_ID +" integer primary key autoincrement, " +
			""+TABLE_ENTRY_ENTRYID+" integer, " +
			""+TABLE_ENTRY_TYPE+" text, " +
			""+TABLE_ENTRY_TAGS+" text, " +
			""+TABLE_ENTRY_DATE+" text, " +
			""+TABLE_ENTRY_TEXT+" text, " +
			""+TABLE_ENTRY_HEADER+" text, " +
			""+TABLE_ENTRY_PATH+" text, " +
			""+TABLE_ENTRY_UPLOADED+" integer"
			+ ");";
	
}
