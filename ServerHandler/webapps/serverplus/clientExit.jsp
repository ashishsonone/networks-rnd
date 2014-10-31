<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="serverplus.*" %>

<% 	
	Integer _ssid = new Integer(Integer.parseInt((String)session.getAttribute("session")));
	Session _session = (Main.getSessionMap()).get(_ssid);
	String macAddress = (String)request.getParameter(Constants.getMacAddress());
	int res = Handlers.ClientExit(macAddress,_session);
	System.out.println("CientExit: result is : " + res) ;
%>

