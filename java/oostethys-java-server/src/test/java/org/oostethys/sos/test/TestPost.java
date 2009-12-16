package org.oostethys.sos.test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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


public class TestPost extends OOSTethysTest {
	
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
        MockServletConfig config = new MockServletConfig("sos");
        servlet.init(config);
        
        MockHttpServletRequest request =
            new MockHttpServletRequest("POST", "/oostethys/sos");

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream wr = new PrintStream(buffer);
      
        BufferedReader in =
            new BufferedReader(new InputStreamReader(getClass()
                                                         .getResourceAsStream("/getObsExample.xml")));
        String line = null;

        while ((line = in.readLine()) != null) {
            wr.append(line);
        }

        wr.flush();
        request.setContent(buffer.toByteArray());

        MockHttpServletResponse response = new MockHttpServletResponse();

        // invoke servlet
        servlet.doPost(request, response);
   
        final String result = response.getContentAsString();

    	assertDoesNotContain(result, "ExceptionReport");

       
    }

}