<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	import="org.oostethys.sos.Netcdf2sos100,org.oostethys.schemas.x010.oostethys.SystemDocument.System"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page
	import="org.oostethys.schemas.x010.oostethys.SystemDocument.System"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Test results for your OOSTethys configuration</title>



 <script type="text/javascript" src="http://www.google.com/jsapi"></script>
    <script type="text/javascript">
      google.load('visualization', '1', {packages: ['linechart']});
    </script>
    <script type="text/javascript">
      function drawVisualization() {
        // Create and populate the data table.
        var data = new google.visualization.DataTable();
        data.addColumn('string', 'Name');
            data.addColumn('number', 'Height');
            
            data.addRows(4);
            data.setCell(0, 0, 'Tong Ning mu');
            data.setCell(1, 0, 'Huang Ang fa');
            data.setCell(2, 0, 'Teng nu');
            data.setCell(3, 0, 'fu');
            data.setCell(0, 1, 174);
            data.setCell(1, 1, 523);
            data.setCell(2, 1, 86);
             data.setCell(3, 1, 6);
          
      
        // Create and draw the visualization.
        new google.visualization.LineChart(document.getElementById('visualization')).
            draw(data, null);  
      }
      

      google.setOnLoadCallback(drawVisualization);
    </script>

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
	String version = "0.2.2_20080805";
	Netcdf2sos100 ns = new Netcdf2sos100();
	try {

		String url = "config/oostethys.xml";
		//url = "oostethys_notfoundvars.xml";
		ns.setUrlOostethys(Thread.currentThread()
				.getContextClassLoader().getResource(url));
		ns.processForTest();
		// read obs
		
%>
</p>
<p>Congratulations your configuration has pass the test !</p>
<%
	} catch (Throwable e) {
		StringBuffer buffy = new StringBuffer(
				"Problem Report OOSTethys " + version + "%0D%0A"
						+ "%0D%0A");
%>
<p>The following problem was detected:</p>
<p><font color="red"><%=e.getClass().getName() + ": " + e.getMessage()%></font><br>
<%
	buffy.append(e.getMessage() + "%0D%0A");
		StackTraceElement[] ele = e.getStackTrace();
		for (int i = 0; i < ele.length; i++) {
			buffy.append(ele[i].toString() + "%0D%0A");
%> <%=ele[i].toString()%><br>
<%
	//	buffy.append(ns.getOostDoc().xmlText());
		}
%> <br>
<br>
To send this error to OOSTethys click <a
	href="mailto:oostethys-support@lists.sourceforge.net?subject=problem oostethys&body=<%=buffy.toString()%>">here.</a>


<%
	}
%>
</p>
 <div id="visualization" style="width: 500px; height: 300px;"></div>

</body>
</html>