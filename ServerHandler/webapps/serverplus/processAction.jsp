<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="serverplus.*" %>

<%@ include file="checksession.jsp" %>

<%
	int result = -1;
	//int sessionID = Integer.parseInt((String)session.getAttribute("session"));

	Integer _ssid = new Integer(Integer.parseInt((String)session.getAttribute("session")));
	Session _session = (Main.getSessionMap()).get(_ssid);

	if(request.getParameter("startRegistration")!=null){
		result = Handlers.StartRegistration(_session);
		if(result == 0)
			response.sendRedirect("index.jsp");
	}
	else if(request.getParameter("stopRegistration")!=null){
		result = Handlers.StopRegistration(_session);
		if(result == 0)
			response.sendRedirect("index.jsp");
	}
	
	else if(request.getParameter("stopExperiment")!=null){
		result = Handlers.StopExperiment(_session);
		if(result == 0)
			response.sendRedirect("index.jsp");
	}
	
	else if(request.getParameter("addExperiment")!=null){
		response.sendRedirect("addExperiment.jsp");
	}
	
	else if(request.getParameter("clearRegistration")!=null){
		Handlers.ClearRegistrations(_session);
		response.sendRedirect("index.jsp");
	}
}
%>
