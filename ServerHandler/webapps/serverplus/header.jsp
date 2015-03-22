
<%
	String hsessionid= (String)session.getAttribute("session");
	String link = "<a href=\"listExperiments.jsp\">" + "Experiments</a>";
%>
<header>  
  <div class="navbar navbar-fixed-top">
	<div class="navbar-inner">
	  <div class="container">
	  	<div class="nav navbar-nav navbar-left">
			<a href="session.jsp" class="brand brand-bootbus">ServerHandler</a>
		</div>



		<div class="nav navbar-nav navbar-right">
			<ul class="nav navbar-nav navbar-right">
				<li><a href="index.jsp">Home</a> </li>
				<li ><%out.print(link);%></li>
				<li><%@ include file="logout_button.jsp" %></li>
			</ul>
		</div>



	  </div> 
	</div>
  </div>   
</header>