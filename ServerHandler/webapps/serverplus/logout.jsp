<%
    session.removeAttribute("username");
    session.removeAttribute("session");
    response.sendRedirect("login.jsp");
%>
