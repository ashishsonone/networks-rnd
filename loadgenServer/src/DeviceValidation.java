
public class DeviceValidation {
	DeviceInfo device;
	boolean validation;
	
	public DeviceValidation() {
		device = new DeviceInfo();
		validation = false;
	}
	
	public DeviceValidation(DeviceInfo d) {
		device = d;
		validation = false;
	}
	
	public DeviceValidation(DeviceInfo d, boolean valid) {
		device = d;
		validation = valid;
	}
	
	public void print(){
		System.out.print("mac: " + device.macAddress + " port: " 
				+ device.port + " valid: " + validation);
	}
}
