<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="serverplus.*" %>

<% 	
	System.out.println("In clientExit.jsp");
	String s = (String)request.getParameter(Constants.getSessionID());
	if(s==null){
		System.out.println("session id received is null");
	}
	else{
		Integer _ssid = new Integer(Integer.parseInt(s));
		String macAddress = (String)request.getParameter(Constants.getMacAddress());

		System.out.println("sessionid: " + _ssid + " , macAddress: "+ macAddress);
		Session _session = (Main.getSessionMap()).get(_ssid);

		if(_session!=null){
			int res = Handlers.ClientExit(macAddress,_session);
			System.out.println("CientExit: result is : " + res);
		}
	} 
%>

