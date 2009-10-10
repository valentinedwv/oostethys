package org.oostethys.smlmor.gwt.client.rpc.model;

import java.io.Serializable;


public class MetadataModel implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private AttrGroupModel systemContact;
	
	private AttrGroupModel systemMetadata;
	
	

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

	
}
