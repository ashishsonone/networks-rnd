<%@ page import="serverplus.*" %>
<%
	if(session.getAttribute("username")== null || session.getAttribute("username")== "" ){
		response.sendRedirect("login.jsp");
	}
	else if(Main.getRegisteredClients().size()<=0){
		response.sendRedirect("index.jsp");
	}
%>	
