package org.oostethys.sos.test;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.oostethys.sos.Netcdf2sos100;
import org.oostethys.test.OOSTethysTest;

public class Netcdf2sos_mbari_ctd_test extends OOSTethysTest {
	Netcdf2sos100 ns = null;
	
//	netcdf file:/Users/bermudez/Documents/workspace31/org.oostethys/WebRoot/WEB-INF/classes/ctd0010.nc {
//		 dimensions:
//		   esecs = 8265;   // (has coord.var)
//		   NominalDepth = 1;   // (has coord.var)
//		   Longitude = 1;   // (has coord.var)
//		   Latitude = 1;   // (has coord.var)
//		 variables:
//		   float Temperature(esecs=8265, NominalDepth=1, Latitude=1, Longitude=1);
//		     :long_name = "Water Temperature";
//		     :units = "deg C";
//		     :standard_name = "Temperature";
//		   float Conductivity(esecs=8265, NominalDepth=1, Latitude=1, Longitude=1);
//		     :long_name = "Conductivity";
//		     :units = "S/m";
//		     :standard_name = "conductivity";
//		   float Pressure(esecs=8265, NominalDepth=1, Latitude=1, Longitude=1);
//		     :long_name = "Pressure";
//		     :units = "decibars";
//		     :standard_name = "pressure";
//		   float Salinity(esecs=8265, NominalDepth=1, Latitude=1, Longitude=1);
//		     :long_name = "Salinity";
//		     :units = "";
//		     :standard_name = "sea_water_salinity";
//		     :missing_value = -99999.0f; // float
//		     :_FillValue = -99999.0f; // float
//		   double esecs(esecs=8265);
//		     :long_name = "time GMT";
//		     :units = "seconds since 1970-01-01 00:00:00";
//		     :_CoordinateAxisType = "Time";
//		   float NominalDepth(NominalDepth=1);
//		     :long_name = "Depth";
//		     :units = "m";
//		     :standard_name = "depth";
//		     :_CoordinateAxisType = "Height";
//		   double Longitude(Longitude=1);
//		     :long_name = "Longitude";
//		     :units = "degrees_east";
//		     :standard_name = "longitude";
//		     :_CoordinateAxisType = "Lon";
//		   double Latitude(Latitude=1);
//		     :long_name = "Latitude";
//		     :units = "degrees_north";
//		     :standard_name = "latitude";
//		     :_CoordinateAxisType = "Lat";
//

	protected void setUp() throws Exception {
		super.setUp();
		ns = new Netcdf2sos100();
		URL file = getURL("mbari-oost.xml");
		URL url = file;

		ns.setUrlOostethys(url);

	}
	
	
	public void testGetCapabilities() throws Exception {
			URL urlService = new URL("http://localhost:8080/oostethys/sos");
			ns.setServletURL(urlService.toString());
			
			Map<String, String[]> map = new HashMap<String, String[]>();
			map.put("Request", createArray("getCapabilities"));
			map.put("service", createArray("SOS"));
			map.put("version", createArray("1.0.0"));
	
			
			

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ns.process(map, outputStream);
			
			String s = outputStream.toString();
			
			ns.processForTest();
			ns.getCapabilities(System.out);
			String surl = ns.getServletURL();
			assertEquals(urlService.toString(), surl);
			assertEquals(urlService.toString(),ns.getOostDocTemp().getOostethys().getWebServerURL().toString());

	}

	public void testDescribeSensor() throws Exception {
			Map<String, String[]> map = new HashMap<String, String[]>();
			map.put("Request", createArray("DescribeSensor"));
			map.put("procedure", createArray("urn:mbari:org:device:1455"));
			map.put("service", createArray("SOS"));
			map.put("version", createArray("1.0.0"));
			map.put("outputformat", createArray(Netcdf2sos100.responseFormat));
			
			

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ns.process(map, outputStream);
			
			String s = outputStream.toString();
			
			assertContains(s,"<gml:beginPosition>2008-06-10T00:36:19Z</gml:beginPosition>");
			assertContains(s,"<swe:field name=\"Salinity\">");
			assertContains(s,"</sml:SensorML>");

	}

	public void testgetObservation() throws Exception {
			// 42.20551 -70.7238
			String minLon = "-71";
			String minLat = "40";
			String maxLon = "-69";
			String maxlat = "45";
			String bbox = minLon + "," + minLat + "," + maxLon + "," + maxlat;

			Map<String, String[]> map = new HashMap<String, String[]>();
			map.put("Request", createArray("getObservation"));
			map.put("bbox", createArray(bbox));
			map.put("SERVICE", createArray("SOS"));
			map.put("VERSION", createArray("1.0.0"));
			map.put("offering", createArray("observationOffering_1455"));

			// ns.setValue_BBOX(bbox);
			
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ns.process(map, outputStream);
			
			String s = outputStream.toString();
			
			assertDoesNotContain(s, "ExceptionReport");
	}

}
