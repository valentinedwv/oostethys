/**
 *
 */
package org.oostethys.servlet;

import org.oostethys.test.OOSTethysTest;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;


/**
 * Tests from the TEAM engine tests for SOS.
 * @author Jesper Zedlitz &lt;jze@informatik.uni-kiel.de&gt;
 *
 */
public class SOS_ServletTest extends OOSTethysTest {
    SOS_Servlet servlet = new SOS_Servlet();

    /**
      * @see junit.framework.TestCase#setUp()
      */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        MockServletConfig config = new MockServletConfig("sos");
        servlet.init(config);
    }

    public void testContentType_1() throws Exception {
        MockHttpServletRequest request =
            new MockHttpServletRequest("GET", "/oostethys/sos");
        request.setParameter("service", "SOS");
        request.setParameter("version", "1.0.0");
        request.setParameter("request", "GetCapabilities");

        MockHttpServletResponse response = new MockHttpServletResponse();

        // invoke servlet
        servlet.doGet(request, response);

        // get the response
        String responseString = response.getContentAsString();

        assertEquals("response type is text/xml", "text/xml",
            response.getContentType());
        assertContains("response is a Capabilities document", responseString,
            "Capabilities");
    }

    /**
     * Parameter service missing.
     * @throws Exception
     */
    public void testGetCapabilities_Exceptions_1() throws Exception {
        MockHttpServletRequest request =
            new MockHttpServletRequest("GET", "/oostethys/sos");
        request.setParameter("version", "1.0.0");
        request.setParameter("request", "GetCapabilities");

        MockHttpServletResponse response = new MockHttpServletResponse();

        // invoke servlet
        servlet.doGet(request, response);

        // get the response
        String responseString = response.getContentAsString();

        assertContains("response is an ExceptionReport", responseString,
            "ExceptionReport");
        assertContains(responseString, "MissingParameterValue");
    }

    /**
     * Invalid value for parameter "service"
     * @throws Exception
     */
    public void testGetCapabilities_Exceptions_2() throws Exception {
        MockHttpServletRequest request =
            new MockHttpServletRequest("GET", "/oostethys/sos");
        request.setParameter("service", "ASDF");
        request.setParameter("version", "1.0.0");
        request.setParameter("request", "GetCapabilities");

        MockHttpServletResponse response = new MockHttpServletResponse();

        // invoke servlet
        servlet.doGet(request, response);

        // get the response
        String responseString = response.getContentAsString();

        System.out.println(responseString);
        assertContains("response is an ExceptionReport", responseString,
            "ExceptionReport");
        assertContains(responseString, "InvalidParameterValue");
    }

    /**
     * Unknown SOS version
     * @throws Exception
     */
    public void testGetCapabilities_Exceptions_3() throws Exception {
        MockHttpServletRequest request =
            new MockHttpServletRequest("GET", "/oostethys/sos");
        request.setParameter("service", "SOS");
        request.setParameter("acceptversions", "2000-01-01");
        request.setParameter("request", "GetCapabilities");

        MockHttpServletResponse response = new MockHttpServletResponse();

        // invoke servlet
        servlet.doGet(request, response);

        // get the response
        String responseString = response.getContentAsString();

        assertContains("response is an ExceptionReport", responseString,
            "ExceptionReport");
        assertContains(responseString, "VersionNegotiationFailed");
    }

    /**
     * GetCapabilities with an incorrect KVP query string, triggering the missing parameter value exception.
     * @throws Exception
     */
    public void testGetCapabilities_Exceptions_5() throws Exception {
        MockHttpServletRequest request =
            new MockHttpServletRequest("GET",
                "/oostethys/sos?request~GetCapabilities!service~!SOSversion~1.0.0");

        MockHttpServletResponse response = new MockHttpServletResponse();

        // invoke servlet
        servlet.doGet(request, response);

        // get the response
        String responseString = response.getContentAsString();

        assertContains("response is an ExceptionReport", responseString,
            "ExceptionReport");
        assertContains(responseString, "MissingParameterValue");
    }
}
