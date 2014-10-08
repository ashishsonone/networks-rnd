<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="ServerHandler.*" %>

<%
	String username = (String)session.getAttribute("username");
	String serverIP = (String)session.getAttribute("serverIP");
	String serverPort = (String)session.getAttribute("serverPort");
	if ((serverIP == null) || (serverIP.compareTo("")==0) || (serverPort == null) || (serverPort.compareTo("")==0)){
		serverIP = request.getParameter("serverIP");
		serverPort = request.getParameter("serverPort");
	}
	session.setAttribute("serverIP", serverIP);
	session.setAttribute("serverPort", serverPort);
	
	String[] req = {Constants.getSendStatus(), serverIP, serverPort};
	int result = Handler.Handle(req);
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

    <header>
      
      <div class="navbar navbar-fixed-top">
        <div class="navbar-inner">
          <div class="container">
            <a href="index.jsp" class="brand brand-bootbus">ServerHandler</a>

            <button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
              <span class="icon-bar"></span>
              <span class="icon-bar"></span>
              <span class="icon-bar"></span>
            </button>
          </div>
        </div>
      </div>     
    </header>

    <div class="content">
      <div class="container">
        <div class="page-header">

		<h1>Load Generator's Server Handler</h1>
<%
  if(username!=null && username.compareTo("")!=0){
%>
	<div align="right">
		<a href="logout.jsp" >Logout</a>
	</div>
<%	
  }
%>
        
			
        
<%
	if(result!=0){
		session.removeAttribute("serverIP");
		session.removeAttribute("serverPort");
%>
		<h4>Request for connection failed. Try Again</h4>
		<form action="index.jsp" class="form-horizontal form-signin-signup">
            <input type="submit" name="connect" value="ReConnect" class="btn btn-primary btn-large">
          </form>
<%	
	}
	else{
%>	
        
        
          
          
          <h4>Click to start the registration process</h4>
          <form action="startRegistration.jsp" class="form-horizontal form-signin-signup">
            <input type="submit" name="startRegistration" value="Start Registration" class="btn btn-primary btn-large">
          </form>
          
          <h4>Click to stop the registration process</h4>
          <form action="stopRegistration.jsp" class="form-horizontal form-signin-signup">
            <input type="submit" name="stopRegistration" value="Stop Registration" class="btn btn-primary btn-large">
          </form>
          
          <h4>Click to start the experiment. Make sure that events file is added</h4>
          <form action="startExperiment.jsp" class="form-horizontal form-signin-signup">
            <input type="file" name="eventsFile" placeholder="Upload Event File">
            <br>
            <input type="submit" name="startExperiment" value="Start Experiment" class="btn btn-primary btn-large">
          </form>
          
          <h4>Click to stop the experiment</h4>
          <form action="stopExperiment.jsp" class="form-horizontal form-signin-signup">
            <input type="submit" name="stopExperiment" value="Stop Experiment" class="btn btn-primary btn-large">
          </form>
          
          <%
				}
          %>
          
        </div>
      </div>
    </div>

	<div class="navbar navbar-fixed-bottom">
    <footer>
      
      <div class="container">
        <p>
          &copy; 2014-3000 ServerHandler. All Rights Reserved
        </p>
      </div>
      </footer>
      </div>
    
    <script type="text/javascript" src="./js/jquery.min.js"></script>
    <script type="text/javascript" src="./js/bootstrap.min.js"></script>
    <script type="text/javascript" src="./js/boot-business.js"></script>
  </body>
</html>

      
