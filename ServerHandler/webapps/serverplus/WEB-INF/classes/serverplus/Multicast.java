package serverplus;
import java.lang.*;
import java.util.Map;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.Socket;



public class Multicast implements Runnable{
	final int startExp=0;
	final int stopExp=1;
	final int clearReg=2;
	final int refresh=3;
	int expectedCount;
	DeviceInfo device;
	Session session;
	int whatToDo;
	String jsonString;
	String message;

	public Multicast(DeviceInfo d, Session s, int todo, String str){
		device=d;
		session=s;
		whatToDo=todo;
		message=str;
	}

	public Multicast(DeviceInfo d, Session s, int todo, String str1, String str2, int count){
		device=d;
		session=s;
		whatToDo=todo;
		jsonString=str1;
		message=str2;
		expectedCount=count;
	}

	public void run(){
		System.out.println("IN THREAD.............");
		DataOutputStream dout = null;
		DataInputStream din = null;
		int timeoutWindow;
		switch(whatToDo){
			case startExp:
				System.out.println("Starting Experiment in THREAD");
				timeoutWindow = Constants.sendControlFileTimeoutWindow;
				synchronized(session.startExpTCounter){
					System.out.println("Starting Experiment in THREAD");
					if(expectedCount > session.actualFilteredDevices.size()){
						try {
							System.out.println("run(): while sending control files to devices..."
											+ ": IP: " + device.ip + " and Port" + device.port);
							Socket s = new Socket(device.ip, device.port);
							s.setSoTimeout(timeoutWindow);
							dout = new DataOutputStream(s.getOutputStream());
							
							dout.writeInt(jsonString.length());
							dout.writeBytes(jsonString);
							dout.writeInt(message.length());
							dout.writeBytes(message);
							
							din = new DataInputStream(s.getInputStream());
							int response = din.readInt();
							if(response == Constants.responseOK){
								synchronized(session.actualFilteredDevices){
									if(expectedCount > session.actualFilteredDevices.size()){
										session.actualFilteredDevices.add(device);
									}
								}
							}
							s.close();
						} catch (InterruptedIOException ie){
							System.out.println("run(): Timeout occured for sending control file to device with ip: "
														+ device.ip + " and Port: " + device.port);
						} catch (IOException ioe) {
							System.out.println("run(): 'new DataOutputStream(out)' or " +
												"'DataInputStream(s.getInputStream())' Failed...");
						}
					}
					session.startExpTCounter--;
				}
			break;

			case stopExp:
				System.out.println("Stopping Experiment in THREAD");
				timeoutWindow = Constants.sendStopSignalTimeoutWindow;	//10 seconds
				System.out.println("Stopping Experiment in THREAD");

				try {
					System.out.println("run(): while sending Stop Experiment signal to device"
									+ ":IP: " + device.ip + " and Port" + device.port);
					Socket s = new Socket(device.ip, device.port);
					s.setSoTimeout(timeoutWindow);
					dout = new DataOutputStream(s.getOutputStream());
					
					dout.writeInt(jsonString.length());
					dout.writeBytes(jsonString);
					
					din = new DataInputStream(s.getInputStream());
					int response = din.readInt();
					if(response == Constants.responseOK){
						System.out.println("run(): device with ip: " + device.ip + " and Port: " + device.port 
												+ " has stopped experiment");
					}
					else{
						System.out.println("run(): cannot contact to device with ip: " 
										+ device.ip + " and Port: " + device.port 
										+ " for sending stopp experiment signal");	
					}
					s.close();
					
				} catch (InterruptedIOException ie){
					System.out.println("run(): Timeout occured for sending stop Signal to device with ip: "
												+ device.ip + " and Port: " + device.port);
				} catch (IOException ioe) {
					ioe.printStackTrace();
					System.out.println("run(): 'new DataOutputStream(out)' or " +
										"'DataInputStream(s.getInputStream())' Failed...");
				}	

				synchronized(session.stopExpTCounter){
					session.stopExpTCounter--;
				}
			break;

			case clearReg:
				System.out.println("Clearing out Registrations in THREAD");
				timeoutWindow = Constants.clearRegistrationTimeoutWindow;	//10 seconds
				synchronized(session.clearRegTCounter){
					System.out.println("Clearing out Registrations in THREAD");
					try {
						Socket s = new Socket(device.ip, device.port);
						s.setSoTimeout(timeoutWindow);
						dout = new DataOutputStream(s.getOutputStream());
						dout.writeInt(jsonString.length());
						dout.writeBytes(jsonString);	
						s.close();
					} catch (InterruptedIOException ie){
						System.out.println("run(): Timeout occured for sending stop Signal to device with ip: "
													+ device.ip + " and Port: " + device.port 
													+ " while clearing registration");
					} catch (IOException ioe) {
						System.out.println("run():'new DataOutputStream()' or 'DataInputStream()' Failed..."
											+ " while clearing registration");
					}


					session.clearRegTCounter--;
				}
			break;

			case refresh:
				System.out.println("Refreshing Registrations in THREAD");
				synchronized(session.refreshTCounter){
					System.out.println("Refreshing Registrations in THREAD");



					session.refreshTCounter--;
				}
			break;
		}
	}
}