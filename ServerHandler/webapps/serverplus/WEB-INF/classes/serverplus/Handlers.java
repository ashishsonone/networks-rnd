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
import java.util.List;
import java.util.Calendar;


/**
 *
 * @author sanchitgarg
 * This class holds all the handler functionalities the clients (Web or android) interacts with server
 */
public class Handlers {

	/**
	* This method is called when experimenter selects random filteration of devices
	* with 'number' as number of device required
	*/
	private static void RandomFilterDevices(Session session, int number){
		System.out.println("Filtereing Devices....");
		Vector<DeviceInfo> devices = new Vector<DeviceInfo>();
		for (Map.Entry<String, DeviceInfo> e : session.registeredClients.entrySet()) {
			System.out.println("filterDevices: " + e.getValue().ip + " " + e.getValue().port);
		    devices.add(new DeviceInfo(e.getValue()));
		}
		Collections.shuffle(devices, new Random(System.nanoTime()));
		int count=0;
		for(DeviceInfo d : devices){
			if(count>=number) break;
			session.filteredDevices.add(d);
			count++;
		}			
	}


	/**
	* This method is called when android device presses exit button. This method removes the device from
	* registered clients, filtered and actual filtered list in the session
	*/
	public static int ClientExit(String macAddress, Session session){
		System.out.println("ClientExit: " + "macAddress is " + macAddress);
		if(macAddress==null || macAddress.equals("")){
			return -1;	
		} 

		DeviceInfo d = session.registeredClients.remove(macAddress);
		if(d==null) return 0;

		System.out.println("ClientExit: " + "macAddress " + macAddress + " deleted from session.registeredClients");

		Iterator<DeviceInfo> failSafeIterator = session.filteredDevices.iterator();
		while(failSafeIterator.hasNext()){
			d = failSafeIterator.next();
			if(d.macAddress.equals(macAddress)){
				session.filteredDevices.remove(d);
				break;
			}
		}

		if(! failSafeIterator.hasNext()) return 1;
		
		System.out.println("ClientExit: " + "macAddress " + macAddress + " deleted from session.filteredDevices");

		failSafeIterator = session.actualFilteredDevices.iterator();
		while(failSafeIterator.hasNext()){
			d = failSafeIterator.next();
			if(d.macAddress.equals(macAddress)){
				session.actualFilteredDevices.remove(d);
				break;
			}
		}
		if(! failSafeIterator.hasNext()) return 2;
		System.out.println("ClientExit: " + "macAddress " + macAddress + " deleted from Main.actualFilteredDevices");
		return 3;
	}
	
	/**
	* The method opens the registration window for the session. Now devices can register
	*/
	public static int StartRegistration(Session session){
		session.registrationWindowOpen = true;
		System.out.println("StartRegistration: Registrations are open. Now devices can register....");
		return 0;
	}
	
	/**
	* The method closes the registration window for the session. Now devices cannot register
	*/
	public static int StopRegistration(Session session){
		session.registrationWindowOpen = false;
		System.out.println("StopRegistration: Registration is now closed....");
		return 0;
	}


	/**
	* Method is called if experiment demands selecting 'number' number of devices randomly.
	* It first Filter the devices and then calls StartExperiment
	*/
	public static int StartRandomExperiment(Experiment e, Session session, int number){
		RandomFilterDevices(session,number);
		int expectedFilterCount = number;
		int result = StartExperiment(e, session, expectedFilterCount);
		System.out.println("StartRandomExperiment: " +"count: "+number+" result: "+result);
		return result;
	}


	/**
	* Method is called if experiment demands selecting devices manually
	*/
	public static int StartManualExperiment(Experiment e, Session session, Vector<String> devices){
		for (String macAddress : devices) {
			DeviceInfo d = session.registeredClients.get(macAddress);
			if(d!=null) session.filteredDevices.add(d);
		}
		int expectedFilterCount = devices.size();
		int result = StartExperiment(e, session, expectedFilterCount);
		System.out.println("StartManualExperiment: " +"count: "+devices.size()+" result: "+result);
		return result;
	}
	
	//returns max id enterd in database
	/**
	* This method is responsible for actual starting the experiment. 'expectedFilterCount' is an 
	* argument which is required as how many devices from list 'session.filteredDevices' experiment 
	* is to be started with. It returns the id of the experiment started. If returns -1, there is 
	* problem with starting experiment.
	* The method sends control file to atmost 'expectedFilterCount' number of devices. On receiving 
	* 200 OK message from the device, device details are added to the experiment.
	*/
	public static int StartExperiment(Experiment e, Session session, int expectedFilterCount){
		System.out.println("\n"+"StartExperiment: "+"Starting Experiment " +  e.Name + "....");
		e.InitializeStartTime();

		final int timeoutWindow = Constants.sendControlFileTimeoutWindow;
		int filteredCount = 0;
		
		DataOutputStream dout = null;
		String jsonString = Utils.getControlFileJson();
		String events = EventGen.generateEvents(e.ID);
		
		System.out.println(events);
		if(events.startsWith("ERROR")) return -1;
		//if(events.equals("error")) return -1;
		
		for(DeviceInfo d : session.filteredDevices){
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
					session.actualFilteredDevices.add(d);
					filteredCount++;
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
		
		System.out.println("Total actual filtered count is " + filteredCount);
		
		//if filteredDevices = 0 ,then no control files have been sent so addExperiment.jsp should show error
		if(filteredCount==0) {
			StopExperiment(session);
			Utils.deleteExperiment(e.ID);
			return -1;
		}
		
	
		for(DeviceInfo d : session.actualFilteredDevices){
			int status = Utils.addExperimentDetails(e.ID, d, false);
			if(status<0){
				System.out.println("StartExperiment: Error occured during inserting experiment details for device: " 
										+ d.ip + ", " + d.macAddress);
				StopExperiment(session);
				Utils.deleteExperiment(e.ID);
				return -1;
			}
		}
		
		session.currentExperiment=e.ID;
		session.experimentRunning=true;	
		//Main.RunningExperimentMap.put(session.currentExperiment,new Boolean(true));
		Main.RunningExperimentMap.put(session.currentExperiment,e);
		return session.currentExperiment;
	}
	

	/**
	* This method is called when web-client presses Stop Experiment button or session expires.
	* On stopping experiment, the devices which are in 'session.actualFilteredDevices' list are
	* sent stop experiment signal and then 'session.filteredDevices' and 'session.actualFilteredDevices' 
	* lists are emptied.
	*/
	public static int StopExperiment(Session session){

		Main.RunningExperimentMap.remove(session.currentExperiment);
		System.out.println("Experiment Stopped...");
		session.experimentRunning = false;
		session.currentExperiment = -1;

		final int timeoutWindow = Constants.sendStopSignalTimeoutWindow;	//10 seconds
		
		System.out.println("StopExperiment: while sending stop signal to devices...");
		
		for(DeviceInfo d : session.actualFilteredDevices){
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
				ioe.printStackTrace();
				System.out.println("StopExperiment: 'new DataOutputStream(out)' or " +
									"'DataInputStream(s.getInputStream())' Failed...");
			}	
		}
		
		//clearing all filtered devices;
		session.filteredDevices.clear();
		session.actualFilteredDevices.clear();
		System.out.println("StopExperiment: Filtered devices....");
		return 0;
		
	}
	
	
	/**
	* This method adds device to the registered devices list
	*/
	public static int RegisterClient(DeviceInfo d, Session session){
		System.out.println("\nRegistering Client....");
		
		/*
		
		try {
			System.out.println("RegisterClient: IP: " + d.ip + " and Port" + d.port);
			Socket s = new Socket(d.ip, d.port);
			s.setSoTimeout(Constants.sendSessionDurationTimeoutWindow);
			DataOutputStream dout = new DataOutputStream(s.getOutputStream());
			
			String jsonString = Utils.getSessionDurationJson(session.duration);
			System.out.println(jsonString);
			dout.writeInt(jsonString.length());
			dout.writeBytes(jsonString);

			s.close();
			
		} catch (InterruptedIOException ie){
			System.out.println("RegisterClient: Timeout occured for sending session duration to device with ip: "
										+ d.ip + " and Port: " + d.port);
		} catch (IOException ioe) {
			System.out.println("RegisterClient: 'new DataOutputStream(out)' Failed...");
		}	

		*/
		
		
		(session.registeredClients).put(d.macAddress, d);
		
		System.out.println("Client Registered....");
		return 0;
	}
	

	/**
	* This method clears the registered devices and send signal to android clients about the same
	*/
	public static void ClearRegistrations(Session session){
		String jsonString = Utils.getClearRegistrationJson();
		System.out.println("ClearRegistrations: " + "jsonString= " + jsonString);
		
		final int timeoutWindow = Constants.clearRegistrationTimeoutWindow;	//10 seconds

		for (Map.Entry<String, DeviceInfo> e : (session.registeredClients).entrySet()) {
			DeviceInfo d = e.getValue();
			try {
				Socket s = new Socket(d.ip, d.port);
				s.setSoTimeout(timeoutWindow);
				DataOutputStream dout = new DataOutputStream(s.getOutputStream());
				dout = new DataOutputStream(s.getOutputStream());
				dout.writeInt(jsonString.length());
				dout.writeBytes(jsonString);	
				s.close();
			} catch (InterruptedIOException ie){
				System.out.println("ClearRegistrations: Timeout occured for sending stop Signal to device with ip: "
											+ d.ip + " and Port: " + d.port);
			} catch (IOException ioe) {
				System.out.println("ClearRegistrations: 'new DataOutputStream(out)' or " +
									"'DataInputStream(s.getInputStream())' Failed...");
			}	
		}

		session.registeredClients.clear();
		session.filteredDevices.clear();
		session.actualFilteredDevices.clear();
	}

	
	/**
	* This method creates session and returns id of the session created
	*/
	public static int CreateSession(String sessionName, String description, String username){
		if(Main.freeSessions.size() == 0){
			System.out.println("CreateSession: " + "Free Sessions list size is zero");
			return Constants.NOTOK;	
		} 

		//Integer sessionID = Main.freeSessions.remove(0);
		Session s = new Session(sessionName, description, username);

		s.sessionID = Utils.addSession(s);

		Main.SessionMap.put(s.sessionID,s);
		System.out.println("CreateSession: " + "Free Sessions list size is: " + Main.freeSessions.size());


		for (Map.Entry<Integer, Session> e : (Main.SessionMap).entrySet()) {
			Session ss = e.getValue();
			System.out.println(ss.sessionID + ss.name + ss.user);
		}
//		System.out.println("--------------"+s.sessionID + s.name + s.user + s.duration + " " + s.cal);
		return s.sessionID;
	}

	
	/**
	* This method deletes session whose id is session
	* If the experiment is running, it doesn't delte the experiment and returns notOK
	*/
	public static int DeleteSession(Integer session){
		//! to variables cleaning first
		Session s = (Main.SessionMap).get(session);
		if(s!=null && !s.experimentRunning){
			s= (Main.SessionMap).remove(session);
			Main.freeSessions.add(session);
			return Constants.OK;
		}
		else{
			return Constants.NOTOK;
		}
	}
	
	/**
	* This method validates session whose id in string is ss
	* If session duration is over it deletes the session. Also if the experiment is running, it first
	* stop the experiment and then delete the session. 
	*/
	public static boolean SessionValidation(String ss){
		Calendar cal = Calendar.getInstance();
		if(ss==null || ss.equals("")) return false;
		Integer sid = new Integer(Integer.parseInt(ss));
		Session session = Main.SessionMap.get(sid);
		if(session==null) return false;
		
		long cur = cal.getTimeInMillis();
		long prev = (session.cal).getTimeInMillis();
		long hrs = (cur - prev)/(3600*1000);
		//long hrs = (cur - prev)/(60*1000);

		if(hrs<session.duration) return true;
		
		if(session.experimentRunning){
			System.out.println("SessionValidation: Calling Stop Experiment");
			StopExperiment(session);
		}
			
		Main.SessionMap.remove(sid);
		Main.freeSessions.add(sid);
		return false;
	}

}
