package org.oostethys.sos.test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

import javax.servlet.ServletException;

import org.oostethys.servlet.SOS_Servlet;
import org.oostethys.test.OOSTethysTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;


public class TestPost extends OOSTethysTest {
    // before running this test youe need to run the local server
    public void testPostGetObs() throws IOException, ServletException {
        SOS_Servlet servlet = new SOS_Servlet();
        MockServletConfig config = new MockServletConfig("sos");
        servlet.init(config);
        
        MockHttpServletRequest request =
            new MockHttpServletRequest("POST", "/oostethys/sos");

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream wr = new PrintStream(new ByteArrayOutputStream());
        BufferedReader in =
            new BufferedReader(new InputStreamReader(getClass()
                                                         .getResourceAsStream("/getObsExample.xml")));
        String line = null;

        while ((line = in.readLine()) != null) {
            wr.append(line);
        }

        buffer.flush();
        request.setContent(buffer.toByteArray());

        MockHttpServletResponse response = new MockHttpServletResponse();

        // invoke servlet
        servlet.doPost(request, response);

        // get the response
        String responseString = response.getContentAsString();

        assertContains(responseString,"ObservationCollection");
        assertContains(responseString,"36.69623 -122.39965");
        assertContains(responseString,"urn:ogc:phenomenon:time:iso8601");
        assertContains(responseString,"featureOfInterest");
    }

    //http://mmisw.org/oostethys/sos?VERSION=1.0.0&SERVICE=SOS&request=DescribeSensor&procedure=urn:mbari:org:device:1455
//    public void testPostDescribeSensor() throws IOException {
//        // Send data
//        URL url =
//            new URL(
//                "http://mmisw.org/oostethys/sos?VERSION=1.0.0&SERVICE=SOS&REQUEST=describeSensor");
//        URLConnection conn = url.openConnection();
//        conn.setDoOutput(true);
//
//        if (conn instanceof HttpURLConnection) {
//            ((HttpURLConnection) conn).setRequestMethod("POST");
//        }
//
//        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
//
//        String file =
//            Thread.currentThread().getContextClassLoader()
//                  .getResource("getDescribeSensor.xml").getFile();
//
//        FileInputStream inputStream = new FileInputStream(file);
//        BufferedReader in =
//            new BufferedReader(new InputStreamReader(inputStream));
//        StringBuffer buffer = new StringBuffer();
//        String line = null;
//
//        while ((line = in.readLine()) != null) {
//            buffer.append(line);
//        }
//
//        String xml = buffer.toString();
//        System.out.println(xml);
//
//        wr.write(xml);
//        wr.flush();
//
//        System.out.println(" header " + conn.getHeaderField(0) + " >>>header");
//        ;
//
//        // Get the response
//        BufferedReader rd =
//            new BufferedReader(new InputStreamReader(conn.getInputStream()));
//
//        StringBuffer value = new StringBuffer();
//
//        while ((line = rd.readLine()) != null) {
//            value.append(line);
//        }
//
//        wr.close();
//        rd.close();
//
//        String responseString = value.toString();
//        System.out.println(responseString);
//        assertTrue(responseString.contains("SensorML"));
//        assertTrue(responseString.contains("urn:mbari:org:device:1455"));
//        assertTrue(responseString.contains("Serial CTD"));
//        assertTrue(responseString.contains(
//                "http://mmisw.org/cf#sea_water_salinity"));
//
//        wr.close();
//        rd.close();
//    }
}
