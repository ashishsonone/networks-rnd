<%
if(session.getAttribute("username")== null || session.getAttribute("username")== "" ){
    response.sendRedirect("login.jsp");
}
%>
