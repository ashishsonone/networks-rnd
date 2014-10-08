package com.iitb.loadgenerator;


import java.io.File;
import java.net.ServerSocket;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {
	static TextView textbox;
	static EditText ipbox;
	static EditText portbox;
	static Button startbutton;
	static WebView webview;
	
	static String serverip = "192.168.0.119";
	static int serverport = 22222;
	static String myip;
	
	static boolean experimentOn = true;
	
	static ServerSocket listen = null;
	
	//Alarm specific
	static Load load;
	static int currEvent = 0;
	
	static AlarmManager am ;
	static WifiManager wifimanager;
	static SimpleDateFormat sdf = new SimpleDateFormat("ZZZZ HH:mm:s : S", Locale.US);
	
	static File logDir; //directory containing log files

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		textbox = (TextView) findViewById(R.id.response_id);
		ipbox = (EditText) findViewById(R.id.serverip);
		portbox = (EditText) findViewById(R.id.serverport);
		webview = (WebView) findViewById(R.id.webview);
		
		ipbox.setText(serverip);
		portbox.setText(Integer.toString(serverport));
		
		
		startbutton = (Button) findViewById(R.id.startbutton);
		
		am = (AlarmManager) getSystemService(ALARM_SERVICE);
		wifimanager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		
		wifimanager.setWifiEnabled(true); //Switch on the wifi if not already
		
		experimentOn = true;
		
		logDir = new File(Constants.logDirectory);
		logDir.mkdirs();
		
		//Set the orientation to portrait
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
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
	
	public void startService(View v){
		//TODO Change this to enable normal flow 
		boolean test = false;
		if(test){
			String json = Utils.getMyDetailsJson(MainActivity.listen, MainActivity.myip);
			Log.d(Constants.LOGTAG, "JSON : " + json);
			textbox.append(json);
		}
		else{
			serverip = ipbox.getText().toString();
			serverport = Integer.parseInt(portbox.getText().toString());
			
			textbox.setText("");
			textbox.append("\n1) Register \n 2)Listen  \n3)Alarms  \n4) Send log\n");
			textbox.append("ip = " + serverip + " port" + serverport);
			
			Intent mServiceIntent = new Intent(this, BackgroundService.class);
	    	startbutton.setEnabled(false);
	    	startService(mServiceIntent);
		}
	}
	
	public void exit(View v){
		finish();
        android.os.Process.killProcess(android.os.Process.myPid());
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
}
