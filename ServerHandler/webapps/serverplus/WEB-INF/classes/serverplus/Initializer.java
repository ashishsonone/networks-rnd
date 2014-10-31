package serverplus;
 
import javax.servlet.*;
import javax.servlet.http.HttpServlet;

 
@SuppressWarnings("serial")
public class Initializer extends HttpServlet{
 
    public void init() throws ServletException{
    	System.out.println("Callinf Initializer");
        for(int i =0;i<Constants.maxSessions;i++)
        	(Main.freeSessions).add(i);
        System.out.println("size of freeSessions list: " + Main.freeSessions.size());        	
    }
}