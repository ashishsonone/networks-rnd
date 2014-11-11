<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="serverplus.*" %>
<%@ include file="checksession.jsp" %>
<% 	
	String ename=request.getParameter("expname");
	String loc=request.getParameter("location");
	String des=request.getParameter("description");
	int sessionID = Integer.parseInt(request.getParameter("session"));
	Integer ssid = new Integer(sessionID);
	int result=0;
	if(ename==null || loc==null || des==null) result =-1;
	
	Experiment e = null;
	
	Session curSession = (Main.getSessionMap()).get(ssid);
	if(curSession.isExperimentRunning()){
		result = -2;
	}
	else{
		e=new Experiment(ename, loc, des);
		result = Handlers.StartExperiment(e,curSession);
	}
	
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
<%	if(result>=0 && e!=null){
%>	
					<div>
						 <h4>Experiment Started.. <% out.print(result + " " + e.getName() + " " + e.getLocation() + " " + e.getDescription()); %></h4>
						 <form method="post" action="stopExperiment.jsp" class="form-horizontal form-signin-signup">
							<input type="submit" name="stopExperiment" value="Stop Experiment" class="btn btn-primary btn-large">
						</form>
					</div>
<%
	}
	else if(result==-2){
%>
					 <div>
						 <h4>A Experiment with id <%out.print(curSession.getCurrentExperiment());%> already running</h4>
					</div>
<%
	}
	else{
%>
					<div>
						 <h4>Experiment Failed to Start</h4>
					</div>
<%
	}
%>
					</div>
					<div class="span6">
						<div>
							<h4>Summary</h4>
							<p> Number of <a title="click here to list devices" href="listDevices.jsp">Devices </a> registed: <% out.print((curSession.getRegisteredClients()).size()); %> </p>
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
<%@ include file="closeBracket.msg" %>
      

