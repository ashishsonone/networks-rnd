package com.example.background_test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

class ResponseReceiver extends BroadcastReceiver
{
	private Handler handler;
	
    // Prevents instantiation
    ResponseReceiver(Handler h) {
    	handler = h;
    }
    // Called when the BroadcastReceiver gets an Intent it's registered to receive
    
    @Override
    public void onReceive(final Context context, Intent intent) {
    	final String msg = (String) intent.getExtras().get(Constants.EXTENDED_DATA_STATUS);
    	Log.d("On Receive", msg);
    	handler.post(new Runnable() {
            @Override
            public void run() {
                MainActivity.button.setEnabled(true);
            	Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}