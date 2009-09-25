package org.oostethys.smlmor.gwt.client.rpc;

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
	

}
