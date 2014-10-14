package com.iitb.loadgenerator;


import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class DownloaderService extends IntentService{
	public DownloaderService() {
		super("DownloaderService");
		// TODO Auto-generated constructor stub
	}

	@Override
    protected void onHandleIntent(Intent intent) {
		if(!MainActivity.running){
			Log.d(Constants.LOGTAG, "DownloaderService : entered. But experiment not running");
			return;
		}
		Log.d(Constants.LOGTAG, "DownloaderService : just entered");
		Bundle bundle = intent.getExtras();
        final int eventid = bundle.getInt("eventid");
        
        //final RequestEvent e = MainActivity.load.events.get(eventid);
        
		Log.d(Constants.LOGTAG, "DownloaderService : Handling event " + eventid + "in a thread ... ");
		
        
		Runnable r = new Runnable() {
			public void run() {
				Threads.HandleEvent(eventid, getApplicationContext());
			}
		};
		
		Thread t = new Thread(r);
		
        t.start();
		
		AlarmReceiver.completeWakefulIntent(intent);
	}

}