package com.iitb.loadgenerator;


import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;


public class AlarmReceiver extends WakefulBroadcastReceiver
{
    // Prevents instantiation
	public AlarmReceiver() {
    }
	
    // Called when the BroadcastReceiver gets an Intent it's registered to receive
    
    @Override
    public void onReceive(final Context context, Intent intent) {
    	Log.d(Constants.LOGTAG, "Alarm Receiver : alarm just received. Now setting up to handle event");
    	Bundle bundle = intent.getExtras();
        int eventid = bundle.getInt("eventid");
        
        if(eventid >= 0){
	    	Intent callingIntent = new Intent(context, DownloaderService.class);
	
	        callingIntent.putExtra("eventid", (int)eventid);
	        
	        startWakefulService(context, callingIntent);
	        Log.d(Constants.LOGTAG, "Alarm Receiver : started the Downloader Service");
        }
		
		scheduleNextAlarm(context);
		
    }
    
    void scheduleNextAlarm(Context context){
		if(MainActivity.currEvent >= MainActivity.load.events.size()) {
			Log.d(Constants.LOGTAG, "scheduleNextAlarm : All alarms over. Experiment finished");
			MainActivity.experimentOn = false;
			return;
		}
		
		RequestEvent e = MainActivity.load.events.get(MainActivity.currEvent);
		
		
		Intent intent = new Intent(context, AlarmReceiver.class);
		intent.putExtra("eventid", (int)MainActivity.currEvent);
		
		PendingIntent sender = PendingIntent.getBroadcast(context, 192837 + MainActivity.currEvent, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		
		
		//just for now while control file is getting ready
		Calendar cal = Calendar.getInstance();
//		MainActivity.am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis() + 5000, sender);
		Log.d(Constants.LOGTAG, MainActivity.sdf.format(cal.getTime()) + "Scheduling " + MainActivity.currEvent + "@" + MainActivity.sdf.format(e.cal.getTime()) + "\n");
		

		MainActivity.am.set(AlarmManager.RTC_WAKEUP, e.cal.getTimeInMillis(), sender);
		MainActivity.currEvent++;
	}
}
