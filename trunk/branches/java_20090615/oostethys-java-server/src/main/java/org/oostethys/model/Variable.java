
package org.oostethys.model;

/**
 *<p> Interface for Variable</p><hr>
 *A variable is a Resource that has a unit and has a coordinate flag.
 * @author  : $Author: luisbermudez $
 * @version : $Revision: 1.3 $
 * @since   : Feb 21, 2007
*/

public interface Variable extends Resource{

	/**
	 * @link composition
	 */
	public String getStandardName();
	public void setStandardName(String name);
	public boolean isCoordinate();
	public void setCoordinate (boolean isCoordinate);


}