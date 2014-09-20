package com.example.loadgen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	TextView textbox;
	EditText ipbox;
	Button startbutton;
	
	String serverip;
	AlarmManager am ;
	SimpleDateFormat sdf = new SimpleDateFormat("ZZZZ HH:mm:s : S", Locale.US);
	
	String Users[] = { "A1", "A2", "A3" };
	
	Load load;
	
	int currEvent = 0;
	int downloaded = 0;
	
	File log = null;
	String logfileuri = null;
	
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
	
	String getTimeInFormat(){
		Calendar cal = Calendar.getInstance();
		return sdf.format(cal.getTime());
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		textbox = (TextView) findViewById(R.id.response_id);
		ipbox = (EditText) findViewById(R.id.server);
		startbutton = (Button) findViewById(R.id.startbutton);
		am = (AlarmManager) getSystemService(ALARM_SERVICE);
		
		
		//scheduleNextAlarm();
	}
	
	public void startEvents(View v){
		startbutton.setEnabled(false); //disble the button so that events are not started again
		
		HttpGet getRequest = new HttpGet("http://www.cse.iitb.ac.in/~sanchitgarg/events.txt");
		serverip = ipbox.getText().toString().trim();
		textbox.append("\n===========================\n");
		textbox.append("server : " + serverip + "\n");
		textbox.append("\n===========================\n");
		
 		try {
 			HttpClient client = getHttpClient();
 			// Execute HTTP Get Request
 	        ResponseHandler<String> responseHandler = new BasicResponseHandler();
 	        String responseBody = client.execute(getRequest, responseHandler);
 	        
 	        textbox.append(responseBody);
 	        load = RequestEventParser.parseEvents(responseBody);
 	        textbox.append("\n===========================\n");
 	        textbox.append("#EVENTS : " + load.events.size() + "\n");
 	        textbox.append("\n===========================\n");
 	        
 	        logfileuri = "/sdcard/log_" + load.loadid + ".txt";
 	        log = new File(logfileuri);
 	       
 	        /**
 	       String serverUri = "http://" + serverip + "/fup.php";
 	       textbox.append("Uploading log file to " + serverUri + " ...... \n");
 	       int res = Uploader.uploadFile("/sdcard/log.txt", serverUri);
  		 
 	       textbox.append("Upload Over : responsecode " + res + "\n");
 	       */
  		 
 	       scheduleNextAlarm();
 	        
 		} catch (ClientProtocolException e){
 			e.printStackTrace();
 			Toast.makeText(this, "ClientProtocolException while fetching eventlist", Toast.LENGTH_SHORT).show();
 		} catch (IOException e) {
 			e.printStackTrace();
 			Toast.makeText(this, "IOException while fetching eventlist", Toast.LENGTH_SHORT).show();
 		}
	}
	
	void scheduleNextAlarm(){
		if(currEvent >= load.events.size()) return;
		
		RequestEvent e = load.events.get(currEvent);
		
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
		
		textbox.append("Received " + eventid + " @ " + getTimeInFormat() + "\n");
				
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
	    	 textbox.append("download status : " + result + "\n");
	    	 
	    	 downloaded++;
	    	 if(downloaded == load.events.size()){
	    		 String serverUri = "http://" + serverip + "/fup.php";
	    		 textbox.append("Uploading log file to " + serverUri + " ...... \n");
	    		 int res = Uploader.uploadFile(logfileuri, serverUri);
	    		 
	    		 textbox.append("Upload Over : responsecode " + res + "\n");
	    	 }
	 		 //Log.d("Response HTTP GET", result);
	     }

	     /*
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
		*/
	     
	    @Override
		protected String doInBackground(Integer... args) {
	    	InputStream input = null;
	     
	        OutputStream output = null;
	        HttpURLConnection connection = null;
	        FileWriter fw = null;
	        
	        try {
	        	RequestEvent event = load.events.get(args[0]);
	            URL url = new URL(event.url);
	          
	            String filename = event.url.substring(event.url.lastIndexOf('/') + 1);
	            connection = (HttpURLConnection) url.openConnection();
	            connection.connect();

	            // expect HTTP 200 OK, so we don't mistakenly save error report
	            // instead of the file
	            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
	                return "Server returned HTTP " + connection.getResponseCode()
	                        + " " + connection.getResponseMessage();
	            }

	            // this will be useful to display download percentage
	            // might be -1: server did not report the length
	            int fileLength = connection.getContentLength();

	            // download the file
	            input = connection.getInputStream();
	            output = new FileOutputStream("/sdcard/" + filename);
	            
	            fw = new FileWriter(log.getAbsoluteFile(), true);
	            
	            fw.write("LOG EVENT:" + args[0] + " url: " + url + " filesize: " + fileLength + "\n");
	            

	            byte data[] = new byte[4096];
	            long total = 0;
	            int count;
	            int oldprogress = -1, currprogress = 0;
	            while ((count = input.read(data)) != -1) {
	                // allow canceling with back button
	                if (isCancelled()) {
	                    input.close();
	                    fw.close();
	                    return null;
	                }
	                total += count;
	                
	                // publishing the progress....
	                if (fileLength > 0){ // only if total length is known
	                	currprogress = (int) (total * 100 / fileLength);
	                	if(currprogress > oldprogress){
	                		oldprogress = currprogress;
	                		fw.write(getTimeInFormat() + " " + currprogress + "% " + total + "\n");
	                	}
	                    //publishProgress((int) (total * 100 / fileLength));
	                }
	                output.write(data, 0, count);
	            }
	        } catch (Exception e) {
	            return e.toString();
	        } finally {
	            try {
	                if (output != null)
	                    output.close();
	                if (input != null)
	                    input.close();
	                if(fw != null)
	                	fw.close();
	            } catch (IOException ignored) {
	            }

	            if (connection != null)
	                connection.disconnect();
	        }
	        return "Success";
	     }
	 }
}
