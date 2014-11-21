<%@page import="java.io.File"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="java.io.IOException"%>
<%@page import="java.util.zip.ZipOutputStream"%>
<%@page import="java.util.zip.ZipEntry"%>
<%@ page import="serverplus.*" %>


<%@ include file="checksession2.jsp" %>


<%!
void addFile( ZipOutputStream outZip, File f, String name ){
	FileInputStream in = null ;
	try{
		// Add ZIP entry to output stream.
		outZip.putNextEntry( new ZipEntry( name ) ) ;
		in = new FileInputStream( f ) ;

		// Transfer bytes from the file to the ZIP file
		byte[] buf = new byte[ 4096 ] ;
		int len ;
		while( ( len = in.read( buf ) ) > 0 ){
			outZip.write( buf, 0, len ) ;
		}
	}
	catch( IOException ex ) { ex.printStackTrace(); }
	finally
		{
		// Complete the entry
		try{ outZip.closeEntry() ; } catch( IOException ex ) { }
		try{ in.close() ; } catch( IOException ex ) { }
	}
}
%>

<%

	String expid = (String)request.getParameter(Constants.getExpID());
	
	ServletOutputStream outStream = null;

	try{
		// set the content type and the filename
		response.setContentType( "application/zip" ) ;
		response.addHeader( "Content-Disposition", "attachment; filename=" + expid + ".zip" ) ;

		// get a ZipOutputStream, so we can zip our files together
		ZipOutputStream outZip = new ZipOutputStream( response.getOutputStream() );

		//get directory path
		String directoryName = Constants.getMainExpLogsDir()+expid+"/";
		File directory = new File(directoryName);
		File[] fList = directory.listFiles();
		
		// add some files to the zip...
		for(File file : fList){
			if (file.isFile() && (!(file.getName().equals(Constants.getEventFile())))) {
				addFile(outZip, file, file.getName());
			} 
		}
		

		// flush the stream, and close it
		outZip.flush() ;
		outZip.close() ;
	}
	catch(Exception e){
		System.out.println(e.getMessage());
	}
%> 

<%@ include file="closeBracket.msg" %>
