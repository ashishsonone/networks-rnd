package serverplus;

import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.Setter;

public class Main {
	static @Getter @Setter boolean serverOn = true;
	static @Getter @Setter boolean experimentRunning = false;
	static @Getter @Setter boolean registrationWindowOpen = false;
	static ConcurrentHashMap<String, DeviceInfo> registeredClients = new ConcurrentHashMap<String, DeviceInfo>();
}
