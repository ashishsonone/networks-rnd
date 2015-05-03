package serverplus;
import java.lang.*;
import java.util.Map;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.Socket;
import java.net.InetSocketAddress;


/*
* This is implementation of thread in which we send each request to client using thread
* Request can be of type 'start Experiment', 'Stop Experiment', 'clear Registrations' or 'refresh Registrations'
*/
public class Multicast implements Runnable{
	final int startExp=0;
	final int stopExp=1;
	final int clearReg=2;
	final int refresh=3;
	int expID;
	DeviceInfo device;
	Session session;
	int whatToDo;
	String jsonString;
	String message;

	public Multicast(DeviceInfo d, Session s, int todo, String str){
		device=d;
		session=s;
		whatToDo=todo;
		jsonString=str;
	}

	public Multicast(DeviceInfo d, Session s, int todo, String str1, String str2, int eid){
		device=d;
		session=s;
		whatToDo=todo;
		jsonString=str1;
		message=str2;
		expID=eid;
	}

	public void run(){
		System.out.println("IN THREAD.............");
		DataOutputStream dout = null;
		DataInputStream din = null;
		int timeoutWindow;
		long tid = Thread.currentThread().getId();
		switch(whatToDo){
			case startExp:
				System.out.println("Starting Experiment in THREAD");
				timeoutWindow = Constants.sendControlFileTimeoutWindow;
				
				System.out.println("Starting Experiment in THREAD");
				try {
					System.out.println("run() "+ tid+": while sending control files to devices..."
									+ ": IP: " + device.ip + " and Port" + device.port);
					Socket s = new Socket();
					s.setSoTimeout(timeoutWindow);
					s.connect(new InetSocketAddress(device.ip, device.port),Constants.connectionTimeoutWindow);
					dout = new DataOutputStream(s.getOutputStream());
					
					dout.writeInt(jsonString.length());
					dout.writeBytes(jsonString);
					dout.writeInt(message.length());
					dout.writeBytes(message);
					
					din = new DataInputStream(s.getInputStream());
					int response = din.readInt();
					System.out.println("run() "+ tid+": startExp : response=" + response);
					if(response == Constants.responseOK){
						System.out.println("run() "+ tid+": responseOK for StartExp");
						session.actualFilteredDevices.add(device);
						System.out.println("run() "+ tid+": adding experiment details of exp " + expID +"to database "
										+ "of device " + device.macAddress);
						Utils.addExperimentDetails(expID, device, false);
					}
					s.close();
				} catch (InterruptedIOException ie){
					System.out.println("run() "+ tid+": Timeout occured for sending control file to device with ip: "
												+ device.ip + " and Port: " + device.port);
				} catch (IOException ioe) {
					System.out.println("run() "+ tid+": 'new DataOutputStream(out)' or " +
										"'DataInputStream(s.getInputStream())' Failed...");
				}
				
				synchronized(session.startExpTCounter){
					session.startExpTCounter--;
					System.out.println("run() "+ tid+": value of startExpTCounter = " + session.startExpTCounter);
				}
			break;

			case stopExp:
				System.out.println("Stopping Experiment in THREAD");
				timeoutWindow = Constants.sendStopSignalTimeoutWindow;
				System.out.println("Stopping Experiment in THREAD");

				try {
					System.out.println("run() "+ tid+": while sending Stop Experiment signal to device"
									+ ":IP: " + device.ip + " and Port" + device.port);
					Socket s = new Socket();
					s.setSoTimeout(timeoutWindow);
					s.connect(new InetSocketAddress(device.ip, device.port),Constants.connectionTimeoutWindow);
					dout = new DataOutputStream(s.getOutputStream());
					
					dout.writeInt(jsonString.length());
					dout.writeBytes(jsonString);
					
					din = new DataInputStream(s.getInputStream());
					int response = din.readInt();
					if(response == Constants.responseOK){
						System.out.println("run() "+ tid+": device with ip: " + device.ip + " and Port: " + device.port 
												+ " has stopped experiment");
					}
					else{
						System.out.println("run() "+ tid+": cannot contact to device with ip: " 
										+ device.ip + " and Port: " + device.port 
										+ " for sending stopp experiment signal");	
					}
					s.close();
					
				} catch (InterruptedIOException ie){
					System.out.println("run() "+ tid+": Timeout occured for sending stop Signal to device with ip: "
												+ device.ip + " and Port: " + device.port);
				} catch (IOException ioe) {
					ioe.printStackTrace();
					System.out.println("run() "+ tid+": 'new DataOutputStream(out)' or " +
										"'DataInputStream(s.getInputStream())' Failed...");
				}	

				synchronized(session.stopExpTCounter){
					System.out.println("run() "+ tid+": value of stopExpTCounter = " + session.stopExpTCounter);
					session.stopExpTCounter--;
				}
			break;

			case clearReg:
				System.out.println("Clearing out Registrations in THREAD");
				timeoutWindow = Constants.clearRegistrationTimeoutWindow;
				System.out.println("Clearing out Registrations in THREAD" + jsonString);
				try {
					System.out.println("run() "+ tid+": while sending Clear clearRegistration signal to device"
									+ ":IP: " + device.ip + " and Port" + device.port);
					Socket s = new Socket();
					s.setSoTimeout(timeoutWindow);
					s.connect(new InetSocketAddress(device.ip, device.port),Constants.connectionTimeoutWindow);
					dout = new DataOutputStream(s.getOutputStream());
					dout.writeInt(jsonString.length());
					dout.writeBytes(jsonString);
					s.close();
				} catch (InterruptedIOException ie){
					System.out.println("run() "+ tid+": Timeout occured for sending signal to device with ip: "
												+ device.ip + " and Port: " + device.port 
												+ " while clearing registration");
				} catch (IOException ioe) {
					System.out.println("run() "+ tid+":'new DataOutputStream()' or 'DataInputStream()' Failed..."
										+ " while clearing registration");
				}

				synchronized(session.clearRegTCounter){
					session.clearRegTCounter--;
				}
			break;

			case refresh:
				System.out.println("Refreshing Registrations in THREAD");
				timeoutWindow = Constants.refreshRegistrationTimeoutWindow;
				try {
					System.out.println("run() "+ tid+": while sending Refresh Registration signal to device"
									+ ":IP: " + device.ip + " and Port" + device.port);
					Socket s = new Socket();
					s.setSoTimeout(timeoutWindow);
					s.connect(new InetSocketAddress(device.ip, device.port),Constants.connectionTimeoutWindow);
					dout = new DataOutputStream(s.getOutputStream());
					dout.writeInt(jsonString.length());
					dout.writeBytes(jsonString);	

					din = new DataInputStream(s.getInputStream());
					int response = din.readInt();
					if(response == Constants.responseOK){
						System.out.println("run() "+ tid+": device with ip: " + device.ip + " and Port: " + device.port 
												+ " has responsed back");
						session.tempRegisteredClients.put(device.macAddress, device);
					}
					else{
						System.out.println("run() "+ tid+": cannot contact to device with ip: " 
										+ device.ip + " and Port: " + device.port);	
					}
					s.close();
				} catch (InterruptedIOException ie){
					System.out.println("run() "+ tid+": Timeout occured for sending stop Signal to device with ip: "
												+ device.ip + " and Port: " + device.port 
												+ " while refreshing registration");
				} catch (IOException ioe) {
					System.out.println("run() "+ tid+":'new DataOutputStream()' or 'DataInputStream()' Failed..."
										+ " while refreshing registration");
				}

				synchronized(session.refreshTCounter){
					session.DecrementRefreshTCounter();
					System.out.println("value of refreshTCounter=" + session.refreshTCounter);
				}
			break;
		}
	}
}