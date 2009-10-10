package org.oostethys.smlmor.gwt.client.rpc.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Carlos Rueda
 */
public class AttrGroupValues implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Map<String,String> values;
	
	
	public void setValues(Map<String,String> values) {
		this.values = values;
	}

	/** never null */
	public Map<String,String> getValues() {
		if ( values == null ) {
			values = new HashMap<String,String>();
		}
		return values;
	}

}
