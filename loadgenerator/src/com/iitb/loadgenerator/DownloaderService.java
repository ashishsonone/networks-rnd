package com.iitb.loadgenerator;


import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

//called by alarm receiver to start serving next download event
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
		
		boolean webviewon = true;
		if(!webviewon){
	        
			Runnable r = new Runnable() {
				public void run() {
					Threads.HandleEvent(eventid, getApplicationContext());
				}
			};
			
			Thread t = new Thread(r);
			
	        t.start();
		}
		else{
			RequestEvent event = MainActivity.load.events.get(eventid);
			final String url = event.url;
			MainActivity.logfilename = "" + MainActivity.load.loadid;
			
			MainActivity.logwriter = new StringBuilder();
			MainActivity.loggingOn = true;
			
			Log.d(Constants.LOGTAG, "HandleEvent : just entered thread");
			
			MainActivity.webview1.post(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					MainActivity.webview1.loadUrl(url);
				}
			});
		}
		
		AlarmReceiver.completeWakefulIntent(intent);
	}

}