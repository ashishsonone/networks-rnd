<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="serverplus.*" %>

<% 	
	if(!Main.isRegistrationWindowOpen()){
		response.setStatus(response.SC_REQUEST_URI_TOO_LONG);
	}
	else{
		DeviceInfo d = new DeviceInfo();
		
		d.setIp(request.getParameter(Constants.getIp()));
		d.setPort(Integer.parseInt(request.getParameter(Constants.getPort())));
		d.setMacAddress(request.getParameter(Constants.getMacAddress()));
		d.setOsVersion(Integer.parseInt(request.getParameter(Constants.getOsVersion())));
		d.setWifiVersion(request.getParameter(Constants.getWifiVersion()));
		d.setProcessorSpeed(Integer.parseInt(request.getParameter(Constants.getProcessorSpeed())));
		d.setNumberOfCores(Integer.parseInt(request.getParameter(Constants.getNumberOfCores())));
		d.setStorageSpace(Integer.parseInt(request.getParameter(Constants.getStorageSpace())));
		d.setMemory(Integer.parseInt(request.getParameter(Constants.getMemory())));
		d.setWifiSignalStrength(Integer.parseInt(request.getParameter(Constants.getWifiSignalStrength())));
		
		Handlers.RegisterClient(d);
	}
%>
