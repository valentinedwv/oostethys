package org.oostethys.smlmor.gwt.client.rpc;

import java.util.Map;

import org.mmisw.iserver.gwt.client.rpc.AppInfo;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * Interface to get info from the server.
 * 
 * @author Carlos Rueda
 * @version $Id$
 */
public interface SmlMorService extends RemoteService {
	
	/**
	 * Gets basic application info.
	 */
	AppInfo getAppInfo();
	
	
	// TODO actual operations ...
	
	String getSensorML(Map<String,String> values);

}
