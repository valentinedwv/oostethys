package org.oostethys.sos.test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;

import javax.servlet.ServletException;

import org.oostethys.servlet.SOS_Servlet;
import org.oostethys.test.OOSTethysTest;
import org.oostethys.testutils.LocalResourceServer;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;

public class TestPostOM_NG extends OOSTethysTest {

	private LocalResourceServer server = new LocalResourceServer();

	protected void setUp() throws Exception {
		super.setUp();
		server.startServer();
	}

	@Override
	protected void tearDown() throws Exception {
		server.stopServer();
		super.tearDown();
	}

	public void testPostGetObs() throws IOException, ServletException {
		SOS_Servlet servlet = new SOS_Servlet();
		
		servlet.setConfigFile("mbari-oost.xml");

		MockServletConfig config = new MockServletConfig("sos");
		servlet.init(config);

		MockHttpServletRequest request = new MockHttpServletRequest("POST",
				"/oostethys/sos");
		
		// get Capabilities get
		
		request.setParameter("VERSION", "1.0.0");
		request.setParameter("SERVICE", "SOS");
		request.setParameter("REQUEST", "GetCapabilities");
		
		
		MockHttpServletResponse response = new MockHttpServletResponse();

		servlet.doGet(request, response);
		
		final String result = response.getContentAsString();
		
		
		
		// do Observation post
		
		  ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	        PrintStream wr = new PrintStream(buffer);
		
		 BufferedReader in =
	            new BufferedReader(new InputStreamReader(getClass()
	                                                         .getResourceAsStream("/getObsNG.xml")));
	        String line = null;

	        while ((line = in.readLine()) != null) {
	            wr.append(line);
	        }

	        wr.flush();
	        request.setContent(buffer.toByteArray());
	        
	        servlet.doPost(request, response);
	    	final String result2 = response.getContentAsString();
	    	System.out.println(result2);
	
			assertTrue(result2.contains("ObservationCollection"));
		
		
		
	}

}