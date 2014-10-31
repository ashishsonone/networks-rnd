<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.Map" %>
<%@ page import="serverplus.*" %>

<%@ include file="checksession2.jsp" %>

<% 
	String username = (String)session.getAttribute("username");
	String password = (String)session.getAttribute("password");
	int size = Main.getRegisteredClients().size();
	session.removeAttribute("session");
	
%>

<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="ServerHandler">
    <meta name="IITB" content="IITB Wi-Fi Load Generator">
    <title>ServerHandler</title>
	
	<link type="text/css" rel="stylesheet" href="./css/bootstrap.min.css" />
	<link type="text/css" rel="stylesheet" href="./css/bootstrap-responsive.min.css" />
	<link type="text/css" rel="stylesheet" href="./css/font-awesome.css" />
	<link type="text/css" rel="stylesheet" href="./css/font-awesome-ie7.css" />
	<link type="text/css" rel="stylesheet" href="./css/boot-business.css" />
  </head>

  <body>

  <%@ include file="header.jsp" %>  
    

    <div class="content">
      	<div class="container">
      
	        <div class="page-header">        
				<h1>Load Generator's Server Handler</h1>
			</div>
			
			
			<div class="container-fluid">
				<div class="row-fluid">
					<div class="span6">

						<h4>List of all current sessions</h4>
					 		<table class="table">
								<thead>
									<tr>
										<th>Id</th>
										<th>Name</th>
										<th>Delete</th>
									</tr>
								</thead>
								<tbody>
									
									<%
										for (Map.Entry<Integer, Session> e : Main.getSessionMap().entrySet()) {
											Session s = e.getValue();
											if(!username.equals(s.getUser())){
												continue;
											}
											String link = "<a href=\"index.jsp?session=" +  s.getSessionID()  + "\"> " 
													+  s.getName()  +"  </a>";
									%>
										<tr>
									<%		
											out.print("<td>" + s.getSessionID() + "</td>");
											out.print("<td>" + link + "</td>");
											out.print("<td>" + "<a href=\"deleteSession.jsp?session=" 
											+ s.getSessionID() + "\">" + "Delete" + "</td>");
									%>
										</tr>
									<%		
										}

									%>
								</tbody>
							</table>
					 
					</div>
					<div class="span6">
										
						<div>
							<h4>Create new Session</h4>
							<form method="post" action="createSession.jsp" class="form-horizontal form-signin-signup">
								<input type="text" name="sessionName" placeholder="Enter Session Name(Required)" required>
								<input type="submit" name="createSession" value="Create Session" class="btn btn-primary btn-large">
							</form>
						</div>
					</div>
				</div>
			</div>     
		</div>
    </div>

	<%@ include file="footer.jsp" %>
    <script type="text/javascript" src="./js/jquery.min.js"></script>
    <script type="text/javascript" src="./js/bootstrap.min.js"></script>
   
  </body>
</html>

      
