package org.oostethys.smlmor.gwt.client.rpc.model;

import java.io.Serializable;

/**
 * Basic models used to create the complete model implemented in
 * this prototype. These basic models are various attri ute groups.
 * Note that this "complete" model is hard-coded, particularly by the
 * Controller while creating the interface.
 * 
 * @author Carlos Rueda
 */
public class BasicModels implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private AttrGroupModel serviceContact;
	
	private AttrGroupModel systemContact;
	
	private AttrGroupModel systemMetadata;
	
	private AttrGroupModel output;


	public void setServiceContact(AttrGroupModel serviceContact) {
		this.serviceContact = serviceContact;
	}

	public AttrGroupModel getServiceContact() {
		return serviceContact;
	}

	public void setSystemContact(AttrGroupModel systemContact) {
		this.systemContact = systemContact;
	}

	public AttrGroupModel getSystemContact() {
		return systemContact;
	}

	public void setSystemMetadata(AttrGroupModel systemMetadata) {
		this.systemMetadata = systemMetadata;
	}

	public AttrGroupModel getSystemMetadata() {
		return systemMetadata;
	}

	public void setOutput(AttrGroupModel output) {
		this.output = output;
	}

	public AttrGroupModel getOutput() {
		return output;
	}
	
	
}
