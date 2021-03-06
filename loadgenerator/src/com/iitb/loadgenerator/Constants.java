package com.iitb.loadgenerator;

import android.os.Environment;


public class Constants {
	static final String action = "action";
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
	static final String action_controlFile = "controlFile";
	static final String textFileFollow = "textFileFollow";
	
	static final String logDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/loadgenerator/logs";
	
	
	//temporary
	static final String LOGTAG = "LOADGENERATOR";
	static final String LINEDELIMITER = "***\n";
	
	// Defines a custom intent for alarm receiver
	public static final String BROADCAST_ALARM_ACTION = "com.iitb.loadgenerator.BROADCAST_ALARM";
	
	// Defines a custom Intent action
    public static final String BROADCAST_ACTION = "com.example.android.threadsample.BROADCAST";
    
    // Defines the key for the status "extra" in an Intent
    public static final String BROADCAST_MESSAGE = "com.example.android.threadsample.STATUS";
}
