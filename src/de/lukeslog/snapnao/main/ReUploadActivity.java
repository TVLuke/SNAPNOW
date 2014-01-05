package de.lukeslog.snapnao.main;

import java.util.ArrayList;

import de.lukeslog.snapnao.constants.SnapNowConstants;
import de.lukeslog.snapnao.database.EntryDatabase;
import de.lukeslog.snapnao.posting.Entry;
import de.lukeslog.snapnow.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ReUploadActivity extends Activity
{
	private final String TAG = SnapNowConstants.TAG;
	SharedPreferences prefs;
	Context ctx;
	ArrayList<String> listItems=new ArrayList<String>();
	ArrayAdapter<String> adapter;
	ArrayList<Entry> entrys;

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reupload);
        Log.i(TAG, "reupload activity");
        ctx=this;
        prefs = getSharedPreferences(SnapNowConstants.PREFS, 0);
        entrys = EntryDatabase.getAllEntrys();
        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listItems);
        ListView lv = (ListView) findViewById(R.id.list);
        lv.setAdapter(adapter);
        update();
        lv.setOnItemClickListener(new OnItemClickListener()
        {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) 
			{
				Log.i(TAG, "click");
				 AlertDialog.Builder alertDialog = new  AlertDialog.Builder(ctx);
	             alertDialog.setTitle("ReUpload");
	             alertDialog.setMessage("ReUpload?");     
	             alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
	             	public void onClick(DialogInterface dialog, int which) 
	             	{


	             	} }); 
	                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) 
	                {
	    				Log.i(TAG, "selected: "+arg2);
	    				Log.i(TAG, ""+entrys.get((entrys.size()-1)-arg2).getHeader());
	    				entrys.get((entrys.size()-1)-arg2).upload(ctx);
	                } 
	            }); 
	            alertDialog.show();
			}
        	
        });
        
        lv.setOnItemLongClickListener(new OnItemLongClickListener()
        {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) 
			{
				Log.i(TAG, "long click");
				 AlertDialog.Builder alertDialog = new  AlertDialog.Builder(ctx);
	             alertDialog.setTitle("Delete");
	             alertDialog.setMessage("Delete?");     
	             alertDialog.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
	             	public void onClick(DialogInterface dialog, int which) 
	             	{

	             		EntryDatabase.deleteEntry(entrys.get((entrys.size()-1)-arg2).getId());
	             		entrys = EntryDatabase.getAllEntrys();
	             		update();

	             	} }); 
	                alertDialog.setPositiveButton("Keep", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) 
	                {
	                          //alertDialog.dismiss();
	                } 
	            }); 
	            alertDialog.show();
				return false;
			}
        	
        });
    }
    
    private void update()
    {
    	 for(int i=(entrys.size()-1); i>=0; i--)
         {
         	Log.i(TAG, "->re: "+entrys.get(i).getHeader());
         	listItems.add(entrys.get(i).getHeader());
         }
         adapter.notifyDataSetChanged();
    }
}
