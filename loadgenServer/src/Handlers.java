import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.Vector;


public class Handlers {
	
	private static Vector<DeviceInfo> filterDevices(){
		System.out.println("Filtereing Devices....");
		
		Vector<DeviceInfo> filteredDevices = new Vector<DeviceInfo>();
		
		int i=0;
			for (Map.Entry<String, DeviceInfo> e : Main.registeredClients.entrySet()) {
			    filteredDevices.add(e.getValue());
			    i++;
			    if(i==3){
			    	break;
			    }
			}
		
		System.out.println("Number of filtered devices: " + filteredDevices.size());	
		
		return filteredDevices;
	}
	
	public static void StartRegistration(Socket client, Map<String,String> jsonMap){
		Main.registrationWindowOpen = true;
	}
	public static void StopRegistration(Socket client, Map<String,String> jsonMap){
		Main.registrationWindowOpen = false;
	}
	
	public static void StartExperiment(Socket client, Map<String,String> jsonMap){
		
		OutputStream out = null;
		DataOutputStream dout = null;
		try {
			out = client.getOutputStream();
			dout = new DataOutputStream(out);
			dout.writeInt(200);	//ack stating I got the command to start experiment
		} catch (IOException e) {
			System.out.println("StartExperiment: 'new DataOutputStream(out)' Failed...");
			e.printStackTrace();
		}
		
		System.out.println("\nStarting Experiment....");
		//1. filter the devices
		Vector<DeviceInfo> devices = filterDevices();
		
		//2. send control files one by one
		String fileName = "/home/sanchit/Desktop/events.txt";
		for(DeviceInfo d : devices){
			try {
				Socket s = new Socket(d.ip, d.port);
				out = s.getOutputStream();
				dout = new DataOutputStream(out);
				String jsonString = Utils.getControlFileJson();
				dout.writeInt(jsonString.length());
				dout.writeBytes(jsonString);
				Utils.SendFile(dout, fileName);
				s.close();
			} catch (IOException e) {
				System.out.println("StartExperiment: 'new DataOutputStream(out)' Failed...");
				e.printStackTrace();
			}
		}
	}
	
	public static void StopExperiment(Socket client, Map<String,String> jsonMap){
		////!!
	}
	
	public static void ReceiveLogFile(Socket client, Map<String,String> jsonMap){
		//1. receive log file
		System.out.println("\nReceiving Log File....");
		String fileName = "/home/sanchit/Desktop/experimentLogs/" + client.getPort();
		
		try {
			DataInputStream dis = new DataInputStream(client.getInputStream());
			Utils.ReceiveFile(dis, client.getReceiveBufferSize(), fileName);
			System.out.println("Log File Received....");
		} catch (IOException e) {
			System.out.println("Error in Receiving Log File....");
			e.printStackTrace();
		}
	}
	
	public static void RegisterClient(Socket client, Map<String,String> jsonMap){
		System.out.println("\nRegistering Client....");
		
		DataOutputStream dout = null;
		
		try {
			dout = new DataOutputStream(client.getOutputStream());
			if(Main.registrationWindowOpen){
				System.out.print("Client Registered with ip and port: ");
				System.out.println(jsonMap.get(Constants.ip) + " & " + jsonMap.get(Constants.port));
				
				DeviceInfo d = new DeviceInfo();
				d.ip = (String)jsonMap.get(Constants.ip);
				d.port = Integer.parseInt((String)jsonMap.get(Constants.port));
				d.macAddress = (String)jsonMap.get(Constants.macAddress);
				d.osVersion = (String)jsonMap.get(Constants.osVersion);
				d.wifiVersion = (String)jsonMap.get(Constants.wifiVersion);
				d.processorSpeed = Double.parseDouble((String)jsonMap.get(Constants.processorSpeed));
				d.numberOfCores = Integer.parseInt((String)jsonMap.get(Constants.numberOfCores));
				d.storageSpace = Integer.parseInt((String)jsonMap.get(Constants.storageSpace));
				d.memory = Integer.parseInt((String)jsonMap.get(Constants.memory));
				d.wifiSignalStrength = Double.parseDouble((String)jsonMap.get(Constants.wifiSignalStrength));
				d.packetCaptureAppUsed = Boolean.parseBoolean((String)jsonMap.get(Constants.packetCaptureAppUsed));
				
				d.print();
				Main.registeredClients.put(d.ip + Integer.toString(d.port), d);
				
				dout.writeInt(200);
			}
			else{
				System.out.println(jsonMap.get(Constants.ip) + " " + jsonMap.get(Constants.port));
				dout.writeInt(404);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void MasterHandler(Socket client){
		InputStream is=null;
		DataInputStream dis = null;
		String data = "";
		Map<String, String> jsonMap;
		try {
			is = client.getInputStream();
			dis = new DataInputStream(is);
			
			int lengthJson = dis.readInt();
			
			for(int i=0;i<lengthJson;++i){
				data += (char)dis.readByte();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		jsonMap = Utils.ParseJson(data);
		
		String action = (String)jsonMap.get(Constants.action);
		
		switch(action){
			case Constants.Action.startRegistration:
				StartRegistration(client, jsonMap);
				break;
				
			case Constants.Action.stopRegistration:
				StopRegistration(client, jsonMap);
				break;
				
			case Constants.Action.registerClient:
				RegisterClient(client, jsonMap);
				break;
				
			case Constants.Action.startExperiment:
				StartExperiment(client, jsonMap);
				break;
				
			case Constants.Action.receiveLogFile:
				ReceiveLogFile(client, jsonMap);
				break;
				
		}
		
		try {
			client.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
}
