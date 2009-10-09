package org.oostethys.smlmor.gwt.server;

import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmisw.iserver.core.IServer;
import org.mmisw.iserver.core.Server;
import org.mmisw.iserver.gwt.client.rpc.AppInfo;
import org.oostethys.schemas.x010.oostethys.OostethysDocument;
import org.oostethys.smlmor.gwt.client.rpc.SmlMorService;
import org.oostethys.smlmor.gwt.server.test.OostToSos;

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
	
	// TODO use it ;)
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

	public String getSensorML(Map<String, String> values) {
		
		// very quick test based on OostToSos
		
		InputStream xslIS = Thread.currentThread().getContextClassLoader().getResourceAsStream("oostethys2describeSensor.xsl");
		
		
		OostToSos app = new OostToSos();

		// create an oostethys document
		OostethysDocument doc = app.getoostethysDocument();
		
		// create the stream to send the xml file
		Writer os = new StringWriter();
		
		// transform to SensorML
		app.getSensorML(doc, os, xslIS);
		
		return os.toString();
	}
	
}
