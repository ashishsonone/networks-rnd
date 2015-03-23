package serverplus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.io.FileReader;
import java.io.File;
import java.util.Scanner;
import java.util.*;

/**
 *
 * @author sanchitgarg
 * This class deals with all the interaction required with DataBase.
 * It establishs and closes connection with Databse Server.
 */
public class DBManager {
	
	public static String home = System.getenv("HOME");
    public Connection conn=null;
	
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  

	//Url your database server	
	static final String DB_URL = "jdbc:mysql://localhost/server";
	
	//  Database credentials
	static final String USER = "root";	//username
	static final String PASS = "p";		//password
    	

    /**
    * Before executing any query this method should be called. This method creates connection with 
    * the database server
    *
    */	
	private int createConnection() {
 
		try{
			Class.forName(JDBC_DRIVER);
		} catch (ClassNotFoundException cnfe){
			System.out.println("Could not find the JDBC driver!");			
		}

		try {
			System.out.println("Establishing connection to the server..");
			conn = DriverManager.getConnection(DB_URL,USER, PASS);
			System.out.println("Connected successfullly");
			return Constants.connectionSuccess;
		} catch (SQLException sqle) {
			System.out.println("Connection failed");
			System.out.println(sqle);
		}
		return Constants.connectionFailure;
	}
	
	/**
	* 
	* This method closes connection with the database server
	* 
	*/

	public int closeConnection(){
		
		try{
			if(conn!=null){
				conn.close();
				System.out.println("Connection Closed successfullly");
			}
			return Constants.connectionSuccess;
		}catch(SQLException se){
			se.printStackTrace();
		}
		return Constants.connectionFailure;
	}
	
	/**
	* This method authenticates the user credentials. This method is mainly required by 
	* the web-client that is the experimenter. If success it returns loginSuccess else
	* if there is connection issue, it returns connectionFailure and in case of wrong
	* credentials it returns loginFailure
	*/

	public int authenticate(String u, String p) {
		int result = createConnection();
		
		if(result==Constants.connectionFailure){
			return result;
		}
			
		try {
			PreparedStatement p1=conn.prepareStatement("select password from users where username=?;");
			p1.setString(1, u);
			ResultSet rs=p1.executeQuery();
			if(!rs.next()) {
				result=Constants.loginFailure;//1
			}
			else {
				if(p.compareTo((String)rs.getString(1)) == 0) {
					result=Constants.loginSuccess;//0
				}
			}
		} catch (Exception sqle) {
			result = Constants.internalError;
			System.out.println(sqle);
		}
		
		int con_result = closeConnection();
		if(con_result==Constants.connectionFailure){
			return con_result;
		}
		return result;
	}

	/**
	* returns the maximum experiment ID till now.
	*/
	public int getMaxExperimentID(String sid){
		int status = createConnection();
		if(status == Constants.connectionFailure) return -1;
		int expID =-1;
		PreparedStatement p;
		
		try {
			String Query ="SELECT max(id) FROM experiments where sid="+sid+";";
			p=conn.prepareStatement(Query);
			p.addBatch();				
			ResultSet rs = p.executeQuery();
			if(rs.next()){
				expID = rs.getInt(1);
				System.out.println("exprimentid = " + expID);
				//expID = rs.getInt(1);
			}
			System.out.println("DBManager.java: getMaxExperimentID result=" + expID );
			status = closeConnection();
			if(status == Constants.connectionFailure) return -1;

		} catch (SQLException sqle) {
			System.out.println(sqle);
			return -1;
		}
		
		return expID;
	}

	/**
	* returns the maximum session ID till now.
	*/
	public int getMaxSessionID(){
		int status = createConnection();
		if(status == Constants.connectionFailure) return -1;
		int sID =-1;
		PreparedStatement p;
		
		try {
			String Query ="SELECT max(id) FROM sessions;";
			p=conn.prepareStatement(Query);
			p.addBatch();				
			ResultSet rs = p.executeQuery();
			if(rs.next()){
				sID = rs.getInt(1);
				System.out.println("sessionid = " + sID);
				//expID = rs.getInt(1);
			}
			status = closeConnection();
			if(status == Constants.connectionFailure) return -1;

		} catch (SQLException sqle) {
			System.out.println(sqle);
			return -1;
		}
		
		return sID;
	}

	
	/**
	* Add Experiment details to the database coreesponding to experiment id 'expid' and details 
	* are the information regarding the device registered
	*/
	public int addExperimentDetail(int expID, DeviceInfo d, boolean fileReceived){
		int status = createConnection();
		if(status == Constants.connectionFailure) return -1;
		try {
	 		PreparedStatement p1=conn.prepareStatement("insert into experimentdetails values(?,?,?,?,?,?,?,?,?,?);");
			p1.setInt(1, expID);
			p1.setString(2, d.macAddress);
			p1.setInt(3, d.osVersion);
			p1.setString(4, d.wifiVersion);
			p1.setInt(5, d.numberOfCores);
			p1.setInt(6, d.storageSpace);
			p1.setInt(7, d.memory);
			p1.setInt(8, d.processorSpeed);
			p1.setInt(9, d.wifiSignalStrength);
			p1.setBoolean(10, fileReceived);
			p1.executeUpdate();
			status = closeConnection();
			return 0;
			
		} catch (SQLException sqle) {
			status = -1;
			System.out.println(sqle);
			//System.exit(1);
		}
		return -1;
	}
	

	/** 
	* When the log file is received from device 'macaddress' for the experiment number expid, 
	* corresponding fileReceived column in the relation 'experimentdetails' is set to true
	*/ 
	public int updateFileReceivedField(int expID, String macaddress, boolean fileReceived){
		int status = createConnection();
		if(status == Constants.connectionFailure) return -1;
		try {
	 		PreparedStatement p=conn.prepareStatement("update experimentdetails set filereceived=? where expid=? and macaddress=?;");
	 		p.setBoolean(1,fileReceived);
	 		p.setInt(2,expID);
	 		p.setString(3,macaddress);
	 		p.addBatch();
	 		p.executeUpdate();
	 		status = closeConnection();
			return 0;
	 		
	 	} catch (SQLException sqle) {
			System.out.println(sqle);
		}
		return -1;
	}

	public int updateTraceFileReceived(int expID, boolean fileReceived){
		int status = createConnection();
		if(status == Constants.connectionFailure) return -1;
		try {
	 		PreparedStatement p=conn.prepareStatement("update experiments set tracefilereceived=? where id=?;");
	 		p.setBoolean(1,fileReceived);
	 		p.setInt(2,expID);
	 		p.addBatch();
	 		p.executeUpdate();
	 		status = closeConnection();
			return 0;
	 		
	 	} catch (SQLException sqle) {
			System.out.println(sqle);
		}
		return -1;
	}
	
	/**
	* Add new entry in the 'experiments' retation for the experiment 'e'
	*/
	public int addExperiment(Experiment e, String sid){
		int status = createConnection();
		if(status == Constants.connectionFailure) return -1;
		try {
			long unixTime = System.currentTimeMillis() / 1000L;
	 		PreparedStatement p1=conn.prepareStatement("insert into experiments(name,location,description,user,filename,datetime,sid) values(?,?,?,?,?,?,?);");
			p1.setString(1, e.Name);
			p1.setString(2, e.Location);
			p1.setString(3, e.Description);
			p1.setString(4, e.User);
			p1.setString(5, e.FileName);
			p1.setLong(6, unixTime);
			p1.setInt(7,Integer.parseInt(sid));
			p1.executeUpdate();
			status = closeConnection();
			return 0;
			
		} catch (SQLException sqle) {
			status = -1;
			System.out.println(sqle);
		}
		return -1;
	}

	/**
	* Add new entry in the 'sessions' retation for the session 's'
	*/
	public int addSession(Session s){
		int status = createConnection();
		if(status == Constants.connectionFailure) return -1;
		try {
			long unixTime = System.currentTimeMillis() / 1000L;
	 		PreparedStatement p1=conn.prepareStatement("insert into sessions(name,description,user,datetime) values(?,?,?,?);");
			p1.setString(1, s.name);
			p1.setString(2, s.description);
			p1.setString(3, s.user);
			p1.setLong(4, unixTime);
			p1.executeUpdate();
			status = closeConnection();
			return 1;
			
		} catch (SQLException sqle) {
			status = -1;
			System.out.println(sqle);
		}
		return -1;
	}
	
	/**
	* Retuns the ResultSet of all the experiments corresponding to user='username' and session='sid'
	* returns null if no user with username 'username' exists
	*/
	public ResultSet getExperiments(String username,String sid){
		ResultSet rs = null;
		int status = createConnection();
		if(status == Constants.connectionFailure) return rs;
		PreparedStatement p;
		
		try {
			String Query ="select id, name, location, description, user, filename, FROM_UNIXTIME(datetime), "
			 			+ "tracefilereceived from experiments where user='" + username 
			 			+ "' and sid="+ sid + " ORDER BY datetime DESC;";
			p=conn.prepareStatement(Query);
			p.addBatch();				
			rs = p.executeQuery();
			//status = closeConnection();
			

		} catch (SQLException sqle) {
			System.out.println(sqle);
			return rs;
		}
		return rs;

	}


	public List<Integer> getExpIDs(String sid){
		ResultSet rs = null;
		int status = createConnection();
		if(status == Constants.connectionFailure) return null;
		PreparedStatement p;
		List<Integer> list = new ArrayList<Integer>();
		
		try {
			String Query ="select id from experiments where sid="+ sid + ";";
			p=conn.prepareStatement(Query);
			p.addBatch();				
			rs = p.executeQuery();
			if(rs.next()){
				do{
					list.add(new Integer(rs.getInt(1)));
				}while(rs.next());
				return list;
			}
		} catch (SQLException sqle) {
			System.out.println(sqle);
		}
		return list;
	}


	/**
	* Retuns the ResultSet of all the sessions corresponding to user 'username'
	* returns null if no user with username 'username' exists
	*/
	public ResultSet getSessions(String username){
		ResultSet rs = null;
		int status = createConnection();
		if(status == Constants.connectionFailure) return rs;
		PreparedStatement p;
		
		try {
			String Query ="select id, name, description, FROM_UNIXTIME(datetime)"
						+" from sessions where user='" +username+ "';";
			p=conn.prepareStatement(Query);
			p.addBatch();				
			rs = p.executeQuery();
			//status = closeConnection();
			

		} catch (SQLException sqle) {
			System.out.println(sqle);
			return rs;
		}
		return rs;

	}

	/**
	* Retuns the Session corresponding to sessionid='sid'
	* returns null if no session exists
	*/
	public Session getSession(String sid){
		ResultSet rs = null;
		int status = createConnection();
		if(status == Constants.connectionFailure) return null;
		Session s;
		PreparedStatement p;
		
		try {
			String Query ="select id, name, description, user"
						+" from sessions where id='" +sid+ "';";
			p=conn.prepareStatement(Query);
			p.addBatch();				
			rs = p.executeQuery();
			if(rs.next()){
				s=new Session(new Integer(rs.getInt(1)), rs.getString(2), rs.getString(3), rs.getString(4));
				return s;
			}

			

		} catch (SQLException sqle) {
			System.out.println(sqle);
			return null;
		}
		return null;
	}


	/**
	* returns ResultSet of all experiment details for the experiment number 'expid'
	* returns null if no details for experiment with id = expid found
	*/
	public ResultSet getExperimentDetails(int expid){
		ResultSet rs = null;
		int status = createConnection();
		if(status == Constants.connectionFailure) return rs;
		PreparedStatement p;
		
		try {
			String Query ="SELECT * FROM experimentdetails where expid=" +expid+ ";";
			p=conn.prepareStatement(Query);
			p.addBatch();				
			rs = p.executeQuery();
			

		} catch (SQLException sqle) {
			System.out.println(sqle);
			return rs;
		}
		return rs;

	}

	/**
	* Delete experiment from the relation 'experiments' and remove all its details
	*/
	public int deleteExperiment(int expid){
		int status = createConnection();
		if(status == Constants.connectionFailure) return -1;
		try {
	 		PreparedStatement p1=conn.prepareStatement("delete from experiments where id=?;");
			p1.setInt(1, expid);
			p1.executeUpdate();
			status = closeConnection();
			return 1;
			
		} catch (SQLException sqle) {
			System.out.println(sqle);
		}
		return -1;
	}

	/**
	* Delete Session from the relation 'sessions' and remove all its experiments
	*/
	public int deleteSession(int sid){
		int status = createConnection();
		if(status == Constants.connectionFailure) return -1;
		try {
	 		PreparedStatement p1=conn.prepareStatement("delete from sessions where id=?;");
			p1.setInt(1, sid);
			p1.executeUpdate();
			status = closeConnection();
			return 1;
			
		} catch (SQLException sqle) {
			System.out.println(sqle);
		}
		return -1;
	}
	
	/**
	* returns String containing name of event control file for experiment number 'expid'
	*/
	public String getEventFileOfExperiment(int expid){
		String result=Constants.ERRORFILE;
		int res = createConnection();
		
		if(res==Constants.connectionFailure){
			return Constants.ERRORFILE;
		}
		
		System.out.println("experiment id of filename" + expid);
		try {
			PreparedStatement p1=conn.prepareStatement("select filename from experiments where id=?;");
			p1.setInt(1, expid);
			ResultSet rs=p1.executeQuery();
			if(rs.next()) {
				System.out.println("filename1:"  +  result);
				result=(String)rs.getString(1);
			}
			else {
				System.out.println("filename2:"  +  result);
				result=Constants.ERRORFILE;
			}
		} catch (Exception sqle) {
			System.out.println("filename3:"  +  result);
			result = Constants.ERRORFILE;
			System.out.println(sqle);
		}
		
		int con_result = closeConnection();
		if(con_result==Constants.connectionFailure){
			System.out.println("filename4:"  +  result);
			return Constants.ERRORFILE;
		}
		return result;
	}

	public String[] testTIME(int expid){
		String[] datetime = new String[2];
		PreparedStatement p;
		int status = createConnection();
		
		if(status==Constants.connectionFailure){
			return null;
		}
		try {
			String Query ="SELECT FROM_UNIXTIME(thedate) FROM smart_date;";
			p=conn.prepareStatement(Query);
			p.addBatch();				
			ResultSet rs = p.executeQuery();
			if(rs.next()){
				java.sql.Date d = rs.getDate(1);
				java.sql.Time t = rs.getTime(1); 
				datetime[0] = d.toString();
				datetime[1] = t.toString();

				System.out.println("datetime = " + datetime[0]);
				//expID = rs.getInt(1);
			}
			status = closeConnection();
			if(status == Constants.connectionFailure) return null;

		} catch (SQLException sqle) {
			System.out.println(sqle);
			return null;
		}

		return datetime;
	}	
};
