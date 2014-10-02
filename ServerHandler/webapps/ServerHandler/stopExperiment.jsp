<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="ServerHandler.*" %>

<%
	String serverIP = request.getParameter("serverIP");
	String serverPort = request.getParameter("serverPort");
	//String action = Constants.Action.sendstatus;
	//String action2 = Constants.action;
	String[] req = {"expstop", serverIP, serverPort};
	
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

	 <div class="container">
	<form action="clickConnect.jsp" class="form-horizontal form-signin-signup">
		<input type="hidden" name="serverIP" value= <% out.print("\"" + serverIP + "\""); %> >
		<input type="hidden" name="serverPort" value= <% out.print("\"" + serverPort + "\""); %> >
        <input type="submit" name="back" value="Back" class="btn btn-primary btn-large">
    </form>
    </div>

    <div class="content">
      <div class="container">
        <div class="page-header">
        
			<h1>Load Generator's Server Handler</h1>
        
			<%
			if(result!=0){
				out.print("<h4>Request for stop experiment failed</h4>");
			}
			else{
				out.print("<h3>Experiment stopped on Server with IP and Port are: "+serverIP + " "+ serverPort+"</h3>");
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

      


