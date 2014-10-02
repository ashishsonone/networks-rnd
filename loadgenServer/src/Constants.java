
public class Constants {
	
	static final String action = "action";
	static final String noOfFilteringDevices = "filteringDevicesCount";
	static final String timeoutWindow = "timeoutWindow";
	static final String textFileFollow = "textFileFollow";
	static final String sendStatus = "sendStatus";
	
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
		static final String registerClient = "register";
		static final String ping = "ping";
	}
	
	static final int responseOK = 200; 
	static final int responseRepeat = 300;
	static final int responseError = 404; 
	
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
		static final String packetCaptureAppUsed = "packetCaptureAppUsed";
	}
}