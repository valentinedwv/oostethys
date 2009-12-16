/**
 * 
 */
package org.oostethys.model.impl;

import org.oostethys.model.Units;
import org.oostethys.model.VariableQuantityWithSection;

import ucar.ma2.InvalidRangeException;
import ucar.ma2.Section;

/**
 * @author bermudez
 * 
 */
public class VariableQuantityWithSectionImpl extends VariableQuantityImpl
		implements VariableQuantityWithSection {

	private Section sec = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.oostethys.model.VariableQuantityWithSection#getSection()
	 */
	public Section getSection() {
		return sec;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.oostethys.model.VariableQuantityWithSection#setSection(ucar.ma2.Section
	 * )
	 */
	public void setSection(Section section) {
		this.sec = section;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.oostethys.model.VariableQuantityWithSection#setSection(java.lang.
	 * String)
	 */
	public void setSection(String opendapVariableRange) {
		// expected something like: zeta[0:72][26559]
		
		sec = new Section();
		String sel = opendapVariableRange;
		int ind = sel.indexOf("[");
		String subsel = sel;
		try {
			while (ind > -1) {
				subsel = subsel.substring(ind);
				int indexColon = subsel.indexOf(":");
				int indexofClosingParan = subsel.indexOf("]");
				int lower = -1;
				int upper = -1;
				if (indexColon > -1) {
					if (indexColon < indexofClosingParan) {
						lower = Integer.parseInt(subsel
								.substring(1, indexColon));
						upper = Integer.parseInt(subsel.substring(
								indexColon + 1, indexofClosingParan));
					}
				} else {
					lower = Integer.parseInt(subsel.substring(1,
							indexofClosingParan));
					upper = lower;
				}

				sec.appendRange(lower, upper);
				ind = subsel.indexOf("[", 1);

			}

		} catch (InvalidRangeException e1) {
			
			e1.printStackTrace();

			

		}

	}

}
