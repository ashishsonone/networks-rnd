package com.iitb.loadgenerator;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
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

				Log.d(Constants.LOGTAG,"Registration request rejected");

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
			e.printStackTrace();
		}
	}

	static int HandleEvent(RequestEvent event, final Context context){
		Log.d(Constants.LOGTAG, "HandleEvent : just entered thread");
		InputStream input = null;

		OutputStream output = null;
		HttpURLConnection connection = null;
		FileWriter fw = null;
		String filename = "unknown";

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
			output = new FileOutputStream("/sdcard/" + filename);

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
					}
					//publishProgress((int) (total * 100 / fileLength));
				}
				output.write(data, 0, count);
			}
			
			//File download over
			//on complete  
	        Intent localIntent = new Intent(Constants.BROADCAST_ACTION)
	        					.putExtra(Constants.BROADCAST_MESSAGE, "File  ... " + filename + "\n");
	        	
	        // Broadcasts the Intent to receivers in this application.
	        LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
	        
		} catch (Exception e) {
			return -1;
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
		

        
		return 0;
	}

	static void sendLog(){
		Socket client;

		OutputStream outToServer;
		try {
			client = new Socket(MainActivity.serverip, MainActivity.serverport);
			outToServer = client.getOutputStream();
			DataOutputStream dout =
					new DataOutputStream(outToServer);
			//			DataInputStream din =
			//					new DataInputStream(client.getInputStream());

			//String json = "HelloWorld from " + client.getLocalSocketAddress();

			String json = Utils.getLogFileJson();

			dout.writeInt(json.length());

			dout.writeBytes(json); //json sent
			Log.d(Constants.LOGTAG,"sendLog() : JSON sent");
			//Now send log file


			Utils.SendFile(dout, "/sdcard/ip.txt");
			client.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
