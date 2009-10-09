package org.oostethys.smlmor.gwt.client.rpc;

import java.util.Map;

import org.mmisw.iserver.gwt.client.rpc.AppInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Async interface for {@link SmlMorService}.
 * 
 * @author Carlos Rueda
 * @version $Id$
 */
public interface SmlMorServiceAsync {

	void getAppInfo(AsyncCallback<AppInfo> callback);
	

	void getSensorML(Map<String,String> values, AsyncCallback<String> callback);
}
