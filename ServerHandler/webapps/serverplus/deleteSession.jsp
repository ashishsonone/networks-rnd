<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.io.*,java.util.*, javax.servlet.*" %>
<%@ page import="javax.servlet.http.*" %>
<%@ page import="org.apache.commons.io.FileUtils" %>

<%@ page import="serverplus.*" %>

<%@ include file="checksession2.jsp" %>

<%
	String username= (String)session.getAttribute("username");
	String ss = (String)request.getParameter("session");
	session.removeAttribute("session");
	System.out.println("deletesession: " + (String)session.getAttribute("session"));
	if(ss==null){
		System.out.println("sending to index");
		response.sendRedirect("deletesession.jsp: index.jsp");
	}
	else{
		Integer ssid1 = Integer.parseInt(ss);
		int res = Handlers.DeleteSession(ssid1);
		System.out.println("deletesession.jsp: DeleteSession result= " + res);
		System.out.println("deletesession.jsp: sending to session");
		response.sendRedirect("session.jsp");
	}
%>

<%@ include file="closeBracket.msg" %>
