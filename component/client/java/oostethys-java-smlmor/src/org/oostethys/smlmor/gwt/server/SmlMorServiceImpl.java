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
import org.oostethys.smlmor.gwt.client.rpc.model.AttrGroupModel;
import org.oostethys.smlmor.gwt.client.rpc.model.AttributeModel;
import org.oostethys.smlmor.gwt.client.rpc.model.MetadataModel;
import org.oostethys.smlmor.gwt.client.rpc.model.OostethysModel;
import org.oostethys.smlmor.gwt.client.rpc.model.OostethysValues;
import org.oostethys.smlmor.gwt.client.rpc.model.SystemModel;

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
	
	
	private OostethysModel oostethysModel;
	
	
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

	
	public OostethysModel getOostethysModel() {
		if ( oostethysModel == null ) {
			createOostethysModel();
		}
		return oostethysModel;
	}
	
	
	private void createOostethysModel() {
		
		oostethysModel = new OostethysModel();
		
		oostethysModel.setServiceContact(createContact("Service contact", "Service contact"));
		
		SystemModel systemModel = new SystemModel();
		MetadataModel metadata = new MetadataModel();
		metadata.setSystemContact(createContact("System contact", "System contact"));
		metadata.setSystemMetadata(createSystemMetadata("Metadata", "system md"));
		systemModel.setMetadataModel(metadata);
		oostethysModel.setSystemModel(systemModel);
	}
	
	
	private AttrGroupModel createContact(String name, String htmlInfo) {
		AttrGroupModel contact = new AttrGroupModel(name, htmlInfo);
		contact.addAttributes(
				new AttributeModel("urlOrganization", "Organization URL", "ooooooooooo"),
				new AttributeModel("longNameOrganization", "Organization long name", "llllllllllllll"),
				new AttributeModel("shortNameOrganization", "Organization short name", "ssssssssssss"),
				new AttributeModel("individualName", "Individual name", "nnnnnnnnnnnnn"),
				new AttributeModel("individualEmail", "Individual email", "eeeeeeeeee")
		);
		return contact;
	}

	private AttrGroupModel createSystemMetadata(String name, String htmlInfo) {
		AttrGroupModel contact = new AttrGroupModel(name, htmlInfo);
		contact.addAttributes(
				new AttributeModel("systemType", "type", "ooooooooooo"),
				new AttributeModel("systemShortName", "Short name", "llllllllllllll"),
				new AttributeModel("systemLongName", "Long name", "ssssssssssss"),
				new AttributeModel("systemIdentifier", "Identifier", "nnnnnnnnnnnnn")
		);
		return contact;
	}

	
	public String getSensorML(OostethysValues soostValues) {
		OostethysDocument doc = SOostethys2Doc.getoostethysDocument(
				oostethysModel, soostValues
		);
		Writer os = new StringWriter();
		try {
			Doc2Sml.getSensorML(doc, os);
		}
		catch (Exception e) {
			return "Error: " +e.getMessage();
		}
		
		return os.toString();
	}

	
}
