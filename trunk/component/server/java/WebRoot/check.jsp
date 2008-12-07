<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	import="org.oostethys.sos.Netcdf2sos100, org.oostethys.schemas.x010.oostethys.SystemDocument.System" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="org.oostethys.schemas.x010.oostethys.SystemDocument.System"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Test results for your OOSTethys configuration</title>
</head>
<body>
<h1>Results of your OOSTethys configuration</h1>
<!--  
read oostethys.xml 
process
show errors
-->
<p></p>
<p>
<%
String version ="0.2.2_20080805";
Netcdf2sos100 ns = new Netcdf2sos100();
	try {
		
		String url = "config/oostethys.xml";
		//url = "oostethys_notfoundvars.xml";
		ns.setUrlOostethys(Thread.currentThread()
				.getContextClassLoader().getResource(
						url));
		ns.processForTest();
		
		%>		
	

<p>Congratulations your configuration has pass the test !</p>
<%
	
	} catch (Exception e) {
		StringBuffer buffy = new StringBuffer("Problem Report OOSTethys "+version+"%0D%0A"+"%0D%0A" );
		%>
		<p>The following problem was detected:<p>
		<font color="red"><%=e.getMessage()%></font><br>
		<%
		buffy.append(e.getMessage()+"%0D%0A");
		StackTraceElement[] ele = e.getStackTrace();
		for (int i = 0; i < ele.length; i++) {
			buffy.append(ele[i].toString()+"%0D%0A");
		%>
		<%=ele[i].toString()%><br>
		<%
	
	//	buffy.append(ns.getOostDoc().xmlText());
		}
		
		%> 
		<br><br>
		To send this error to OOSTethys click 
		<a href="mailto:oostethys-support@lists.sourceforge.net?subject=problem oostethys&body=<%=buffy.toString()%>">here.</a>
		
		
		<% 
		
		
	}
%>
</p>
</body>
</html>