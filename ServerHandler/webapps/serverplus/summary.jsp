<%@ page import="serverplus.*" %>
<%
	Integer _sid = new Integer(Integer.parseInt((String)session.getAttribute("session")));
	Session _session = (Main.getSessionMap()).get(_sid);
%>
<div>
	<h4>Summary</h4>
	<p> Number of <a title="click here to list registered devices" href="listDevices.jsp">Devices </a> registered: <% out.print(_session.getRegisteredClients().size()); %> </p>
	<p> List all <a title="click here to list experiments" href="listExperiments.jsp">Experiments </a> </p>
<%
	
	if (_session.isExperimentRunning()){
		out.print("<p> Experiment number " +  _session.getCurrentExperiment()  +  " is running  </p>");
		out.print("<p> List all <a title=\"click here to list filtered devices\"" 
					+	"href=\"listFilteredDevices.jsp\">Filtered Devices </a> </p>");
	}
%>
	<p> .... </p>
</div>
