package com.iitb.loadgenerator;


import java.io.File;
import java.net.ServerSocket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	static TextView textbox;
	static EditText ipbox;
	static EditText portbox;
	static EditText sessionidbox;
	static Button startbutton;
	
	static boolean exitThreadComplete; //whether exit thread successful
	
	static String serverip = "192.168.0.104";
	static int serverport = 8080;
	static int sessionid = 2312;
	static String myip;
	
	static boolean experimentOn = true; //whether to listen as server
	static boolean running = false; //whether scheduling alarms and downloading is going on
	static int numDownloadOver = 0; //indicates for how many events download in thread is over
	
	static ServerSocket listen = null;
	
	//Alarm specific
	static Load load = null;
	static int currEvent = 0;
	
	static AlarmManager am ;
	static WifiManager wifimanager;
	static SimpleDateFormat sdf = new SimpleDateFormat("ZZZZ HH:mm:s : S", Locale.US);
	
	static File logDir; //directory containing log files
	SharedPreferences sharedPreferences;	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		textbox = (TextView) findViewById(R.id.response_id);
		ipbox = (EditText) findViewById(R.id.serverip);
		portbox = (EditText) findViewById(R.id.serverport);
		sessionidbox = (EditText) findViewById(R.id.sessionid);
		
		sharedPreferences = getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
		if(sharedPreferences.contains(Constants.keyServerAdd)){
			ipbox.setText(sharedPreferences.getString(Constants.keyServerAdd, ""));
		}
		if(sharedPreferences.contains(Constants.keyServerPort)){
			portbox.setText(sharedPreferences.getString(Constants.keyServerPort, ""));
		}
		
		//ipbox.setText(serverip);
		//portbox.setText(Integer.toString(serverport));
		
		
		startbutton = (Button) findViewById(R.id.startbutton);
		
		am = (AlarmManager) getSystemService(ALARM_SERVICE);
		wifimanager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		
		wifimanager.setWifiEnabled(true); //Switch on the wifi if not already
		
		logDir = new File(Constants.logDirectory);
		logDir.mkdirs();
		
		//Set the orientation to portrait
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); //hide keyboard until actually needed 
		
		//Register Broadcast receiver
		IntentFilter broadcastIntentFilter = new IntentFilter(
                Constants.BROADCAST_ACTION);
        
        ResponseReceiver broadcastReceiver = new ResponseReceiver(new Handler());
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, broadcastIntentFilter);
        
        //Register AlarmReceiver
        IntentFilter alarmIntentFilter = new IntentFilter(Constants.BROADCAST_ALARM_ACTION);
        AlarmReceiver alarmReceiver = new AlarmReceiver();

        LocalBroadcastManager.getInstance(this).registerReceiver(alarmReceiver, alarmIntentFilter);
	}
	
	//reset all alarms, load, etc. Continue to listen for new experiment again from scratch
	public static void reset(Context ctx){
		if(running){
			load = null;
			currEvent = 0;
			running = false;
			numDownloadOver = 0;
			
			//cancel all alarms
			Intent intent = new Intent(ctx, AlarmReceiver.class);
			PendingIntent sender = PendingIntent.getBroadcast(ctx, Constants.alarmRequestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
			am.cancel(sender);
		}
	}
	
	@Override
	public void onBackPressed() {
	   Log.d(Constants.LOGTAG, "onBackPressed Called");
	   Intent setIntent = new Intent(Intent.ACTION_MAIN);
	   setIntent.addCategory(Intent.CATEGORY_HOME);
	   setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	   startActivity(setIntent);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}
	
	boolean isEmpty(EditText et){
		if(et.getText().toString().toString().trim().length() > 0) return false;
		return true;
	}
	
	public void startService(View v){
		//save the server ip and port into shared prefs
		if(isEmpty(ipbox)){
			Toast.makeText(this, "Please enter ip", Toast.LENGTH_SHORT).show();
			return;
		}
		if(isEmpty(portbox)){
			Toast.makeText(this, "Please enter port", Toast.LENGTH_SHORT).show();
			return;
		}
		if(isEmpty(sessionidbox)){
			Toast.makeText(this, "Please enter sessionid", Toast.LENGTH_SHORT).show();
			return;
		}
		serverip = ipbox.getText().toString();
		serverport = Integer.parseInt(portbox.getText().toString());
		sessionid = Integer.parseInt(sessionidbox.getText().toString());
		
		//every time start button is pressed
		experimentOn = true;
		
		Editor editor = sharedPreferences.edit();
	    editor.putString(Constants.keyServerAdd, serverip);
	    editor.putString(Constants.keyServerPort, Integer.toString(serverport));
	    editor.commit();
		
		textbox.setText("");
		textbox.append("Server : IP = " + serverip + " port" + serverport + "\n");
		textbox.append("My IP : " + Utils.getIP() + "\n");
		textbox.append("My MAC Address : " + Utils.getMACAddress() + "\n");
		
//		Runnable r = new Runnable() {
//			public void run() {
//				Threads.sendLog("10");
//			}
//		};
//		Thread t = new Thread(r);
//		
//        t.start();
        
		
		Intent mServiceIntent = new Intent(this, BackgroundService.class);
    	startbutton.setEnabled(false);
    	startService(mServiceIntent);
	}
	
	public void exit(View v){
		Log.d(Constants.LOGTAG, "creating asynctask : ExitTask");
		new ExitTask().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	 private class ExitTask extends AsyncTask<URL, Integer, Integer> {
	     protected Integer doInBackground(URL... urls) {
	         Utils.sendExitSignal();
	         return 0;
	     }

	     protected void onProgressUpdate(Integer... progress) {
	     }

	     protected void onPostExecute(Integer result) {
	    	 Log.d(Constants.LOGTAG, "killing self");
	    	 finish();
	         android.os.Process.killProcess(android.os.Process.myPid());
	     }
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
}
