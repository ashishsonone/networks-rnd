package serverplus;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
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

		obj.put(Constants.macAddress, "00:00:00:00:00:00");
		obj.put(Constants.processorSpeed, "2.7");
		obj.put(Constants.numberOfCores, Integer.toString(4));
		obj.put(Constants.wifiSignalStrength, Double.toString(2.3));

		obj.put(Constants.storageSpace, Integer.toString(4096));
		obj.put(Constants.memory, Integer.toString(1024));

		String jsonString = obj.toJSONString();
		System.out.println(jsonString);

		return jsonString;
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
	static String getControlFileJson(){
		JSONObject obj = new JSONObject();
		obj.put(Constants.action, Constants.Action.sendControlFile);
		obj.put(Constants.textFileFollow, Boolean.toString(true));
		String jsonString = obj.toJSONString();
		System.out.println(jsonString);
		return jsonString;
	}
	@SuppressWarnings("unchecked")
	static String getStopSignalJson(){
		JSONObject obj = new JSONObject();
		obj.put(Constants.action, Constants.Action.stopExperiment);
		String jsonString = obj.toJSONString();
		System.out.println(jsonString);
		return jsonString;
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
	
	static void ReceiveFile(DataInputStream dis, int bufferSize, String fileName){
		
		System.out.println(fileName);
		byte[] buffer = new byte[bufferSize];
		int bytesRead;
        try {
        	int size = dis.readInt();
			OutputStream fout = new FileOutputStream(fileName);
			
			while (size > 0
	                && (bytesRead = dis.read(buffer, 0,
	                        (int) Math.min(bufferSize, size))) != -1) {
	            fout.write(buffer, 0, bytesRead);
	            size -= bytesRead;
	        }
			
			fout.close();
			
		} catch (IOException e) {
			System.out.println("Utils.ReceiveFile: 'dis.readInt()' Failed...");
			e.printStackTrace();
		}
        
	}
	
	static void SendResponse(Socket s, int response){
		try {
			DataOutputStream dout = new DataOutputStream(s.getOutputStream());
			dout.writeInt(response);
			dout.close();
		} catch (IOException e) {
			System.out.println("SendResponse: Sending Response " + response + " Failed...");
			e.printStackTrace();
		}
	}
	
	public static int getCurrentExperimentID(){
		DBManager db = new DBManager();
		int res = db.getMaxExperimentID();
		System.out.println("Utils.getCurrentExperimentID: maximum exp id = " + res);
		return res;
	}
	
	public static int addExperimentDetails(int expID, DeviceInfo d, boolean fileReceived){
		DBManager db = new DBManager();
		int res = db.addExperimentDetail(expID, d, fileReceived); 
		return res;
	}
	
	public static int updateFileReceivedField(int expID, String macAddress, boolean fileReceived){
		DBManager db = new DBManager();
		int res = db.updateFileReceivedField(expID, macAddress, fileReceived); 
		return res;
	}
	
	public static int addExperiment(Experiment e){
		DBManager db = new DBManager();
		int res = db.addExperiment(e); 
		return res;
	}
	
	public static String getEventFileOfExperiment(int expid){
		DBManager db = new DBManager();
		String res = db.getEventFileOfExperiment(expid); 
		return res;
	}
	
	
}
