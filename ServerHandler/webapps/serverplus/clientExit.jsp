<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="serverplus.*" %>

<% 	
	String macAddress = (String)request.getParameter(Constants.getMacAddress());
	int res = Handlers.ClientExit(macAddress);
	System.out.println("CientExit: result is : " + res) ;
%>

