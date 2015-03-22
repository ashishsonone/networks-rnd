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
	
String filename;
int expid=-1;

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
		System.out.println(":::::::::uploadTrace:::::::");

		List fileItems = upload.parseRequest(request);
		Iterator i = fileItems.iterator();
		while(i.hasNext()){
			System.out.println("in while.........");
			FileItem fi = (FileItem)i.next();
			if(fi.isFormField()){
				String fieldName = fi.getFieldName();
				String fieldValue = fi.getString();
				if(fieldName.equals("expid")){
					expid = Integer.parseInt(fieldValue);
				}
				System.out.println("in while.........1"+ fieldName);
			}
			else if(!fi.isFormField()){
				System.out.println("in while.........2");
				filename = fi.getName();
				long sizeInBytes = fi.getSize();
				filePath=filePath+expid + "/";
				file = new File(filePath + Constants.getTraceFile() + "." + Utils.getExtensionOfFile(filename));
				fi.write( file );
			}
			else{
				System.out.println("uploadTrace.jsp: in else field");
			}
		}
		Utils.updateTraceFileReceived(expid, true);
		System.out.println("file path and name=" + filePath+"trace.txt");
		response.sendRedirect("listExperiments.jsp");
    }catch(Exception ex) {
		System.out.println(ex);
		response.sendRedirect("index.jsp");
	} 
  
}
else{
	response.sendRedirect("index.jsp?");
}	
%>

<%@ include file="closeBracket.msg" %>