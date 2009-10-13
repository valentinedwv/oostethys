package org.oostethys.smlmor.gwt.client.rpc.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class SystemValues implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private MetadataValues metadataValues;
	
	// choice: variables (ie, output) or components
	private List<AttrGroupValues> outputValuesList;
	private List<SystemValues> systemValuesList;
	

	public void setMetadataValues(MetadataValues metadataValues) {
		this.metadataValues = metadataValues;
	}

	public MetadataValues getMetadataValues() {
		return metadataValues;
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

	public void setOutputValuesList(List<AttrGroupValues> outputValuesList) {
		this.outputValuesList = outputValuesList;
	}

	public List<AttrGroupValues> getOutputValuesList() {
		if ( outputValuesList == null ) {
			outputValuesList = new ArrayList<AttrGroupValues>();
		}
		return outputValuesList;
	}	
}
