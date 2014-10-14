package serverplus;

import lombok.Getter;
import lombok.Setter;
public class DeviceInfo {	
	@Getter @Setter int port = 0;
	@Getter @Setter String ip = "";
	@Getter @Setter String macAddress = "00:00:00:00:00:00";
	@Getter @Setter int osVersion = 1;
	@Getter @Setter String wifiVersion = "802.11";
	@Getter @Setter int numberOfCores = 1;
	@Getter @Setter int storageSpace = 0;
	@Getter @Setter int memory = 0;
	@Getter @Setter int processorSpeed = 0;
	@Getter @Setter int  wifiSignalStrength = 1;
	
	public DeviceInfo(){
	
	}
	
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
