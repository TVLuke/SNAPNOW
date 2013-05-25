package de.lukeslog.snapnow.main;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.lukeslog.snapnow.R;
import de.lukeslog.snapnow.actors.NotificationActor;
import de.lukeslog.snapnow.constants.SnapNowConstants;
import de.lukeslog.snapnow.database.EntryDatabase;
import de.lukeslog.snapnow.posting.Entry;
import de.lukeslog.snapnow.posting.TextEntry;
import de.lukeslog.snapnow.posting.UploadService;
import de.lukeslog.snapnow.stats.Statistics;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

/**
 * Helpfull litle websites
 * http://blog.root-of-all-evil.com/2010/03/math-random-zufallszahlen-in-java/
 * 
 * @author lukas
 *
 */
public class SnapNowBackgroundService  extends IntentService
{

	private final String TAG = SnapNowConstants.TAG;
	private Context context;
	Intent intent;
	private static int countdown=-1;
	SharedPreferences prefs;
	private static ArrayList<Entry> entrys= new ArrayList<Entry>();
	private static int month=-1;
	//private static long lastTimeRandomNumber=0;

	public SnapNowBackgroundService() 
	{
		super("SnapNowBackgroundService");
	}

	@Override
	public IBinder onBind(Intent arg0) 
	{
		return null;
	}
	
	@Override
	public void onCreate() 
	{
		super.onCreate();
	 	//Log.i(TAG, "SNAPNOW Service Läuft");
	    context=this;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		super.onStartCommand(intent, flags, startId);
		//Log.i(TAG, "Received start id " + startId + ": " + intent);
		return Service.START_NOT_STICKY;
	}
	
    @Override
    public void onDestroy() 
    {
        super.onDestroy();
    	//AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
    	//if(intent!=null)
    	//{
	    //	PendingIntent p = PendingIntent.getService(this, SnapNowConstants.SNAPALARM, intent, PendingIntent.FLAG_NO_CREATE);
	    //	am.cancel(p);
    	//}
    }
	    
	@Override
	protected void onHandleIntent(Intent arg0) 
	{
		Log.i(TAG, "handle");
		//...
		Intent notif = new Intent(this,NotificationService.class);
		startService(notif);
		//try to create the folder, nothing will happen if this folder exists...
    	File dir = new File(Environment.getExternalStorageDirectory() + "/SnapNowImages");
   	 
    	if(!dir.exists())
    	{
    		dir.mkdir();
    	}
    	//EntryDatabase.createDataBase(this); //if its already there it will not be created...
    	
    	//TODO: check if there are any not uploaded things in there
    	//TODO: Upload them
		//check if we are in a new month, if so, geerate a summative post
		Date d = new Date();
		if(month==-1)
		{
			//month not set yet;
			//lastTimeRandomNumber=d.getTime();
			month= d.getMonth();
		}
		else
		{
			if(d.getMonth()!=month)
			{
				//a new month has started.
				generateSumationEntry(month);
				month=d.getMonth();
			}
		}
		//...
		prefs = getSharedPreferences(SnapNowConstants.PREFS, 0);
		if(entrys.size()>0)
		{
			Log.d(TAG, "uploadEntrys");
			Thread tt = new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					uploadentrys();
				}
				
			});
			tt.start(); 	
		}
		if(countdown==0)//the countdown is over. you did not act in time
		{
			//create a fail antry
			countdown=-1;
			Statistics.fail(this);
			//TextEntry entry = new TextEntry("", "", this);
			//entry.setHeader("I missed a moment.");
			//entry.setText(entry.dateAsAString());
			//SnapNowBackgroundService.addEntry(entry);
			//Log.d(TAG, "created fail Entry");
		}
		if(countdown==-1) //
		{
			SnapNowBackgroundService.cleanEntrys();
			NotificationManager mNotMan = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	    	mNotMan.cancel(48);
			//Log.d(TAG, "Countdown=0");
	    	//once a minute we generate a new random number
    		//lastTimeRandomNumber=d.getTime();
		    	
	    	boolean blackoutbool = prefs.getBoolean("blackout", false);
	    	Log.d(TAG, "blackout"+blackoutbool);
	    	boolean instantblackoutbool = prefs.getBoolean("instantblackout", false);
	    	Log.d(TAG, "instantblackout"+instantblackoutbool);
	    	boolean blackoutnow = isBlackout();
			if((!blackoutbool || !blackoutnow) && !instantblackoutbool)
			{
	    	
				int number = myRandom(SnapNowConstants.RANDOMNUMBER);
				Statistics.randomNumber(this, number);
				Log.d(TAG, "randomnumber="+number);
			
			
				if(number==5)
				{
					Statistics.alert(this);
					Log.d(TAG, "hit random");
					int momentnumber = prefs.getInt("momentnumber", 0);
					Editor edit = prefs.edit();
					momentnumber++;
					edit.putInt("momentnumber", momentnumber);
					edit.commit();
					//The chances of this happening are small. probabaly abaout once eacht day when one is awake... or two times. I don't care.
					countdown = 5; //how long in minutes

					Intent notiact = new Intent(this,NotificationActor.class);
					startService(notiact);
				}
			}
    	}
		else
		{
			countdown=countdown-1;
			Log.d(TAG, countdown+" seconds left");
		}
		scheduleNext();
	}
	
	private boolean isBlackout()
	{
		Log.d(TAG, "is Blackout");
        int blackoutstarthour = prefs.getInt("blackoutstarthour", 0);
        int blackoutstartminute = prefs.getInt("blackoutstartminute", 0);
        
        int blackoutendhour = prefs.getInt("blackoutendhour", 0);
        int blackoutendminute = prefs.getInt("blackoutendminute", 0);
        
        boolean instantblackoutbool = prefs.getBoolean("instantblackout", false);
        if(instantblackoutbool)
        {
        	return true;
        }
        else
        {
        	Date d = new Date();
        	int hour = d.getHours();
        	int minute = d.getMinutes();
        	if(blackoutstarthour<blackoutendhour) //in this case the blackout starts at some point and ends before midnight
        	{
        		Log.d(TAG, "c1");
        		if(hour>blackoutstarthour && hour <blackoutendhour)
        		{
        			Log.d(TAG, "c1a");
        			return true;
        		}
        		else if(hour==blackoutstarthour && minute >blackoutstartminute)
        		{
        			Log.d(TAG, "c1b");
        			return true;
        		}
        		else if(hour==blackoutendhour && minute < blackoutendminute)
        		{
        			Log.d(TAG, "c1c");
        			return true;
        		}
        		else
        		{
        			Log.d(TAG, "not c1");
        			return false;
        		}
        	}
        	if(blackoutstarthour==blackoutendhour && blackoutstartminute > blackoutendminute) //in this case the blackout is ovber 23 hours
        	{
        		Log.d(TAG, "c2");
        		if(blackoutendminute<minute && minute<blackoutstartminute)
        		{
        			return false;
        		}
        		else
        		{
        			return true;
        		}
        	}
        	if(blackoutstarthour<blackoutendhour) //in this case the blackout goes over midnight
        	{
        		Log.d(TAG, "c3");
        		if(blackoutstarthour==hour && blackoutstartminute<minute)
        		{
        			Log.d(TAG, "c3a");
        			return true;
        		}
        		else if(blackoutstarthour < hour)
        		{
        			Log.d(TAG, "c3b");
        			return true;
        		}
        		else if(blackoutendhour>hour)
        		{
        			return true;
        		}
        		else if(blackoutendhour==hour && blackoutendminute>minute)
        		{
        			return true;
        		}
        		else
        		{
        			return true;
        		}
        	}
        }
		return false;
	}
	private int myRandom(double high) 
	{
		int x = (int) (Math.random() * high);
		return x;
	}
	
	private void scheduleNext()
	{
		//Log.d(TAG, "sNext");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 1);
        intent = new Intent(this, SnapNowBackgroundService.class);
        //Log.d(TAG, "snext2");
        PendingIntent pendingIntent = PendingIntent.getService(this, SnapNowConstants.SNAPALARM, intent, 0);
        //Log.d(TAG, "snext3");
        AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        //Log.d(TAG, "snext4");
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
        //Log.d(TAG, "snext5");
	}

	public static int getSeconds() 
	{
		return countdown;
	}

	public static void resetCountdown() 
	{
		countdown=-1;
	}
	
	public static void addEntry(Entry e)
	{
		Log.d("SNAPNOW", "addEntry"+SnapNowBackgroundService.getEntrys().size());
		entrys.add(e);
		Log.d("SNAPNOW", "addEntry"+SnapNowBackgroundService.getEntrys().size());
		//EntryDatabase.put(e);
	}
	
	public static ArrayList<Entry> getEntrys()
	{
		return entrys;
	}
	
	public static void clearEntrys()
	{
		entrys.clear();
	}
	
	public static void cleanEntrys()
	{
		if(entrys.size()>0)
		{
			for (int i=entrys.size()-1; i>=0; i--)
			{
				if(entrys.get(i).isUploaded())
				{
					entrys.remove(i);
				}
			}
		}
	}
	
	public static void uploaded(Entry entryX)
	{
		long entryID = entryX.getId();
		for (Entry entry : entrys)
		{
			if(entry.getId()==entryID)
			{
				entry.setUploaded(true);
			}
		}
	}
	
	public static Entry getEntryById(long entryId)
	{
		for (Entry entry : entrys)
		{
			if(entry.getId()==entryId)
			{
				return  entry;
			}
		}
		return null;
	}
	
	public void uploadentrys()
	{
		Log.d("SNAPNOW", "->Entry"+SnapNowBackgroundService.getEntrys().size());
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		boolean wifi = mWifi.isConnected();
		boolean onlywifi = prefs.getBoolean("onlywifi", false);
		if(onlywifi && wifi || !onlywifi)
		{
			Log.d(TAG, "startUploadService");
			Intent backgr = new Intent(this,UploadService.class);
			startService(backgr);
		}
	}
	
	private void generateSumationEntry(int i)
	{
		String thismonth = getResources().getString(R.string.thismonth);
		String statsforthemonthof = getResources().getString(R.string.statsforthemonthof);
		String stats="";
		stats=stats+""+statsforthemonthof+" ";
		Date d = new Date();
		if(i==0)
		{
			stats = stats+getResources().getString(R.string.january)+"\n";
		}
		if(i==1)
		{
			stats = stats+getResources().getString(R.string.february)+"\n";				
		}
		if(i==2)
		{
			stats = stats+getResources().getString(R.string.march)+"\n";
		}
		if(i==3)
		{
			stats = stats+getResources().getString(R.string.april)+"\n";				
		}
		if(i==4)
		{
			stats = stats+getResources().getString(R.string.may)+"\n";
		}
		if(i==5)
		{
			stats = stats+getResources().getString(R.string.june)+"\n";
		}
		if(i==6)
		{
			stats = stats+getResources().getString(R.string.july)+"\n";
		}
		if(i==7)
		{
			stats = stats+getResources().getString(R.string.august)+"\n";				
		}
		if(i==8)
		{
			stats = stats+getResources().getString(R.string.september)+"\n";				
		}
		if(i==9)
		{
			stats = stats+getResources().getString(R.string.october)+"\n";
		}
		if(i==10)
		{
			stats = stats+getResources().getString(R.string.november)+"\n";
		}
		if(i==11)
		{
			stats = stats+getResources().getString(R.string.december)+"\n";
		}
		long firststart = Statistics.getTimeOfFirstActivation(this);
		Date f = new Date(firststart);
	    long now = d.getTime();
	    long dif = now-firststart;
	    double dif2=dif/1000;
	    int difvalue=(int)dif2;
	    String [] difarray = Statistics.generateRightTimeUnit(difvalue, this);		
		stats = stats+"\n";
		stats = stats+""+getResources().getString(R.string.currentlyrunningversion)+" "+SnapNowConstants.VERSION+"\n";
		stats = stats+""+getResources().getString(R.string.istartedthison)+" "+(f.getYear()+1900)+"-"+(f.getMonth()+1)+"-"+f.getDate()+", "+difarray[0]+" "+difarray[1]+" "+getResources().getString(R.string.ago)+".\n \n";
		stats = stats+""+getResources().getString(R.string.overallruntime)+": "+Statistics.runtimeInSeconds(this)+" "+getResources().getString(R.string.seconds)+". \n";
		stats = stats+""+getResources().getString(R.string.overallcought)+": "+(Statistics.sumofallerts(this)-Statistics.sumoffails(this))+".\n \n";
		
	    long [] rtis = Statistics.runtimeInSecondsMonth(this, d.getTime()-100000);
	    double r1 = rtis[0];
	    int r1i = (int)r1;
	    String [] difarray2 = Statistics.generateRightTimeUnit(r1i, this);
	    double r2 = rtis[1];
	    double upmonth = r1/(r2/100); 
	    double upmonth2 = upmonth*100;
	    int upmonth3 = (int) upmonth2;
	    upmonth2 = upmonth3;
	    upmonth2 = upmonth2/100;
	    
		stats = stats+"<b>"+thismonth+"</b>\n";
		stats = stats+""+getResources().getString(R.string.runtime)+": "+difarray2[0]+" "+difarray2[1]+" ("+upmonth2+"%)\n";
		stats=stats+String.format(getResources().getString(R.string.coughtofmoments), ""+(Statistics.sumofallertsMonth(this,  d.getTime())-Statistics.sumoffailsMonth(this, d.getTime())), ""+Statistics.sumofallertsMonth(this, d.getTime()))+"\n";
		
		TextEntry entry = new TextEntry(getResources().getString(R.string.monthendstats), stats, this);
		SnapNowBackgroundService.addEntry(entry);
	}

}
