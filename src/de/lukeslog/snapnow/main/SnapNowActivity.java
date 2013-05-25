package de.lukeslog.snapnow.main;

/**
 * 
 * helpfull Links: http://www.vogella.com/blog/2011/09/13/android-how-to-get-an-image-via-an-intent/
 * http://stackoverflow.com/questions/5991319/capture-image-from-camera-and-display-in-activity
 * 
 *@author Lukas Ruge
 */
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.lukeslog.snapnow.R;
import de.lukeslog.snapnow.actors.NotificationActor;
import de.lukeslog.snapnow.constants.SnapNowConstants;
import de.lukeslog.snapnow.posting.PhotoEntry;
import de.lukeslog.snapnow.posting.PostToTumblr;
import de.lukeslog.snapnow.posting.PostToTumblrViaMail;
import de.lukeslog.snapnow.posting.PostToTwitter;
import de.lukeslog.snapnow.posting.TumblrApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SnapNowActivity extends Activity implements SurfaceHolder.Callback
{
    /** Called when the activity is first created. */
	private static final int CAMERA_REQUEST = 1888;
	private Bitmap bitmap;
	private final String TAG = SnapNowConstants.TAG;
	SharedPreferences prefs;
	private int volume=0;
	Camera camera;
	Activity ctx;
	private SurfaceHolder sHolder;  
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ctx=this;
        Log.i(TAG, "gogogo");
        prefs = getSharedPreferences(SnapNowConstants.PREFS, 0);
        NotificationManager mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        mNM.cancel(48);
        Intent notiact = new Intent(this,NotificationActor.class);
        stopService(notiact);
        AudioManager mgr = (AudioManager) getSystemService(AUDIO_SERVICE);
        volume = mgr.getStreamVolume(AudioManager.STREAM_SYSTEM);
        mgr.setStreamVolume(AudioManager.STREAM_SYSTEM, 0, 0);
    	if(SnapNowBackgroundService.getSeconds()>0)
    	{
    		Log.i(TAG, "req");
    		SurfaceView surfaceView = (SurfaceView)findViewById(R.id.surfaceView1);
    		Log.i(TAG, "req2");
    		//Get a surface  
            sHolder = surfaceView.getHolder();  
            Log.i(TAG, "req3");
            //add the callback interface methods defined below as the Surface View callbacks  
            sHolder.addCallback(this);  
            Log.i(TAG, "req4");
            //tells Android that this surface will have its data constantly replaced  
            sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);  
            Log.i(TAG, "req5");
    		//amera.takePicture(shutterCallback, rawCallback, jpegCallback);
            //Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
            //cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
            //startActivityForResult(cameraIntent, CAMERA_REQUEST); 
    	}
    	else
    	{
    		Log.i(TAG, "req500");
    		SnapNowActivity.this.finish();
    	}
    }
    
    @Override  
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3)  
    {  
    	Log.i(TAG, "req6b");
         camera.startPreview();  
         Log.i(TAG, "req7b");
         camera.takePicture(null, null, jpegCallback);  
         Log.i(TAG, "req8b");
    }  
  
    @Override  
    public void surfaceCreated(SurfaceHolder holder)  
    {  
        // The Surface has been created, acquire the camera and tell it where  
        // to draw the preview.
    	Log.i(TAG, "req6a");
        camera = Camera.open();  
        Log.i(TAG, "req7a");
        try 
        {  
        	Log.i(TAG, "req8a");
           camera.setPreviewDisplay(holder);  
           Log.i(TAG, "req9a");
  
        } 
        catch (IOException exception) 
        {  
        	Log.i(TAG, "req10a");
            camera.release();  
            camera = null;  
            Log.i(TAG, "req11a");
        }  
    }  
    

	PictureCallback jpegCallback = new PictureCallback() 
	{

		@Override
		public void onPictureTaken(byte[] data, Camera camera)
		{
			Log.i(TAG, "req13");
	    	Log.i(TAG, "req14");
	    	AudioManager mgr = (AudioManager) getSystemService(AUDIO_SERVICE);
	        mgr.setStreamVolume(AudioManager.STREAM_SYSTEM, volume, 0);
			// TODO Auto-generated method stub
			Log.d(TAG, "onPictureTaken - jpg");
			SnapNowBackgroundService.resetCountdown();
			try 
			{
				File xy = new File(Environment.getExternalStorageDirectory() + "/SnapNowImages/newpic.jpg");
				FileOutputStream fos;
				fos = new FileOutputStream(xy);
				fos.write(data);
				fos.flush();
				fos.close();
	        	File dir = new File(Environment.getExternalStorageDirectory() + "/SnapNowImages");
	        	if(!dir.exists())
	        	{
	        		Log.d(TAG, "dir does nor exist");
	            	
	        		dir.mkdir();
	        	}
	        	Date d = new Date();
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
	       		String filnename= y+"-"+smonth+"-"+sday+" "+shour+"."+sminute;
	       		
	       		File f = new File(dir + File.separator + filnename+".jpg");
	       		
	        	try 
	        	{
					copyDirectory(xy, f);
				} 
	        	catch (Exception e) 
	        	{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	SnapNowBackgroundService.addEntry(new PhotoEntry(f.getAbsolutePath(), ctx));
	        	Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
	            Uri contentUri = Uri.fromFile(f);
	            mediaScanIntent.setData(contentUri);
	            ctx.sendBroadcast(mediaScanIntent);
	        	Log.i(TAG, "req99");
	        	SnapNowActivity.this.finish();
			} 
			catch (FileNotFoundException e1) 
			{
				// TODO Auto-generated catch block
				Log.i(TAG, "req14e1");
				e1.printStackTrace();
				SnapNowActivity.this.finish();
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				Log.i(TAG, "req14e2");
				e.printStackTrace();
				SnapNowActivity.this.finish();
			}

		}
		
	};
	
	/**
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {  
    	boolean totwitter=false;
    	boolean totumblr=false;
    	AudioManager mgr = (AudioManager) getSystemService(AUDIO_SERVICE);
        mgr.setStreamVolume(AudioManager.STREAM_NOTIFICATION, volume, 0);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) 
        {  
        	Log.d(TAG, "bla");
        	SnapNowBackgroundService.resetCountdown();
        	Log.d(TAG, "blo");
        	Log.d(TAG, "uri old: "+data.getData());
        	//Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        	String u = getPath(data.getData());
        	File xy = new File(u);
        	Log.d(TAG, "filesize: "+xy.length());
        	//photo.compress(Bitmap.CompressFormat.PNG, 100, bytes);

        	File dir = new File(Environment.getExternalStorageDirectory() + "/SnapNowImages");
        	 
            
        	if(!dir.exists())
        	{
        		Log.d(TAG, "dir does nor exist");
            	
        		dir.mkdir();
        	}
       		Date d = new Date();
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
       		String filnename= y+"-"+smonth+"-"+sday+" "+shour+"."+sminute;
       		
       		File f = new File(dir + File.separator + filnename+".jpg");
       		
        	try 
        	{
				copyDirectory(xy, f);
			} 
        	catch (IOException e) 
        	{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

        	SnapNowBackgroundService.addEntry(new PhotoEntry(f.getAbsolutePath(), this));
      	}  
        SnapNowActivity.this.finish();
    } 
    
    **/
	
	public String getPath(Uri uri) 
	{
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		startManagingCursor(cursor);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}
	
    // If targetLocation does not exist, it will be created.
    public void copyDirectory(File sourceLocation , File targetLocation) throws IOException 
    {
        
        if (sourceLocation.isDirectory()) 
        {
            if (!targetLocation.exists()) 
            {
                targetLocation.mkdir();
            }
            
            String[] children = sourceLocation.list();
            for (int i=0; i<children.length; i++) 
            {
                copyDirectory(new File(sourceLocation, children[i]),
                        new File(targetLocation, children[i]));
            }
        } else {
            
            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);
            
            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }
    }

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) 
	{
		// TODO Auto-generated method stub        //stop the preview  
        camera.stopPreview();  
        //release the camera  
        camera.release();  
        //unbind the camera from this object  
        camera = null;  		
	}
}