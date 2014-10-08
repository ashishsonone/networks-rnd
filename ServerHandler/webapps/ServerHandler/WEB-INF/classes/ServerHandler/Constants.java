package ServerHandler;

import lombok.Getter;
 
public class Constants {
	public static final class Action{
		static final String regstart = "regstart";
		static final String regstop = "regstop";
		static final String expstart = "expstart";
		static final String expstop = "expstop";
	}
	
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
	static final @Getter String packetCaptureAppUsed = "packetCaptureAppUsed";
	static final @Getter String action_controlFile = "controlFile";
	static final @Getter String textFileFollow = "textFileFollow";
	
	static final @Getter int loginSuccess = 0;
	static final @Getter int loginFailure = 1;
	static final @Getter int connectionSuccess = 2;
	static final @Getter int connectionFailure = 3;
	static final @Getter int internalError = 4;
	
	static final @Getter String sendStatus = "sendstatus";
	
}
