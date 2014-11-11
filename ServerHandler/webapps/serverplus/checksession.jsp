<%
	String ssid = (String)request.getParameter("session");
	boolean __flag = true;
	if(session.getAttribute("username")== null || ((String)session.getAttribute("username")).equals("") ){
		System.out.println("checksession.jsp: " + "username is null");
	    __flag = false;
	    response.sendRedirect("login.jsp");
	}
	
	else if(ssid!=null && !ssid.equals("")) {
		System.out.println("checksession.jsp: " + "sessionid is=" + ssid);
		session.setAttribute("session",ssid);
	}

	else if(session.getAttribute("session")== null || ((String)session.getAttribute("session")).equals("")){
		System.out.println("checksession.jsp: " + "sessionid is null");
		__flag = false;
		response.sendRedirect("session.jsp");	
	}

	if(__flag){
		
%>
