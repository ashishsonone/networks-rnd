package com.iitb.loadgenerator;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class BackgroundService extends IntentService{
	
	public BackgroundService() {
		super("BackgroundService");
	}
	
	@Override
    protected void onHandleIntent(Intent workIntent) {
		try
		{
			Log.d(Constants.LOGTAG, "Connecting to " + MainActivity.serverip
					+ " on port " + MainActivity.serverport);
			Socket client = new Socket(MainActivity.serverip, MainActivity.serverport);
			MainActivity.myip = client.getLocalAddress().getHostAddress();
			
			MainActivity.listen = new ServerSocket(0);
			MainActivity.listen.setSoTimeout(0);
			
			sendDeviceInfo(client);
			client.close();
			
			Runnable r = new Runnable() {
				public void run() {
					Threads.ListenServer();
				}
			};
			
			Thread t = new Thread(r);
			
	        t.start();
			
		}catch(IOException e)
		{
			e.printStackTrace();
		}
		
        //on complete  
        Intent localIntent = new Intent(Constants.BROADCAST_ACTION)
        					.putExtra(Constants.BROADCAST_MESSAGE, "Now Listening ... ");
        	
        // Broadcasts the Intent to receivers in this application.
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }
	
	public static int sendDeviceInfo(Socket client){
		Log.d(Constants.LOGTAG,"Just connected to "
				+ client.getRemoteSocketAddress());
		OutputStream outToServer;
		try {
			outToServer = client.getOutputStream();
			DataOutputStream dout =
					new DataOutputStream(outToServer);
			DataInputStream din =
					new DataInputStream(client.getInputStream());

			//String json = "HelloWorld from " + client.getLocalSocketAddress();
			String json = Utils.getMyDetailsJson(MainActivity.listen, MainActivity.myip);

			Utils.tryParse(json);

			Log.d(Constants.LOGTAG,json);
			dout.writeInt(json.length());

			dout.writeBytes(json);
			
			Log.d(Constants.LOGTAG,"waiting for response code");
			int rescode = din.readInt();
			System.out.println(rescode);
			if(rescode == 404){
				MainActivity.experimentOn = false;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return 0;
	}
}
