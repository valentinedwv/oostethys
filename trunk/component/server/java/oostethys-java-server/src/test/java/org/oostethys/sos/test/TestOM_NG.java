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

	public void testPostGetObs() throws IOException {
			// Send data
			URL url = new URL("http://mmisw/oostethys/sos");
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);

			if (conn instanceof HttpURLConnection)
				((HttpURLConnection) conn).setRequestMethod("POST");

			OutputStreamWriter wr = new OutputStreamWriter(conn
					.getOutputStream());

			String file = Thread.currentThread().getContextClassLoader()
					.getResource("getObsNG.xml").getFile();

			FileInputStream inputStream = new FileInputStream(file);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					inputStream));
			StringBuffer buffer = new StringBuffer();
			String line = null;
			while ((line = in.readLine()) != null) {
				buffer.append(line);
			}

			String xml = buffer.toString();
			System.out.println(xml);

			wr.write(xml);
			wr.flush();

			System.out.println("--- start HTTP header "
					+ conn.getHeaderField(0) + "/n ---end HTTP header");
			;

			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn
					.getInputStream()));
			StringBuffer value = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				value.append(line);
			}
			wr.close();
			rd.close();
			String responseString = value.toString();
			System.out.println("response: " + responseString);
			assertTrue(responseString.contains("ObservationCollection"));
	
	}

}
