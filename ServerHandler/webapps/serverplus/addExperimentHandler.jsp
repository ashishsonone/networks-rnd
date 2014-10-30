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
	if(Main.isExperimentRunning()){
		response.sendRedirect("index.jsp");
	}
	
	
	String ename=null,loc=null,des=null,filename=null;


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
			
			result = Utils.getCurrentExperimentID();
			if(result==-1){
				System.out.print("getting experiment from database failed...");
				response.sendRedirect("addExperiment.jsp");
			}
			
			e.setID(result);
			
			filePath=filePath+Integer.toString(result) + "/";
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
				/*
				if( fileName.lastIndexOf("\\") >= 0 ){
					file = new File( filePath + 
					fileName.substring( fileName.lastIndexOf("\\"))) ;
				}else{
					file = new File( filePath + 
					fileName.substring(fileName.lastIndexOf("\\")+1)) ;
				}
				*/
				file = new File(filePath + Constants.getEventFile());
				fi.write( file ) ;
			}
		 }
		 
		 
		 result = Handlers.StartExperiment(e);
		 if(result>0)
			response.sendRedirect("index.jsp");
		
		else
			response.sendRedirect("addExperiment.jsp");
		
		 
	  }catch(Exception ex) {
		 System.out.println(ex);
	  } 
	  
	}

	
%>
