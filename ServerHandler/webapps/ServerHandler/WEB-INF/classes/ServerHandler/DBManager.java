package ServerHandler;

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
	static final String DB_URL = "jdbc:mysql://localhost/serverhandler";
	
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
	
	private int closeConnection(){
		
		try{
			if(conn!=null){
				conn.close();
			}
			return Constants.connectionSuccess;
		}catch(SQLException se){
			se.printStackTrace();
		}
		return Constants.connectionFailure;
	}
	
	public int loginVerification(String u, String p) {
		int result = createConnection();
		
		if(result==Constants.connectionFailure){
			return result;
		}
			
		try {
			PreparedStatement p1=conn.prepareStatement("select password from users where username=?");
			p1.setString(1, u);
			ResultSet rs=p1.executeQuery();
			if(!rs.next()) {
				result=Constants.loginFailure;
			}
			else {
				if(p.equals((String)rs.getString(1))) {
					result=Constants.loginSuccess;
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
};
