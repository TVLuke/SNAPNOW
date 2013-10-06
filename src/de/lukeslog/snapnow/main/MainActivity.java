package de.lukeslog.snapnow.main;

import java.util.Date;

import de.lukeslog.snapnow.R;
import de.lukeslog.snapnow.constants.SnapNowConstants;
import de.lukeslog.snapnow.stats.Statistics;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity
{
	private final String TAG = SnapNowConstants.TAG;
	SharedPreferences prefs;
	Context ctx;
	boolean dontshutdown=false;
	
	private UIUpdater updater;
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actualmain);
        Log.i(TAG, "main activity");
        ctx=this;
        prefs = getSharedPreferences(SnapNowConstants.PREFS, 0);

        //Set the strings I need
        String seconds = getResources().getString(R.string.seconds);
        String missed = getResources().getString(R.string.missed);
        
        TextView runtime = (TextView) findViewById(R.id.runtime);
        runtime.setText(""+Statistics.runtimeInSeconds(this)+" "+seconds+"");
        TextView alerts = (TextView) findViewById(R.id.alerts);
        alerts.setText(""+Statistics.sumofallerts(this)+" ("+Statistics.sumoffails(this)+" "+missed+")");
        TextView tsl = (TextView) findViewById(R.id.tsl);
        tsl.setText(Statistics.timesincelastalert(this)+" "+seconds+"");
        ComponentName a = getCallingActivity();
        if(a!=null)
        {
        	Log.d(TAG, "called from"+a.toString());
        }
        else
        {
        	Log.d(TAG, "called from null");
        }
        
        getAccountInfo();
        
        boolean onlywifi = prefs.getBoolean("onlywifi", false);
        
        boolean tumblerBool = prefs.getBoolean("tumblr", false);
        String tumblrmailadress= prefs.getString("tumblrmail", "");
        
        String gmailaccString= prefs.getString("gmailacc", "");
        String gmailpswString= prefs.getString("gmailpsw", "");
        String emailadresses = prefs.getString("emailadresses", "");
        
        boolean twitterBool = prefs.getBoolean("twitter", false);
        
        boolean sendViaEmail = prefs.getBoolean("sendViaEmail", false);
        
		Intent backgr = new Intent(this,SnapNowBackgroundService.class);
		startService(backgr);
		
		EditText gmailacc = (EditText) findViewById(R.id.gmailacc);
		gmailacc.setText(gmailaccString);
		gmailacc.addTextChangedListener(new TextWatcher()
		{
	        public void afterTextChanged(Editable s) 
	        {
	        	Editor edit = prefs.edit();
	        	edit.putString("gmailacc", s.toString());
	        	edit.commit();
	        	checkiftumblrisOK();
	        }
	        public void beforeTextChanged(CharSequence s, int start, int count, int after)
	        {
	        	
	        }
	        public void onTextChanged(CharSequence s, int start, int before, int count)
	        {
	        	
	        }
	    }); 
		
		EditText gmailpsw = (EditText) findViewById(R.id.gmailpsw);
		gmailpsw.setText(gmailpswString);
		gmailpsw.addTextChangedListener(new TextWatcher()
		{
	        public void afterTextChanged(Editable s) 
	        {
	        	Editor edit = prefs.edit();
	        	edit.putString("gmailpsw", s.toString());
	        	edit.commit();
	        	checkiftumblrisOK();
	        }
	        public void beforeTextChanged(CharSequence s, int start, int count, int after)
	        {
	        	
	        }
	        public void onTextChanged(CharSequence s, int start, int before, int count)
	        {
	        	
	        }
	    }); 
		
		final CheckBox tumblrB = (CheckBox) findViewById(R.id.tumblrBox);
		tumblrB.setChecked(tumblerBool);
		tumblrB.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
		    {
		       tumblrB.setChecked(isChecked);
		       Editor edit = prefs.edit();
		       edit.putBoolean("tumblr", isChecked);
		       edit.commit();
		       checkiftumblrisOK();
		    }
		});
		
		EditText tumblrmail = (EditText) findViewById(R.id.tumblrmail);
		tumblrmail.setText(tumblrmailadress);
		tumblrmail.addTextChangedListener(new TextWatcher()
		{
	        public void afterTextChanged(Editable s) 
	        {
	        	Editor edit = prefs.edit();
	        	edit.putString("tumblrmail", s.toString());
	        	edit.commit();
	        }
	        public void beforeTextChanged(CharSequence s, int start, int count, int after)
	        {
	        	
	        }
	        public void onTextChanged(CharSequence s, int start, int before, int count)
	        {
	        	
	        }
	    }); 
		
		EditText adresses = (EditText) findViewById(R.id.emailadresses);
		adresses.setText(emailadresses);
		adresses.addTextChangedListener(new TextWatcher()
		{
	        public void afterTextChanged(Editable s) 
	        {
	        	Editor edit = prefs.edit();
	        	edit.putString("emailadresses", s.toString());
	        	edit.commit();
	        }
	        public void beforeTextChanged(CharSequence s, int start, int count, int after)
	        {
	        	
	        }
	        public void onTextChanged(CharSequence s, int start, int before, int count)
	        {
	        	
	        }
	    }); 
		
		final TextView learnmore = (TextView) findViewById(R.id.learnmore);
		learnmore.setOnClickListener(new View.OnClickListener() 
		{
		    public void onClick(View v) 
		    {
		    	dontshutdown=true;
		        Intent intexpl = new Intent(ctx, TumblrExplain.class);
		        startActivity(intexpl);
		    }
		});
		
		final CheckBox twitterB = (CheckBox) findViewById(R.id.twitterChackBox);
		twitterB.setChecked(twitterBool);
		twitterB.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
		    {
		    	twitterB.setChecked(isChecked);
		       Editor edit = prefs.edit();
		       edit.putBoolean("twitter", isChecked);
		       edit.commit();
		       checkiftumblrisOK();
		    }
		});
		
		final CheckBox onlywificheckbox = (CheckBox) findViewById(R.id.onlywificheckbox);
		onlywificheckbox.setChecked(onlywifi);
		onlywificheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
		    {
		    	onlywificheckbox.setChecked(isChecked);
		       Editor edit = prefs.edit();
		       edit.putBoolean("onlywifi", isChecked);
		       edit.commit();
		    }
		});
		updater= new UIUpdater();
		updater.run();
		
		final CheckBox sendviaEmailCheckbox = (CheckBox) findViewById(R.id.sendviaEmailCheckbox);
		sendviaEmailCheckbox.setChecked(sendViaEmail);
		sendviaEmailCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
		    {
		    	sendviaEmailCheckbox.setChecked(isChecked);
		       Editor edit = prefs.edit();
		       edit.putBoolean("sendViaEmail", isChecked);
		       edit.commit();
		    }
		});
    }
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
	/**
	 * This method is called whenever the user chooses an options menu item.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		Log.d(TAG, "menu?");
		switch (item.getItemId()) {
		case R.id.blackout:
			dontshutdown=true;
			Intent black = new Intent(this, BlackoutDefinition.class);
			startActivity(black);
			return true;
		case R.id.reupload:
			dontshutdown=true;
			Intent reupload = new Intent(this, ReUploadActivity.class);
			startActivity(reupload);
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected void onPause() 
	{
		updater.onPause();
		if(!dontshutdown)
		{
			MainActivity.this.finish();
		}
		super.onPause();
	}
	
	@Override
	protected void onResume() 
	{
		dontshutdown=false;
	    updater.onResume();
		super.onResume();
	}
	
	private void getAccountInfo()
	{
		AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
		Account[] list = manager.getAccounts();
		for(Account account: list)
		{
		    if(account.type.equalsIgnoreCase("com.google"))
		    {
		    	Log.d(TAG, account.name);
		        String gmail = account.name;
	        	Editor edit = prefs.edit();
	        	edit.putString("gmailacc", gmail);
	        	edit.commit();
		        break;
		    }
		}
	}
	
	private void checkiftumblrisOK()
	{
		final CheckBox tumblrB = (CheckBox) findViewById(R.id.tumblrBox);
	       TextView tumblrwarn = (TextView) findViewById(R.id.tumblrwarn);
		boolean isChecked = tumblrB.isChecked();
		 if(isChecked)
	       {
	    	   String gm1= prefs.getString("gmailacc", "");
	           String gm2= prefs.getString("gmailpsw", "");
	    	   if(gm1.equals("") || gm2.equals(""))
	    	   {

	    		   tumblrwarn.setText("Upload To Tumblr is only possible when google data is set");
	    	   }
	    	   else
	    	   {
	    		   tumblrwarn.setText(" ");
	    	   }
	       }
	       else
	       {
	    	   tumblrwarn.setText(" ");
	       }

	}
	
	
	@Override
	protected void onDestroy()
	{
		Log.d(TAG, "m onDestroy");
		updater.onPause();
		super.onDestroy();
	}
	

	private class  UIUpdater implements Runnable
	{
    	private Handler handler = new Handler();
    	public static final int delay= 1000;
		
        String seconds = getResources().getString(R.string.seconds);
        String missed = getResources().getString(R.string.missed);
        String active = getResources().getString(R.string.active);
        String ago = getResources().getString(R.string.ago);
        
        
    	@Override
		public void run() 
    	{
    	    long firststart = Statistics.getTimeOfFirstActivation(ctx);
    	 	long rt = Statistics.runtimeInSeconds(ctx);
    	 	
    	 	TextView version = (TextView) findViewById(R.id.versionnumber);
    	 	TextView firstactive = (TextView) findViewById(R.id.firstactive);
    	    TextView runtime = (TextView) findViewById(R.id.runtime);
    	    TextView tsl = (TextView) findViewById(R.id.tsl);
    	    TextView alerts = (TextView) findViewById(R.id.alerts);
    	    TextView monthruntime = (TextView) findViewById(R.id.monthruntime);
    	    TextView monthalerts = (TextView) findViewById(R.id.monthalerts);

    	    //second
    		Date f = new Date(firststart);
    		Date d = new Date();
    	    long now = d.getTime();
    	    long dif = now-firststart;
    	    double dif2=dif/1000;
    	    int difvalue=(int)dif2;
    	    String [] difarray = Statistics.generateRightTimeUnit(difvalue, ctx);
 
    	    String[] tslaString = Statistics.generateRightTimeUnit(Statistics.timesincelastalert(ctx), ctx);

    	    double rt2 = (double) rt;
    	    double nx = rt2/(dif2/100);
    	    int nx2 = (int) (nx*100);
    	    double nx2b = (double) nx2;
    	    double nx3 = nx2b/100;
    	    String [] difarray2 = Statistics.generateRightTimeUnit(Statistics.runtimeInSeconds(ctx), ctx);

    	    long [] rtis = Statistics.runtimeInSecondsMonth(ctx, d.getTime(), false);
    	    double r1 = rtis[0];
    	    double r2 = rtis[1];
    	    double upmonth = r1/(r2/100); 
    	    double upmonth2 = upmonth*100;
    	    int upmonth3 = (int) upmonth2;
    	    upmonth2 = upmonth3;
    	    upmonth2 = upmonth2/100;
    	    
    	    //set Textviews
    	    version.setText(SnapNowConstants.VERSION);
       		firstactive.setText((f.getYear()+1900)+"-"+(f.getMonth()+1)+"-"+f.getDate()+" ( "+difarray[0]+" "+difarray[1]+" "+ago+")");
    	    runtime.setText(""+difarray2[0]+" "+difarray2[1]+" ("+nx3+" % "+active +")");
    	    alerts.setText(""+Statistics.sumofallerts(ctx)+" ("+Statistics.sumoffails(ctx)+" "+missed+")");
    	    tsl.setText( tslaString[0]+" "+tslaString[1]);
    	    monthruntime.setText(upmonth2+"%");
    	    monthalerts.setText(""+Statistics.sumofallertsMonth(ctx,  d.getTime())+" ("+Statistics.sumoffailsMonth(ctx, d.getTime())+" "+missed+")");
    	    

	        handler.removeCallbacks(this); // remove the old callback
	        handler.postDelayed(this, delay); // register a new one
		}
    	
    	public void onResume()
    	{
            handler.removeCallbacks(this); // remove the old callback
            handler.postDelayed(this, delay); // register a new one
    	}
    	
        public void onPause()
        {
            handler.removeCallbacks(this); // stop the map from updating
        }
        
	}
}
