package org.oostethys.sos.test;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.oostethys.sos.Netcdf2sos100;
import org.oostethys.test.OOSTethysTest;

public class Netcdf2sos_capabilitiesparameterIssue extends OOSTethysTest {
	Netcdf2sos100 ns = null;
	protected void setUp() throws Exception {
		super.setUp();
		ns = new Netcdf2sos100();
		URL file = getURL("usmass-oost.xml");
		URL url = file;

		ns.setUrlOostethys(url);
	}
	
	public void testGetCapabilities() throws Exception {
			URL urlService = new URL("http://localhost:8080/oostethys/sos");
			ns.setServletURL(urlService.toString());

			Map<String, String[]> map = new HashMap<String, String[]>();
			map.put("REQUEST", createArray("GetCapabilities"));
			map.put("SERVICE", createArray("SOS"));
			// this is a bug ? https://sourceforge.net/tracker/index.php?func=detail&aid=2136348
			map.put("VERSION", createArray("1.0.0"));
			
			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ns.process(map, baos);
			final String result = baos.toString();

			assertDoesNotContain(result, "ExceptionReport");

	}

}
