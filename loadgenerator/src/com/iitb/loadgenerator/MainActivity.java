package com.iitb.loadgenerator;


import java.net.ServerSocket;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.app.AlarmManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {
	static TextView textbox;
	static EditText ipbox;
	static EditText portbox;
	static Button startbutton;
	
	static String serverip = "192.168.0.119";
	static int serverport = 11111;
	static String myip;
	
	static boolean experimentOn = true;
	
	static ServerSocket listen = null;
	
	//Alarm specific
	static Load load;
	static int currEvent = 0;
	
	static AlarmManager am ;
	static SimpleDateFormat sdf = new SimpleDateFormat("ZZZZ HH:mm:s : S", Locale.US);
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		textbox = (TextView) findViewById(R.id.response_id);
		ipbox = (EditText) findViewById(R.id.serverip);
		portbox = (EditText) findViewById(R.id.serverport);
		
		ipbox.setText(serverip);
		portbox.setText(Integer.toString(serverport));
		
		
		startbutton = (Button) findViewById(R.id.startbutton);
		
		am = (AlarmManager) getSystemService(ALARM_SERVICE);
		
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
	
	public void startService(View v){
		serverip = ipbox.getText().toString();
		serverport = Integer.parseInt(portbox.getText().toString());
		
		textbox.setText("");
		textbox.append("\n1) Register \n 2)Listen  \n3)Alarms  \n4) Send log\n");
		textbox.append("ip = " + serverip + " port" + serverport);
		
		Intent mServiceIntent = new Intent(this, BackgroundService.class);
    	startbutton.setEnabled(false);
    	startService(mServiceIntent);
	}
	
	public void exit(View v){
		finish();          
        moveTaskToBack(true);
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
