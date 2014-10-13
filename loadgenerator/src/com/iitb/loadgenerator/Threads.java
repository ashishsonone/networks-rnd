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

				if(MainActivity.running == false){
					MainActivity.running = true; //At a time only one downloading activity in background can take place
					Thread t = new Thread(r);
					t.start();
				}

				//Log.d(Constants.LOGTAG,"Registration request rejected");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				Log.d(Constants.LOGTAG, "ServerListen : timeout");
			}
		}
		Log.d(Constants.LOGTAG, "ListenServer : Experiment Over, Stopped listening ... ");
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

					//Thread.sleep(10000); //Alarms are set and events processed during this time. Also log file gets generated
					MainActivity.load = RequestEventParser.parseEvents(controlFile);
					MainActivity.numDownloadOver = 0; //reset it
					//on complete  

					Log.d(Constants.LOGTAG,"eventRunner : Experiment over. Now sending log file");

					Intent localIntent = new Intent(Constants.BROADCAST_ALARM_ACTION);
					localIntent.putExtra("eventid", (int) -1); //this is just to trigger first scheduleNextAlarm

					// Broadcasts the Intent to receivers in this application.
					LocalBroadcastManager.getInstance(ctx).sendBroadcast(localIntent);

					//					sendLog();

					//					Log.d(Constants.LOGTAG,"eventRunner : Log file sent successfully");

				}
				else{
					Log.d(Constants.LOGTAG,"eventRunner : No control file in response");
				}
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
		
		RequestEvent event = MainActivity.load.events.get(eventid);
		String logfilename = "" + MainActivity.load.loadid;
		File logfile = new File(MainActivity.logDir, logfilename);
		
		Log.d(Constants.LOGTAG, "HandleEvent : just entered thread");
		InputStream input = null;

		OutputStream output = null;
		HttpURLConnection connection = null;
		String filename = "unknown";
		BufferedWriter logwriter = null;

		try {
			URL url = new URL(event.url);

			filename = event.url.substring(event.url.lastIndexOf('/') + 1);
			
			Log.d(Constants.LOGTAG, "HandleEvent : " + event.url + " " + filename);
			
			connection = (HttpURLConnection) url.openConnection();
			connection.connect();

			// expect HTTP 200 OK, so we don't mistakenly save error report
			// instead of the file
			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				Log.d(Constants.LOGTAG, "HandleEvent : " + " connection response code error");
				return -1;
			}

			// this will be useful to display download percentage
			// might be -1: server did not report the length
			int fileLength = connection.getContentLength();
			
			Log.d(Constants.LOGTAG, "HandleEvent : " + " filelength " + fileLength);

			// download the file
			input = connection.getInputStream();
			output = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + filename);
			
			logwriter = new BufferedWriter(new FileWriter(logfile, true));
			
			logwriter.write(MainActivity.load.loadid + " " + eventid +  " " + fileLength + "\n");
			logwriter.write(url + "\n");

			byte data[] = new byte[4096];
			long total = 0;
			int count;
			int oldprogress = -1, currprogress = 0;
			
			Log.d(Constants.LOGTAG, "HandleEvent : " + " file opened on sd card");
			
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
			logwriter.write(Constants.LINEDELIMITER); //this marks the end of this log
			
			//File download over
			//on complete  
	        Intent localIntent = new Intent(Constants.BROADCAST_ACTION)
	        					.putExtra(Constants.BROADCAST_MESSAGE, "File  ... " + filename + "\n");
	        	
	        // Broadcasts the Intent to receivers in this application.
	        LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
	        
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (output != null)
					output.close();
				if (input != null)
					input.close();
				if (logwriter != null)
					logwriter.close();
			} catch (IOException ignored) {
			}

			if (connection != null)
				connection.disconnect();
		}
		int num = MainActivity.numDownloadOver++;
		Log.d(Constants.LOGTAG, "handle event thread : END . Incrementing numDownloadOver to " + MainActivity.numDownloadOver + " #events is "+ MainActivity.load.events.size());
		if(num+1 == MainActivity.load.events.size()){
			//send the consolidated log file
			Log.d(Constants.LOGTAG, "handle event thread . Sending the log file");
			int ret = Threads.sendLog(logfilename);
		}
		return 0;
	}

	@SuppressWarnings("deprecation")
	static int sendLog(String logFileName){
		int statusCode = 404;
		
		String logFilePath = Constants.logDirectory + "/" + logFileName;
		String url = "http://" + MainActivity.serverip + ":" + MainActivity.serverport + "/" + Constants.SERVLET_NAME + "/receiveLogFile.jsp";
//		String url = "http://192.168.0.107/fup.php";
		
		File logFile = new File(logFilePath);
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
					//logFile.delete();
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
