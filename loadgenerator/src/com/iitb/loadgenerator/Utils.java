package com.iitb.loadgenerator;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;


public class Utils {
	static String getLogFileJson(String expID){
		JSONObject obj = new JSONObject();
		obj.put(Constants.action, "receiveLog");
		obj.put("expID", expID);
		obj.put(Constants.macAddress, Utils.getMACAddress());
		String jsonString = obj.toJSONString();
		System.out.println(jsonString);

		return jsonString;
	}

	static SimpleDateFormat sdf = new SimpleDateFormat("ZZZZ HH:mm:s : S", Locale.US);
	
	static String getTimeInFormat(){
		Calendar cal = Calendar.getInstance();
		return sdf.format(cal.getTime());
	}
	
	

	public static String getMyDetailsJson(ServerSocket listen, String myip){
		JSONObject obj = new JSONObject();
		InetAddress IP = null; 
		String osVersion = Integer.toString(android.os.Build.VERSION.SDK_INT);

		obj.put(Constants.action, "register");
		//TODO remove comment following two lines
		obj.put(Constants.ip, myip);
		obj.put(Constants.port, Integer.toString(listen.getLocalPort()));
		obj.put(Constants.osVersion, osVersion);
		obj.put(Constants.wifiVersion, "802.11n");

		obj.put(Constants.macAddress, getMACAddress());
		obj.put(Constants.processorSpeed, getProcessorSpeed());
		obj.put(Constants.numberOfCores, Integer.toString(getNumCores()));
		obj.put(Constants.wifiSignalStrength, getWifiStrength());

		obj.put(Constants.storageSpace, getAvailableStorage());
		obj.put(Constants.memory, getTotalRAM());
		obj.put(Constants.packetCaptureAppUsed, (new Boolean(false)).toString());

		String jsonString = obj.toJSONString();
		System.out.println(jsonString);

		return jsonString;
	}

	public static String getMACAddress(){
		WifiInfo info = MainActivity.wifimanager.getConnectionInfo();
		String address = info.getMacAddress();
		return address;
	}
	
	public static String getWifiStrength(){
		WifiInfo info = MainActivity.wifimanager.getConnectionInfo();
		int level = WifiManager.calculateSignalLevel(info.getRssi(), 10);
		return Integer.toString(level);
	}
	
	public static String getAvailableStorage(){
		File path = Environment.getDataDirectory(); //internal storage
		StatFs sf = new StatFs(path.getPath());
		long blocks = sf.getAvailableBlocksLong();
		long blocksize = sf.getBlockSizeLong();
		long availStorage = blocks * blocksize/(1024 * 1024); //Mega bytes
		return Long.toString(availStorage);
	}
	
	public static String getTotalRAM() {
	    RandomAccessFile reader = null;
	    String load = "0";
	    try {
	        reader = new RandomAccessFile("/proc/meminfo", "r");
	        load = reader.readLine();
	        String[] tokens = load.split(" +");
	        load = tokens[1].trim(); //here is the memory
	        int ram = Integer.parseInt(load); //KB
	        ram = ram/1024;
	        load = Integer.toString(ram);
	        reader.close();
	    } catch (IOException ex) {
	        ex.printStackTrace();
	    }
	    return load;
	}
	
	/**
	 * Gets the number of cores available in this device, across all processors.
	 * Requires: Ability to peruse the filesystem at "/sys/devices/system/cpu"
	 * @return The number of cores, or 1 if failed to get result
	 */
	public static int getNumCores() {
	    //Private Class to display only CPU devices in the directory listing
	    class CpuFilter implements FileFilter {
	        @Override
	        public boolean accept(File pathname) {
	            //Check if filename is "cpu", followed by a single digit number
	            if(Pattern.matches("cpu[0-9]+", pathname.getName())) {
	                return true;
	            }
	            return false;
	        }      
	    }

	    try {
	        //Get directory containing CPU info
	        File dir = new File("/sys/devices/system/cpu/");
	        //Filter to only list the devices we care about
	        File[] files = dir.listFiles(new CpuFilter());
	        //Return the number of cores (virtual CPU devices)
	        return files.length;
	    } catch(Exception e) {
	        //Default to return 1 core
	        return 1;
	    }
	}
	
	public static String getProcessorSpeed() {
	    RandomAccessFile reader = null;
	    String load = "0";
	    try {
	        reader = new RandomAccessFile("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq", "r");
	        load = reader.readLine();
	        int speed = Integer.parseInt(load); //Khz
	        speed = speed / 1000; //Mhz
	        load = Integer.toString(speed);
	        reader.close();
	    } catch (IOException ex) {
	        ex.printStackTrace();
	    }
	    return load;
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
		    Log.d(Constants.LOGTAG, "Deleting log file(not yet)" + fileName);
		    //!TODO
		    //file.delete(); //since this file sending was succesful we can delete it from the log directory
		    
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	
}
