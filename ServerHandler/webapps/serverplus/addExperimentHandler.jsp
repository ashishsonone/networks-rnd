<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.io.*,java.util.*, javax.servlet.*" %>
<%@ page import="javax.servlet.http.*" %>
<%@ page import="org.apache.commons.fileupload.*" %>
<%@ page import="org.apache.commons.fileupload.disk.*" %>
<%@ page import="org.apache.commons.fileupload.servlet.*" %>
<%@ page import="org.apache.commons.io.output.*" %>

<%@ page import="serverplus.*" %>

<%@ include file="checksession.jsp" %>

<%
	String username= (String)session.getAttribute("username");
	String sessionid = (String)session.getAttribute("session");
	Integer _ssid = new Integer(Integer.parseInt(sessionid));
	Session _session = (Main.getSessionMap()).get(_ssid);
	if(_session.isExperimentRunning()){
		response.sendRedirect("index.jsp");
	}
	
else{	
	String ename=null,loc=null,des=null,filename=null;
	String filter = null;
	int filterNumber=-1;
	Vector<String> devices=new Vector<String>();

	File file ;
	int maxFileSize = 5 * 1024 * 1024;	
	int maxMemSize = 5 * 1024 * 1024;
	String filePath = Constants.getMainExpLogsDir();
	String tempFiles = Constants.getTempFiles();
	
	// Verify the content type
	String contentType = request.getContentType();
	if ((contentType != null) && (contentType.indexOf("multipart/form-data") >= 0)) {

	  DiskFileItemFactory factory = new DiskFileItemFactory();
	  factory.setSizeThreshold(maxMemSize);
	  factory.setRepository(new File(tempFiles));

	  ServletFileUpload upload = new ServletFileUpload(factory);
	  upload.setSizeMax( maxFileSize );
	  try{ 
		 List fileItems = upload.parseRequest(request);

			Iterator i = fileItems.iterator();
			while(i.hasNext()){
				FileItem fi = (FileItem)i.next();
				if(fi.isFormField()){
					String fieldName = fi.getFieldName();
					String fieldValue = fi.getString();
					if(fieldName.equals("expname")){
						ename = fieldValue;
					}
					else if(fieldName.equals("location")){
						loc = fieldValue;
					}
					else if(fieldName.equals("description")){
						des = fieldValue;
					}
					else if(fieldName.equals("filter")){
						filter = fieldValue;
					}
					else if(fieldName.equals("filterNumber")){
						System.out.println("-------filterNumber: " +  fieldValue);
						filterNumber = Integer.parseInt(fieldValue);
						System.out.println("-------filterNumber: " +  filterNumber);
					}else if(fieldName.equals("devices")){
						devices.addElement(fieldValue);
					}
					else{
						System.out.println("else");
					}
				}
				else if(!fi.isFormField()){
					filename = fi.getName();
				}
			}

			if(ename==null || loc==null || des==null || filename==null) response.sendRedirect("addExperiment.jsp");
			
			//Experiment e = new Experiment(ename,loc,des,username,filename);
			Experiment e = new Experiment(ename,loc,des,username,Constants.getEventFile());
			System.out.println("addExperimentHandler:");
			e.print();
			

			int result = Utils.addExperiment(e);
			if(result==-1){
				System.out.print("adding experiment to database failed...");
				response.sendRedirect("addExperiment.jsp");
			}
			
			/*
			int result = Utils.getCurrentExperimentID();
			if(result < 0){
				System.out.print("getting experiment from database failed...");
				response.sendRedirect("addExperiment.jsp");
			}
			System.out.println("------\nCurrent Experiment ID from database is " + result +"\n");
			//e.setID(result);
			e.setID(result+1);
			*/
			
			
			filePath=filePath+Integer.toString(e.getID()) + "/";
			File theDir = new File(filePath);
			if (!theDir.exists()) {
				theDir.mkdir();
			}

		 i = fileItems.iterator();
			
		 while ( i.hasNext () ){
			FileItem fi = (FileItem)i.next();
			if ( !fi.isFormField () ){
				String fieldName = fi.getFieldName();
				String fileName = fi.getName();
				boolean isInMemory = fi.isInMemory();
				long sizeInBytes = fi.getSize();
				file = new File(filePath + Constants.getEventFile());
				fi.write( file ) ;
			}
		 }
		 
		 System.out.println("addExperimenthandler.jsp:" + "filter value=" + filter);
		 i = fileItems.iterator();
		 int rand_number=0;
		if(filter.equals("random")){
			System.out.println("addExperimenthandler.jsp:" + "filter Number=" + filterNumber);
			result = Handlers.StartRandomExperiment(e,_session,filterNumber);
		}
		else if(filter.equals("manual")){
			for(String mac : devices){
				System.out.println(mac);
			}
			result=Handlers.StartManualExperiment(e,_session,devices);
		}

		 if(result>0)
			response.sendRedirect("index.jsp");
		
		else{
			//response.sendRedirect("addExperiment.jsp");
			//Utils.deleteExperiment(e.getID());
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
    <script type="text/javascript" src="./js/jquery.min.js"></script>
    <script type="text/javascript" src="./js/bootstrap.min.js"></script>
    <script type="text/javascript" src="./js/bootstrap-tooltip.js"></script>
    <script type="text/javascript" src="./js/bootstrap-popover.js"></script>
  </head>
  
  <body>

  <%@ include file="header.jsp" %>  
    

    <div class="content">
      <div class="container">
      
        <div class="page-header">        
			<h1>Load Generator's Server Handler</h1>
		</div>
			
		<div class="container-fluid">
			<h4>Session ID <%  out.print(sessionid); %>  </h4>
			<%@ include file="sessionValidation.jsp" %>

			<div class="row-fluid">
				<div class="span6">
					 
				<h4>Experiment could not be started due to some error</h4>
				
				Back to <a href="addExperiment.jsp">Add Experiment</a>
					 
				</div>
				<div class="span6">
					<%@ include file="summary.jsp" %>
				</div>
			</div>
				
				<%@ include file="sessionExpiredMessage.msg" %>
				<%@ include file="closeBracket.msg" %>
				
		</div>     
	  </div>
    </div>

	<%@ include file="footer.jsp" %>

   
  </body>
</html>







		
<%			
		}
		 
	  }catch(Exception ex) {
		 System.out.println(ex);
	  } 
	  
	}
}	
%>

<%@ include file="closeBracket.msg" %>
