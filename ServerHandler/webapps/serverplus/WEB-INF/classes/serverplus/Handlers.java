package serverplus;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.Socket;
import java.util.Map;
import java.util.Vector;
import java.util.Iterator;

public class Handlers {
	
	private static void FilterDevices(){
		System.out.println("Filtereing Devices....");
		
		for (Map.Entry<String, DeviceInfo> e : Main.registeredClients.entrySet()) {
			System.out.println("filterDevices: " + e.getValue().ip + " " + e.getValue().port);
		    Main.filteredDevices.add(new DeviceInfo(e.getValue()));
		}
			
	}
	
	public static int StartRegistration(){
		Main.registrationWindowOpen = true;
		System.out.println("StartRegistration: Registrations are open. Now devices can register....");
		return 0;
	}
	
	public static int StopRegistration(){
		Main.registrationWindowOpen = false;
		System.out.println("StopRegistration: Registration is now closed....");
		return 0;
	}
	
	//returns max id enterd in database
	public static int StartExperiment(Experiment e){
		
		Main.currentExperiment=e.ID;
		Main.experimentRunning=true;
		if(Main.currentExperiment<0){
			System.out.print("Kuch th Panga hai");
			return -1;
		}
		
		System.out.println("\nStarting Experiment....");

		final int expectedFilterCount = 5;		//need to get from web
		
		
		final int timeoutWindow = Constants.sendControlFileTimeoutWindow;	//10 seconds
		int filteredCount = 0;
		
		FilterDevices();
		
		DataOutputStream dout = null;
		String jsonString = Utils.getControlFileJson();
		//String events = EventGen.generateEvents(1);
		String events = EventGen.generateEvents(Main.currentExperiment);
		
		for(DeviceInfo d : Main.filteredDevices){
			try {
				System.out.println("StartExperiment: while sending control files to devices...");
				System.out.println("StartExperiment: IP: " + d.ip + 
										" and Port" + d.port);
				Socket s = new Socket(d.ip, d.port);
				s.setSoTimeout(timeoutWindow);
				dout = new DataOutputStream(s.getOutputStream());
				dout.writeInt(jsonString.length());
				dout.writeBytes(jsonString);
				
				events = Integer.toString(Main.currentExperiment) + "\n" + events;
				System.out.println(events);
				
				
				dout.writeInt(events.length());
				dout.writeBytes(events);
				
				DataInputStream din = new DataInputStream(s.getInputStream());
				int response = din.readInt();
				if(response == Constants.responseOK){
					int status = Utils.addExperimentDetails(Main.currentExperiment, d, false);
					if(status<0){
						System.out.println("StartExperiment: Error occured during inserting experiment details for device: " 
												+ d.ip + ", " + d.macAddress);
					}
					else{
						filteredCount++;
					}
				}
				s.close();
				if(filteredCount >= expectedFilterCount){
					break;
				}
				
			} catch (InterruptedIOException ie){
				System.out.println("StartExperiment: Timeout occured for sending control file to device with ip: "
											+ d.ip + " and Port: " + d.port);
			} catch (IOException ioe) {
				System.out.println("StartExperiment: 'new DataOutputStream(out)' or " +
									"'DataInputStream(s.getInputStream())' Failed...");
			}
		}
		return Main.currentExperiment;
	}
	
	public static int StopExperiment(){
		Main.experimentRunning = false;
		Main.currentExperiment = -1;
		System.out.println("Experiment Stopped...");

		final int timeoutWindow = Constants.sendStopSignalTimeoutWindow;	//10 seconds
		
		System.out.println("StopExperiment: while sending stop signal to devices...");
		
		for(DeviceInfo d : Main.filteredDevices){
			try {
				System.out.println("StopExperiment: IP: " + d.ip + " and Port" + d.port);
				Socket s = new Socket(d.ip, d.port);
				s.setSoTimeout(timeoutWindow);
				DataOutputStream dout = new DataOutputStream(s.getOutputStream());
				
				String jsonString = Utils.getStopSignalJson();
				System.out.println(jsonString);
				dout.writeInt(jsonString.length());
				dout.writeBytes(jsonString);
				
				DataInputStream din = new DataInputStream(s.getInputStream());
				int response = din.readInt();
				if(response == Constants.responseOK){
					System.out.println("StopExperiment: device with ip: " + d.ip + " and Port: " + d.port 
											+ " stopped experiment");
				}
				s.close();
				
			} catch (InterruptedIOException ie){
				System.out.println("StopExperiment: Timeout occured for sending stop Signal to device with ip: "
											+ d.ip + " and Port: " + d.port);
			} catch (IOException ioe) {
				System.out.println("StopExperiment: 'new DataOutputStream(out)' or " +
									"'DataInputStream(s.getInputStream())' Failed...");
			}	
		}
		
		//clearing all filtered devices;
		Main.filteredDevices.clear();
		System.out.println("StopExperiment: Filtered devices....");
		return 0;
		
	}
	
	public static int RegisterClient(DeviceInfo d){
		System.out.println("\nRegistering Client....");
		Main.registeredClients.put(d.macAddress, d);
		System.out.println("Client Registered....");
		return 0;
	}
	
	public static void ClearRegistrations(){
		Main.registeredClients.clear();
	}
	
	/*
	public static void ReceiveLogFile(Socket client, Map<String,String> jsonMap){

		System.out.println("\nReceiving Log File....");
		
		int expID = Integer.parseInt((String)jsonMap.get(Constants.expID));
		String macAddress = (String)jsonMap.get(Constants.Device.macAddress);
		String dir = Constants.mainExpLogsDir + expID + "/";
		String fileName = dir + macAddress;
		
		File theDir = new File(dir);

		try {
			if (!theDir.exists()) {
				theDir.mkdir();
			}
			DataInputStream dis = new DataInputStream(client.getInputStream());
			Utils.ReceiveFile(dis, client.getReceiveBufferSize(), fileName);
			
			int status = Utils.updateFileReceivedField(expID, macAddress, true);
			if(status < 0){
				Utils.SendResponse(client, Constants.responseError);
				System.out.println("ReceiveLogFile: Error while updating Database....");
			}
			else{
				Utils.SendResponse(client, Constants.responseOK);
				System.out.println("ReceiveLogFile: Log File Received....");
			}
			
		} catch (IOException e) {
			System.out.println("Error in Receiving Log File....");
			e.printStackTrace();
		} catch(SecurityException se){
			System.out.println("Error in Creating Directory" + dir +"....");
			se.printStackTrace();
	    } 
	}

	public static void ReceiveEventFile(Socket client, Map<String,String> jsonMap){

		System.out.println("\nReceiving Event File....");
		
		int expID = Integer.parseInt((String)jsonMap.get(Constants.expID));
		String macAddress = (String)jsonMap.get(Constants.Device.macAddress);
		String dir = Constants.mainExpLogsDir + expID + "/";
		String fileName = dir + macAddress;
		
		File theDir = new File(dir);

		try {
			if (!theDir.exists()) {
				theDir.mkdir();
			}
			DataInputStream dis = new DataInputStream(client.getInputStream());
			Utils.ReceiveFile(dis, client.getReceiveBufferSize(), fileName);
			
			int status = Utils.updateFileReceivedField(expID, macAddress, true);
			if(status < 0){
				Utils.SendResponse(client, Constants.responseError);
				System.out.println("ReceiveLogFile: Error while updating Database....");
			}
			else{
				Utils.SendResponse(client, Constants.responseOK);
				System.out.println("ReceiveLogFile: Log File Received....");
			}
			
		} catch (IOException e) {
			System.out.println("Error in Receiving Log File....");
			e.printStackTrace();
		} catch(SecurityException se){
			System.out.println("Error in Creating Directory" + dir +"....");
			se.printStackTrace();
	    } 
	}

	
	
	
	//if not registration process started: send Status
	//if registration process started: send status and the devices which are registered
	//if experiment started: send status, registered devices and devices which are filtered
	
	public static void SendStatus(Socket client, Map<String,String> jsonMap){
		System.out.println("\nSending Status Client....");
		int msg=Constants.responseOK;
		
		DataOutputStream dout = null;
		try {
			dout = new DataOutputStream(client.getOutputStream());
			//String json = Utils.getStatusResponse(status, msg);
			//dout.writeInt(json.length());
			dout.writeInt(msg);
			//dout.writeBytes(json);
			dout.close();
	
		} catch (IOException e) {
			System.out.println("SendStatus: 'DataOutputStream(client.getOutputStream())' Failed...");
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
			
			case Constants.Action.stopExperiment:
				StopExperiment(client, jsonMap);
				break;
			
			case Constants.Action.receiveLogFile:
				ReceiveLogFile(client, jsonMap);
				break;
			
			case Constants.Action.receiveEventFile:
				ReceiveEventFile(client, jsonMap);
				break;
				
			case Constants.sendStatus:
				SendStatus(client, jsonMap);
				
		}
		
		try {
			client.close();
		} catch (IOException e) {
			System.out.println("MasterHandler: 'client.close()' Failed...");
			e.printStackTrace();
		}
		
	}
	
	*/
	
	
}
