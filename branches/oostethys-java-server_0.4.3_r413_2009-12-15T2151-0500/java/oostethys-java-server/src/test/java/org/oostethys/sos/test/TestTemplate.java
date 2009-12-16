package org.oostethys.sos.test;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.opengis.ows.x11.ExceptionReportDocument;
import net.opengis.ows.x11.ExceptionReportDocument.ExceptionReport;

import org.oostethys.sos.Netcdf2sos100;
import org.oostethys.test.OOSTethysTest;
import org.oostethys.testutils.LocalResourceServer;

/**
 * This is a template for fast testing a configuration file.
 * 1) do copy paste, 2) change the name of this name of the class
 * 3) change the name of the file to test.
 * 4) run the test.
 * 
 * @author bermudez
 *
 */
public class TestTemplate extends OOSTethysTest {
	Netcdf2sos100 ns = null;
	private LocalResourceServer server = new LocalResourceServer();
	
	private String fileNameToTest = "oostethys-opendap.xml";

	protected void setUp() throws Exception {

		super.setUp();
		ns = new Netcdf2sos100();

		URL url = Thread.currentThread().getContextClassLoader().getResource(
				fileNameToTest);
		ns.setUrlOostethys(url);

		URL urlService = new URL("http://localhost:8080/oostethys/sos");
		ns.setServletURL(urlService.toString());

		// start test server
		server.startServer();

	}

	public void testGetCapabilities() throws Exception {
		Map<String, String[]> map = new HashMap<String, String[]>();
		map.put("REQUEST", createArray("GetCapabilities"));
		map.put("SERVICE", createArray("SOS"));
		map.put("VERSION", createArray("1.0.0"));

		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ns.process(map, baos);

		final String result = baos.toString();

		assertDoesNotContain(result, "ExceptionReport");

	}
	
	 protected void tearDown() throws Exception {
	     	server.stopServer();
	        super.tearDown();
	    }
 

}
