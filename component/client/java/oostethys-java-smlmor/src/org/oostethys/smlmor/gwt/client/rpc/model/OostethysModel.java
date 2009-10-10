package org.oostethys.smlmor.gwt.client.rpc.model;

import java.io.Serializable;

/**
 * The entry point of the model.
 * @author Carlos Rueda
 */
public class OostethysModel implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private AttributeModel webServerUrl = new AttributeModel("webServiceURL", "Web service URL", "UUUUU");
	
	private AttrGroupModel serviceContact;
	
	private SystemModel systemModel;


	
	public AttributeModel getWebServerUrlAttribute() {
		return webServerUrl;
	}
	

	public void setServiceContact(AttrGroupModel serviceContact) {
		this.serviceContact = serviceContact;
	}

	public AttrGroupModel getServiceContact() {
		if ( serviceContact == null ) {
			serviceContact = new AttrGroupModel();
		}
		return serviceContact;
	}


	public void setSystemModel(SystemModel systemModel) {
		this.systemModel = systemModel;
	}


	public SystemModel getSystemModel() {
		return systemModel;
	}


	
}
