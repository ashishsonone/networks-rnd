
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
    	
	public int createConnection() {
 
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
			}
			return Constants.connectionSuccess;
		}catch(SQLException se){
			se.printStackTrace();
		}
		return Constants.connectionFailure;
	}
	
	public int getMaxExperimentID(){
		int status = createConnection();
		if(status == Constants.connectionFailure) return -1;
		int expID =-1;
		PreparedStatement p;
		
		try {
			String Query ="SELECT max(expID) FROM experimentDetails;";
			p=conn.prepareStatement(Query);
			p.addBatch();				
			ResultSet rs = p.executeQuery();
			if(rs.next()){
				expID = rs.getInt(1);
				System.out.println("exprimentid = " + expID);
				//expID = rs.getInt(1);
			}
			else{
				expID=0;
			}
			status = closeConnection();
			if(status == Constants.connectionFailure) return -1;

		} catch (SQLException sqle) {
			expID = -1;
			System.out.println(sqle);
		}
		
		return expID;
	}
	
	public int addExperimentDetail(int expID, DeviceInfo d, boolean fileReceived){
		int status = createConnection();
		if(status == Constants.connectionFailure) return -1;
		try {
	 		PreparedStatement p1=conn.prepareStatement("insert into experimentDetails values(?,?,?,?,?,?,?,?,?,?,?);");
			p1.setInt(1, expID);
			p1.setString(2, d.macAddress);
			p1.setString(3, d.osVersion);
			p1.setString(4, d.wifiVersion);
			p1.setInt(5, d.numberOfCores);
			p1.setInt(6, d.storageSpace);
			p1.setInt(7, d.memory);
			p1.setInt(8, d.processorSpeed);
			p1.setInt(9, d.wifiSignalStrength);
			p1.setBoolean(10, d.packetCaptureAppUsed);
			p1.setBoolean(11, fileReceived);
			p1.executeUpdate();
			status = closeConnection();
			if(status == Constants.connectionFailure) return -1;
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
	 		PreparedStatement p=conn.prepareStatement("update experimentDetails set fileReceived=? where expID=? and macaddress=?;");
	 		p.setBoolean(1,fileReceived);
	 		p.setInt(2,expID);
	 		p.setString(3,macaddress);
	 		p.addBatch();
	 		p.executeUpdate();
	 		status = closeConnection();
			if(status == Constants.connectionFailure) return -1;
			return 0;
	 		
	 	} catch (SQLException sqle) {
			System.out.println(sqle);
		}
		return -1;
	}
	
};
