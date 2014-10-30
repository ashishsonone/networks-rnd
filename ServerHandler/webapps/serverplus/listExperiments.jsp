<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="serverplus.*" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.concurrent.ConcurrentHashMap" %>
<%@ page import="java.sql.*" %>
<%@ include file="checksession.jsp" %>

<%
	String username = (String)session.getAttribute("username");
	ConcurrentHashMap<String, DeviceInfo> registeredClients = Main.getRegisteredClients();
	int size = registeredClients.size();

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
						</tr>
					</thead>
					<tbody>
<%
		while(rs.next()){
%>				
					<tr>
						<td><%out.print(""+rs.getInt(1));%></td>  
						<td><%out.print(""+rs.getString(2));%></td>   
						<td><%out.print(""+rs.getString(3));%></td>   
						<td><%out.print(""+rs.getString(4));%></td>
						<td><%out.print("<a href=\"download.jsp?" + Constants.getExpID() +"="+ rs.getInt(1) 
							+ "&download=event\" > Download </a>");%>
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

