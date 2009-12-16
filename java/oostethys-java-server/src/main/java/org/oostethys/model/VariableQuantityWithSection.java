package org.oostethys.model;

import ucar.ma2.Section;

/**
 * Contains operations to define and get sections. Sections are ucar.ma2.Section and could be used to append
 * ranges useful to read variable ranges from opendap queries.
 * 
 * 
 * 
 * @author bermudez
 * 
 */
public interface VariableQuantityWithSection extends VariableQuantity {

	/**
	 * Sets a ucar.ma2.Section to a variable
	 * @param section
	 */
	public void setSection(Section section);

	/**
	 * Gets the ucar.ma2.Section 
	 * @return
	 */
	public Section getSection();
	
	/**
	 * Sets  section from an opendap range. For example:
	 * zeta[0:72][26559]
	 * lon[26559]
	 * 
	 * In the first case adds 2 ranges. First one with 
	 * first =0 and last = 72. Second with first = 26559 and last = 26559.
	 * 
	 * @param opendapVariableRange it is string that contains letters 
	 * and set f ranges. Each range is enclosed by square brackets. Each 
	 * range is composed of a  number or a number, colon and a number. 
	 */
	public void setSection (String opendapVariableRange);
	

}
