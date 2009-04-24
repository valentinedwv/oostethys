/*****************************************************************************
 * Source code information
 * -----------------------
 * Original author    Luis Bermudez, MBARI/MMI
 * Author email       bermudez@mbari.org
 * Package            org.oostethys.data
 * Web                http://marinemetadata.org/mmitools
 * Created            Nov 30, 2006
 * Filename           $RCSfile: VariableQuantityImpl.java,v $
 * Revision           $Revision: 1.1 $
 *
 * Last modified on   $Date: 2008/06/19 19:47:37 $
 *               by   $Author: luisbermudez $
 *
 * (c) Copyright 2005, 2006 Monterey Bay Aquarium Research Institute
 * Marine Metadata Interoperability (MMI) Project http://marinemetadata.org
 *
 * License Information
 * ------------------------
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, you can download it from 
 *  http://marinementadata.org/gpl or write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *********************************************************************************/
package org.oostethys.model.impl;

import org.oostethys.model.Units;
import org.oostethys.model.VariableQuantity;
import org.oostethys.voc.Voc;

/**
 *<p> InsertDescription </p><hr>
 * @author  : $Author: luisbermudez $
 * @version : $Revision: 1.1 $
 * @since   : Nov 30, 2006
 */

public class VariableQuantityImpl extends ResourceImpl implements VariableQuantity {

	
	
	private Units units;
	private boolean isCoordinate;
	private String referenceFrame;
	
	
	

	public VariableQuantityImpl(String variableLabel, String labelURI, String unitsLabel, String unitsURI, boolean isCoordiante) {
		super(labelURI);
		setLabel(variableLabel);
		Units units = new UnitsImpl();
		units.setLabel(unitsLabel);
		setCoordinate(isCoordiante);
		setUnits(units);
	
		
	}
	
	public VariableQuantityImpl(String URI) {
		super(URI);
		// TODO Auto-generated constructor stub
	}

	public VariableQuantityImpl() {
		super();
		
	}

	public Units getUnits() {
		// TODO Auto-generated method stub
		return units;
	}

	public void setUnits(Units units) {
		this.units = units;
		
	}

	public boolean isCoordinate() {
		return isCoordinate;
	}

	public void setCoordinate(boolean isCoordinate) {
		this.isCoordinate = isCoordinate;
		
		
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return getLabel();
	}

	public String getStandardName() {
		
		return getLabel();
	}

	public void setStandardName(String name) {
		setLabel(name);
		
	}

	public String getReferenceFrame() {
		
		return referenceFrame;
	}

	public boolean isTime() {
		if (getURI().equalsIgnoreCase(Voc.time)){
			return true;
		}else{
			return false;
		}
	}

	public void setReferenceFrame(String referenceFrame) {
		this.referenceFrame = referenceFrame;
		
	}



	
	
	

}
