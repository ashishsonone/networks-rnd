package com.example.background_test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends ActionBarActivity {

	public static Button button;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        IntentFilter mStatusIntentFilter = new IntentFilter(
                Constants.BROADCAST_ACTION);
        
        
        ResponseReceiver receiver = new ResponseReceiver(new Handler());

        LocalBroadcastManager.getInstance(this).registerReceiver(
                										receiver,
                										mStatusIntentFilter);
        
        
        button = (Button)findViewById(R.id.startbutton);
        
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    //!called on button click
    public void startBackgroundTask(View view){
    	File storage = new File("/sdcard/com.iitb.back/");
    	storage.mkdirs();
    	//storage.d
    	File[] files = storage.listFiles();
    	
    	for(int i=0; i<files.length; i++){
    		File c = files[i];
    		try {
				BufferedReader reader = new BufferedReader(new FileReader(c));
				Log.d("background-test", reader.readLine());
				reader.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		Log.d("filename :", c.getName());
    		//c.delete();
    	}
    	
    	Calendar cal = Calendar.getInstance();
    	String filename = Long.toString(cal.getTimeInMillis());
    	File file = new File(storage, filename);
    	try {
    		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(file.getAbsolutePath());
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	/*Intent mServiceIntent = new Intent(this, BackgroundService.class);
    	button.setEnabled(false);
    	startService(mServiceIntent);*/

    }
}
