package org.oostethys.sos.test;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.oostethys.sos.Netcdf2sos100;
import org.oostethys.test.OOSTethysTest;
import org.oostethys.testutils.LocalResourceServer;

public class Netcdf2sos_roym_test extends OOSTethysTest {
	Netcdf2sos100 ns = null;
//	
//	 usmass-oost.xml describe un nc file asi:
	
	//float elev(time=96, ypos=1, xpos=1);
//     :long_name = "Elevation";
//     :units = "meters";
//     :standard_name = "sea_surface_height";
//     :coordinates = "lon lat";
//   float lat(ypos=1, xpos=1);
//     :units = "degrees_north";
//     :standard_name = "latitude";
//     :_CoordinateAxisType = "Lat";
//   float lon(ypos=1, xpos=1);
//     :units = "degrees_east";
//     :standard_name = "longitude";
//     :_CoordinateAxisType = "Lon";
//   float time(time=96);
//     :long_name = "Time";
//     :units = "hours since 2006-01-01 00:00 UTC";
//     :_CoordinateAxisType = "Time";
	
// 2008-06-05T04:30:00Z,42.20551 -1.01651,  2008-06-05T23:30:00Z,42.20551,-70.72384,1.319742

	private LocalResourceServer server = new LocalResourceServer();
	
	protected void setUp() throws Exception {
		super.setUp();
		ns = new Netcdf2sos100();
		
		URL url = Thread.currentThread().getContextClassLoader()
		.getResource("oostethys-roym.xml");
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
			map.put("REQUEST",createArray("GetCapabilities"));
			map.put("SERVICE", createArray("SOS"));
			map.put("VERSION", createArray("1.0.0"));
			
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ns.process(map,baos);
		
		final String result = baos.toString();
		
		assertDoesNotContain(result, "ExceptionReport");

	}

	public void testDescribeSensor() throws Exception {
		final Map<String, String[]> map = new HashMap<String, String[]>();
		map.put("Request", new String[]{"describeSensor"});
		map.put("sensorid", new String[]{"urn:usgs:gov:model:um1-gom"});

		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
		ns.process(map, null);
		ns.getDescribeSensor(baos);
			
		final String result = baos.toString();
			
		assertDoesNotContain(result, "ExceptionReport");
	}

	public void testgetObservation() throws Exception {
			// 42.20551 -70.7238
			String minLon = "-71";
			String minLat = "40";
			String maxLon = "-69";
			String maxlat = "45";
			String bbox = minLon + "," + minLat + "," + maxLon + "," + maxlat;

			Map<String, String[]> map = new HashMap<String, String[]>();
			map.put("Request", new String[]{"getObservation"});
			map.put("bbox", new String[]{bbox});

			// ns.setValue_BBOX(bbox);

		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		ns.process(map, null);
		ns.getObservation(baos);

		final String result = baos.toString();
		
		assertDoesNotContain(result, "ExceptionReport");
	}

}
