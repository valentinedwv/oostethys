package org.oostethys.smlmor.gwt.client.rpc.model;

import java.io.Serializable;


public class MetadataValues implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private AttrGroupValues systemContactValues;
	
	private AttrGroupValues systemMetadataValues;

	public void setSystemContactValues(AttrGroupValues systemContactValues) {
		this.systemContactValues = systemContactValues;
	}

	public AttrGroupValues getSystemContactValues() {
		return systemContactValues;
	}

	public void setSystemMetadataValues(AttrGroupValues systemMetadataValues) {
		this.systemMetadataValues = systemMetadataValues;
	}

	public AttrGroupValues getSystemMetadataValues() {
		return systemMetadataValues;
	}


	
}
