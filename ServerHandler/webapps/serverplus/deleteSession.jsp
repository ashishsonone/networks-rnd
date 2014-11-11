<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.io.*,java.util.*, javax.servlet.*" %>
<%@ page import="javax.servlet.http.*" %>
<%@ page import="org.apache.commons.io.FileUtils" %>

<%@ page import="serverplus.*" %>

<%@ include file="checksession2.jsp" %>

<%
	String username= (String)session.getAttribute("username");
	String ss = (String)request.getParameter("session");
	if(ss==null){
		response.sendRedirect("index.jsp");
	}
	else{
		Integer ssid1 = Integer.parseInt(ss);
		int res = Handlers.DeleteSession(ssid1);
		System.out.println("deletesession.jsp: DeleteSession result= " + res);
		response.sendRedirect("session.jsp");
	}
%>

<%@ include file="closeBracket.msg" %>
