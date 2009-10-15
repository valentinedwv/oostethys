package org.oostethys.smlmor.gwt.client.rpc;

import org.mmisw.iserver.gwt.client.rpc.AppInfo;
import org.oostethys.smlmor.gwt.client.rpc.model.BasicModels;
import org.oostethys.smlmor.gwt.client.rpc.model.OostethysValues;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Async interface for {@link SmlMorService}.
 * 
 * @author Carlos Rueda
 * @version $Id$
 */
public interface SmlMorServiceAsync {

	void getAppInfo(AsyncCallback<AppInfo> callback);
	
	
	void getModels(AsyncCallback<BasicModels> callback);
	
	void getSensorML(OostethysValues soostValues, AsyncCallback<SmlResult> callback);
	
}
