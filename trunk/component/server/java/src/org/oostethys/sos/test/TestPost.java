package org.oostethys.sos.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import HTTPClient.HttpURLConnection;

import junit.framework.TestCase;

public class TestPost extends TestCase {

	// before running this test youe need to run the local server

	public void testPostGetObs() {
		try {
			System.out.println("testing");

			// Send data
			URL url = new URL("http://localhost:8080/oostethys/sos");
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);

			if (conn instanceof HttpURLConnection)
				((HttpURLConnection) conn).setRequestMethod("POST");

			OutputStreamWriter wr = new OutputStreamWriter(conn
					.getOutputStream());

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

			System.out.println("error in header " + conn.getHeaderField(0)
					+ " >>>end error in header");
			;

			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn
					.getInputStream()));
			StringBuffer value = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				value.append(line);
			}
			wr.close();
			rd.close();
			String responseString = value.toString();
			assertTrue(responseString.contains("ObservationCollection"));
			assertTrue(responseString.contains("36.69623 -122.39965"));
			assertTrue(responseString
					.contains("urn:ogc:phenomenon:time:iso8601"));
			assertTrue(responseString.contains("featureOfInterest"));

		} catch (Exception e) {
		}

	}
	
	//http://mmisw.org/oostethys/sos?VERSION=1.0.0&SERVICE=SOS&request=DescribeSensor&procedure=urn:mbari:org:device:1455

	public void testPostDescribeSensor() {
		try {
			System.out.println("testing");

			// Send data
			URL url = new URL(
					"http://mmisw.org/oostethys/sos?VERSION=1.0.0&SERVICE=SOS&REQUEST=describeSensor");
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);

			if (conn instanceof HttpURLConnection)
				((HttpURLConnection) conn).setRequestMethod("POST");

			OutputStreamWriter wr = new OutputStreamWriter(conn
					.getOutputStream());

			String file = Thread.currentThread().getContextClassLoader()
					.getResource("getDescribeSensor.xml").getFile();

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

			System.out.println(" header " + conn.getHeaderField(0)
					+ " >>>header");
			;

			// Get the response
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
			assertTrue(responseString.contains("SensorML"));
			assertTrue(responseString.contains("urn:mbari:org:device:1455"));
			assertTrue(responseString.contains("Serial CTD"));
			assertTrue(responseString
					.contains("http://mmisw.org/cf#sea_water_salinity"));

			wr.close();
			rd.close();
		} catch (Exception e) {
		}

	}

}
