package serverplus;

import lombok.Getter;
 
public class Constants {
	
	public class Status{
		static final String registrationNotStarted = "registrationNotStarted";
		static final String registrationStarted = "registrationStarted";
	}
	
	public class Action{
		static final String startRegistration = "startRegistration";
		static final String stopRegistration = "stopRegistration";
		static final String startExperiment = "startExperiment";
		static final String stopExperiment = "stopExperiment";
		static final String sendControlFile = "controlFile";
		static final String receiveLogFile = "receiveLog";
		static final String receiveEventFile = "receiveEventFile";		
		static final String registerClient = "register";
		static final String ping = "ping";
	}
	
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
	
	static final int responseOK = 200; 
	static final int responseRepeat = 300;
	static final int responseError = 404; 
	
	
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
	
	static final @Getter int loginSuccess = 0;
	static final @Getter int loginFailure = 1;
	static final @Getter int connectionSuccess = 2;
	static final @Getter int connectionFailure = 3;
	static final @Getter int internalError = 4;
	static final @Getter String sendStatus = "sendstatus";
	
	
	static final String noOfFilteringDevices = "filteringDevicesCount";
	static final String timeoutWindow = "timeoutWindow";
	static final @Getter String expID = "expID";
	static final @Getter String mainExpLogsDir = "/home/sanchit/Desktop/experimentLogs/";
	static final @Getter String tempFiles = "/home/sanchit/Desktop/tempFilesforRnD/";
	
}
