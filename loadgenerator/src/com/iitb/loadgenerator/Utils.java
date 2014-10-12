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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.simple.JSONObject;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;


public class Utils {
	static List <NameValuePair> getLogFileJson(String expID){
		List < NameValuePair > nameValuePairs = new ArrayList <NameValuePair> ();
		
		nameValuePairs.add(new BasicNameValuePair("expID", expID));
		nameValuePairs.add(new BasicNameValuePair(Constants.macAddress, Utils.getMACAddress()));
		
		return nameValuePairs;
	}
	
	static HttpClient getClient(){
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, Constants.timeoutConnection);
		HttpConnectionParams.setSoTimeout(httpParameters, Constants.timeoutSocket);
		
		DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
		
		return httpClient;
	}

	static SimpleDateFormat sdf = new SimpleDateFormat("ZZZZ HH:mm:s : S", Locale.US);
	
	static String getTimeInFormat(){
		Calendar cal = Calendar.getInstance();
		return sdf.format(cal.getTime());
	}
	
	

	public static List <NameValuePair> getMyDetailsJson(ServerSocket listen){
		
		List < NameValuePair > nameValuePairs = new ArrayList <NameValuePair> ();
		String osVersion = Integer.toString(android.os.Build.VERSION.SDK_INT);
		
		nameValuePairs.add(new BasicNameValuePair(Constants.ip, getIP()));
		nameValuePairs.add(new BasicNameValuePair(Constants.port, Integer.toString(listen.getLocalPort())));
		nameValuePairs.add(new BasicNameValuePair(Constants.osVersion, osVersion));
		nameValuePairs.add(new BasicNameValuePair(Constants.wifiVersion, "802.11n"));

		nameValuePairs.add(new BasicNameValuePair(Constants.macAddress, getMACAddress()));
		nameValuePairs.add(new BasicNameValuePair(Constants.processorSpeed, getProcessorSpeed()));
		nameValuePairs.add(new BasicNameValuePair(Constants.numberOfCores, Integer.toString(getNumCores())));
		nameValuePairs.add(new BasicNameValuePair(Constants.wifiSignalStrength, getWifiStrength()));

		nameValuePairs.add(new BasicNameValuePair(Constants.storageSpace, getAvailableStorage()));
		nameValuePairs.add(new BasicNameValuePair(Constants.memory, getTotalRAM()));
		nameValuePairs.add(new BasicNameValuePair(Constants.packetCaptureAppUsed, (new Boolean(false)).toString()));

		return nameValuePairs;
	}
	
	public static String getIP(){
		WifiInfo info = MainActivity.wifimanager.getConnectionInfo();
		int ip = info.getIpAddress();
		@SuppressWarnings("deprecation")
		String ipString = Formatter.formatIpAddress(ip);
		return ipString;
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
