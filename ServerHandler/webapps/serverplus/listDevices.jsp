<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="serverplus.*" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.concurrent.ConcurrentHashMap" %>

<%@ include file="checksession.jsp" %>

<%
	String sessionid = (String)session.getAttribute("session");
	Integer _ssid = new Integer(Integer.parseInt(sessionid));
	Session _session = (Main.getSessionMap()).get(_ssid);
	String username = (String)session.getAttribute("username");
	ConcurrentHashMap<String, DeviceInfo> registeredClients = _session.getRegisteredClients();
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
	
	<script type="text/JavaScript">
		<!--
		function AutoRefresh( t ) {
			setTimeout("location.reload(true);", t);
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
				<a href="index.jsp">Back</a> 
			</div>
			
			<div class="container-fluid">	
				<h4>Session ID <%  out.print(sessionid); %>  </h4>
				<%@ include file="sessionValidation.jsp" %>
				
				<div>
					<table class="table">
						<thead>
							<tr>
								<th>Mac Address</th>
								<th>OS Version</th>
								<th>WiFi Version</th>
								<th>Processor Speed</th>
								<th>Signal Strength</th>
							</tr>
						</thead>
						<tbody>
<%
	for (Map.Entry<String, DeviceInfo> e : registeredClients.entrySet()) {
		DeviceInfo d = e.getValue();
%>
							<tr>
								<td><%out.print(d.getMacAddress());%></td>  
								<td><%out.print(""+d.getOsVersion());%></td>  
								<td><%out.print(d.getWifiVersion());%></td>
								<td><%out.print(d.getProcessorSpeed());%></td>
								<td><%out.print(d.getWifiSignalStrength());%></td>
							</tr>
<% 
	}
%>  
						</tbody>
					</table>
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
