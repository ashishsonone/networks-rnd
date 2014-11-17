package serverplus;
 
import javax.servlet.*;
import javax.servlet.http.HttpServlet;

/**
 *
 * @author sanchitgarg
 * This class initializes the pool of free Session IDs(Described in Main.java). 
 * The method init() is called when the servlet starts.
 */
@SuppressWarnings("serial")
public class Initializer extends HttpServlet{
 
    public void init() throws ServletException{
    	System.out.println("Callinf Initializer");
        for(int i =0;i<Constants.maxSessions;i++)
        	(Main.freeSessions).add(i);
        System.out.println("size of freeSessions list: " + Main.freeSessions.size());        	
    }
}