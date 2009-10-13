package org.oostethys.smlmor.gwt.client.rpc;

import org.mmisw.iserver.gwt.client.rpc.AppInfo;
import org.oostethys.smlmor.gwt.client.rpc.model.BasicModels;
import org.oostethys.smlmor.gwt.client.rpc.model.OostethysValues;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * Interface with the server.
 * 
 * @author Carlos Rueda
 * @version $Id$
 */
public interface SmlMorService extends RemoteService {
	
	/**
	 * Gets basic application info.
	 */
	AppInfo getAppInfo();
	
	BasicModels getModels();
	
	String getSensorML(OostethysValues soostValues);
	
}
