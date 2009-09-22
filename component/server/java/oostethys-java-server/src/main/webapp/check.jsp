<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	import="org.oostethys.sos.Netcdf2sos100,org.oostethys.schemas.x010.oostethys.SystemDocument.System,org.oostethys.schemas.x010.oostethys.SystemDocument.System,org.oostethys.sos.reader.SOSSimpleGeneralReader,org.oostethys.sos.reader.SOSObservationSweCommonReader,java.net.URL,org.oostethys.sos.reader.SOSSimpleGeneralReader,
	org.oostethys.sos.reader.PlotData,java.util.Calendar,java.util.TimeZone"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Test results for your OOSTethys configuration</title>

<%
	SOSSimpleGeneralReader simpleReader = new SOSSimpleGeneralReader();
	simpleReader
			.processCapabilities("http://localhost:8080/oostethys/sos?VERSION=1.0.0&SERVICE=SOS&REQUEST=GetCapabilities");

	URL urlGetObservation = new URL(simpleReader.getObservationURL());
	SOSObservationSweCommonReader reader = new SOSObservationSweCommonReader();
	reader.setURLGetObservation(urlGetObservation);

	String var = simpleReader.getVariableURL();

	String varURI = var;
	reader.process(varURI);
	PlotData plotData = reader.getPlotData();
	String title = plotData.getTitle();
	int rows =plotData.getValues().length;
%>



<script type="text/javascript" src="http://www.google.com/jsapi"></script>
<script type="text/javascript">
      google.load('visualization', '1', {packages: ['linechart']});
    </script>
<script type="text/javascript"><!--
      function drawVisualization() {
        // Create and populate the data table.
        var data = new google.visualization.DataTable();
        	data.addColumn('date', 'Date'); 
            data.addColumn('number', '<%=varURI%>');
            
            data.addRows(<%=rows%>);
            
            // add time
            <%long[] times = plotData.getTimes();
			for (int i = 0; i < rows; i++) {

			Calendar cal = Calendar.getInstance();
			cal.setTimeZone(TimeZone.getTimeZone("GMT"));
			cal.setTimeInMillis(times[i]);
			int year =cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_MONTH);
			int hr = cal.get(Calendar.HOUR);
			int min = cal.get(Calendar.MINUTE);
			int sec = cal.get(Calendar.SECOND);
			%>
            	 data.setCell(<%=i%>, 0, new Date(<%=year%>, <%=month%> ,<%=day%>, <%=hr%>,<%=min%>,<%=sec%>));
            	 
            	
            <%}%>

			// add values
            <%double[] values = plotData.getValues();
			for (int i = 0; i < rows; i++) {%>
       	 	data.setCell(<%=i%>, 1, <%=values[i]%>);
       	
      		 <%}%>

            
            

            // create formatter
            var formatter_simple = new google.visualization.DateFormat({pattern: "MMM dd '@' hh':'mm':'ss"});
            // Reformat our date 1st column ( start in '0)
            formatter_simple.format(data, 0);
                      
            
          
      
        // Create and draw the visualization.
        new google.visualization.LineChart(document.getElementById('visualization')).
            draw(data, {title:'<%=title%>'});  
      }
      

      google.setOnLoadCallback(drawVisualization);
    --></script>

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
<p>Here is a sample of your data</p>
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
<div id="visualization" style="width: 600px; height: 300px;"></div>

</body>
</html>