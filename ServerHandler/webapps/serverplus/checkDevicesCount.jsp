<%
	int size = Main.getRegisteredClients().size();
	if(size<=0) response.sendRedirect("index.jsp");
	
%>	
