package serverplus;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Vector;
import java.util.Iterator;
import java.util.Collections;
import java.util.Random;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.io.FileUtils;

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
		int filteredCount = 0;
		
		String jsonString = Utils.getControlFileJson();
		String events = EventGen.generateEvents(e.ID);
		
		System.out.println(events);
		if(events.startsWith("ERROR")) return -1;
		
		session.startExpTCounter = session.filteredDevices.size();
		System.out.println("StartExperiment: " + "size of filterDevices = " + session.startExpTCounter);

		for(DeviceInfo d : session.filteredDevices){
			if(filteredCount >= expectedFilterCount){
				break;
			}
			Thread multicast = new Thread(new Multicast(d,session,0,jsonString,events,e.ID));
			multicast.start();
			filteredCount++;
		}
		
		System.out.println("Total actual filtered count is " + filteredCount);
		
		session.currentExperiment=e.ID;
		session.experimentRunning=true;	
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
		

		String jsonString = Utils.getStopSignalJson();
		System.out.println(jsonString);


		session.stopExpTCounter = session.actualFilteredDevices.size();


		for(DeviceInfo d : session.actualFilteredDevices){
			Thread multicast = new Thread(new Multicast(d,session,1,jsonString));
			multicast.start();
		}
		
		//clearing all filtered devices;
		session.filteredDevices.clear();
		session.actualFilteredDevices.clear();
		return 0;
		
	}
	
	
	/**
	* This method adds device to the registered devices list
	*/
	public static int RegisterClient(DeviceInfo d, Session session){
		System.out.println("\nRegistering Client....");
		session.registeredClients.put(d.macAddress, d);
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

		
		session.clearRegTCounter = session.registeredClients.size();

		for (Map.Entry<String, DeviceInfo> e : (session.registeredClients).entrySet()){
			Thread multicast = new Thread(new Multicast(e.getValue(),session,2,jsonString));
			multicast.start();
		}

		session.registeredClients.clear();
		session.filteredDevices.clear();
		session.actualFilteredDevices.clear();
	}

	public static void RefreshRegistrations(Session session){
		ConcurrentHashMap<String, DeviceInfo> registeredClients = new ConcurrentHashMap<String, DeviceInfo>();
		String jsonString = Utils.getRefreshRegistrationJson();
		session.tempRegisteredClients = new ConcurrentHashMap<String, DeviceInfo>();
		session.refreshTCounter = session.registeredClients.size();

		for (Map.Entry<String, DeviceInfo> e : (session.registeredClients).entrySet()){
			Thread multicast = new Thread(new Multicast(e.getValue(),session,3,jsonString));
			multicast.start();
		}
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
	public static int DeleteSession(String sid){
		//! to variables cleaning first
		if(sid==null || sid==""){
			System.out.println("DeleteSession: sid is null or sid is empty");
			return 0;
		}

		int ssid = Integer.parseInt(sid);
		Integer _sid = new Integer(ssid);
		Session s = (Main.SessionMap).get(_sid);
		DBManager db = new DBManager();

		System.out.println("Yaha1");

		if(s!=null && s.currentExperiment>=0){
			System.out.println("DeleteSession: experiment number "+ s.currentExperiment +" is running");
			return 1;
		}

		System.out.println("Yaha2");

		System.out.println("DeleteSession: deleting session "+ sid);

		if(Main.SessionMap.contains(_sid)){
			s= (Main.SessionMap).remove(_sid);
		}

		List<Integer> explist = db.getExpIDs(sid);

		System.out.println("DeleteSession: number of experiments in session "+sid+" are " + explist.size());

		for(Integer expid : explist){
			try{
				FileUtils.deleteDirectory(new File(Constants.mainExpLogsDir + expid));
			}
			catch(IOException e){
				System.out.println(e);
			}
		}

		System.out.println("Yaha3");

		int res = db.deleteSession(ssid);
		if(res>0) {System.out.println("DeleteSession: from database success");return 2; }
		else{System.out.println("DeleteSession: from database fail");return 3; }

	}
	
	/**
	* This method validates session whose id in string is ss
	* If session duration is over it deletes the session. Also if the experiment is running, it first
	* stop the experiment and then delete the session. 
	* 
	* Now the sessions are persistent hence is always valid
	*/
	public static boolean SessionValidation(String ss){
		return true;
	}

}
