/**
 *
 */
package org.oostethys.testutils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.mortbay.io.WriterOutputStream;
import org.mortbay.jetty.HttpConnection;
import org.mortbay.jetty.HttpHeaders;
import org.mortbay.jetty.HttpMethods;
import org.mortbay.jetty.HttpStatus;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.handler.AbstractHandler;
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.jetty.handler.ContextHandler.SContext;
import org.mortbay.log.Log;
import org.mortbay.resource.Resource;
import org.mortbay.util.URIUtil;


/* ------------------------------------------------------------ */
/**
 * Resource Handler.
 *
 * This handle will serve static content and handle If-Modified-Since headers.
 * No caching is done. Requests that cannot be handled are let pass (Eg no
 * 404's)
 *
 * @author Greg Wilkins (gregw)
 * @org.apache.xbean.XBean
 */
public class ResourceHandlerWithQuery extends AbstractHandler {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger
	    .getLogger(ResourceHandlerWithQuery.class.getName());
    private ContextHandler context;
    private Resource baseResource;

    public void doStart() throws Exception {
        SContext scontext = ContextHandler.getCurrentContext();
        context = ((scontext == null) ? null : scontext.getContextHandler());

        super.doStart();
    }

    /**
     * @return Returns the resourceBase.
     */
    public Resource getBaseResource() {
        return baseResource;
    }

    /**
     * @param base
     *            The resourceBase to set.
     */
    public void setBaseResource(final Resource base) {
        baseResource = base;
    }

    /**
     * @param resourceBase
     *            The base resource as a string.
     */
    public void setResourceBase(final String resourceBase) {
        try {
            setBaseResource(Resource.newResource(resourceBase));
        } catch (final Exception e) {
            Log.warn(e);
            throw new IllegalArgumentException(resourceBase);
        }
    }

    public Resource getResource(final String path) throws MalformedURLException {
        if ((path == null) || !path.startsWith("/")) {
            throw new MalformedURLException(path);
        }

        Resource base = baseResource;

        if (base == null) {
            if (context == null) {
                return null;
            }

            base = context.getBaseResource();

            if (base == null) {
                return null;
            }
        }

        try {
            Resource resource = base.addPath(URIUtil.canonicalPath(path));

            return resource;
        } catch (final Exception e) {
            Log.ignore(e);
        }

        return null;
    }

    protected Resource getResource(final HttpServletRequest request)
        throws MalformedURLException {
        String path_info = request.getPathInfo();

        if (path_info == null) {
            return null;
        }

        if (StringUtils.isEmpty(request.getQueryString())) {
            // no query string
            return getResource(path_info);
        }

        return getResource(path_info + '?' + request.getQueryString());
    }

    /**
     * @see
     * org.mortbay.jetty.Handler#handle(javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse, int)
     */
    public void handle(final String target, final HttpServletRequest request,
        final HttpServletResponse response, final int dispatch)
        throws IOException, ServletException {
        Request baseRequest;

        if (request instanceof Request) {
            baseRequest = (Request) request;
        } else {
            HttpConnection con = HttpConnection.getCurrentConnection();

            if (con != null) {
                baseRequest = con.getRequest();
            } else {
                baseRequest = null;
            }
        }

        if ((baseRequest != null) && baseRequest.isHandled()) {
            return;
        }

        boolean skipContentBody = false;

        if (!HttpMethods.GET.equals(request.getMethod())) {
            if (!HttpMethods.HEAD.equals(request.getMethod())) {
                return;
            }

            skipContentBody = true;
        }

        Resource resource = getResource(request);

        if ((resource == null) || !resource.exists()) {
            response.setStatus(HttpStatus.ORDINAL_404_Not_Found);

            log.warn("resource not found "+request.getPathInfo()+'?'+request.getQueryString());
            
            return;
        }

        // We are going to server something
        if (baseRequest != null) {
            baseRequest.setHandled(true);
        }

        if (resource.isDirectory()) {
            if (!request.getPathInfo().endsWith(URIUtil.SLASH)) {
                response.sendRedirect(URIUtil.addPaths(
                        request.getRequestURI(), URIUtil.SLASH));

                return;
            }

            if ((resource == null) || !resource.exists() ||
                    resource.isDirectory()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);

                return;
            }
        }

        // set some headers
        long last_modified = resource.lastModified();

        if (last_modified > 0) {
            long if_modified =
                request.getDateHeader(HttpHeaders.IF_MODIFIED_SINCE);

            if ((if_modified > 0) &&
                    ((last_modified / 1000) <= (if_modified / 1000))) {
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);

                return;
            }
        }

        long start = 0;
        long contentLength = resource.length();
        response.setStatus(HttpStatus.ORDINAL_200_OK);

        /*
         * handle partial content requests
         */
        String range = request.getHeader(HttpHeaders.RANGE);

        if (StringUtils.isNotEmpty(range)) {
            response.setStatus(HttpStatus.ORDINAL_206_Partial_Content);

            // Range: bytes=0-19531
            start = NumberUtils.toInt(StringUtils.substringBetween(range,
                        "bytes=", "-"));

            int lastByte =
                NumberUtils.toInt(StringUtils.substringAfter(range, "-"));

            if (lastByte < contentLength) {
                contentLength = lastByte - start + 1;
            }
        }

        // set the headers
        response.setIntHeader(HttpHeaders.CONTENT_LENGTH, (int) contentLength);
        response.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");
        response.setDateHeader(HttpHeaders.LAST_MODIFIED, last_modified);

        if (skipContentBody) {
            return;
        }

        // Send the content
        OutputStream out = null;

        try {
            out = response.getOutputStream();
        } catch (final IllegalStateException e) {
            out = new WriterOutputStream(response.getWriter());
        }

        resource.writeTo(out, start, contentLength);
    }
}
