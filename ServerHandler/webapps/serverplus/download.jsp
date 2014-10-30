<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.io.*,java.util.*, javax.servlet.*" %>
<%@ page import="javax.servlet.http.*" %>
<%@ page import="javax.servlet.ServletOutputStream" %>
<%@ page import="serverplus.*" %>
<%@ page import="org.apache.commons.io.*" %>

<%@ include file="checksession.jsp" %>

<%
	String username= (String)session.getAttribute("username");

	String expid = (String)request.getParameter(Constants.getExpID());
	String download = (String)request.getParameter("download");
	String filePath = Constants.getMainExpLogsDir()+expid+"/";
	
	System.out.println("expid: " + expid + ". download: " + download + ". filePath: "+filePath);

	response.setContentType("application/octet-stream");

	if(download.equals("event")){
		filePath = filePath+Constants.getEventFile();
		response.setHeader("Content-Disposition", "attachment;filename=Exp" + expid + "_" + Constants.getEventFile());
	}
	else if(download.equals("log")){
		String macAddress = (String)request.getParameter(Constants.getMacAddress());
		filePath = filePath + macAddress;
		response.setHeader("Content-Disposition", "attachment;filename=Exp" + expid + "_" + macAddress + ".log");
	}

	File file = new File(filePath);
	FileInputStream fileIn = new FileInputStream(file);
	ServletOutputStream out1 = response.getOutputStream();
	 
	IOUtils.copy(fileIn, out1);

/*
	byte[] outputByte = new byte[4096];
	//copy binary contect to output stream
	while(fileIn.read(outputByte, 0, 4096) != -1){
		out1.write(outputByte, 0, 4096);
	}
*/
	fileIn.close();
	out1.flush();
	out1.close();




%>