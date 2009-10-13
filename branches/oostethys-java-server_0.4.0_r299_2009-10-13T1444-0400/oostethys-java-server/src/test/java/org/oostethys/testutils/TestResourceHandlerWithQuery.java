/**
 *
 */
package org.oostethys.testutils;

import java.io.File;
import java.io.InputStream;

import junit.framework.TestCase;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.mortbay.jetty.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 * @author Jesper Zedlitz &lt;jze@informatik.uni-kiel.de&gt;
 *
 */
public class TestResourceHandlerWithQuery extends TestCase {
    private ResourceHandlerWithQuery handler;

    /**
      * @see junit.framework.TestCase#setUp()
      */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        File fileInResourceDir =
            new File(getClass().getResource("/oostethys.xml").getFile());
        File resourceDir = fileInResourceDir.getParentFile();

        handler = new ResourceHandlerWithQuery();
        handler.setResourceBase(resourceDir.getAbsolutePath());
    }

    public void testHead() throws Exception {
        long expectedContentLength =
            new File(getClass().getResource("/oostethys.xml").getFile()).length();

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("HEAD");
        request.setPathInfo("/oostethys.xml");
        request.setProtocol("HTTP/1.1");

        handler.handle("/oostethys.xml", request, response, 1);

        long contentLength =
            NumberUtils.toLong(ObjectUtils.toString(response.getHeader(
                        HttpHeaders.CONTENT_LENGTH)));

        assertEquals("status OK", 200, response.getStatus());
        assertEquals("content length", expectedContentLength, contentLength);
    }

    /**
     * The server will offer ACCEPT_RANGES = bytes
     * @throws Exception
     */
    public void testOffersAcceptRanges() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("HEAD");
        request.setPathInfo("/oostethys.xml");
        request.setProtocol("HTTP/1.1");

        handler.handle("/oostethys.xml", request, response, 1);

        assertEquals("bytes", response.getHeader(HttpHeaders.ACCEPT_RANGES));
    }

    public void testGet() throws Exception {
        String url = "/oostethys.xml";

        long expectedContentLength =
            new File(getClass().getResource(url).getFile()).length();

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setPathInfo(url);
        request.setProtocol("HTTP/1.1");

        handler.handle(url, request, response, 1);

        long contentLength =
            NumberUtils.toLong(ObjectUtils.toString(response.getHeader(
                        HttpHeaders.CONTENT_LENGTH)));

        assertEquals("status OK", 200, response.getStatus());
        assertEquals("content length in header", expectedContentLength,
            contentLength);
        assertEquals("real content length", expectedContentLength,
            response.getContentAsByteArray().length);
    }

    public void testWithQuery() throws Exception {
        String url =
            "/oceanwatch.pfeg.noaa.gov/thredds/dodsC/satellite/BA/ssta/mday.dods?altitude,lat,lon,time";

        // determine the true size of the file
        InputStream in = getClass().getResourceAsStream(url);
        byte[] buffer = new byte[1000];
        long expectedContentLength = 0;
        int read = in.read(buffer);

        while (read == 1000) {
            expectedContentLength += read;
            read = in.read(buffer);
        }

        expectedContentLength += read;

        // prepare mock objects
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setPathInfo(url);
        request.setProtocol("HTTP/1.1");

        // invoke method
        handler.handle(url, request, response, 1);

        long contentLength =
            NumberUtils.toLong(ObjectUtils.toString(response.getHeader(
                        HttpHeaders.CONTENT_LENGTH)));

        assertEquals("status OK", 200, response.getStatus());
        assertEquals("content length in header", expectedContentLength,
            contentLength);
        assertEquals("real content length", expectedContentLength,
            response.getContentAsByteArray().length);
    }

    public void test404() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setPathInfo("/not_there");
        request.setProtocol("HTTP/1.1");

        handler.handle("/not_there", request, response, 1);

        assertEquals("status 404", 404, response.getStatus());
    }

    /**
     * The client requests only partial content
     * @throws Exception
     */
    public void testPartialContent() throws Exception {
        String url = "/oostethys.xml";

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setPathInfo(url);
        request.setProtocol("HTTP/1.1");
        request.addHeader("Range", "bytes=0-49");

        handler.handle(url, request, response, 1);

        long contentLength =
            NumberUtils.toLong(ObjectUtils.toString(response.getHeader(
                        HttpHeaders.CONTENT_LENGTH)));

        assertEquals("status partial content", 206, response.getStatus());
        assertEquals("content length in header", 50, contentLength);
        assertEquals("real content length", 50,
            response.getContentAsByteArray().length);
    }
}
