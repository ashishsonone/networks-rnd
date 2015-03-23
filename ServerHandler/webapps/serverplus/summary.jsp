<%@ page import="serverplus.*" %>
<%@ page import="java.util.Date" %>
<%
	Integer _sid = new Integer(Integer.parseInt((String)session.getAttribute("session")));
	Session _sumsession = (Main.getSessionMap()).get(_sid);
	int registeredSize = _sumsession.getRegisteredClients().size();
	
	/*if(_sumsession==null) System.out.println("summary.jsp: Bhai ye th null hai");
	else System.out.println("summary.jsp : " + _sumsession.getDescription());*/
%>
<div>
	<h4>Summary</h4>
	<p> Number of <a title="click here to list registered devices" href="listDevices.jsp">Devices </a> registered: <% out.print(registeredSize); %> </p>
	<!--<p> List all <a title="click here to list experiments" href="listExperiments.jsp">Experiments </a> </p> -->

<%
	if (_sumsession.isExperimentRunning()){
	    int _sumexpid = _sumsession.getCurrentExperiment();
	    Experiment _sume = Main.getRunningExperimentMap().get(_sumexpid);

	    int cur_filereceived = _sume.getReceivedFiles();
	    int tot_filereceived = _sumsession.getActualFilteredDevices().size();

	    Date prev = _sume.getStartTime();
	    Date cur = new Date();

	    long diff = cur.getTime() - prev.getTime();
	    long diffSeconds = diff / 1000 % 60;
    	long diffMinutes = diff / (60 * 1000) % 60;
    	long diffHours = diff / (60 * 60 * 1000);

	    out.print("<p> Experiment number <a href=\"experimentDetails.jsp?" + Constants.getExpID() +"="
	    			+  _sumexpid + "\">" + _sumexpid + " </a> is running for time " 
	    			+ diffHours + ":" + diffMinutes + ":" + diffSeconds + " hrs </p>");


	    //success + failure + pending
	    int setotal = _sumsession.getFilteredDevices().size();
	    int sesuccess = _sumsession.getActualFilteredDevices().size();
	    int sepending = _sumsession.getStartExpTCounter();
	    int sefailure = setotal - sesuccess - sepending;
	    out.print("<p>START EXP. STATUS :: Total:"+ setotal +" | Success:" + sesuccess + " | Pending:" 
	    		+ sepending + " | Failure:" + sefailure +"</p>");

		out.print("<p> List all <a title=\"click here to list filtered devices\"" 
					+	"href=\"listFilteredDevices.jsp\">Filtered Devices </a> </p>");


		if(tot_filereceived==0){
		
		}
		else if(cur_filereceived==tot_filereceived){
	    	out.print("<p> All Log Files are received. Experiment can be stopped </p>");
	    }
	    else{
	    	out.print("<p>" + cur_filereceived + " out of " + tot_filereceived 
	    				+ " Log Files are received </p>");
	    }
	}
	else if(registeredSize >0){
		out.print("<p>STOP EXP. STATUS :: Pending Requests:" + _sumsession.getStopExpTCounter() +"</p>");
	    out.print("<p>CLEAR REG. STATUS :: Pending Requests:" + _sumsession.getClearRegTCounter() +"</p>");
	    out.print("<p>REFRESH REG. STATUS :: Pending Requests:" + _sumsession.getRefreshTCounter() +"</p>");
	}
%>
	<p> .... </p>
</div>
