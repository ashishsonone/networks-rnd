package com.iitb.loadgenerator;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

import android.util.Log;

public class Threads {
	public static void ListenServer(){
		try {
			
			while(MainActivity.experimentOn){//listen as a server
				Log.d(Constants.LOGTAG,"Waiting for connection from server");
				final Socket temp = MainActivity.listen.accept(); 
				Log.d(Constants.LOGTAG,"accepted: will now get config file");
				Runnable r = new Runnable() {
					public void run() {
						Threads.eventRunner(temp);
					}
				};
				
				Thread t = new Thread(r);
				
		        t.start();
			}
			
			Log.d(Constants.LOGTAG,"Registration request rejected");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void eventRunner(Socket server){
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

					Log.d(Constants.LOGTAG,controlFile);
					
					server.close();
					
					Log.d(Constants.LOGTAG,"eventRunner : Now setting up alarms");
					
					Thread.sleep(10000); //Alarms are set and events processed during this time. Also log file gets generated
					Log.d(Constants.LOGTAG,"eventRunner : Experiment over. Now sending log file");
					
					sendLog();
					
					Log.d(Constants.LOGTAG,"eventRunner : Log file sent successfully");
					
				}
				else{
					Log.d(Constants.LOGTAG,"eventRunner : No control file in response");
				}
			}
			else{
				Log.d(Constants.LOGTAG,"eventRunner() : Wrong action code");
			}

		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
