<%@ page import="serverplus.*" %>
<div>
	<h4>Summary</h4>
	<p> Number of <a title="click here to list registered devices" href="listDevices.jsp">Devices </a> registered: <% out.print(Main.getRegisteredClients().size()); %> </p>
	<p> List all <a title="click here to list experiments" href="listExperiments.jsp">Experiments </a> </p>
<%
	if (Main.isExperimentRunning()){
		out.print("<p> Experiment number " +  Main.getCurrentExperiment()  +  " is running  </p>");
		out.print("<p> List all <a title=\"click here to list filtered devices\"" 
					+	"href=\"listFilteredDevices.jsp\">Filtered Devices </a> </p>");
	}
%>
	<p> .... </p>
</div>
