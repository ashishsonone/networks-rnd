<%
boolean __flag = false;
String ssid = (String)request.getParameter("session");
if(session.getAttribute("username")== null || session.getAttribute("username")== "" ){
    response.sendRedirect("login.jsp");
}
else{
	__flag=true;
}
if(!__flag){}
else{
%>