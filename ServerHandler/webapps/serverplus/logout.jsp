<%
    session.removeAttribute("username");
    session.removeAttribute("password");
    session.removeAttribute("session");
    response.sendRedirect("login.jsp");
%>
