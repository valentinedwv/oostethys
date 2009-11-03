package org.oostethys.smlmor.gwt.client.rpc.model;

import java.io.Serializable;

/**
 * 
 * @author Carlos Rueda
 */
public class AttributeModel implements Serializable {
	private static final long serialVersionUID = 1L;

	private String beanAttributeName;
	private String label;
	
	private String defaultValue;
	
	/** Used to retrieve optionsfor this attribute */
	private String optionsVocabulary;
	
	
	public AttributeModel() {}
	
	public AttributeModel(String beanAttributeName, String label) {
		this(beanAttributeName, label, null);
	}
	
	public AttributeModel(String beanAttributeName, String label, String defaultValue) {
		super();
		this.beanAttributeName = beanAttributeName;
		this.label = label;
		this.defaultValue = defaultValue;
	}
	
	public void setBeanAttributeName(String beanAttributeName) {
		this.beanAttributeName = beanAttributeName;
	}
	public String getBeanAttributeName() {
		return beanAttributeName;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getLabel() {
		return label;
	}


	public String getTooltip() {
		// TODO Auto-generated method stub
		return null;
	}


	public int getNumberOfLines() {
		return 1;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setOptionsVocabulary(String optionsVocabulary) {
		this.optionsVocabulary = optionsVocabulary;
	}

	public String getOptionsVocabulary() {
		return optionsVocabulary;
	}

}
