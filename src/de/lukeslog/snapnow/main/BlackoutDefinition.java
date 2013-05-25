package de.lukeslog.snapnow.main;

import de.lukeslog.snapnow.R;
import de.lukeslog.snapnow.constants.SnapNowConstants;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TimePicker;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

public class BlackoutDefinition extends Activity{

	private final String TAG = SnapNowConstants.TAG;
	SharedPreferences prefs;

	  @Override
	    public void onCreate(Bundle savedInstanceState) 
	    {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.blackout);
	        prefs = getSharedPreferences(SnapNowConstants.PREFS, 0);
	        
	        boolean blackoutbool = prefs.getBoolean("blackout", false);
	        
	        int blackoutstarthour = prefs.getInt("blackoutstarthour", 0);
	        int blackoutstartminute = prefs.getInt("blackoutstartminute", 0);
	        
	        int blackoutendhour = prefs.getInt("blackoutendhour", 0);
	        int blackoutendminute = prefs.getInt("blackoutendminute", 0);
	        
	        boolean instantblackoutbool = prefs.getBoolean("instantblackout", false);
	        
	        final CheckBox blackoutCheckBox = (CheckBox) findViewById(R.id.blackout);
	        blackoutCheckBox.setChecked(blackoutbool);
	        blackoutCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener()
			{
			    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			    {
			    	blackoutCheckBox.setChecked(isChecked);
			       Editor edit = prefs.edit();
			       edit.putBoolean("blackout", isChecked);
			       edit.commit();
			    }
			});
			
	        TimePicker startTime = (TimePicker) findViewById(R.id.timePicker1);
	        startTime.setIs24HourView(true);
	        startTime.setCurrentHour(blackoutstarthour);
	        startTime.setCurrentMinute(blackoutstartminute);
	        startTime.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {

	            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
	                Log.d(TAG, hourOfDay+" "+minute);
	                Editor edit = prefs.edit();
		        	edit.putInt("blackoutstarthour", hourOfDay);
		        	edit.putInt("blackoutstartminute", minute);
		        	edit.commit();
	            }
	        });
	        
	        
	        TimePicker endTime = (TimePicker) findViewById(R.id.timePicker2);
	        endTime.setIs24HourView(true);
	        endTime.setCurrentHour(blackoutendhour);
	        endTime.setCurrentMinute(blackoutendminute);
	        endTime.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {

	            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
	                Editor edit = prefs.edit();
		        	edit.putInt("blackoutendhour", hourOfDay);
		        	edit.putInt("blackoutendminute", minute);
		        	edit.commit();
	            }
	        });
	        
	        ToggleButton instantblackout = (ToggleButton) findViewById(R.id.toggleButton1);
	        instantblackout.setChecked(instantblackoutbool); 

	    }
	
	public void instantBlackout(View v)
	{
		Log.d(TAG, "togglebutton pressed");
		ToggleButton instantblackout = (ToggleButton) v;
        Editor edit = prefs.edit();
		edit.putBoolean("instantblackout", instantblackout.isChecked());
    	edit.commit();
	}	
}
