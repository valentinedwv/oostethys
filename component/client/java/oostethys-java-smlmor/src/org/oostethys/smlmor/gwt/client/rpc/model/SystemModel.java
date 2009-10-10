package org.oostethys.smlmor.gwt.client.rpc.model;

import java.io.Serializable;


public class SystemModel implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private MetadataModel metadataModel;
	
	

	public void setMetadataModel(MetadataModel metadata) {
		this.metadataModel = metadata;
	}

	public MetadataModel getMetadataModel() {
		return metadataModel;
	}
	
	
	
}
