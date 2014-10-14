package serverplus;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.Getter;
import lombok.Setter;

public class Main {
	static @Getter @Setter boolean serverOn = true;
	static @Getter @Setter boolean experimentRunning = false;
	static @Getter @Setter boolean registrationWindowOpen = false;
	static @Getter @Setter int currentExperiment = -1;
	static @Getter @Setter ConcurrentHashMap<String, DeviceInfo> registeredClients = new ConcurrentHashMap<String, DeviceInfo>();
	static @Getter @Setter CopyOnWriteArrayList<DeviceInfo> filteredDevices = new CopyOnWriteArrayList<DeviceInfo>();
}
