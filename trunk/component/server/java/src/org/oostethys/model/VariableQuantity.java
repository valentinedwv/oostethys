
package org.oostethys.model;

/**
 *<p> Interface for Variable</p><hr>
 *A variable is a Resource that has a unit and has a coordinate flag.
 * @author  : $Author: luisbermudez $
 * @version : $Revision: 1.1 $
 * @since   : Feb 21, 2007
*/

public interface VariableQuantity extends Variable{

	/**
	 * @link composition
	 */
	org.oostethys.model.Units units = null;

	public Units getUnits();

	public void setUnits(Units units);

	public String getReferenceFrame();
	
	public void setReferenceFrame(String referenceFrame);
	
	public boolean isTime();
	
	


}