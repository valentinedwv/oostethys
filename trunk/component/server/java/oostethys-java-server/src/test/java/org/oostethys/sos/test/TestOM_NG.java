package org.oostethys.sos.test;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import org.oostethys.sos.Netcdf2sos100;
import org.oostethys.test.OOSTethysTest;

public class TestOM_NG extends OOSTethysTest {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testGetObsInternal() throws Exception {

		Netcdf2sos100 ns = new Netcdf2sos100();
		URL file =getURL("mbari-oost.xml");
		ns.setUrlOostethys(file);
		ns.setServletURL("http://localhost:8080/sss");

		
		String fileRequest = getURL("getObsNG.xml").getFile();
		InputStream inputStream;
			inputStream = new FileInputStream(fileRequest);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

			ns.process(inputStream, outputStream);
			String s = outputStream.toString();
			
			assertTrue(s.contains("ObservationCollection"));
	}

}
