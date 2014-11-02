<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.io.*,java.util.*, javax.servlet.*" %>
<%@ page import="javax.servlet.http.*" %>
<%@ page import="org.apache.commons.io.FileUtils" %>

<%@ page import="serverplus.*" %>

<%@ include file="checksession.jsp" %>

<%
	String username= (String)session.getAttribute("username");
	String exp = (String)request.getParameter(Constants.getExpID());

	Integer _ssid = new Integer(Integer.parseInt((String)session.getAttribute("session")));
	Session _session = (Main.getSessionMap()).get(_ssid);

	if(exp==null) response.sendRedirect("index.jsp");
	int expid = Integer.parseInt(exp);
	if(_session.isExperimentRunning() && _session.getCurrentExperiment()==expid){
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

}	//checksession.jsp ke if wala
	
%>
