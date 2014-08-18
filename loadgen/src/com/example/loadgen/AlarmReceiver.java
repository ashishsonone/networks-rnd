package com.example.loadgen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
 
public class AlarmReceiver extends BroadcastReceiver {
 
 @Override
 public void onReceive(Context context, Intent intent) {
   try {
     Bundle bundle = intent.getExtras();
     int eventid = bundle.getInt("eventid");
     Toast.makeText(context, "Received alarm " + eventid, Toast.LENGTH_SHORT).show();
     
     Intent callingIntent = new Intent(context, MainActivity.class);
     callingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
     callingIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

     callingIntent.putExtra("eventid", (int)eventid);
     context.startActivity(callingIntent);  
     
    } catch (Exception e) {
     Toast.makeText(context, "There was an error somewhere, but we still received an alarm", Toast.LENGTH_SHORT).show();
     e.printStackTrace();
 
    }
 }
 
}