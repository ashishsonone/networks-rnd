package serverplus;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author sanchitgarg
 * This class is used for holding information about a device.
 * 
 */

public class DeviceInfo {	
	@Getter @Setter int port = 0;		//port through which device is registered
	@Getter @Setter String ip = "";		//ip of the device
	@Getter @Setter String macAddress = "00:00:00:00:00:00";	//macaddress of device
	@Getter @Setter int osVersion = 1;		//Android Build Number
	@Getter @Setter String wifiVersion = "802.11";	//Wifi version number
	@Getter @Setter int numberOfCores = 1;			//number of cores
	@Getter @Setter int storageSpace = 0;			//storage space in SD card in MB
	@Getter @Setter int memory = 0;					//RAM of the device in MB
	@Getter @Setter int processorSpeed = 0;			//processor speed in MHz
	@Getter @Setter int  wifiSignalStrength = 1;	// Wifi connection Strength from 1-poor to 10-full
	
	/**
	* constructor
	*/
	public DeviceInfo(){
	
	}
	
	/**
	* constructor
	*/
	public DeviceInfo(DeviceInfo d){
		port=d.port;
		ip=d.ip;
		macAddress=d.macAddress;
		osVersion=d.osVersion;
		wifiVersion=d.wifiVersion;
		numberOfCores=d.numberOfCores;
		storageSpace=d.storageSpace;
		memory=d.memory;
		processorSpeed=d.processorSpeed;
		wifiSignalStrength=d.wifiSignalStrength;
	}

	/**
	* prints the device information
	*/
	void print(){
		System.out.println("IP: " + ip);
		System.out.println("Port: " + port);
		System.out.println("MAC Address: " + macAddress);
		System.out.println("WIFI Version " + wifiVersion);
		System.out.println("No of Cores: " + numberOfCores);
		System.out.println("Storage Space: " + storageSpace);
		System.out.println("Memory: " + memory);
		System.out.println("Processor Speed: " + processorSpeed);
		System.out.println("WIFI signal Strength: " + wifiSignalStrength);
		
		
	}
}
