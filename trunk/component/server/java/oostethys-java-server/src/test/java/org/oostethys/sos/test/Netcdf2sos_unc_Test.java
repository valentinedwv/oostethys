package org.oostethys.sos.test;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.oostethys.sos.Netcdf2sos100;
import org.oostethys.test.OOSTethysTest;
import org.oostethys.testutils.LocalResourceServer;

public class Netcdf2sos_unc_Test extends OOSTethysTest {
    Netcdf2sos100 ns = null;

    private LocalResourceServer server = new LocalResourceServer();
    
    protected void setUp() throws Exception {
	super.setUp();
	ns = new Netcdf2sos100();

	server.startServer();
	
	URL url = Thread.currentThread().getContextClassLoader().getResource(
		"oostethys-unc.xml");
	System.out.println(url);
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

	final ByteArrayOutputStream baos = new ByteArrayOutputStream();
	ns.process(map, baos);

	final String result = baos.toString();

	assertDoesNotContain(result, "ExceptionReport");
    }

    public void testDescribeSensor() throws Exception {
	Map<String, String[]> map = new HashMap<String, String[]>();
	map.put("Request", new String[] { "describeSensor" });
	map.put("procedure", new String[] { "urn:unc:org:jpier" });
	map.put("service", createArray("SOS"));
	map.put("version", createArray("1.0.0"));
	
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
	map.put("Request", new String[] { "getObservation" });
	map.put("bbox", new String[] { bbox });
	map.put("SERVICE", createArray("SOS"));
	map.put("VERSION", createArray("1.0.0"));
	map.put("offering", createArray("observationOffering_jpier"));

	
	// ns.setValue_BBOX(bbox);

	final ByteArrayOutputStream baos = new ByteArrayOutputStream();
	
	ns.process(map, null);
	ns.getObservation(baos);
	final String result = baos.toString();

	assertDoesNotContain(result, "ExceptionReport");
    }

}
