import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
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
		obj.put(Constants.action, Constants.Action.receiveLogFile);
		String jsonString = obj.toJSONString();
		System.out.println(jsonString);

		return jsonString;
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
	static String getStatusResponse(String status, int msg){
		JSONObject obj = new JSONObject();
		obj.put(Constants.sendStatus, status);
		obj.put("msg", Integer.toString(msg));
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
		System.out.println("Sending File...");
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
			System.out.println("Utils.SendFile: 'FileInputStream(file)' Failed...");
			e.printStackTrace();
		} catch (IOException e){
			System.out.println("Utils.SendFile: 'BufferedInputStream(fis)' Failed...");
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
	
	static int getCurrentExperimentID(){
		DBManager db = new DBManager();
		return db.getMaxExperimentID();
	}
	
	static int addExperimentDetails(int expID, DeviceInfo d, boolean fileReceived){
		DBManager db = new DBManager();
		return db.addExperimentDetail(expID, d, fileReceived); 
	}
	
	static int updateFileReceivedField(int expID, String macAddress, boolean fileReceived){
		DBManager db = new DBManager();
		return db.updateFileReceivedField(expID, macAddress, fileReceived); 
	}
	
}
