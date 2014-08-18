package com.example.loadgen;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Vector;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	TextView textbox;
	AlarmManager am ;
	SimpleDateFormat sdf = new SimpleDateFormat("ZZZZ HH:mm:s : S", Locale.US);
	
	String Users[] = { "A1", "A2", "A3" };
	
	Vector<RequestEvent> events;
	
	int currEvent = 0;
	
	HttpClient getHttpClient(){
		//Set timeout parameters for httpclient
		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		// The default value is zero, that means the timeout is not used. 
		int timeoutConnection = 2000;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		// Set the default socket timeout (SO_TIMEOUT) 
		// in milliseconds which is the timeout for waiting for data.
		int timeoutSocket = 3000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		
		return new DefaultHttpClient(httpParameters);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		textbox = (TextView) findViewById(R.id.response_id);
		am = (AlarmManager) getSystemService(ALARM_SERVICE);
		textbox.append("\n===========================\n");
	        
		HttpGet getRequest = new HttpGet("http://www.cse.iitb.ac.in/~ashishsonone/events.txt");
 		
 		try {
 			HttpClient client = getHttpClient();
 			// Execute HTTP Get Request
 	        ResponseHandler<String> responseHandler = new BasicResponseHandler();
 	        String responseBody = client.execute(getRequest, responseHandler);
 	        
 	        textbox.append(responseBody);
 	        events = RequestEventParser.parseEvents(responseBody);
 	        textbox.append("\n===========================\n");
 	        textbox.append("#EVENTS : " + events.size() + "\n");
 	        textbox.append("\n===========================\n");
 	        
 	        scheduleNextAlarm();
 	        
 		} catch (ClientProtocolException e){
 			e.printStackTrace();
 			Toast.makeText(this, "ClientProtocolException while fetching eventlist", Toast.LENGTH_SHORT).show();
 		} catch (IOException e) {
 			e.printStackTrace();
 			Toast.makeText(this, "IOException while fetching eventlist", Toast.LENGTH_SHORT).show();
 		}
		
		
		//scheduleNextAlarm();
	}
	
	void scheduleNextAlarm(){
		if(currEvent >= events.size()) return;
		
		RequestEvent e = events.get(currEvent);
		
		textbox.append("Scheduling " + currEvent + "@" + sdf.format(e.cal.getTime()) + "\n");
		
		Intent intent = new Intent(this, AlarmReceiver.class);
		intent.putExtra("eventid", (int)currEvent);
		
		// In reality, you would want to have a static variable for the request code instead of 192837
		PendingIntent sender = PendingIntent.getBroadcast(this, 192837 + currEvent, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		 
		// Get the AlarmManager service
		am.set(AlarmManager.RTC_WAKEUP, e.cal.getTimeInMillis(), sender);
		currEvent++;
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onNewIntent(Intent intent) {
		
		
		int eventid = intent.getExtras().getInt("eventid");
		
		// get a Calendar object with current time
		Calendar cal = Calendar.getInstance();
		textbox.append("Received " + eventid + " @ " + sdf.format(cal.getTime()) + "\n");
				
		RequestMaker as = new RequestMaker();
		
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB_MR1) {
            as.execute(eventid);
        } else {
            as.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, eventid);
        }
		
		scheduleNextAlarm();
		super.onNewIntent(intent);
		setIntent(intent);
		
	} // End of onNewIntent(Intent intent)

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
	
	private class RequestMaker extends AsyncTask<Integer, Void, String> {
		
	     protected void onPostExecute(String result) {
	    	 textbox.append(result + "\n");
	 	        
	 		 Log.d("Response HTTP GET", result);
	     }

		@Override
		protected String doInBackground(Integer... args) {
			
			RequestEvent event = events.get(args[0]);
			HttpGet getRequest = new HttpGet(event.url);
	 		
	 		try {
	 			HttpClient client = getHttpClient();
	 			// Execute HTTP Get Request
	 	        ResponseHandler<String> responseHandler = new BasicResponseHandler();
	 	        String responseBody = client.execute(getRequest, responseHandler);
	 	        
	 	        return responseBody;
	 	        
	 		} catch (ClientProtocolException e){
	 			e.printStackTrace();
	 		} catch (IOException e) {
	 			e.printStackTrace();
	 		}
			return "GET Response is NULL(ERROR)";
		}
	 }
}
