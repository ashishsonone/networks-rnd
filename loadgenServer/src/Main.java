import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Main {
	static int PORT = 11111;
	static int Timeout = 5000; //in milliseconds
	static ExecutorService executor = Executors.newCachedThreadPool();
	
	static boolean serverON = true;
	static boolean experimentRunning = false;
	
	//TODO this is temporary. Change to false
	static boolean registrationWindowOpen = true;
	static ConcurrentHashMap<String, DeviceInfo> registeredClients = new ConcurrentHashMap<String, DeviceInfo>();
	
	public static void main(String args[]){
		
		
		ServerSocket server = null;
		try {
			server = new ServerSocket(PORT);
			server.setSoTimeout(Timeout);
			PORT = server.getLocalPort();
			System.out.println("Server starts listening on port: " + PORT);
		} catch (IOException e) {
			System.out.println("Main: 'ss = new ServerSocket(PORT)' Failed....");
			e.printStackTrace();
		}
		
		while(serverON){
			final Socket client;
			try {
				client = server.accept();
				Runnable r = new Runnable(){
					public void run(){
						Handlers.MasterHandler(client);
					}
			};
			System.out.println("Main: Master Thread executing....");
			executor.execute(r);
			
			} catch(InterruptedIOException iex){
				System.out.println("Main: Server Timeout to listen....");
			} catch (IOException e) {
				System.out.println("Main: 'server.accept()' Failed.....");
				executor.shutdown();
				e.printStackTrace();
			}
		}
		
		try {
			server.close();
		} catch (IOException e) {
			System.out.println("Main: 'server.close()' Failed.....");
			e.printStackTrace();
		}
		
		System.out.println("Server Stopped....");
	}
}
