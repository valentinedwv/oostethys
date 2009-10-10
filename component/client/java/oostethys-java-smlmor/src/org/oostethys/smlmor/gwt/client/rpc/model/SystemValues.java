package org.oostethys.smlmor.gwt.client.rpc.model;

import java.io.Serializable;


public class SystemValues implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private MetadataValues metadataValues;

	public void setMetadataValues(MetadataValues metadataValues) {
		this.metadataValues = metadataValues;
	}

	public MetadataValues getMetadataValues() {
		return metadataValues;
	}

	
	
}
