package org.oostethys.other.test;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.opengis.sensorML.x101.SensorMLDocument;

import org.oostethys.sos.Netcdf2sos100;
import org.oostethys.test.OOSTethysTest;
import org.oostethys.testutils.LocalResourceServer;

/**
 * 
 * This test has been disabled - will not work if you are offline
 * 
 * @author bermudez
 * 
 */
public class Netcdf2sos_roym_Test extends OOSTethysTest {
	Netcdf2sos100 ns = null;

	private LocalResourceServer server = new LocalResourceServer();

	protected void setUp() throws Exception {
		super.setUp();
		ns = new Netcdf2sos100();

		URL url = Thread.currentThread().getContextClassLoader().getResource(
				"oostethys-opendap.xml");
		ns.setUrlOostethys(url);

		// start test server
		server.startServer();
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

		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ns.process(map, baos);

		final String result = baos.toString();
		System.out.println(result);

		assertDoesNotContain(result, "ExceptionReport");

	}

	public void testDescribeSensor() throws Exception {
		final Map<String, String[]> map = new HashMap<String, String[]>();
		map.put("Request", new String[] { "describeSensor" });
		map.put("procedure", new String[] { "urn:xxx:org:model1" });
		map.put("SERVICE", createArray("SOS"));
		map.put("VERSION", createArray("1.0.0"));
		map.put("outputformat",
				createArray("text/xml;subtype=\"sensorML/1.0.1\""));

		final ByteArrayOutputStream baos = new ByteArrayOutputStream();

		ns.process(map, null);
		ns.getDescribeSensor(baos);

		final String result = baos.toString();
		System.out.println(result);
		SensorMLDocument.Factory.parse(result);

	}

	public void testgetObservation() throws Exception {
		// 42.20551 -70.7238
		String minLon = "-71";
		String minLat = "40";
		String maxLon = "-69";
		String maxlat = "45";
		String bbox = minLon + "," + minLat + "," + maxLon + "," + maxlat;

		Map<String, String[]> map = new HashMap<String, String[]>();
		map.put("Request", new String[] { "getObservation" });
		map.put("bbox", new String[] { bbox });
		map.put("SERVICE", createArray("SOS"));
		map.put("VERSION", createArray("1.0.0"));
		map.put("offering",
				createArray("observationOffering_um"));

		// ns.setValue_BBOX(bbox);

		final ByteArrayOutputStream baos = new ByteArrayOutputStream();

		ns.process(map, null);
		ns.getObservation(baos);

		final String result = baos.toString();

		assertDoesNotContain(result, "ExceptionReport");
	}

}
