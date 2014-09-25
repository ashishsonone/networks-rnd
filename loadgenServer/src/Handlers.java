import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.Vector;


public class Handlers {
	
	private static Vector<DeviceValidation> filterDevices(){
		System.out.println("Filtereing Devices....");
		Vector<DeviceValidation> filteredDevices = new Vector<DeviceValidation>();
		for (Map.Entry<String, DeviceInfo> e : Main.registeredClients.entrySet()) {
			System.out.println("filterDevices: " + e.getValue().ip + " " + e.getValue().port);
		    filteredDevices.add(new DeviceValidation(e.getValue(), true));
		}
			
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
		DataInputStream din = null;
		try {
			out = client.getOutputStream();
			dout = new DataOutputStream(out);
			dout.writeInt(Constants.responseOK);	//ack stating I got the command to start experiment
		} catch (IOException e) {
			System.out.println("StartExperiment: 'new DataOutputStream(out)' Failed...");
			e.printStackTrace();
		}
		
		System.out.println("\nStarting Experiment....");
		//1. filter the devices
		
		final int expectedFilterCount = Integer.parseInt((String)jsonMap.get(Constants.noOfFilteringDevices));
		final int timeoutWindow = Integer.parseInt((String)jsonMap.get(Constants.timeoutWindow));
		int filteredCount = 0;
		Vector<DeviceValidation> devices = filterDevices();
		
		//2. send control files one by one
		//String fileName = "/home/sanchit/Desktop/events.txt";
		for(DeviceValidation d : devices){
			try {
				System.out.println("StartExperiment: while sending control files to devices...");
				System.out.println("StartExperiment: IP: " + d.device.ip + 
						" and Port" + d.device.port);
				Socket s = new Socket(d.device.ip, d.device.port);
				s.setSoTimeout(timeoutWindow);
				out = s.getOutputStream();
				dout = new DataOutputStream(out);
				String jsonString = Utils.getControlFileJson();
				dout.writeInt(jsonString.length());
				dout.writeBytes(jsonString);
				
				String events = EventGen.generateEvents(1);
				System.out.println(events);
				
				dout.writeInt(events.length());
				dout.writeBytes(events);
				//Utils.SendFile(dout, fileName);
				
				din = new DataInputStream(s.getInputStream());
				int response = din.readInt();
				if(response == Constants.responseOK){
					filteredCount++;
				}
				s.close();
				if(filteredCount >= expectedFilterCount){
					break;
				}
				
			} catch (InterruptedIOException ie){
				System.out.println("StartExperiment: Timeout occured for sending control file to device with ip: "
											+ d.device.ip + " and Port: " + d.device.port);
			} catch (IOException e) {
				System.out.println("StartExperiment: 'new DataOutputStream(out)' or " +
									"'DataInputStream(s.getInputStream())' Failed...");
				//e.printStackTrace();
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
				System.out.println(jsonMap.get(Constants.Device.ip) + " & " + jsonMap.get(Constants.Device.port));
				
				DeviceInfo d = new DeviceInfo();
				d.ip = (String)jsonMap.get(Constants.Device.ip);
				d.port = Integer.parseInt((String)jsonMap.get(Constants.Device.port));
				d.macAddress = (String)jsonMap.get(Constants.Device.macAddress);
				d.osVersion = (String)jsonMap.get(Constants.Device.osVersion);
				d.wifiVersion = (String)jsonMap.get(Constants.Device.wifiVersion);
				d.processorSpeed = Double.parseDouble((String)jsonMap.get(Constants.Device.processorSpeed));
				d.numberOfCores = Integer.parseInt((String)jsonMap.get(Constants.Device.numberOfCores));
				d.storageSpace = Integer.parseInt((String)jsonMap.get(Constants.Device.storageSpace));
				d.memory = Integer.parseInt((String)jsonMap.get(Constants.Device.memory));
				d.wifiSignalStrength = Double.parseDouble((String)jsonMap.get(Constants.Device.wifiSignalStrength));
				d.packetCaptureAppUsed = Boolean.parseBoolean((String)jsonMap.get(Constants.Device.packetCaptureAppUsed));
				
				d.print();
				Main.registeredClients.put(d.ip + Integer.toString(d.port), d);
				
				dout.writeInt(Constants.responseOK);
			}
			else{
				System.out.println(jsonMap.get(Constants.Device.ip) + " " + jsonMap.get(Constants.Device.port));
				dout.writeInt(Constants.responseError);
			}
		} catch (IOException e) {
			System.out.println("RegisterClient: 'DataOutputStream(client.getOutputStream())' Failed...");
			e.printStackTrace();
		}
	}
	
	public static void MasterHandler(Socket client){
		DataInputStream dis = null;
		String data = "";
		Map<String, String> jsonMap;
		try {
			dis = new DataInputStream(client.getInputStream());
			
			int lengthJson = dis.readInt();
			
			for(int i=0;i<lengthJson;++i){
				data += (char)dis.readByte();
			}
			
		} catch (IOException e) {
			System.out.println("MasterHandler: 'client.getInputStream()' Failed...");
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
			System.out.println("MasterHandler: 'client.close()' Failed...");
			e.printStackTrace();
		}
		
	}
	
	
}
