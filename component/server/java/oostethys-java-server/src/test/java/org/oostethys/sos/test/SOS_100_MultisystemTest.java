package org.oostethys.sos.test;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.oostethys.sos.Netcdf2sos100;
import org.oostethys.test.OOSTethysTest;
import org.oostethys.testutils.LocalResourceServer;

public class SOS_100_MultisystemTest extends OOSTethysTest {
	Netcdf2sos100 ns = null;

	LocalResourceServer server = new LocalResourceServer();
	
	protected void setUp() throws Exception {
		super.setUp();
		
		server.startServer();
		
		ns = new Netcdf2sos100();
		URL file = Thread.currentThread().getContextClassLoader().getResource(
				"oostethys_multisystem.xml");
		URL url = file;

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

	
	public void testGetObservation_noTime() throws Exception {
			// 42.20551 -70.7238
			String minLon = "-140";
			String minLat = "36";
			String maxLon = "-68";
			String maxlat = "45";
			String bbox = minLon + "," + minLat + "," + maxLon + "," + maxlat;

			Map<String, String[]> map = new HashMap<String, String[]>();
			map.put("Request", createArray("getObservation"));
			map.put("bbox", createArray(bbox));
			map.put("Service", createArray("SOS"));
			map.put("version", createArray("1.0.0"));
			map.put("offering", createArray("observationOffering_1455"));
			
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			

			// ns.setValue_BBOX(bbox);

			ns.process(map, outputStream);
			String s = outputStream.toString();
			
			assertContains(s,"<swe:values>");
	}

}
