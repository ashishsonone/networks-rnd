<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="serverplus.*" %>

<%@ include file="checksession2.jsp" %>

<% 
	String username = (String)session.getAttribute("username");
	String password = (String)session.getAttribute("password");

	String sessionName = (String)request.getParameter("sessionName");
	Integer sessionID = Handlers.CreateSession(username,sessionName);

	if(sessionID==Constants.getNOTOK()){
		System.out.println("session could not be created");
		response.sendRedirect("session.jsp");
	}
	else{
		session.setAttribute("session", sessionID);
		response.sendRedirect("index.jsp");
	}

%>
