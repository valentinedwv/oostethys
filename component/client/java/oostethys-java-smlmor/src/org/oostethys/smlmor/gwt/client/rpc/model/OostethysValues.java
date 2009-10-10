package org.oostethys.smlmor.gwt.client.rpc.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The entry point of the model.
 * @author Carlos Rueda
 */
public class OostethysValues implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String webServerUrl;
	
	private AttrGroupValues serviceContactValues;
	
	private List<SystemValues> systemValuesList;

	public void setWebServerUrl(String webServerUrl) {
		this.webServerUrl = webServerUrl;
	}

	public String getWebServerUrl() {
		return webServerUrl;
	}


	public void setServiceContactValues(AttrGroupValues serviceContactValues) {
		this.serviceContactValues = serviceContactValues;
	}

	public AttrGroupValues getServiceContactValues() {
		return serviceContactValues;
	}

	public void setSystemValuesList(List<SystemValues> systemValuesList) {
		this.systemValuesList = systemValuesList;
	}

	public List<SystemValues> getSystemValuesList() {
		if ( systemValuesList == null ) {
			systemValuesList = new ArrayList<SystemValues>();
		}
		return systemValuesList;
	}


	
}
