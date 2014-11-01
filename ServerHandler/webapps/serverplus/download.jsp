<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.io.*,java.util.*, javax.servlet.*" %>
<%@ page import="javax.servlet.http.*" %>
<%@ page import="javax.servlet.ServletOutputStream" %>
<%@ page import="serverplus.*" %>
<%@ page import="org.apache.commons.io.*" %>
<%@ page import="java.net.URL" %>
<%@ page import="java.net.URLEncoder" %>

<%@ include file="checksession.jsp" %>

<%
	String username= (String)session.getAttribute("username");

	String expid = (String)request.getParameter(Constants.getExpID());
	String download = (String)request.getParameter("download");
	String filePath = Constants.getMainExpLogsDir()+expid+"/";
	
	

	response.setContentType("application/octet-stream");

	if(download.equals("event")){
		filePath = filePath+Constants.getEventFile();
		System.out.println("expid: " + expid + ". download: " + download + ". filePath: "+filePath);
		response.setHeader("Content-Disposition", "attachment;filename=Exp" + expid + "_" + Constants.getEventFile());
	}
	else if(download.equals("log")){
		String macAddress = (String)request.getParameter(Constants.getMacAddress());
		filePath = filePath + macAddress;
		System.out.println("expid: " + expid + ". download: " + download + "macAddress: " + macAddress +". filePath: "+filePath);
		response.setHeader("Content-Disposition", "attachment;filename=Exp" + expid + "_" 
							+ macAddress + ".log");
	}

	File file = new File(filePath);
	FileInputStream fileIn = new FileInputStream(file);
	ServletOutputStream out1 = response.getOutputStream();
	 
	IOUtils.copy(fileIn, out1);

	fileIn.close();
	out1.flush();
	out1.close();


}	//checksession.jsp ke if wala

%>