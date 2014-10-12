<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="serverplus.*" %>

<% 	
	Handlers.StopExperiment();
	response.sendRedirect("index.jsp");
%>

