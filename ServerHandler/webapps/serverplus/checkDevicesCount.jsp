<%@ page import="serverplus.*" %>
<%
	Integer _ssid = new Integer(Integer.parseInt((String)session.getAttribute("session")));
	Session _session1 = (Main.getSessionMap()).get(_ssid);
	if(session.getAttribute("username")== null || session.getAttribute("username")== "" ){
		response.sendRedirect("login.jsp");
	}

	else if(_session1.getRegisteredClients().size()<=0){
		response.sendRedirect("index.jsp");
	}

	{
%>	
