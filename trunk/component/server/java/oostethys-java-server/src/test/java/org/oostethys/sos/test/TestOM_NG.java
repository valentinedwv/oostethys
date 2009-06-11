package org.oostethys.sos.test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

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

		// ns.setValue_BBOX(bbox);

		String fileRequest = getURL("getObsNG.xml").getFile();
		InputStream inputStream;
			inputStream = new FileInputStream(fileRequest);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

			ns.process(inputStream, outputStream);
			String s = outputStream.toString();
			System.out.println(s);
			
			assertTrue(s.contains("ObservationCollection"));
	}

}
