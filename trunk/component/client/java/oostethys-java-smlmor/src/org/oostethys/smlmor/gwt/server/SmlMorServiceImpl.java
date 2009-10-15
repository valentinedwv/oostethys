package org.oostethys.smlmor.gwt.server;

import java.io.StringWriter;
import java.io.Writer;

import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmisw.iserver.core.IServer;
import org.mmisw.iserver.core.Server;
import org.mmisw.iserver.gwt.client.rpc.AppInfo;
import org.oostethys.schemas.x010.oostethys.OostethysDocument;
import org.oostethys.smlmor.gwt.client.rpc.SmlMorService;
import org.oostethys.smlmor.gwt.client.rpc.SmlResult;
import org.oostethys.smlmor.gwt.client.rpc.model.BasicModels;
import org.oostethys.smlmor.gwt.client.rpc.model.OostethysValues;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;



/**
 * Implementation of the service. 
 * 
 * @author Carlos Rueda
 * @version $Id$
 */
public class SmlMorServiceImpl extends RemoteServiceServlet implements SmlMorService {
	private static final long serialVersionUID = 1L;
	
	private final Log log = LogFactory.getLog(SmlMorServiceImpl.class);
	
	private final AppInfo appInfo = new AppInfo("smlmor");
	
	// TODO use it ;)
	private IServer iserver;
	
	
	private BasicModels basicModels;
	

	
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

	
	
	public BasicModels getModels() {
		if ( basicModels == null ) {
			basicModels = ModelsCreator.createModels();
		}
		return basicModels;
	}


	
	public SmlResult getSensorML(OostethysValues soostValues) {
		if ( basicModels == null ) {
			throw new IllegalStateException("getModels must be called first");
		}
		
		SmlResult smlResult = new SmlResult();
		
		SOostethys2Doc soost2doc = new SOostethys2Doc(basicModels, soostValues);
		OostethysDocument doc = soost2doc.getOostethysDocument();
		smlResult.setDump(soost2doc.getDump());
		
		Writer os = new StringWriter();
		try {
			Doc2Sml.getSensorML(doc, os);
			smlResult.setSml(os.toString());
		}
		catch (Exception e) {
			String error = "Error: " +e.getMessage();
			smlResult.setError(error);
		}
		
		return smlResult;
	}

	
}
