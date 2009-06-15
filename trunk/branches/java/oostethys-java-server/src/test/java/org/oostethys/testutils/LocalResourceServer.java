/**
 * 
 */
package org.oostethys.testutils;

import java.io.File;

import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.handler.HandlerList;

/**
 * @author Jesper Zedlitz &lt;jze@informatik.uni-kiel.de&gt;
 * 
 */
public class LocalResourceServer {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger
	    .getLogger(LocalResourceServer.class.getName());

    private static final int PORT = 21983;

    private Server server = new Server(PORT);

    public void startServer() throws Exception {
	File fileInResourceDir = new File(getClass().getResource(
		"/oostethys.xml").getFile());
	File resourceDir = fileInResourceDir.getParentFile();

	log.debug("Using " + resourceDir.getAbsolutePath()
		+ " as resource base.");

	ResourceHandlerWithQuery resource_handler = new ResourceHandlerWithQuery();
	resource_handler.setResourceBase(resourceDir.getAbsolutePath());

	HandlerList handlers = new HandlerList();
	handlers.setHandlers(new Handler[] { resource_handler,
		new DefaultHandler() });
	server.setHandler(handlers);

	server.start();
    }
    
    public void stopServer() throws Exception {
	this.server.stop();
    }

}
