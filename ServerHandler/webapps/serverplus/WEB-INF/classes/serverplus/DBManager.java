package serverplus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.io.FileReader;
import java.util.StringTokenizer;
import java.util.Scanner;
import java.io.File;
import java.util.Scanner;
import java.util.*;

/**
 *
 * @author sanchitgarg
 */
public class DBManager {
	
	public static String home = System.getenv("HOME");
    public Connection conn=null;
	
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://localhost/server";
	
	//  Database credentials
	static final String USER = "root";
	static final String PASS = "p";
    	
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
	
	public int getMaxExperimentID(){
		int status = createConnection();
		if(status == Constants.connectionFailure) return -1;
		int expID =-1;
		PreparedStatement p;
		
		try {
			String Query ="SELECT max(id) FROM experiments;";
			p=conn.prepareStatement(Query);
			p.addBatch();				
			ResultSet rs = p.executeQuery();
			if(rs.next()){
				expID = rs.getInt(1);
				System.out.println("exprimentid = " + expID);
				//expID = rs.getInt(1);
			}
			status = closeConnection();
			if(status == Constants.connectionFailure) return -1;

		} catch (SQLException sqle) {
			System.out.println(sqle);
			return -1;
		}
		
		return expID;
	}
	
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
	
	public int addExperiment(Experiment e){
		int status = createConnection();
		if(status == Constants.connectionFailure) return -1;
		try {
	 		PreparedStatement p1=conn.prepareStatement("insert into experiments(name,location,description) values(?,?,?);");
			p1.setString(1, e.Name);
			p1.setString(2, e.Location);
			p1.setString(3, e.Description);
			p1.executeUpdate();
			status = closeConnection();
			return 0;
			
		} catch (SQLException sqle) {
			status = -1;
			System.out.println(sqle);
		}
		return -1;
	}
	
	public ResultSet getExperiments(){
		ResultSet rs = null;
		int status = createConnection();
		if(status == Constants.connectionFailure) return rs;
		PreparedStatement p;
		
		try {
			String Query ="SELECT * FROM experiments;";
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
};
