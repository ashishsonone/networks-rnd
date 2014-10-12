package serverplus;
public class DeviceInfo {	
	int port = 0;
	String ip = "";
	String macAddress = "00:00:00:00:00:00";
	String osVersion = "1";
	String wifiVersion = "802.11";
	int numberOfCores = 1;
	int storageSpace = 0;
	int memory = 0;
	int processorSpeed = 0;
	int  wifiSignalStrength = 1;
	
	void print(){
		System.out.println("IP: " + ip);
		System.out.println("Port: " + port);
		System.out.println("MAC Address: " + macAddress);
		System.out.println("OS Version" + osVersion);
		System.out.println("WIFI Version " + wifiVersion);
		System.out.println("No of Cores: " + numberOfCores);
		System.out.println("Storage Space: " + storageSpace);
		System.out.println("Memory: " + memory);
		System.out.println("Processor Speed: " + processorSpeed);
		System.out.println("WIFI signal Strength: " + wifiSignalStrength);
		
		
	}
}
