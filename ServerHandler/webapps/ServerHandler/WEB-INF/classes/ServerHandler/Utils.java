package ServerHandler;


import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class Utils {
	
	@SuppressWarnings("unchecked")
	static String getLogFileJson(){
		JSONObject obj = new JSONObject();
		obj.put(Constants.action, "receiveLog");
		String jsonString = obj.toJSONString();
		System.out.println(jsonString);

		return jsonString;
	}

	@SuppressWarnings("unchecked")
	public static String getMyDetailsJson(ServerSocket listen, String myip){
		JSONObject obj = new JSONObject();
		InetAddress IP = null; 

		obj.put(Constants.action, "register");
		obj.put(Constants.ip, myip);
		obj.put(Constants.port, Integer.toString(listen.getLocalPort()));
		obj.put(Constants.osVersion, "2.3");
		obj.put(Constants.wifiVersion, "802.11n");

		obj.put(Constants.macAddress, getMAC(listen.getInetAddress()));
		obj.put(Constants.processorSpeed, "2.7");
		obj.put(Constants.numberOfCores, Integer.toString(4));
		obj.put(Constants.wifiSignalStrength, Double.toString(2.3));

		obj.put(Constants.storageSpace, Integer.toString(4096));
		obj.put(Constants.memory, Integer.toString(1024));
		obj.put(Constants.packetCaptureAppUsed, (new Boolean(false)).toString());

		String jsonString = obj.toJSONString();
		System.out.println(jsonString);

		return jsonString;
	}

	public static String getMAC(InetAddress addr){
		/*try {
				NetworkInterface network = NetworkInterface.getByInetAddress(addr);
				byte[] mac = network.getHardwareAddress();

				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < mac.length; i++) {
					sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));		
				}
				return sb.toString();
		   } catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		   }*/
		return "12-0x-25";
	}

	public static void tryParse(String json){
		JSONParser parser = new JSONParser();
		ContainerFactory containerFactory = new ContainerFactory(){
			public List creatArrayContainer() {
				return new LinkedList();
			}

			public Map createObjectContainer() {
				return new LinkedHashMap();
			}

		};
		try {
			Map dict = (Map) parser.parse(json, containerFactory);
			System.out.println(dict.get("balance"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	static Map<String, String> ParseJson(String json){
		Map<String, String> jsonMap = null;
		JSONParser parser = new JSONParser();
		ContainerFactory containerFactory = new ContainerFactory(){
			@SuppressWarnings("rawtypes")
			public List creatArrayContainer() {
		      return new LinkedList();
		    }
			@SuppressWarnings("rawtypes")
		    public Map createObjectContainer() {
		      return new LinkedHashMap();
		    }
		                        
		};
		
		try {
			jsonMap = (Map<String, String>) parser.parse(json, containerFactory);
		} catch (ParseException e) {
			System.out.println();
			e.printStackTrace();
		}
		return jsonMap;
	}
	
	static void SendFile(DataOutputStream out, String fileName){
		File file = new File(fileName);
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		int fileLength;
		int count=0;
		try {
			fis = new FileInputStream(file);
			fileLength = (int) file.length();
			bis = new BufferedInputStream(fis);
			if (fileLength > Integer.MAX_VALUE) {
		        System.out.println("File is too large.");
		    }
			
			out.writeInt(fileLength);
		    byte[] bytes = new byte[(int) fileLength];
		    
		    while ((count = bis.read(bytes)) > 0) {
		        out.write(bytes, 0, count);
		    }
		    
		    fis.close();
		    bis.close();
		    
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
