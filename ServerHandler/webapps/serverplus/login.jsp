<%@page contentType="text/html" pageEncoding="UTF-8"%>

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
          <br>
          <br>
          
<%
	String username =  (String)session.getAttribute("username");
	if(username!=null && username!="")
		response.sendRedirect("index.jsp");
	
	else{
%>
    
          <h4>Enter LogIn Details Here</h4>
          <form method="post" action="logincheck.jsp" class="form-horizontal form-signin-signup">
            <input type="text" name="username" placeholder="Username" required>
            <input type="password" name="password" placeholder="Password" required>
            <br>
            <input type="submit" name="login" value="LogIn" class="btn btn-primary btn-large">
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

      

