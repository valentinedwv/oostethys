package org.oostethys.model.impl;

import org.oostethys.model.Units;
import org.oostethys.model.VariableQuantity;
import org.oostethys.model.VariablesConfig;

public class SimpleVariablesConfig extends VariablesConfig {
	
	private VariableQuantity timeVariable ;
	private VariableQuantity latitudeVariable ;
	private VariableQuantity longitudeVariable ;
	
	public SimpleVariablesConfig() {
		super();
	}
	
	public void addTimeVariable(String variableLabel, String variableURI,
			String unitsLabel, String unitsURI){
		VariableQuantity var = new VariableQuantityImpl(variableURI);
		var.setLabel(variableLabel);
		Units units = new UnitsImpl();
		units.setLabel(unitsLabel);
		var.setUnits(units);
		addVariable(var);
		timeVariable = var;
	}
	
	public void addLatitudeVariable(String variableLabel, String variableURI,
			String unitsLabel, String unitsURI){
		VariableQuantity var = new VariableQuantityImpl(variableURI);
		var.setLabel(variableLabel);
		Units units = new UnitsImpl();
		units.setLabel(unitsLabel);
		var.setUnits(units);
		addVariable(var);
		latitudeVariable = var;
	}
	
	public void addLongitudeVariable(String variableLabel, String variableURI,
			String unitsLabel, String unitsURI){
		VariableQuantity var = new VariableQuantityImpl(variableURI);
		var.setLabel(variableLabel);
		Units units = new UnitsImpl();
		units.setLabel(unitsLabel);
		var.setUnits(units);
		addVariable(var);
		longitudeVariable = var;
	}

	/**
	 * @return the timeVariable
	 */
	public VariableQuantity getTimeVariable() {
		return timeVariable;
	}

	/**
	 * @param timeVariable the timeVariable to set
	 */
	public void setTimeVariable(VariableQuantity timeVariable) {
		this.timeVariable = timeVariable;
	}

	/**
	 * @return the latitudeVariable
	 */
	public VariableQuantity getLatitudeVariable() {
		return latitudeVariable;
	}

	/**
	 * @param latitudeVariable the latitudeVariable to set
	 */
	public void setLatitudeVariable(VariableQuantity latitudeVariable) {
		this.latitudeVariable = latitudeVariable;
	}

	/**
	 * @return the longitudeVariable
	 */
	public VariableQuantity getLongitudeVariable() {
		return longitudeVariable;
	}

	/**
	 * @param longitudeVariable the longitudeVariable to set
	 */
	public void setLongitudeVariable(VariableQuantity longitudeVariable) {
		this.longitudeVariable = longitudeVariable;
	}
	

}
