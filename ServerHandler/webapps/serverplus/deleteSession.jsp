<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.io.*,java.util.*, javax.servlet.*" %>
<%@ page import="javax.servlet.http.*" %>
<%@ page import="org.apache.commons.io.FileUtils" %>

<%@ page import="serverplus.*" %>


<%@ include file="checksession2.jsp" %>

<%
	String username= (String)session.getAttribute("username");
	String sid = (String)request.getParameter("session");
	session.removeAttribute("session");

	if(sid==null || sid=="") {
		response.sendRedirect("session.jsp"); System.out.println("deletesession.jsp: sid is null or empty");
	}

	else{
		System.out.println("session " + sid +" is not null");
		int res = Handlers.DeleteSession(sid);

		if(res==1){
			System.out.println("deletesession.jsp:Session "+sid+" couldnot be deleted as experiment is running");
%>
	
	<html lang="en">
		<body>
			<script type="text/javascript">
				alert("Experiment is running. Stop the experiment and then delete session");
			</script>
		</body>
	</html>

<%			
		}
		
		response.sendRedirect("session.jsp");
	}
%>

<%@ include file="closeBracket.msg" %>