<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="serverplus.*" %>

<%@ include file="checksession.jsp" %>

<% 
	String username = (String)session.getAttribute("username");
	String password = (String)session.getAttribute("password");
	String sessionid= (String)session.getAttribute("session");

	if(sessionid==null || sessionid=="") {response.sendRedirect("session.jsp");}

	else{
	System.out.println("index.heml: sessionid is=" + sessionid);
	Integer ssid1 = new Integer(Integer.parseInt(sessionid));
	Session curSession = (Main.getSessionMap()).get(ssid1);

	int size = (curSession.getRegisteredClients()).size();
	
	
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
			setTimeout("window.location.href=window.location.href.substr(window.location.href,window.location.href.indexOf('?'));",t);
			//setTimeout("location.reload(true);", t);
		}
		//   -->
	</script>
  </head>
  <body onload="JavaScript:AutoRefresh(5000);">

  <%@ include file="header.jsp" %>  
    

    <div class="content">
      <div class="container">
      
        <div class="page-header">        
			<h1>Load Generator's Server Handler</h1>
		</div>
		<div>
			<a href="session.jsp">Back</a>
		</div>
				
		<div class="container-fluid">
			<h4>Session ID <%  out.print(sessionid); %>  </h4>
			<%@ include file="sessionValidation.jsp" %>
		
			<div class="row-fluid">
				<div class="span6">

<%
	if(curSession.isExperimentRunning()){
%>
					<div>
						 <h4>Click to stop the Experiment</h4>
						<form method="post" action="processAction.jsp" class="form-horizontal form-signin-signup">
							<input type="submit" onclick="return confirm('Press OK to stop Experiment')"  name="stopExperiment" value="Stop Experiment" class="btn btn-primary btn-large">
						</form>
					</div>

<%
	}
	else if(!curSession.isRegistrationWindowOpen()){
%>
					 <div>
						 <h4>Click to start the Registration</h4>
						<form method="post" action="processAction.jsp" class="form-horizontal form-signin-signup">
							<input type="submit" name="startRegistration" value="Start Registration" class="btn btn-primary btn-large">
						</form>
					</div>
<%
		if(size>0){
%>					
					 <div>
						 <h4>Click to add a Experiment</h4>
						<form method="post" action="processAction.jsp" class="form-horizontal form-signin-signup">
							<input type="submit" name="addExperiment" value="Add Experiment" class="btn btn-primary btn-large">
						</form>
					</div>
<%
		}
	}
	else{
%>

					 <div>
						 <h4>Click to stop the Registration</h4>
						<form method="post" action="processAction.jsp" class="form-horizontal form-signin-signup">
							<input type="submit" name="stopRegistration" value="Stop Registration" class="btn btn-primary btn-large">
						</form>
					</div>
					
<%	
	}
%>
					 
					 
					</div>
					<div class="span6">
						<%@ include file="summary.jsp" %>
						
						
<%if(!curSession.isRegistrationWindowOpen() && size>0 && !curSession.isExperimentRunning()) {%>						
						<div>
							<h4>Click to clear registrations</h4>
							<form method="post" action="processAction.jsp" class="form-horizontal form-signin-signup">
								<input type="submit" name="clearRegistration" value="Clear Registration" class="btn btn-primary btn-large">
							</form>
						</div>
<%
	}
%>	
					</div>
				</div>
				
				
				<%@ include file="sessionExpiredMessage.msg" %>
				<%@ include file="closeBracket.msg" %>
				
			</div>     
		</div>
    </div>

	<%@ include file="footer.jsp" %>
    <script type="text/javascript" src="./js/jquery.min.js"></script>
    <script type="text/javascript" src="./js/bootstrap.min.js"></script>
   
  </body>
</html>

<%@ include file="closeBracket.msg" %>
<%}%>      
