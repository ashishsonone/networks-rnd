package ServerHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.simple.JSONObject;


public class Handler {
	static ServerSocket listen = null;
	static String myip = "";
	static ExecutorService executor = Executors.newCachedThreadPool();
	static boolean experimentOn = true;
	
	//static String serverName = "10.105.42.237";
	static String serverName = "192.168.0.119";
	static int port = 11111;

	public static void usage(){
		System.out.println("Requires two arguments : <action> , <ip> and <port>");
		System.out.println("<action> can be : " + Constants.Action.regstart + ", "
				+ Constants.Action.regstop + ", "
				+ Constants.Action.expstart + ", "
				+ Constants.Action.expstop);
	}
	
	public static int Handle(String [] args)
	{
		int res =-1;
		try
		{
			if(args.length < 3){
				usage();
				return -1;
			}
			
			String action = args[0];
			serverName = args[1];
			port = Integer.parseInt(args[2]);
			
			System.out.println("Connecting to " + serverName
					+ " on port " + port);
					
			Socket client = new Socket(serverName, port);
			myip = client.getLocalAddress().getHostAddress();
			
			
			switch(action){
				case Constants.Action.expstart:
					res = sendStartExperimentRequest(client);
					break;
				case Constants.Action.expstop:
					res = sendStopExperimentRequest(client);
					break;
				case Constants.Action.regstart:
					res = sendStartRegistrationRequest(client);
					break;
				case Constants.Action.regstop:
					res = sendStopRegistrationRequest(client);
					break;
					
				case Constants.Action.sendstatus:
					res = sendStatusRequest(client);
			}
			
		}catch(IOException e)
		{
			e.printStackTrace();
		}
		
		return res;
	}
	
	@SuppressWarnings("unchecked")
	public static int sendStatusRequest(Socket client){
		System.out.println("Into sendStatusRequest()");
		JSONObject obj = new JSONObject();
		obj.put(Constants.action, "sendStatus");
		String json = obj.toJSONString();
		
		int res = sendJSON(client, json);
		if(res == -1){
			System.out.println("sendStatusRequest failed");
			return -1;
		}
		return 0;
	}
	
	
	@SuppressWarnings("unchecked")
	public static int sendStopExperimentRequest(Socket client){
		System.out.println("Into sendStopExperimentRequest()");
		JSONObject obj = new JSONObject();
		obj.put(Constants.action, "stopExperiment");
		String json = obj.toJSONString();
		
		int res = sendJSON(client, json);
		if(res == -1){
			System.out.println("sendStopExperimentRequest failed");
			return -1;
		}
		return 0;
	}
	
	@SuppressWarnings("unchecked")
	public static int sendStartRegistrationRequest(Socket client){
		System.out.println("Into sendStartRegistrationRequest()");
		JSONObject obj = new JSONObject();
		obj.put(Constants.action, "startRegistration");
		String json = obj.toJSONString();
		
		int res = sendJSON(client, json);
		if(res == -1){
			System.out.println("sendStartRegistrationRequest failed");
			return -1;
		}
		return 0;
	}
	
	@SuppressWarnings("unchecked")
	public static int sendStopRegistrationRequest(Socket client){
		System.out.println("Into sendStartRegistrationRequest()");
		JSONObject obj = new JSONObject();
		obj.put(Constants.action, "stopRegistration");
		String json = obj.toJSONString();
		
		int res = sendJSON(client, json);
		if(res == -1){
			System.out.println("sendStopRegistrationRequest failed");
			return -1;
		}
		return 0;
	}
	
	public static int sendStartExperimentRequest(Socket client){
		System.out.println("Into sendStartExperimentRequest()");
		sendExperimentInfo(client);
		return 0;
	}
	
	public static int sendJSON(Socket client, String json){
		System.out.println("sendJSON() : Just connected to " + client.getRemoteSocketAddress() + " for sending JSON");
		try {
			DataOutputStream dout = new DataOutputStream(client.getOutputStream());
			DataInputStream din = new DataInputStream(client.getInputStream());
			
			dout.writeInt(json.length());
			dout.writeBytes(json);
			
			System.out.println("admin waiting for response code");
			int rescode = din.readInt();
			System.out.println(rescode);
			if(rescode == 404){
				System.out.println("sendJSON() :  : request rejected");
			}
			else if(rescode == 200){
				System.out.println("sendJSON() :  : request accepted");
			}
			else if(rescode == 300){
				System.out.println("sendJSON() :  : redundant request declined");
			}
			else{
				System.out.println("sendJSON() :  : Unknown Responce code ");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
		return 0;
	}
	
	
	@SuppressWarnings("unchecked")
	public static int sendExperimentInfo(Socket client){
		
		System.out.println("Just connected to " + client.getRemoteSocketAddress() + " for start experiment");
		try {
			DataOutputStream dout = new DataOutputStream(client.getOutputStream());
			DataInputStream din = new DataInputStream(client.getInputStream());
			
			JSONObject obj = new JSONObject();
			InetAddress IP = null; 
			int timeToStart = 60;

			obj.put(Constants.action, "startExperiment");
			obj.put("timetostart", "60"); //seconds
			obj.put("timeoutWindow", "10000");
			obj.put("filteringDevicesCount", "3");
			
			String json = obj.toJSONString();
			dout.writeInt(json.length());
			dout.writeBytes(json);
			
			System.out.println("admin waiting for response code");
			int rescode = din.readInt();
			System.out.println(rescode);
			if(rescode == 404){
				System.out.println("sendExperimentInfo : Start request rejected");
			}
			else if(rescode == 200){
				System.out.println("sendExperimentInfo : Start request accepted. Experiment will start in " + timeToStart);
			}
			else{
				System.out.println("sendExperimentInfo : Unknown Responce code " + timeToStart);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
}
