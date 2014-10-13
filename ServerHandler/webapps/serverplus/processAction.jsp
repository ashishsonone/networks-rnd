<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="serverplus.*" %>

<%@ include file="checksession.jsp" %>

<%
	int result = -1;
	if(request.getParameter("startRegistration")!=null){
		result = Handlers.StartRegistration();
		if(result == 0)
			response.sendRedirect("index.jsp");
	}
	else if(request.getParameter("stopRegistration")!=null){
		result = Handlers.StopRegistration();
		if(result == 0)
			response.sendRedirect("index.jsp");
	}
	else if(request.getParameter("clearRegistration")!=null){
		Handlers.ClearRegistrations();
		response.sendRedirect("index.jsp");
	}

%>
