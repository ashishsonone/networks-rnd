<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="serverplus.*" %>

<%@ include file="checksession.jsp" %>

<% 
	String username = (String)session.getAttribute("username");
	String password = (String)session.getAttribute("password");
	String sessionid= (String)session.getAttribute("session");

	if(sessionid==null || sessionid==""){
		response.sendRedirect("session.jsp");
	}
	else{
		Session curSession = Utils.getSession(sessionid);
		Handlers.RefreshRegistrations(curSession);
		response.sendRedirect("index.jsp");
	}
	
%>

<%@ include file="closeBracket.msg" %>