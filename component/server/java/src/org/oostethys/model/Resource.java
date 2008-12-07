package org.oostethys.model;

/**
 *<p> A Resource is a simple URI. superclass of all objects. </p><hr>
 * @author  : $Author: luisbermudez $
 * @version : $Revision: 1.3 $
 * @since   : Feb 22, 2007
*/

public interface Resource {

	public String getURI();

	public void setURI(String uri);

	public String getFragment();
	
	public String getLabel();
	public void setLabel(String label);
	public String getNamespace();
	


}