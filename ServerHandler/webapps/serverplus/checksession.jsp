<%
String ssid = (String)request.getParameter("session");
if(session.getAttribute("username")== null || session.getAttribute("username")== "" ){
    response.sendRedirect("login.jsp");
}
else if(ssid!=null && !ssid.equals("")) {
	session.setAttribute("session",ssid);
}
else if(session.getAttribute("session")== null || session.getAttribute("session")== "" ){
	response.sendRedirect("session.jsp");	
}
else{
	
}
%>
