package serverplus;

import lombok.Getter;
 
public class Constants {
	
	public class Status{
		static final String registrationNotStarted = "registrationNotStarted";
		static final String registrationStarted = "registrationStarted";
	}
	
	//Actions specified for Android-Client & Server Interaction
	public class Action{
		static final String startRegistration = "startRegistration";
		static final String stopRegistration = "stopRegistration";
		static final String startExperiment = "startExperiment";
		static final String stopExperiment = "stopExperiment";
		static final String sendControlFile = "controlFile";
		static final String receiveLogFile = "receiveLog";
		static final String receiveEventFile = "receiveEventFile";		
		static final String registerClient = "register";
		static final String refreshRegistration = "refreshRegistration";
		static final String clearRegistration = "clearRegistration";
		static final String sendSessionDuration = "sendSessionDuration";
	}
	
	//key Words for the Information about the device
	public class Device{
		static final String ip = "ip";
		static final String port = "port";
		static final String osVersion = "osVersion";
		static final String wifiVersion = "wifiVersion";
		static final String macAddress = "macAddress";
		static final String numberOfCores = "numberOfCores";
		static final String memory = "memory";				//in MB
		static final String processorSpeed = "processorSpeed";		//in GHz
		static final String wifiSignalStrength = "wifiSignalStrength";
		static final String storageSpace = "storageSpace";		//in MB
	}
	
	//response codes when client ask for Actions
	//if Action is performed successfully responseOK is sent
	//iin case of error responseError is sent
	static final int responseOK = 200; 
	static final int responseRepeat = 300;
	static final int responseError = 404; 
	
	//timeout windows for each sockets
	//in case of sending control file if connection cannot be made until 5 seconds it timeouts and proceeds
	//similarly while sending stop experiment signal and clear registrations signal 
	static final @Getter int sendSessionDurationTimeoutWindow = 5000;	//5 seconds
	static final @Getter int sendControlFileTimeoutWindow = 5000;		//5 seconds
	static final @Getter int sendStopSignalTimeoutWindow = 5000;		//5 seconds
	static final @Getter int clearRegistrationTimeoutWindow = 5000;		//5 seconds
	static final @Getter int refreshRegistrationTimeoutWindow = 5000;	//5 seconds
	static final @Getter int connectionTimeoutWindow = 5000;	//5 seconds

	static final @Getter String sessionDuration = "sessionDuration";

	//Max Number of sessions can be created
	static final @Getter int maxSessions = 10000;
	

	//key Words for the Information about the device
	static final @Getter String action = "action";
	static final @Getter String ip = "ip";
	static final @Getter String port = "port";
	static final @Getter String osVersion = "osVersion";
	static final @Getter String wifiVersion = "wifiVersion";
	static final @Getter String macAddress = "macAddress";
	static final @Getter String numberOfCores = "numberOfCores";
	static final @Getter String memory = "memory";				//in MB
	static final @Getter String processorSpeed = "processorSpeed";		//in GHz
	static final @Getter String wifiSignalStrength = "wifiSignalStrength";
	static final @Getter String storageSpace = "storageSpace";		//in MB
	static final @Getter String action_controlFile = "controlFile";
	static final @Getter String textFileFollow = "textFileFollow";
	
	//for web-client
	static final @Getter int loginSuccess = 0;
	static final @Getter int loginFailure = 1;
	static final @Getter int connectionSuccess = 2;
	static final @Getter int connectionFailure = 3;
	static final @Getter int internalError = 4;
	static final @Getter String sendStatus = "sendstatus";
	
	
	static final String noOfFilteringDevices = "filteringDevicesCount";
	static final String timeoutWindow = "timeoutWindow";
	static final @Getter String expID = "expID";
	static final @Getter String eventFile = "events.txt";
	static final @Getter String traceFile = "trace";
	static final @Getter String mainExpLogsDir = "/home/sanchit/Desktop/experimentLogs/";
	static final @Getter String tempFiles = "/home/sanchit/Desktop/tempFilesforRnD/";
	static final @Getter String ERRORFILE = "ERRORFILE";

	static final @Getter String sessionID = "sessionID";
	
	static final @Getter int OK = 1000;
	static final @Getter int NOTOK = -1000;
}
