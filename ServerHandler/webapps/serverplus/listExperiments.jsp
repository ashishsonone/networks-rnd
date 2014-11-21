<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="serverplus.*" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.concurrent.ConcurrentHashMap" %>
<%@ page import="java.sql.*" %>
<%@ include file="checksession2.jsp" %>

<%
	String username = (String)session.getAttribute("username");
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
			<div>
				<a href="index.jsp">Back</a> 
			</div>
			
			<div>
				
<%
	DBManager db = new DBManager();
	ResultSet rs = db.getExperiments(username);
	
	
	if(rs==null){
		out.print("<h4>There are no experiments yet...</h4>");
	}
	else{
%>		
				<table class="table">
					<thead>
						<tr>
							<th>Id</th>
							<th>Name</th>
							<th>Location</th>
							<th>Description</th>
							<th>Event File </th>
							<th>Delete Experiment</th>
						</tr>
					</thead>
					<tbody>
<%
		while(rs.next()){
%>				
					<tr>
						<td class="span1"><%out.print("<a href=\"experimentDetails.jsp?" + Constants.getExpID() +"="+ rs.getInt(1) 
							+ "\">" + rs.getInt(1) + " </a>");%>
						</td> 
						
						<td class="span3"><%out.print(""+rs.getString(2));%></td>   
						<td class="span2"><%out.print(""+rs.getString(3));%></td>   
						<td class="span5"><%out.print(""+rs.getString(4));%></td>
						<td class="span2"><%out.print("<a href=\"download.jsp?" + Constants.getExpID() +"="+ rs.getInt(1) 
							+ "&download=event\" > Download </a>");%>
						</td>
						<td class="span2"><%out.print("<a href=\"deleteExperiment.jsp?" + Constants.getExpID() +"="+ rs.getInt(1) 
							+ "\" > Delete</a>");%>
						</td>
					</tr>
<%				
		}
		db.closeConnection();
	}
%>
					</tbody>
				</table>
			</div>
		</div>
	</div>
	
	
	<%@ include file="footer.jsp" %>
    <script type="text/javascript" src="./js/jquery.min.js"></script>
    <script type="text/javascript" src="./js/bootstrap.min.js"></script>
   
  </body>
</html>

<%@ include file="closeBracket.msg" %>
