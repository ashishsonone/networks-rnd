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

/**
 *
 * @author sanchitgarg
 * This class provides some of the utility functions
 */
public class Utils {


	/**
	* genetated and returns the String in Json format.
	* String contains information about the action for sending control file
	* This string is later sent to filtered devices.
	*/
	@SuppressWarnings("unchecked")
	static String getControlFileJson(){
		JSONObject obj = new JSONObject();
		obj.put(Constants.action, Constants.Action.sendControlFile);
		obj.put(Constants.textFileFollow, Boolean.toString(true));
		String jsonString = obj.toJSONString();
		System.out.println(jsonString);
		return jsonString;
	}
	
	/**
	* genetated and returns the String in Json format.
	* String contains information about the action for sending session duration in hrs
	* This string is later sent to filtered devices.
	*/
	@SuppressWarnings("unchecked")
	static String getSessionDurationJson(int duration){
		JSONObject obj = new JSONObject();
		obj.put(Constants.action, Constants.Action.sendSessionDuration);
		obj.put(Constants.sessionDuration,Integer.toString(duration));
		String jsonString = obj.toJSONString();
		System.out.println(jsonString);
		return jsonString;
	}

	/**
	* genetated and returns the String in Json format.
	* String contains information that the experiment has been stopped by experimenter
	* This string is later sent to filtered devices.
	*/
	@SuppressWarnings("unchecked")
	static String getStopSignalJson(){
		JSONObject obj = new JSONObject();
		obj.put(Constants.action, Constants.Action.stopExperiment);
		String jsonString = obj.toJSONString();
		System.out.println(jsonString);
		return jsonString;
	}

	/**
	* genetated and returns the String in Json format.
	* String contains information that all the registration has been cleared by experimenter
	* This string is later sent to filtered devices.
	*/
	@SuppressWarnings("unchecked")
	static String getClearRegistrationJson(){
		JSONObject obj = new JSONObject();
		obj.put(Constants.action, Constants.Action.clearRegistration);
		String jsonString = obj.toJSONString();
		System.out.println(jsonString);
		return jsonString;
	}
	

	/**
	* returns ID of the experiment with max experimentID
	*/
	public static int getCurrentExperimentID(String sid){
		DBManager db = new DBManager();
		int res = db.getMaxExperimentID(sid);
		System.out.println("Utils.getCurrentExperimentID: maximum exp id = " + res);
		return res;
	}

	/**
	* returns ID of the session with max sessionID
	*/
	public static int getCurrentSessionID(){
		DBManager db = new DBManager();
		int res = db.getMaxSessionID();
		System.out.println("Utils.getCurrentSessionID: maximum session id = " + res);
		return res;
	}
	

	/**
	* adds device information in 'd' to the experiment corresponds to ID 'expID'
	*/
	public static int addExperimentDetails(int expID, DeviceInfo d, boolean fileReceived){
		DBManager db = new DBManager();
		int res = db.addExperimentDetail(expID, d, fileReceived); 
		return res;
	}
	

	/** 
	* When the log file is received from device 'macaddress' for the experiment number expid, 
	* corresponding fileReceived column in the relation 'experimentdetails' is set to true
	*/ 
	public static int updateFileReceivedField(int expID, String macAddress, boolean fileReceived){
		DBManager db = new DBManager();
		int res = db.updateFileReceivedField(expID, macAddress, fileReceived); 
		return res;
	}
	
	/**
	* Add new entry in the 'experiments' retation for the experiment 'e'
	*/
	public synchronized static int addExperiment(Experiment e, String sid){
		DBManager db = new DBManager();
		int res = db.addExperiment(e,sid);
		System.out.println("Utils.addExperiment: result of db.addExperiment()="+res);
		if(res < 0) return -1;
		res = getCurrentExperimentID(sid);
		System.out.println("Utils.addExperiment: result of db.getCurrentExperimentID()="+res);
		if(res >=0) e.ID = res;
		return res;
	}

	/**
	* Add new entry in the 'sessions' retation for the session 's'
	*/
	public synchronized static int addSession(Session s){
		DBManager db = new DBManager();
		int res = db.addSession(s);
		System.out.println("Utils.addSession: result of db.addSession()="+res);
		if(res < 0) return -1;
		res = getCurrentSessionID();
		System.out.println("Utils.addSession: result of db.getCurrentSessionID()="+res);
		if(res >=0) s.sessionID = res;
		return res;
	}
	
	/**
	* Delete entry in the 'experiments' retation for the experimentid 'eid'
	*/
	public synchronized static int deleteExperiment(int eid){
		DBManager db = new DBManager();
		int res = db.deleteExperiment(eid); 
		return res;
	}
	

	/**
	* returns String containing name of event control file for experiment number 'expid'
	*/
	public synchronized static String getEventFileOfExperiment(int expid){
		DBManager db = new DBManager();
		String res = db.getEventFileOfExperiment(expid);
		return res;
	}

	/**
	* returns Session corresponding to id 'sid'
	*/
	public synchronized static Session getSession(String sid){
		Integer id = Integer.parseInt(sid);
		Session s = Main.SessionMap.get(id);
		if(s!=null) return s;

		DBManager db = new DBManager();
		s=db.getSession(sid);
		Main.getSessionMap().put(id, s);
		System.out.println("Utils.getSession() : session description = " + s.description);
		return s;
	}
	
	
}
