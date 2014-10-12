<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="serverhandler.*" %>
<%
    String username = request.getParameter("username");
    String password = request.getParameter("password");
    if(session.getAttribute("username")!= null){
        response.sendRedirect("index.jsp");
    }
    DBManager db = new DBManager();
    int v = Hub.authenticate(username, password);//0 fail, 1 read, 2 write
    if(v == Constants.getLoginSuccess()){
        session.setAttribute( "username", username );
        response.sendRedirect("index.jsp");
    }
%>
    Login Failed <br>
    <h4><a href="login.jsp"> Try Login Again </a></h4>
