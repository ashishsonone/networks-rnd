<%
	String ssid = (String)request.getParameter("session");
	boolean __flag = true;
	if(session.getAttribute("username")== null || session.getAttribute("username")== "" ){
	    __flag = false;
	    response.sendRedirect("login.jsp");
	}
	
	else if(ssid!=null && !ssid.equals("")) {
		session.setAttribute("session",ssid);
	}

	else if(session.getAttribute("session")== null || session.getAttribute("session")== "" ){
		__flag = false;
		response.sendRedirect("session.jsp");	
	}

	if(__flag){
%>
