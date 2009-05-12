package org.oostethys.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.oostethys.model.impl.UnitsImpl;
import org.oostethys.model.impl.VariableQuantityImpl;
import org.oostethys.voc.Voc;

import sun.nio.cs.ext.ISCII91;

/**
 * <p>
 * This class maps a variable and a units label to corresponding URIs.
 * </p>
 * <hr>
 * 
 * @author : $Author: luisbermudez $
 * @version : $Revision: 1.4 $
 * @since : Feb 21, 2007
 */

public class VariablesConfig {

	/**
	 * @link composition
	 */
	java.util.List<VariableQuantity> variablesConfig = null;

	public VariablesConfig() {
		super();
	}

	public Iterator<VariableQuantity> getIterator() {
		return variablesConfig.iterator();
	}

	public VariableQuantity getVariableByURI(String uri) {
		for (VariableQuantity var : variablesConfig) {
			if (var.getURI().equals(uri)) {
				return var;
			}
		}
		return null;

	}

	/**
	 * Adds a variable and only sets the label
	 * 
	 * @param standardName
	 */
	public void addVariable(String standardName) {

		VariableQuantity var = new VariableQuantityImpl();
		var.setLabel(standardName);
		addVariable(var);

	}

	public void addVariable(String variableLabel, String variableURI,
			String unitsLabel, String unitsURI) {

		VariableQuantity var = new VariableQuantityImpl(variableURI);
		var.setLabel(variableLabel);
		Units units = new UnitsImpl();
		units.setLabel(unitsLabel);

		var.setUnits(units);
		addVariable(var);

	}

	public void addVariable(String variableLabel, String variableURI,
			String unitsLabel, String unitsURI, boolean isCoordindate) {

		VariableQuantity var = new VariableQuantityImpl(variableURI);
		var.setLabel(variableLabel);
		Units units = new UnitsImpl();
		units.setLabel(unitsLabel);

		var.setUnits(units);
		var.setCoordinate(isCoordindate);
		addVariable(var);

	}

	public void addVariable(VariableQuantity var) {
		if (variablesConfig == null) {
			variablesConfig = new ArrayList<VariableQuantity>();
		}

		if (!variablesConfig.contains(var)) {
			variablesConfig.add(var);
		}
	}

	public List<VariableQuantity> getVariables() {
		return variablesConfig;
	}

	public void setVariables(List<VariableQuantity> vars) {
		variablesConfig = vars;
	}

	public String getLabel(String uri) {
		for (VariableQuantity var : variablesConfig) {
			if (var.getURI().equals(uri)) {
				return var.getLabel();
			}
		}

		return null;
	}

	public VariableQuantity getVariableGivenURI(String uri) {
		for (VariableQuantity var : variablesConfig) {
			if (var.getURI().equals(uri)) {
				return var;
			}
		}

		return null;
	}

	public List<VariableQuantity> getVariablesNotCoordinates() {
		List<VariableQuantity> temp = new ArrayList<VariableQuantity>(
				variablesConfig.size());
		for (VariableQuantity var : variablesConfig) {

			if (!var.isCoordinate()) {
				temp.add(var);
			}

		}
		return temp;
	}

	//

	public List<VariableQuantity> getCoordinates() {
		List<VariableQuantity> temp = new ArrayList<VariableQuantity>(
				variablesConfig.size());
		for (VariableQuantity var : variablesConfig) {

			if (var.isCoordinate()) {
				temp.add((VariableQuantity) var);
			}
		}
		return temp;
	}

	public boolean isContainedInCoordinates(String varName, String unitsName) {
		List<VariableQuantity> vars = getCoordinates();
		for (VariableQuantity var : vars) {
			if (var.getLabel().equals(varName)
					&& var.getUnits().getLabel().equals(unitsName)) {
				return true;
			}
		}
		return false;
	}

	public boolean isContainedInVariables(String varName, String unitsName) {
		VariableQuantity var = getVariable(varName, unitsName);
		return var != null;
	}

	public VariableQuantity getVariable(String varName, String unitsName) {

		for (VariableQuantity var : variablesConfig) {
			if (var.getLabel().equals(varName)
					&& var.getUnits().getLabel().equals(unitsName)) {
				return var;
			}
		}
		return null;
	}

	public VariableQuantity getVariable(String varName) {

		for (VariableQuantity var : variablesConfig) {
			if (var.getLabel().equals(varName)) {
				return var;
			}
		}
		return null;
	}

	
	

}