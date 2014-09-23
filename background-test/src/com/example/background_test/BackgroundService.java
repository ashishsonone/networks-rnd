package com.example.background_test;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


public class BackgroundService extends IntentService{
	
	public BackgroundService() {
		super("BackgroundService");
	}
	
	@Override
    protected void onHandleIntent(Intent workIntent) {
		//final String dirPath = Environment.getDataDirectory().toString();
        final String dirPath = "/sdcard";
		System.out.println(dirPath);
		Log.d(Utils.Tag, "directory path " + dirPath);
		
		Runnable r1 = new Runnable() {
            public void run() {
            	Utils.writeLogFile(dirPath + "/bgThread1.txt");
            	Log.d(Utils.Tag, "File " +  dirPath + "/bgThread1.txt written");
            }
        };
        
        Thread t1 = new Thread(r1);
		
        Runnable r2 = new Runnable() {
            public void run() {
//            	try {
//					Thread.sleep(10000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
            	Utils.writeLogFile(dirPath + "/bgThread2.txt");
            	Log.d(Utils.Tag, "File " +  dirPath + "/bgThread2.txt written");
            }
        };
		
        Thread t2 = new Thread(r2);
        
        t1.start(); t2.start();
		
        //on complete  
        Intent localIntent = new Intent(Constants.BROADCAST_ACTION)
        					.putExtra(Constants.EXTENDED_DATA_STATUS, "Log File Written");
        	
        // Broadcasts the Intent to receivers in this application.
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);

        
        
        
        
    }


}
