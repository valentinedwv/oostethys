package org.oostethys.sos.test;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.oostethys.sos.Netcdf2sos100;
import org.oostethys.test.OOSTethysTest;

import junit.framework.TestCase;

public class SOS_100_Multisystem extends OOSTethysTest {
	Netcdf2sos100 ns = null;

	
	protected void setUp() throws Exception {
		super.setUp();
		ns = new Netcdf2sos100();
		URL file = Thread.currentThread().getContextClassLoader().getResource(
				"oostethys_multisystem.xml");
		URL url = file;

		ns.setUrlOostethys(url);

	}

	

	
	public void testGetObservation_noTime() {
		System.out.println("\r\rtesting get observation");
		try {
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
			
			assertTrue(s.contains("<swe:values>"));
			System.out.println(s);

			
		} catch (MalformedURLException e) {
			
			e.printStackTrace();
			assertTrue(false);
		} catch (Exception e) {
			
			e.printStackTrace();
			assertTrue(false);
		}
	}

}
