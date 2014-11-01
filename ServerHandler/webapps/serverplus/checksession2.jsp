<%
	boolean __flag = true;

	if(session.getAttribute("username")== null || session.getAttribute("username")== "" ){
	    __flag=false;
	    response.sendRedirect("login.jsp");
	}

	if(__flag){
%>