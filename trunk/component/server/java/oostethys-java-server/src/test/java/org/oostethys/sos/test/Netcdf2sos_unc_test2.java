package org.oostethys.sos.test;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.oostethys.sos.Netcdf2sos100;
import org.oostethys.test.OOSTethysTest;
import org.oostethys.testutils.LocalResourceServer;

public class Netcdf2sos_unc_test2 extends OOSTethysTest {
	Netcdf2sos100 ns = null;
	
	LocalResourceServer server = new LocalResourceServer();

	protected void setUp() throws Exception {
		super.setUp();
		
		server.startServer();
		ns = new Netcdf2sos100();
		
		URL url = Thread.currentThread().getContextClassLoader().getResource(
		"oostethys-unc.xml");
		ns.setUrlOostethys(url);
	}
	
	/**
	* @see junit.framework.TestCase#tearDown()
	*/
	@Override
	protected void tearDown() throws Exception {
	    server.stopServer();
	    super.tearDown();
	}

	public void testGetCapabilities() throws Exception {
			URL urlService = new URL("http://localhost:8080/oostethys/sos");
			ns.setServletURL(urlService.toString());

			Map<String, String[]> map = new HashMap<String, String[]>();
			map.put("REQUEST", createArray("GetCapabilities"));
			map.put("SERVICE", createArray("SOS"));
			map.put("VERSION", createArray("1.0.0"));
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ns.process(map, outputStream);

			String s = outputStream.toString();
			assertDoesNotContain(s, "ExceptionReport");

	}

	public void testDescribeSensor() throws Exception {
		Map<String, String[]> map = new HashMap<String, String[]>();
		map.put("Request", createArray("DescribeSensor"));
		map.put("procedure", createArray("urn:unc:org:jpier"));
		map.put("SERVICE", createArray("SOS"));
		map.put("VERSION", createArray("1.0.0"));
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		// ns.process(map, outputStream);
		ns.process(map, outputStream);
		String s = outputStream.toString();
		assertDoesNotContain(s, "ExceptionReport");
	}

	public void atestgetObservation() throws Exception {
			// 42.20551 -70.7238
			String minLon = "-71";
			String minLat = "40";
			String maxLon = "-69";
			String maxlat = "45";
			String bbox = minLon + "," + minLat + "," + maxLon + "," + maxlat;

			Map<String, String[]> map = new HashMap<String, String[]>();
			map.put("Request", createArray("getObservation"));
			map.put("bbox", createArray(bbox));
			map.put("service", createArray("SOS"));
			map.put("version", createArray("1.0.0"));
			map.put("OFFERING", createArray("observationOffering_" + "um"));

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ns.process(map, outputStream);

			String s = outputStream.toString();
			assertDoesNotContain(s, "ExceptionReport");

			// ns.setValue_BBOX(bbox);

	}

}
