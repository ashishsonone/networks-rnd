package com.iitb.loadgenerator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;


public class ResponseReceiver extends WakefulBroadcastReceiver
{
	private Handler handler;
	
    // Prevents instantiation
    ResponseReceiver(Handler h) {
    	handler = h;
    }
    // Called when the BroadcastReceiver gets an Intent it's registered to receive
    
    @Override
    public void onReceive(final Context context, Intent intent) {
    	Bundle bundle = intent.getExtras();
    	final String msg = (String) bundle.getString(Constants.BROADCAST_MESSAGE);
    	final int enable = bundle.getInt("enable"); //returns 0 if no suck key exists
    	Log.d("On Receive", msg);
    	handler.post(new Runnable() {
            @Override
            public void run() {
                //MainActivity.button.setEnabled(true);
            	//Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            	MainActivity.textbox.append("\n" + msg + "\n");
            	if(enable == 1) MainActivity.startbutton.setEnabled(true);
            }
        });
    }
}
