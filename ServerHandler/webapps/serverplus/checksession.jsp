<%
String ssid = (String)request.getParameter("session");
boolean __flag = false;
if(session.getAttribute("username")== null || session.getAttribute("username")== "" ){
    response.sendRedirect("login.jsp");

}
else if(ssid!=null && !ssid.equals("")) {
	session.setAttribute("session",ssid);
	__flag=true;
}
else if(session.getAttribute("session")== null || session.getAttribute("session")== "" ){
	response.sendRedirect("session.jsp");	
}
else{
	__flag=true;
}
if(!__flag){}
else{
%>
