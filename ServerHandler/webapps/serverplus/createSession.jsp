<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="serverplus.*" %>

<%@ include file="checksession2.jsp" %>

<% 
	String username = (String)session.getAttribute("username");
	String password = (String)session.getAttribute("password");

	String sessionName = (String)request.getParameter("sessionName");
	String _dur_ = (String)request.getParameter("duration");
	int _duration_ = Integer.parseInt(_dur_);
	Integer sessionID = Handlers.CreateSession(username,sessionName,_duration_);

	if(sessionID==Constants.getNOTOK()){
		System.out.println("session could not be created");
		response.sendRedirect("session.jsp");
	}
	else{
		session.setAttribute("session", Integer.toString(sessionID));
		response.sendRedirect("index.jsp");
	}
%>

<%@ include file="closeBracket.msg" %>
