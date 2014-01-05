package de.lukeslog.snapnao.main;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import de.lukeslog.snapnao.constants.SnapNowConstants;
import de.lukeslog.snapnow.R;

public class TumblrExplain  extends Activity
{
	private final String TAG = SnapNowConstants.TAG;
	SharedPreferences prefs;
	Context ctx;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
		 super.onCreate(savedInstanceState);
	        setContentView(R.layout.tumblrexmplain);
	        Log.i(TAG, "explain");
	        ctx=this;
	        prefs = getSharedPreferences(SnapNowConstants.PREFS, 0);
	        
	        WebView webView = (WebView) findViewById(R.id.tumbrwebview);
			webView.loadUrl("http://www.tumblr.com/docs/en/email_publishing");
    }

}
