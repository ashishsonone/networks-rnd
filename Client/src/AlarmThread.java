import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;


public class AlarmThread {
	public static void eventRunner(Socket server){
		System.out.println("Server connection established");
		String data = "";
		try {
			DataInputStream dis= new DataInputStream(server.getInputStream());
			DataOutputStream dout = new DataOutputStream(server.getOutputStream());
			
			int length = dis.readInt();
			System.out.println("Json length " + length + " read from " + server.getInetAddress().getHostAddress());

			for(int i=0;i<length;++i){
				data += (char)dis.readByte();
			}
			System.out.println("eventRunner : json received : " + data);

			Map<String, String> jsonMap = Utils.ParseJson(data);

			String action = jsonMap.get(Constants.action);
			if(action.compareTo(Constants.action_controlFile) == 0){
				boolean textFileFollow = Boolean.parseBoolean((String) jsonMap.get(Constants.textFileFollow));
				if(textFileFollow){
					int fileSize = dis.readInt();
					System.out.println("eventRunner : fileSize " + fileSize);
					StringBuilder fileBuilder = new StringBuilder();
					for(int i=0;i<fileSize;++i){
						fileBuilder.append((char)dis.readByte());
					}
					String controlFile = fileBuilder.toString();

					System.out.print(controlFile);
					
					server.close();
					
					System.out.println("eventRunner : Now setting up alarms");
					
					Thread.sleep(10000); //Alarms are set and events processed during this time. Also log file gets generated
					System.out.println("eventRunner : Experiment over. Now sending log file");
					
					sendLog();
					
					System.out.println("eventRunner : Log file sent successfully");
					
				}
				else{
					System.out.println("eventRunner : No control file in response");
				}
			}
			else{
				System.out.println("eventRunner() : Wrong action code");
			}

		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static void sendLog(){
		Socket client;
		
		OutputStream outToServer;
		try {
			client = new Socket(Main.serverName, Main.port);
			outToServer = client.getOutputStream();
			DataOutputStream dout =
					new DataOutputStream(outToServer);
//			DataInputStream din =
//					new DataInputStream(client.getInputStream());

			//String json = "HelloWorld from " + client.getLocalSocketAddress();
			
			String json = Utils.getLogFileJson();

			dout.writeInt(json.length());

			dout.writeBytes(json); //json sent
			System.out.println("sendLog() : JSON sent");
			//Now send log file
			
			
			Utils.SendFile(dout, "/home/ashish/Desktop/EventGen.java");
			client.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
