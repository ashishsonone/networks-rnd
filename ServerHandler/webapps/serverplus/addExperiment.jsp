<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="serverplus.*" %>

<%@ include file="checksession.jsp" %>

<% 
	String username = (String)session.getAttribute("username");
	String password = (String)session.getAttribute("password");
	
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
					 <div>
						 <h4>Add Experiment</h4>
						<form method="post" action="startExperiment.jsp" class="form-horizontal form-signin-signup">
							<input type="file" name="eventsFile" placeholder="Upload Event File" required> <br>
							<input type="text" name="expname" placeholder="Experiment Name" required>
							<input type="text" name="location" placeholder="Location of Experiment" required>
							<input type="text" name="description" placeholder="Add Description">
							<input type="submit" name="startExperiment" value="Start Experiment" class="btn btn-primary btn-large">
						</form>
					</div>
									 
					 
					</div>
					<div class="span6">
						<div>
							<h4>Summary</h4>
							<p> Number of <a title="click here to list devices" href="listDevices.jsp">Devices </a> registed: <% out.print(Main.getRegisteredClients().size()); %> </p>
							<p> List all <a title="click here to list experiments" href="listExperiments.jsp">Experiments </a> </p>
							<p> .... </p>
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

      
