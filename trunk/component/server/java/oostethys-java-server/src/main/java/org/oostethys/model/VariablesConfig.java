package org.oostethys.model;

import org.apache.commons.lang.StringUtils;

import org.oostethys.model.impl.UnitsImpl;
import org.oostethys.model.impl.VariableQuantityImpl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * <p>
 * This class maps a variable and a units label to corresponding URIs.
 * It contains the configuration of the variables of an observation.
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
    List<VariableQuantity> variablesConfig = new ArrayList<VariableQuantity>();

    public Iterator<VariableQuantity> getIterator() {
        return variablesConfig.iterator();
    }

    public VariableQuantity getVariableByURI(final String uri) {
        for (final VariableQuantity var : variablesConfig) {
            if (StringUtils.equals(uri, var.getURI())) {
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
    public void addVariable(final String standardName) {
        VariableQuantity var = new VariableQuantityImpl();
        var.setLabel(standardName);
        addVariable(var);
    }

    public void addVariable(final String variableLabel,
        final String variableURI, final String unitsLabel, final String unitsURI) {
        this.addVariable(variableLabel, variableURI, unitsLabel, unitsURI, false);
    }

    public void addVariable(final String variableLabel,
        final String variableURI, final String unitsLabel,
        final String unitsURI, final boolean isCoordindate) {
        VariableQuantity var = new VariableQuantityImpl(variableURI);
        var.setLabel(variableLabel);

        Units units = new UnitsImpl();
        units.setLabel(unitsLabel);

        var.setUnits(units);
        var.setCoordinate(isCoordindate);
        addVariable(var);
    }

    public void addVariable(final VariableQuantity var) {
        if (!variablesConfig.contains(var)) {
            variablesConfig.add(var);
        }
    }

    public List<VariableQuantity> getVariables() {
        return variablesConfig;
    }

    public void setVariables(final List<VariableQuantity> vars) {
        variablesConfig = vars;
    }

    public String getLabel(final String uri) {
        for (final VariableQuantity var : variablesConfig) {
            if (StringUtils.equals(var.getURI(), uri)) {
                return var.getLabel();
            }
        }

        return null;
    }

    public VariableQuantity getVariableGivenURI(final String uri) {
        for (final VariableQuantity var : variablesConfig) {
            if (StringUtils.equals(var.getURI(), uri)) {
                return var;
            }
        }

        return null;
    }

    public List<VariableQuantity> getVariablesNotCoordinates() {
        List<VariableQuantity> temp =
            new ArrayList<VariableQuantity>(variablesConfig.size());

        for (final VariableQuantity var : variablesConfig) {
            if (!var.isCoordinate()) {
                temp.add(var);
            }
        }

        return temp;
    }

    //
    public List<VariableQuantity> getCoordinates() {
        List<VariableQuantity> temp =
            new ArrayList<VariableQuantity>(variablesConfig.size());

        for (final VariableQuantity var : variablesConfig) {
            if (var.isCoordinate()) {
                temp.add(var);
            }
        }

        return temp;
    }

    public boolean isContainedInCoordinates(final String varName,
        final String unitsName) {
        List<VariableQuantity> vars = getCoordinates();

        for (final VariableQuantity var : vars) {
            if (StringUtils.equals(var.getLabel(), varName) &&
                    (var.getUnits() != null) &&
                    StringUtils.equals(var.getUnits().getLabel(), unitsName)) {
                return true;
            }
        }

        return false;
    }

    public boolean isContainedInVariables(final String varName,
        final String unitsName) {
        VariableQuantity var = getVariable(varName, unitsName);

        return var != null;
    }

    public VariableQuantity getVariable(final String varName,
        final String unitsName) {
        for (final VariableQuantity var : variablesConfig) {
            if (StringUtils.equals(var.getLabel(), varName) &&
                    (var.getUnits() != null) &&
                    StringUtils.equals(var.getUnits().getLabel(), unitsName)) {
                return var;
            }
        }

        return null;
    }

    public VariableQuantity getVariable(final String varName) {
        for (final VariableQuantity var : variablesConfig) {
            if (StringUtils.equals(var.getLabel(), varName)) {
                return var;
            }
        }

        return null;
    }
}
