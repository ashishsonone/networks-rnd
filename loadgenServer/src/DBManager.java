
import java.sql.Connection;
import java.sql.DriverManager;
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
	
	
};
