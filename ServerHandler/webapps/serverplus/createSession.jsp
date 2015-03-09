<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="serverplus.*" %>

<%@ include file="checksession2.jsp" %>

<% 
	String username = (String)session.getAttribute("username");
	String password = (String)session.getAttribute("password");

	String sessionName = (String)request.getParameter("sessionName");
	String description = (String)request.getParameter("description");
	
	Integer sessionID = Handlers.CreateSession(sessionName,description,username);

	if(sessionID<0){
		System.out.println("createSession.jsp: session could not be created. ID = " + sessionID);
		response.sendRedirect("session.jsp");
	}
	else{
		session.setAttribute("session", Integer.toString(sessionID));
		response.sendRedirect("index.jsp");
	}
%>

<%@ include file="closeBracket.msg" %>
