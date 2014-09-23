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


public class Main {
	static ServerSocket listen = null;
	static String myip = "";
	static ExecutorService executor = Executors.newCachedThreadPool();
	static boolean experimentOn = true;
	
	//static String serverName = "10.105.42.237";
	static String serverName = "192.168.150.10";
	static int port = 22222;

	public static void main(String [] args)
	{
		try
		{
			System.out.println("Connecting to " + serverName
					+ " on port " + port);
			Socket client = new Socket(serverName, port);
			myip = client.getLocalAddress().getHostAddress();
			
			if(args[0].compareTo("r") == 0){
				sendRegistrationRequest(client);
			}
			else if (args[0].compareTo("s") == 0){
				sendStartExperimentRequest(client);
			}
			
		}catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static int sendStartExperimentRequest(Socket client){
		System.out.println("Into sendStartExperimentRequest()");
		sendExperimentInfo(client);
		return 0;
	}
	
	public static int sendRegistrationRequest(Socket client){
		try {
			System.out.println("Into sendRegistrationRequest()");
			listen = new ServerSocket(0);

			listen.setSoTimeout(0);
			
			sendDeviceInfo(client);
	
			client.close();
	
			while(experimentOn){//listen as a server
				System.out.println("Waiting for connection from server");
				final Socket temp = listen.accept(); 
				System.out.println("accepted: will now get config file");
				Runnable r = new Runnable() {
					public void run() {
						AlarmThread.eventRunner(temp);
					}
				};
				
				executor.submit(r);
			}
			System.out.println("Registration request rejected");
			} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
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
	
	public static int sendDeviceInfo(Socket client){
		System.out.println("Just connected to "
				+ client.getRemoteSocketAddress());
		OutputStream outToServer;
		try {
			outToServer = client.getOutputStream();
			DataOutputStream dout =
					new DataOutputStream(outToServer);
			DataInputStream din =
					new DataInputStream(client.getInputStream());

			//String json = "HelloWorld from " + client.getLocalSocketAddress();
			String json = Utils.getMyDetailsJson(listen, myip);

			Utils.tryParse(json);

			System.out.println(json);
			dout.writeInt(json.length());

			dout.writeBytes(json);
			
			System.out.println("waiting for response code");
			int rescode = din.readInt();
			System.out.println(rescode);
			if(rescode == 404){
				experimentOn = false;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return 0;
	}


}
