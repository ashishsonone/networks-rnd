<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="serverplus.*" %>
<%@ include file="checksession.jsp" %>
<% 	
	Integer _ssid = new Integer(Integer.parseInt((String)session.getAttribute("session")));
	Session _session = (Main.getSessionMap()).get(_ssid);
	Handlers.StopExperiment(_session);
	response.sendRedirect("index.jsp");
%>

<%@ include file="closeBracket.msg" %>
