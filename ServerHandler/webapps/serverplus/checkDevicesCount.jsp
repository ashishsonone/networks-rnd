<%@ page import="serverplus.*" %>
<%
	String _ssidStr = (String)session.getAttribute("session");
	Session _session1 = null;
	boolean __flag = true;

	if(session.getAttribute("username")== null || session.getAttribute("username")== "" ){
		__flag=false;
		response.sendRedirect("login.jsp");
	}

	else if(_ssidStr==null || _ssidStr.equals("")) {
		__flag=false;
		response.sendRedirect("session.jsp");
	}
		
	else{	
		Integer _ssid = new Integer(Integer.parseInt(_ssidStr));
		_session1 = (Main.getSessionMap()).get(_ssid);

		if(_session1.getRegisteredClients().size()<=0){
			__flag=false;
			response.sendRedirect("index.jsp");
		}

	}
	if(__flag){

%>	
