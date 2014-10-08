<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="ServerHandler.*" %>


<% 
	String username = (String)session.getAttribute("username");
	String password = (String)session.getAttribute("password");
	int result = Constants.getLoginSuccess();
	boolean valid = false;
	if(username==null || username.compareTo("")==0 || password==null || password.compareTo("")==0){
		username = request.getParameter("uname");
		password = request.getParameter("passwd");
		DBManager db = new DBManager();
		result = db.loginVerification(username, password);
		if(result==Constants.getLoginSuccess()){
			session.setAttribute("username", username);
			session.setAttribute("password", password);
			valid=true;
		}
	}

	
	String serverIP = (String)session.getAttribute("serverIP");
	String serverPort = (String)session.getAttribute("serverPort");
	//session.invalidate();
	boolean connected = false;
	///*
	if (!((serverIP == null) || (serverIP.compareTo("")==0) || (serverPort == null) || (serverPort.compareTo("")==0))){
		String[] req = {Constants.getSendStatus(), serverIP, serverPort};
		if( Handler.Handle(req) == 0)
			connected = true;
	}
	//*/
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
	if(result==Constants.getConnectionFailure()){
%>
				<h4>Cannot connect to Database. Try again later  <% out.print(" " + result); %></h4>
				<form action="login.jsp" class="form-horizontal form-signin-signup">
					<input type="submit" name="back" value="LogIn Again" class="btn btn-primary btn-large">
				</form>
<%
	}
	if(result==Constants.getConnectionFailure()){
		session.removeAttribute("username");
		session.removeAttribute("serverIP");
		session.removeAttribute("serverPort");
%>
				<h4> Got internal error. Try Again Later</h4>
				  <form method="post" action="login.jsp" class="form-horizontal form-signin-signup">
					<input type="submit" name="login" value="LogIn Again" class="btn btn-primary btn-large">
				  </form>
		
<%		
	
	}
	else if(result==Constants.getLoginFailure()){
%>

				<h4>Username or Password Incorrect... <% out.print(" " + result); %> </h4>
				<form action="login.jsp" class="form-horizontal form-signin-signup">
					<input type="submit" name="back" value="LogIn Again" class="btn btn-primary btn-large">
				</form>

<%	
	} 
	else if(result==Constants.getLoginSuccess() && !connected){	
%>

			  <h4>Enter Server IP and Port here <% out.print(" " + result); %> </h4>
			  <form action="clickConnect.jsp" class="form-horizontal form-signin-signup">
				<input type="text" name="serverIP" placeholder="Server IP" required>
				<input type="text" name="serverPort" placeholder="Server Port" required>
				<br>
				<input type="submit" name="connect" value="Connect" class="btn btn-primary btn-large">
			  </form>
<%	
	}
	else if (result==Constants.getLoginSuccess() && connected){
		response.sendRedirect("clickConnect.jsp");
	}
	else{
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

      
