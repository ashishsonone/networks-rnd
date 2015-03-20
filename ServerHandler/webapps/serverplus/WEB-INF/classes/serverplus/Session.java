package serverplus;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Calendar;
import lombok.Getter;
import lombok.Setter;


/**
 *
 * @author sanchitgarg
 * This class is used for holding information for a session.
 * 
 */
public class Session {
	@Getter @Setter Integer sessionID = 0;		//ID of the session
	@Getter @Setter String name;				//name of session
	@Getter @Setter String user;				//username of user holding that session

	@Getter @Setter String description;			//the description of session

	@Getter @Setter boolean serverOn = true;	//no use of the variable

	//true if any experiment in the session is running, false otherwise
	@Getter @Setter boolean experimentRunning = false;

	//true if registrations are open for the session, false otherwise
	@Getter @Setter boolean registrationWindowOpen = false;

	//= to the ID of current running experiment in the session, -1 otherwise
	@Getter @Setter int currentExperiment = -1;

	//Current Experiment
	@Getter @Setter Experiment curExp;


	//HashMap which stores registered android-clients
	//<key, value> = <macaddress of deivce, DeviceInfo>
	@Getter @Setter ConcurrentHashMap<String, DeviceInfo> registeredClients = new ConcurrentHashMap<String, DeviceInfo>();
	
	//List which stores filtered android-clients.
	@Getter @Setter CopyOnWriteArrayList<DeviceInfo> filteredDevices = new CopyOnWriteArrayList<DeviceInfo>();
	
	//List which stores actual filtered android-clients i.e. 
	//that receives the control file and acknowledged about the same
	@Getter @Setter CopyOnWriteArrayList<DeviceInfo> actualFilteredDevices = new CopyOnWriteArrayList<DeviceInfo>();

	//duration of the session in hours. By default it is 3 hours, but can be set to any number 
	//greater than zero while creating new session.
	@Getter @Setter int duration = 3;

	//this is for tracking the initialization time of the session
	@Getter @Setter Calendar cal = Calendar.getInstance();

	//counters for thread
	@Getter @Setter Integer startExpTCounter=-1;
	@Getter @Setter Integer stopExpTCounter=-1;
	@Getter @Setter Integer clearRegTCounter=-1;
	@Getter @Setter Integer refreshTCounter=-1;	
	

	/**
	* constructor
	*/
	public Session(String u){
		user=u;
		name="Default";
	}


	/**
	* constructor
	*/
	public Session(String n, String u){
		user=u;
		name=n;
	}
	

	/**
	* constructor
	*/
	public Session(String n, String d, String u){
		name=n;
		description=d;
		user=u;
	}


	/**
	* constructor
	*/
	public Session(Integer sid, String n, String d, String u){
		sessionID=sid;
		name=n;
		description=d;
		user=u;
	}

	/**
	* constructor
	*/
	public Session(Integer sid, String n, String d, String u, int to){
		sessionID=sid;
		name=n;
		description=d;
		user=u;
	}

	public void SetStartExpTCounter(){
	}

	public void SetStopExpTCounter(){
	}

	public void SetClearRegTCounter(){
	}

	public void SetRefreshTCounter(){
	}
}
