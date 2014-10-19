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
import java.util.Collections;
import java.util.Random;

public class Handlers {
	
	private static void RandomFilterDevices(){
		System.out.println("Filtereing Devices....");
		
		for (Map.Entry<String, DeviceInfo> e : Main.registeredClients.entrySet()) {
			System.out.println("filterDevices: " + e.getValue().ip + " " + e.getValue().port);
		    Main.filteredDevices.add(new DeviceInfo(e.getValue()));
		}
		Collections.shuffle(Main.filteredDevices, new Random(System.nanoTime()));
			
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
		
		RandomFilterDevices();
		
		DataOutputStream dout = null;
		String jsonString = Utils.getControlFileJson();
		String events = EventGen.generateEvents(Main.currentExperiment);
		
		System.out.println(events);
		if(events.equals("error")) return -1;
		
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
				
				
				dout.writeInt(events.length());
				dout.writeBytes(events);
				
				DataInputStream din = new DataInputStream(s.getInputStream());
				int response = din.readInt();
				if(response == Constants.responseOK){
					int status = Utils.addExperimentDetails(Main.currentExperiment, d, false);
					Main.actualFilteredDevices.add(d);
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
		
		for(DeviceInfo d : Main.actualFilteredDevices){
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
		Main.actualFilteredDevices.clear();
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

}
