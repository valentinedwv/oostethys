package org.oostethys.smlmor.gwt.server;

import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmisw.iserver.core.IServer;
import org.mmisw.iserver.core.Server;
import org.mmisw.iserver.gwt.client.rpc.AppInfo;
import org.oostethys.smlmor.gwt.client.rpc.SmlMorService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;



/**
 * Implementation of the service. 
 * 
 * @author Carlos Rueda
 * @version $Id$
 */
public class SmlMorServiceImpl extends RemoteServiceServlet implements SmlMorService {
	private static final long serialVersionUID = 1L;
	
	
	private final AppInfo appInfo = new AppInfo("smlmor");
	private final Log log = LogFactory.getLog(SmlMorServiceImpl.class);
	
	
	private IServer iserver;
	
	
	public void init() throws ServletException {
		super.init();
		log.info("initializing " +appInfo.getAppName()+ "...");
		try {
			Config.getInstance().init(getServletConfig(), log);
			
			appInfo.setVersion(
					Config.Prop.VERSION.getValue()+ " (" +
						Config.Prop.BUILD.getValue()  + ")"
			);
					
			log.info(appInfo.toString());
			
			// portal initialization
			String ontServiceUrl = Config.Prop.ONT_SERVICE_URL.getValue();
			String bioportalRestUrl = Config.Prop.BIOPORTAL_REST_URL.getValue();
			iserver = Server.getInstance(ontServiceUrl, bioportalRestUrl);
			
		}
		catch (Throwable ex) {
			log.error("Cannot initialize: " +ex.getMessage(), ex);
			// TODO throw ServletException
			// NOTE: apparently this happens because getServletConfig fails in hosted mode (GWT 1.5.2). 
			// Normally we should throw a Servlet exception as the following: 
//			throw new ServletException("Cannot initialize", ex);
			// but, I'm ignoring it as this is currently only for version information.
		}
	}
	
	public void destroy() {
		super.destroy();
		log.info(appInfo+ ": destroy called.\n\n");
	}
	
	public AppInfo getAppInfo() {
		return appInfo;
	}
	
}
