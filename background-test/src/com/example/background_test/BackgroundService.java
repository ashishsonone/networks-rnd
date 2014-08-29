package com.example.background_test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;









public class BackgroundService extends IntentService{
	SimpleDateFormat sdf = new SimpleDateFormat("ZZZZ HH:mm:s : S", Locale.US);
	
	public BackgroundService() {
		super("BackgroundService");
	}

	String getTimeInFormat(){
		Calendar cal = Calendar.getInstance();
		return sdf.format(cal.getTime());
	}
	
	@Override
    protected void onHandleIntent(Intent workIntent) {
        //String dataString = workIntent.getDataString();
        FileWriter fw = null;
        File log = null;
        log = new File("/sdcard/background_log.txt");
        try {
			fw = new FileWriter(log.getAbsoluteFile(), true);
			/*Integer i=1;
	        while(i<1000){
					//fw.write("LOG EVENT TIME: " + getTimeInFormat() + "\n");
					if(i%100 == 0){
						Log.d("BG Service","Writing " + i + "\n");
					}
					i++;
	        }*/
	        fw.write("LOG EVENT TIME: " + getTimeInFormat() + "\n");
	        fw.close();
	        try {
	            Thread.sleep(3000);
	        } catch(InterruptedException ex) {
	            Thread.currentThread().interrupt();
	        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
        //on complete  
        Intent localIntent = new Intent(Constants.BROADCAST_ACTION)
        					.putExtra(Constants.EXTENDED_DATA_STATUS, "Log File Written");
        	
        // Broadcasts the Intent to receivers in this application.
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);

        
        
        
        
    }


}
