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
import org.mortbay.io.WriterOutputStream;
import org.mortbay.jetty.HttpConnection;
import org.mortbay.jetty.HttpFields;
import org.mortbay.jetty.HttpHeaders;
import org.mortbay.jetty.HttpMethods;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.Response;
import org.mortbay.jetty.handler.AbstractHandler;
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.jetty.handler.ContextHandler.SContext;
import org.mortbay.log.Log;
import org.mortbay.resource.Resource;
import org.mortbay.util.TypeUtil;
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
    ContextHandler _context;

    Resource _baseResource;

    /* ------------------------------------------------------------ */
    public void doStart() throws Exception {
	SContext scontext = ContextHandler.getCurrentContext();
	_context = (scontext == null ? null : scontext.getContextHandler());

	super.doStart();
    }

    /* ------------------------------------------------------------ */
    /**
     * @return Returns the resourceBase.
     */
    public Resource getBaseResource() {
	if (_baseResource == null)
	    return null;
	return _baseResource;
    }

    /* ------------------------------------------------------------ */
    /**
     * @return Returns the base resource as a string.
     */
    public String getResourceBase() {
	if (_baseResource == null)
	    return null;
	return _baseResource.toString();
    }

    /* ------------------------------------------------------------ */
    /**
     * @param base
     *            The resourceBase to set.
     */
    public void setBaseResource(Resource base) {
	_baseResource = base;
    }

    /* ------------------------------------------------------------ */
    /**
     * @param resourceBase
     *            The base resource as a string.
     */
    public void setResourceBase(String resourceBase) {
	try {
	    setBaseResource(Resource.newResource(resourceBase));
	} catch (Exception e) {
	    Log.warn(e);
	    throw new IllegalArgumentException(resourceBase);
	}
    }

  

    /* ------------------------------------------------------------ */
    /* 
      */
    public Resource getResource(String path) throws MalformedURLException {
	
	System.out.println("getResource "+path);
	
	if (path == null || !path.startsWith("/"))
	    throw new MalformedURLException(path);

	Resource base = _baseResource;
	if (base == null) {
	    if (_context == null)
		return null;
	    base = _context.getBaseResource();
	    if (base == null)
		return null;
	}

	try {
	    path = URIUtil.canonicalPath(path);
	    Resource resource = base.addPath(path);
	    return resource;
	} catch (Exception e) {
	    Log.ignore(e);
	}

	return null;
    }

    /* ------------------------------------------------------------ */
    protected Resource getResource(HttpServletRequest request)
	    throws MalformedURLException {
	String path_info = request.getPathInfo();
	if (path_info == null)
	    return null;
	
	if( StringUtils.isEmpty(request.getQueryString())) {
	    // no query string
	    return getResource(path_info);
	} 
	
	return getResource(path_info+'?'+request.getQueryString());

    }


    /* ------------------------------------------------------------ */
    /*
     * @see
     * org.mortbay.jetty.Handler#handle(javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse, int)
     */
    public void handle(String target, HttpServletRequest request,
	    HttpServletResponse response, int dispatch) throws IOException,
	    ServletException {
	Request base_request = request instanceof Request ? (Request) request
		: HttpConnection.getCurrentConnection().getRequest();
	if (base_request.isHandled())
	    return;

	boolean skipContentBody = false;
	if (!HttpMethods.GET.equals(request.getMethod())) {
	    if (!HttpMethods.HEAD.equals(request.getMethod()))
		return;
	    skipContentBody = true;
	}

	Resource resource = getResource(request);

	if (resource == null || !resource.exists())
	    return;


	// We are going to server something
	base_request.setHandled(true);

	if (resource.isDirectory()) {
	    if (!request.getPathInfo().endsWith(URIUtil.SLASH)) {
		response.sendRedirect(URIUtil.addPaths(request.getRequestURI(),
			URIUtil.SLASH));
		return;
	    }
	 

	    if (resource == null || !resource.exists()
		    || resource.isDirectory()) {
		response.sendError(HttpServletResponse.SC_FORBIDDEN);
		return;
	    }
	}

	// set some headers
	long last_modified = resource.lastModified();
	if (last_modified > 0) {
	    long if_modified = request
		    .getDateHeader(HttpHeaders.IF_MODIFIED_SINCE);
	    if (if_modified > 0 && last_modified / 1000 <= if_modified / 1000) {
		response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
		return;
	    }
	}


	// set the headers
	doResponseHeaders(response, resource, null);
	response.setDateHeader(HttpHeaders.LAST_MODIFIED, last_modified);
	if (skipContentBody)
	    return;
	// Send the content
	OutputStream out = null;
	try {
	    out = response.getOutputStream();
	} catch (IllegalStateException e) {
	    out = new WriterOutputStream(response.getWriter());
	}

	// See if a short direct method can be used?
	if (out instanceof HttpConnection.Output) {
	    ((HttpConnection.Output) out)
		    .sendContent(resource.getInputStream());
	} else {
	    // Write content normally
	    resource.writeTo(out, 0, resource.length());
	}
    }

    /* ------------------------------------------------------------ */
    /**
     * Set the response headers. This method is called to set the response
     * headers such as content type and content length. May be extended to add
     * additional headers.
     * 
     * @param response
     * @param resource
     * @param mimeType
     */
    protected void doResponseHeaders(HttpServletResponse response,
	    Resource resource, String mimeType) {
	if (mimeType != null)
	    response.setContentType(mimeType);

	long length = resource.length();

	if (response instanceof Response) {
	    HttpFields fields = ((Response) response).getHttpFields();

	    if (length > 0)
		fields.putLongField(HttpHeaders.CONTENT_LENGTH_BUFFER, length);
	} else {
	    if (length > 0)
		response.setHeader(HttpHeaders.CONTENT_LENGTH, TypeUtil
			.toString(length));
	}

    }
}
