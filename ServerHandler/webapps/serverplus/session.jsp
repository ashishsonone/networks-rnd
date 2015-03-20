<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.Map" %>
<%@page import="java.util.Calendar" %>
<%@page import="java.util.Date" %>
<%@page import="java.text.SimpleDateFormat" %>
<%@ page import="java.sql.*" %>

<%@ page import="serverplus.*" %>

<%@ include file="checksession2.jsp" %>

<% 
	String username = (String)session.getAttribute("username");
	String password = (String)session.getAttribute("password");
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

	<script type="text/JavaScript">
		<!--
		function AutoRefresh( t ) {
			setTimeout("location.reload(true);", t);
		}
		//   -->
	</script>

  </head>

  <body onload="JavaScript:AutoRefresh(10000);">

  <%@ include file="header2.jsp" %>  
    

    <div class="content">
      	<div class="container">
      
	        <div class="page-header">        
				<h1>Load Generator's Server Handler</h1>
			</div>
			
			
			<div class="container-fluid">
				<div class="row-fluid">
					<div class="span8">

<%
	DBManager db = new DBManager();
	ResultSet rs = db.getSessions(username);
	
	
	if(!rs.next()){
		out.print("<h4>There are no Sessions yet...</h4>");
	}
	else{
%>		
				<table class="table">
					<thead>
						<tr>
							<th>Id</th>
							<th>Name</th>
							<th>Description</th>
							<th>Date</th>
							<th>Time</th>
							<th>Status</th>
							
							<th>Delete Session</th>
						</tr>
					</thead>
					<tbody>
<%
		System.out.println("No Experiments");
		do{
			String link = "<a href=\"index.jsp?session=" + rs.getInt(1) + "\"> "
							+ rs.getString(2) +" </a>";
			String status = "Running";
			Integer sint = new Integer(rs.getInt(1));
			Session s = Main.getSessionMap().get(sint);
			if(s==null || s.getCurrentExperiment()<0) status="Stopped";
%>				
					<tr>
						<td class="span1"><%out.print(""+rs.getInt(1));%></td>
						<td class="span2"><%out.print(link);%></td>   
						<td class="span6"><%out.print(""+rs.getString(3));%></td>   
						<td class="span2"><%out.print(""+rs.getDate(4).toString());%></td>
						<td class="span2"><%out.print(""+rs.getTime(4).toString());%></td>
						<td class="span2"><%out.print(status);%></td>
						<td class="span2"><%out.print("<a href=\"deleteSession.jsp?session="
								+ rs.getInt(1) + "\" onClick=\"confirm('Press OK to Delete session " 
								+ rs.getInt(1)+ " ')\">" + "Delete</a>");%></td>
					</tr>
<%					
		}while(rs.next());
%>
					</tbody>
				</table>
<%		
	}
	db.closeConnection();
%>
					 
					</div>
					<div class="span4">
										
						<div>
							<h4>Create new Session and select its duration (in hr)</h4>
							<form method="post" action="createSession.jsp" class="form-horizontal form-signin-signup">
								<input type="text" name="sessionName" placeholder="Enter Session Name(Required)" required/>
								<input type="text" name="description" placeholder="Enter Description(Required)" required/>
								
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
<%@ include file="closeBracket.msg" %>
