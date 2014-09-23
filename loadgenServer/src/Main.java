import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Main {
	static int PORT = 22222;
	static ExecutorService executor = Executors.newCachedThreadPool();
	
	static boolean experimentRunning = false;
	
	//TODO this is temporary. Change to false
	static boolean registrationWindowOpen = true;
	static ConcurrentHashMap<String, DeviceInfo> registeredClients = new ConcurrentHashMap<String, DeviceInfo>();
	
	public static void main(String args[]){
		
		ServerSocket ss;
		try {
			ss = new ServerSocket(PORT);
			
			while(true){
				final Socket client = ss.accept();
				Runnable r = new Runnable(){
						public void run(){
							Handlers.MasterHandler(client);
						}
				};
				
			executor.execute(r);
			}
			
		} catch (IOException e) {
			System.out.println("Main: 'ss = new ServerSocket(PORT)' Failed....");
			e.printStackTrace();
		}
	}
}
