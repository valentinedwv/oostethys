package org.oostethys.sos.test;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;

import org.oostethys.sos.Netcdf2sos100;
import org.oostethys.test.OOSTethysTest;

public class Netcdf2sos_mbari_ctd_xmlPost_test extends OOSTethysTest {
	Netcdf2sos100 ns = null;

	protected void setUp() throws Exception {
		super.setUp();
		ns = new Netcdf2sos100();
		URL file = getURL("mbari-oost.xml");
		URL url = file;

		ns.setUrlOostethys(url);

	}

	public void testDescribeSensorPost() {
		String file = getURL("getDescribeSensor.xml").getFile();
		InputStream inputStream;
		try {
			FileInputStream fis = new FileInputStream(file);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ns.process(fis, outputStream);
			ns.setNumberOfRecordsToProcess(10);
			String doc = ns.getOOSTethysDoc();
			System.out.println(doc);
			String s = outputStream.toString();
			System.out.println(s);
			assertTrue(s
					.contains("<gml:beginPosition>2008-06-10T00:36:19Z</gml:beginPosition>"));
			assertTrue(s.contains("<swe:field name=\"Salinity\">"));
			assertTrue(s.contains("</sml:SensorML>"));

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void testDescribeSensorPostErrorBaaProcedureName() {
		String file = getURL("getDescribeSensorError-badProcedure.xml")
				.getFile();
		InputStream inputStream;
		try {
			inputStream = new FileInputStream(file);
			StringWriter sw = new StringWriter();
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ns.process(inputStream, outputStream);

			String s = outputStream.toString();
			System.out.println(s);

			assertTrue(s
					.contains("<ExceptionReport version=\"1.0\"><Exception exceptionCode=\"InvalidParameterValue"));

			assertFalse(s.contains("</sml:SensorML>"));

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void todotestBadXMLPost() {

	}

	public void testDescribeSensorPostErrorBadVersion() {
		String file = getURL("getDescribeSensorError-badVersion.xml").getFile();
		InputStream inputStream;
		try {
			inputStream = new FileInputStream(file);
			StringWriter sw = new StringWriter();
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ns.process(inputStream, outputStream);

			String s = outputStream.toString();
			System.out.println(s);

			assertTrue(s
					.contains("<ExceptionReport version=\"1.0\"><Exception exceptionCode=\"InvalidParameterValue"));

			assertFalse(s.contains("</sml:SensorML>"));

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void testgetObservation() {
		System.out.println("\r\rtesting get observation");
		String file = getURL("getObsExample.xml").getFile();

		try {
			// mimicking post input stream
			InputStream inputStream = new FileInputStream(file);

			// ns.process(inputStream);

			StringWriter sw = new StringWriter();
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ns.process(inputStream, outputStream);
			System.out.println(ns.getOOSTethysDoc());

			String s = outputStream.toString();
			System.out.println(s);
			assertTrue(s.contains("<om:ObservationCollection"));
			assertTrue(s
					.contains("2008-06-10T01:06:20Z,36.69623,-122.39965,10,12.3123,3.83887,8.845,33.152874 2008-06-10T01:16:"));

			assertTrue(s
					.contains(" <om:procedure xlink:href=\"urn:mbari:org:device:1455\"/>"));

		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void testgetObservation2() {
		System.out.println("\r\rtesting get observation");
		String file = getURL("getObsExample2.xml").getFile();

		// 2008-06-02T17:56:19Z,36.69623,-122.39965,10,12.4353,3.83917,9.376,33.046978
		// 2008-06-02T18:06:21Z,36.69623,-122.39965,10,12.4284,3.

		// <eventTime>
		// <ogc:TM_During>
		// <ogc:PropertyName>om:samplingTime</ogc:PropertyName>
		// <gml:TimePeriod>
		// <gml:beginPosition>2008-06-02T17:56:18Z</gml:beginPosition>
		// <gml:endPosition>2008-06-02T18:06:22Z,</gml:endPosition>
		// </gml:TimePeriod>
		// </ogc:TM_During>
		// </eventTime>

		try {
			// mimicking post input stream
			InputStream inputStream = new FileInputStream(file);

			// ns.process(inputStream);

			StringWriter sw = new StringWriter();
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ns.process(inputStream, outputStream);

			String s = outputStream.toString();
			System.out.println(s);
			assertTrue(s.contains("EVENT_TIME"));
			assertTrue(s.contains("<ExceptionReport "));

		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void testgetObservationWrongParameters() {
		System.out.println("\r\rtesting get observation");
		String file = getURL("getObsExample3.xml").getFile();

		try {
			// mimicking post input stream
			InputStream inputStream = new FileInputStream(file);

			// ns.process(inputStream);

			StringWriter sw = new StringWriter();
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ns.process(inputStream, outputStream);

			String s = outputStream.toString();
			System.out.println(s);
			assertFalse(s.contains("<om:ObservationCollection"));
			assertTrue(s
					.contains("<ExceptionReport version=\"1.0\"><Exception exceptionCode=\"InvalidParameterValue\" locator=\"OFFERING\"><ExceptionText>Not able to find any observation with id: observationOffering_1455asdasdasdasdas"));

		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
