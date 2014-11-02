<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.io.*,java.util.*, javax.servlet.*" %>
<%@ page import="javax.servlet.http.*" %>
<%@ page import="org.apache.commons.io.FileUtils" %>

<%@ page import="serverplus.*" %>

<%@ include file="checksession2.jsp" %>

<%
	String username= (String)session.getAttribute("username");
	String exp = (String)request.getParameter(Constants.getExpID());


	if(exp==null) response.sendRedirect("index.jsp");
	else{
		int expid = Integer.parseInt(exp);
		Integer _expid = new Integer(expid);
		//check if experiment is running
		boolean exprunning = Main.getRunningExperimentMap().containsKey(_expid);

		if(exprunning){
			response.sendRedirect("index.jsp");
		}
		
		else{
			DBManager db = new DBManager();
			int res = db.deleteExperiment(expid);
			
			FileUtils.deleteDirectory(new File(Constants.getMainExpLogsDir() + expid));
			
			if(res>0) response.sendRedirect("listExperiments.jsp");
			else{
				response.sendRedirect("index.jsp");
			}
		}
	}

}	//checksession.jsp ke if wala
	
%>
