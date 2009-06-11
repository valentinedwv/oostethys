package org.oostethys.sos.test;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import org.oostethys.sos.Netcdf2sos100;
import org.oostethys.test.OOSTethysTest;

public class Netcdf2sos_mbari_ctd_xmlPost_Test extends OOSTethysTest {
	Netcdf2sos100 ns = null;

	protected void setUp() throws Exception {
		super.setUp();
		ns = new Netcdf2sos100();
		URL file = getURL("mbari-oost.xml");
		URL url = file;

		ns.setUrlOostethys(url);

	}

	public void testDescribeSensorPost() throws Exception {
		String file = getURL("getDescribeSensor.xml").getFile();
			FileInputStream fis = new FileInputStream(file);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ns.process(fis, outputStream);
			ns.setNumberOfRecordsToProcess(10);
			String doc = ns.getOOSTethysDoc();
			String s = outputStream.toString();
			assertContains(s,"<gml:beginPosition>2008-06-09T09:36:19Z</gml:beginPosition>");
			assertContains(s,"<swe:field name=\"Salinity\">");
			assertContains(s,"</sml:SensorML>");

	}

	public void testDescribeSensorPostErrorBaaProcedureName() throws Exception {
		String file = getURL("getDescribeSensorError-badProcedure.xml")
				.getFile();
		InputStream inputStream = new FileInputStream(file);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ns.process(inputStream, outputStream);

			String s = outputStream.toString();

			assertContains(s,"<ExceptionReport version=\"1.0\"><Exception exceptionCode=\"InvalidParameterValue");
			assertDoesNotContain(s,"</sml:SensorML>");

	}

	public void testDescribeSensorPostErrorBadVersion() throws Exception {
		String file = getURL("getDescribeSensorError-badVersion.xml").getFile();
		InputStream inputStream = new FileInputStream(file);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ns.process(inputStream, outputStream);

			String s = outputStream.toString();

			assertContains(s,"<ExceptionReport version=\"1.0\"><Exception exceptionCode=\"InvalidParameterValue");

			assertDoesNotContain(s,"</sml:SensorML>");
	}

	public void testgetObservation() throws Exception {
		String file = getURL("getObsExample.xml").getFile();

			// mimicking post input stream
			InputStream inputStream = new FileInputStream(file);

			// ns.process(inputStream);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ns.process(inputStream, outputStream);

			String s = outputStream.toString();
			
			assertContains(s, "<om:ObservationCollection");
			assertContains(s, "2008-06-10T01:06:20Z,36.69623,-122.39965,10,1,213,059,980,-122.39965,36.69623,10,12.3123,3.83887,8.845,33.152874 2008-06-10T01:16:");
			assertContains(s, " <om:procedure xlink:href=\"urn:mbari:org:device:1455\"/>");

	}

	public void testgetObservation2() throws Exception {
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

			// mimicking post input stream
			InputStream inputStream = new FileInputStream(file);

			// ns.process(inputStream);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ns.process(inputStream, outputStream);

			String s = outputStream.toString();
			assertTrue(s.contains("EVENT_TIME"));
			assertTrue(s.contains("<ExceptionReport "));
	}

	public void testgetObservationWrongParameters() throws Exception {
		String file = getURL("getObsExample3.xml").getFile();

			// mimicking post input stream
			InputStream inputStream = new FileInputStream(file);

			// ns.process(inputStream);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ns.process(inputStream, outputStream);

			String s = outputStream.toString();
			assertFalse(s.contains("<om:ObservationCollection"));
			assertTrue(s
					.contains("<ExceptionReport version=\"1.0\"><Exception exceptionCode=\"InvalidParameterValue\" locator=\"OFFERING\"><ExceptionText>Not able to find any observation with id: observationOffering_1455asdasdasdasdas"));
	}

}
