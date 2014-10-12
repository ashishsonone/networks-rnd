<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="serverplus.*" %>

<%@ include file="checksession.jsp" %>

<% 
	String username = (String)session.getAttribute("username");
	String password = (String)session.getAttribute("password");
	
	String serverIP = (String)session.getAttribute("serverIP");
	String serverPort = (String)session.getAttribute("serverPort");
	boolean connected = true;
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

			<div align="right">
				<%@ include file="logout_button.jsp" %>
			</div>

			<h3> Welcome to Server...  </h3>
<%
	if(Main.isServerOn()){ out.print("<h4> server is on</h4>");}
	if(!Main.isExperimentRunning()){ out.print("<h4> experiment is not running</h4>");}
	if(!Main.isRegistrationWindowOpen()){ out.print("<h4> Registration window is not open </h4>");}
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

      
