
public class DeviceInfo {	
	int port = 0;
	String ip = "";
	String macAddress = "";
	String osVersion = "";
	String wifiVersion = "";
	int numberOfCores = 0;
	int storageSpace = 200;
	int memory = 200;
	double processorSpeed = 2.2;
	double wifiSignalStrength = 0.2;
	boolean packetCaptureAppUsed = false;
	
	void print(){
		System.out.println("Port: " + port);
		System.out.println("MAC Address: " + macAddress);
		System.out.println("IP: " + ip);
	}
}
