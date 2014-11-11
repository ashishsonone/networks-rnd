package com.iitb.loadgenerator;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.Calendar;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class Threads {
	public static void ListenServer(final Context ctx){
		while(MainActivity.experimentOn){//listen as a server
			try {
				Log.d(Constants.LOGTAG,"Waiting for connection from server");
				final Socket temp = MainActivity.listen.accept(); 
				Log.d(Constants.LOGTAG,"accepted: will now get config file");
				Runnable r = new Runnable() {
					public void run() {
						Threads.eventRunner(temp, ctx);
					}
				};

				Thread t = new Thread(r);
				t.start();

				//Log.d(Constants.LOGTAG,"Registration request rejected");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				Log.d(Constants.LOGTAG, "ServerListen : timeout");
			}
		}
		Log.d(Constants.LOGTAG, "ListenServer : Experiment Over, Stopped listening ... ");
		Intent localIntent = new Intent(Constants.BROADCAST_ACTION)
				.putExtra(Constants.BROADCAST_MESSAGE, 
				"Experiment Over, Stopped listening ...\n");

		// Broadcasts the Intent to receivers in this application.
		LocalBroadcastManager.getInstance(ctx).sendBroadcast(localIntent);
	}

	public static void eventRunner(Socket server, final Context ctx){
		Log.d(Constants.LOGTAG,"Server connection established");
		String data = "";
		try {
			DataInputStream dis= new DataInputStream(server.getInputStream());
			DataOutputStream dout = new DataOutputStream(server.getOutputStream());

			int length = dis.readInt();
			Log.d(Constants.LOGTAG,"Json length " + length + " read from " + server.getInetAddress().getHostAddress());

			for(int i=0;i<length;++i){
				data += (char)dis.readByte();
			}
			Log.d(Constants.LOGTAG,"eventRunner : json received : " + data);

			Map<String, String> jsonMap = Utils.ParseJson(data);

			String action = jsonMap.get(Constants.action);
			if(action.compareTo(Constants.action_controlFile) == 0){
				if(MainActivity.running == true){
					//this should not happen. As one experiment is already running Send 300 response
					Log.d(Constants.LOGTAG,"Experiment running but received another control file request");
					dout.writeInt(300);
					return;
				}
				MainActivity.running = true;
				boolean textFileFollow = Boolean.parseBoolean((String) jsonMap.get(Constants.textFileFollow));
				if(textFileFollow){
					int fileSize = dis.readInt();
					Log.d(Constants.LOGTAG,"eventRunner : fileSize " + fileSize);
					StringBuilder fileBuilder = new StringBuilder();
					for(int i=0;i<fileSize;++i){
						fileBuilder.append((char)dis.readByte());
					}
					String controlFile = fileBuilder.toString();

					dout.writeInt(200);

					Log.d(Constants.LOGTAG,controlFile);

					server.close();

					Log.d(Constants.LOGTAG,"eventRunner : Now setting up alarms");
					//display that control file received. Setting up alarms
					Bundle bundle = new Bundle();
					bundle.putString(Constants.BROADCAST_MESSAGE,"## Control file received. Setting up alarms\n");
			        
			        Intent local = new Intent(Constants.BROADCAST_ACTION)
			        					.putExtras(bundle);
			        LocalBroadcastManager.getInstance(ctx).sendBroadcast(local);
					server.close();
					
					//***************
					
					MainActivity.load = RequestEventParser.parseEvents(controlFile);
					MainActivity.numDownloadOver = 0; //reset it
					MainActivity.currEvent = 0;
					
					//send broadcast to trigger alarms
					Intent localIntent = new Intent(Constants.BROADCAST_ALARM_ACTION);
					localIntent.putExtra("eventid", (int) -1); //this is just to trigger first scheduleNextAlarm

					LocalBroadcastManager.getInstance(ctx).sendBroadcast(localIntent);
				}
				else{
					Log.d(Constants.LOGTAG,"eventRunner : No control file in response");
				}
			}
			else if (action.compareTo(Constants.action_stopExperiment) == 0){
				Log.d(Constants.LOGTAG, "MainActivity.running boolean set to false. Reset()");
				
				if(MainActivity.running && MainActivity.load != null){
					final String logFileName = Long.toString(MainActivity.load.loadid);
					Runnable r = new Runnable() {
						public void run() {
							Threads.sendLog(logFileName);
						}
					};
					Thread t = new Thread(r);
			        t.start();
					
					MainActivity.reset(ctx);
				}
				dout.writeInt(200);
				server.close();
			}
			else if (action.compareTo(Constants.action_clearRegistration) == 0){
				Log.d(Constants.LOGTAG, "action clearRegistration");
				MainActivity.reset(ctx); //reset all variables and state
				
				//also stop server. 
				MainActivity.experimentOn = false;
				if(MainActivity.listen != null) MainActivity.listen.close();
				
				//Enable StartButton (send broadcast)
				Bundle bundle = new Bundle();
				bundle.putInt("enable", 1);
				bundle.putString(Constants.BROADCAST_MESSAGE,"clear Registration. Resetting everything.\nPlease Register Again");
		        //on complete  
		        Intent localIntent = new Intent(Constants.BROADCAST_ACTION)
		        					.putExtras(bundle);
		        	
		        // Broadcasts the Intent to receivers in this application.
		        LocalBroadcastManager.getInstance(ctx).sendBroadcast(localIntent);
				server.close();
			}
			else{
				Log.d(Constants.LOGTAG,"eventRunner() : Wrong action code");
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//Somehow experiment could not be started due to some IOException in socket transfer. So again reset running variable to false
			MainActivity.running = false;
			e.printStackTrace();
		}
	}

	static int HandleEvent(int eventid, final Context context){
		//Log file will be named   <eventid> . <loadid>
		if(!MainActivity.running){
			Log.d(Constants.LOGTAG, "HandleEvent : But experiment not running");
			return -1;
		}
		RequestEvent event = MainActivity.load.events.get(eventid);
		String logfilename = "" + MainActivity.load.loadid;
		File logfile = new File(MainActivity.logDir, logfilename);
		
		Log.d(Constants.LOGTAG, "HandleEvent : just entered thread");
		
		InputStream input = null;
		OutputStream output = null;
		HttpURLConnection connection = null;
		String filename = "unknown"; //file name of file to download in GET request
		BufferedWriter logwriter = null;
		boolean success = false;

		Calendar startTime = null, endTime = null;
		long responseTime = -1;
		
		boolean logFileOpened = false;
		try {
			logwriter = new BufferedWriter(new FileWriter(logfile, true));
			logFileOpened = true;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			Log.d(Constants.LOGTAG, "HandleEvent() : can't open log file for writing " + logfilename);
			e1.printStackTrace();
		}
		
		if(logFileOpened){
			//By here log file has been opened for writing
			try {
				URL url = new URL(event.url);
				
				logwriter.write("details: " + MainActivity.load.loadid + " " + eventid +  "\n");
				logwriter.write("url: " + url + "\n");
				
				filename = event.url.substring(event.url.lastIndexOf('/') + 1);
				
				Log.d(Constants.LOGTAG, "HandleEvent : " + event.url + " " + filename);
				
				connection = (HttpURLConnection) url.openConnection();
				connection.setReadTimeout(10000); //10 seconds timeout for reading from input stream
				connection.setConnectTimeout(10000); //10 seconds before connection can be established
				
				//note start time 
				startTime = Calendar.getInstance();
				
				connection.connect();
	
				// expect HTTP 200 OK, so we don't mistakenly save error report
				// instead of the file
				if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
					Log.d(Constants.LOGTAG, "HandleEvent : " + " connection response code error");
					logwriter.write("connect failed : code " + connection.getResponseCode() + "\n");
					logwriter.write(Constants.LINEDELIMITER); //this marks the end of this log
				}
				else{
					// this will be useful to display download percentage
					// might be -1: server did not report the length
					int fileLength = connection.getContentLength();
					logwriter.write("length: " + Integer.toString(fileLength) + " \n");
					
					Log.d(Constants.LOGTAG, "HandleEvent : " + " filelength " + fileLength);
		
					// download the file
					input = connection.getInputStream();
					output = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + filename);
		
					byte data[] = new byte[4096];
					long total = 0;
					int count;
					int oldprogress = 0, currprogress = 0;
					
					Log.d(Constants.LOGTAG, "HandleEvent : " + " file opened on sd card");
					logwriter.write(Utils.getTimeInFormat() + " " + currprogress + "% " + total + "\n"); //first progress 0%
					while ((count = input.read(data)) != -1) {
		//				Log.d(Constants.LOGTAG, "HandleEvent : " + " Received chunk of size " + count);
						total += count;
		
						// publishing the progress....
						if (fileLength > 0){ // only if total length is known
							currprogress = (int) (total * 100 / fileLength);
							if(currprogress > oldprogress){
								oldprogress = currprogress;
								Log.d(Constants.LOGTAG, currprogress + "% " + total + "\n");
								logwriter.write(Utils.getTimeInFormat() + " " + currprogress + "% " + total + "\n");
							}
							//publishProgress((int) (total * 100 / fileLength));
						}
						output.write(data, 0, count);
					}
					//File download over
					success = true;
					
					//note end time take the difference as response time
					endTime = Calendar.getInstance();
					
					responseTime = endTime.getTimeInMillis() - startTime.getTimeInMillis();
					logwriter.write("RT " +  responseTime + "\n");
		
					logwriter.write("success\n");
					logwriter.write(Constants.LINEDELIMITER); //this marks the end of this log
					
				}
			} catch (Exception e) {
				try {
					logwriter.write("failure\n");
					logwriter.write(Constants.LINEDELIMITER); //this marks the end of this log
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.printStackTrace();
			} finally {
				try {
					if (output != null)
						output.close();
					if (input != null)
						input.close();
				} catch (IOException ignored) {
				}
	
				if (connection != null)
					connection.disconnect();
			}
		}
		
		String msg = "GET #" + eventid + " File : " + filename;
		if(!logFileOpened) msg += " Couldn't open log file " + logfilename;
		else if(!success) msg += "FAILED connection problem/timeout";
		else msg += " SUCCESS with RT=" + responseTime + "\n";
		
		Intent localIntent = new Intent(Constants.BROADCAST_ACTION)
			.putExtra(Constants.BROADCAST_MESSAGE, msg);

		// Broadcasts the Intent to receivers in this application.
		LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);

		int num = MainActivity.numDownloadOver++;
		Log.d(Constants.LOGTAG, "handle event thread : END . Incrementing numDownloadOver to " + MainActivity.numDownloadOver + " #events is "+ MainActivity.load.events.size());
		if(num+1 == MainActivity.load.events.size() && logFileOpened){
			//send the consolidated log file
			try {
				logwriter.write("\nEOF\n"); //this indicates that all GET requests have been seen without interruption from either user/server
				logwriter.close();
				logwriter = null; //done with logwriter here. Need to close before sending logfile is called otherwise current 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d(Constants.LOGTAG, "handle event thread . Sending the log file");
			int ret = Threads.sendLog(logfilename);
		}
		else{
			Log.d(Constants.LOGTAG, "log file opening failed . Not sending the log file");
		}
		
		//finally if logwriter is not null, close it.
		if (logwriter != null){ //close logwriter here
			try {
				logwriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return 0;
	}
	
	static void sendLogFilesBackground(final Context ctx){
		File storage = new File(Constants.logDirectory); //log dir has already been created in onCreate
    	File[] files = storage.listFiles();
    	int sent = 0;
    	int errors = 0;
    	
    	String currExpLogFile = "-1" ;
		if(MainActivity.load != null) {
			currExpLogFile = Long.toString(MainActivity.load.loadid); 
		}
		
    	for(int i=0; i<files.length; i++){
    		File c = files[i];
    		String logFileName = c.getName();
    		
    		
    		if(!logFileName.equals(currExpLogFile)){ //pending log file is not current experiment's log
    			int status = Threads.sendLog(logFileName);
    			if(status == 200){
    				sent++;
    			}
    			else{
    				errors++;
    			}
    		}
    	}
    	Intent localIntent = new Intent(Constants.BROADCAST_ACTION)
							.putExtra(Constants.BROADCAST_MESSAGE, 
							"Background log file sending : success "+ sent + " Fail " + errors + "\n");

    	// Broadcasts the Intent to receivers in this application.
    	LocalBroadcastManager.getInstance(ctx).sendBroadcast(localIntent);
	}

	@SuppressWarnings("deprecation")
	static int sendLog(String logFileName){
		int statusCode = 404;
		
		String logFilePath = Constants.logDirectory + "/" + logFileName;
		String url = "http://" + MainActivity.serverip + ":" + MainActivity.serverport + "/" + Constants.SERVLET_NAME + "/receiveLogFile.jsp";
		Log.d(Constants.LOGTAG, "Upload url " + url);
//		String url = "http://192.168.0.107/fup.php";
		
		File logFile = new File(logFilePath);
		if(!logFile.exists()){
			Log.d(Constants.LOGTAG, "sendLog : File not found " + logFilePath + " May be sent earlier");
			return 200; //already sent sometime earlier
		}
		
		MultipartEntity mpEntity  = new MultipartEntity();
		HttpClient client = Utils.getClient();
		
		try {
			mpEntity.addPart("expID", new StringBody(logFileName));
			mpEntity.addPart(Constants.macAddress, new StringBody(Utils.getMACAddress()));
			mpEntity.addPart("file", new FileBody(logFile));
			
			HttpPost httppost = new HttpPost(url);
			httppost.setEntity(mpEntity);
			try {
				HttpResponse response = client.execute( httppost );
				statusCode = response.getStatusLine().getStatusCode();
				if(statusCode == 200){
					Log.d(Constants.LOGTAG, "Log file named " + logFileName + " deleted");
					logFile.delete(); //now deleting log file
				}
				else{
					Log.d(Constants.LOGTAG, "Sending Log file " + logFileName + " failed");
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return statusCode;
	}
}
