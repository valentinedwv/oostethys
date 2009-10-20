import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.oostethys.sos.reader.PlotData;
import org.oostethys.sos.reader.SOSObservationSweCommonReader;

public class ExampleGettingDataFor2DPlotting {
	public static void main4(String[] args) {
		// example MBARI SOS
		try {
			String s = "http://mmisw.org/oostethys/sos?VERSION=1.0.0&SERVICE=SOS&REQUEST=GetObservation&offering=observationOffering_1455";
			URL urlGetObservation = new URL(s);
			SOSObservationSweCommonReader reader = new SOSObservationSweCommonReader();
			reader.setURLGetObservation(urlGetObservation);
			String varURI = "http://mmisw.org/cf#pressure";
			reader.process(varURI);
			PlotData plotData = reader.getPlotData();
			String title = plotData.getTitle();
			long[] times = plotData.getTimes();
			double[] values = plotData.getValues();
			String unitsShort = plotData.getUnitsShort();
			String unitsURL = plotData.getUnitsURI();
			String variableURI = plotData.getVariableURI();

			System.out.println("title: " + title);
			System.out.println("URI Variable: " + variableURI);
			System.out.println("unitsShort: " + unitsShort);
			System.out.println("FirstTimeStep: ");
			System.out.println("   millisec: " + times[0]);
			System.out.println("   values: " + values[0]);

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main6(String[] args) {
		// example MBARI NG from IEEE 1451 using HTTP POST
		try {

//			String endPoint = "http://ww6.geoenterpriselab.com/SOSInterfaceToSTWS/STWS_SOS.asmx/GetCapabilities?request=GetCapabilities&service=SOS";

			URL url = new URL(
					"http://mmisw.org/oostethys/sos?VERSION=1.0.0&SERVICE=SOS&REQUEST=GetObservation");
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);

			if (conn instanceof java.net.HttpURLConnection)
				((java.net.HttpURLConnection) conn).setRequestMethod("POST");

			OutputStreamWriter wr = new OutputStreamWriter(conn
					.getOutputStream());

			// send the post xml
			
			String file = Thread.currentThread().getContextClassLoader()
					.getResource("getObsExample.xml").getFile();

			FileInputStream inputStream = new FileInputStream(file);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					inputStream));
			StringBuffer buffer = new StringBuffer();
			String line = null;
			while ((line = in.readLine()) != null) {
				buffer.append(line);
			}

			String xml = buffer.toString();
			System.out.println(xml);

			wr.write(xml);
			wr.flush();
			
			// get the response
			
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn
					.getInputStream()));
			StringBuffer value = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				value.append(line);
			}
			wr.close();
			rd.close();
			String responseString = value.toString();
			System.out.println(responseString);
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public static void main(String[] args) {
		// example MBARI NG from IEEE 1451 using HTTP POST
		try {

			String endPoint = "http://ww6.geoenterpriselab.com/SOSInterfaceToSTWS/STWS_SOS.asmx/GetObservation?request=GetObservation&service=SOS";

			URL url = new URL(endPoint);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);

			if (conn instanceof java.net.HttpURLConnection)
				((java.net.HttpURLConnection) conn).setRequestMethod("POST");

			OutputStreamWriter wr = new OutputStreamWriter(conn
					.getOutputStream());

			// send the post xml
			
			String file = Thread.currentThread().getContextClassLoader()
					.getResource("getObsExampleNG.xml").getFile();

			FileInputStream inputStream = new FileInputStream(file);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					inputStream));
			StringBuffer buffer = new StringBuffer();
			String line = null;
			while ((line = in.readLine()) != null) {
				buffer.append(line);
			}

			String xml = buffer.toString();
			System.out.println(xml);

			wr.write(xml);
			wr.flush();
			
			// get the response
			
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn
					.getInputStream()));
			StringBuffer value = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				value.append(line);
			}
			wr.close();
			rd.close();
			String responseString = value.toString();
			System.out.println(responseString);
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	
	public void main3() {
		try {

			// URL urlGetObservation = new URL();
			SOSObservationSweCommonReader reader = new SOSObservationSweCommonReader();
			// reader.setURLGetObservation(urlGetObservation);
			String varURI = "http://mmisw.org/cf#pressure";
			reader.process(varURI);
			PlotData plotData = reader.getPlotData();
			String title = plotData.getTitle();
			long[] times = plotData.getTimes();
			double[] values = plotData.getValues();
			String unitsShort = plotData.getUnitsShort();
			String unitsURL = plotData.getUnitsURI();
			String variableURI = plotData.getVariableURI();

			System.out.println("title: " + title);
			System.out.println("URI Variable: " + variableURI);
			System.out.println("unitsShort: " + unitsShort);
			System.out.println("FirstTimeStep: ");
			System.out.println("   millisec: " + times[0]);
			System.out.println("   values: " + values[0]);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
