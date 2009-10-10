package org.oostethys.smlmor.gwt.client.rpc.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for groups of attributes.
 * @author Carlos Rueda
 */
public class AttrGroupModel implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String htmlInfo;
	
	private List<AttributeModel> attributes;
	
	public AttrGroupModel() {}
	
	
	public AttrGroupModel(String name, String htmlInfo) {
		super();
		this.name = name;
		this.htmlInfo = htmlInfo;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setHtmlInfo(String htmlInfo) {
		this.htmlInfo = htmlInfo;
	}

	public String getHtmlInfo() {
		return htmlInfo;
	}

	public void addAttributes(AttributeModel... attrs) {
		if ( attributes == null ) {
			attributes = new ArrayList<AttributeModel>();
		}
		for (AttributeModel attr : attrs) {
			attributes.add(attr);
		}
	}

	/** never null */
	public List<AttributeModel> getAttributes() {
		if ( attributes == null ) {
			attributes = new ArrayList<AttributeModel>();
		}
		return attributes;
	}

}
