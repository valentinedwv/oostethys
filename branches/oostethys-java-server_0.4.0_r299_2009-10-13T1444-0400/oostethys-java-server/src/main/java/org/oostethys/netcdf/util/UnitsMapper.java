package org.oostethys.netcdf.util;

public class UnitsMapper {
	private static String[][] CFtoUCUM = { { "degrees_north", "deg" },
			{ "degrees_east", "deg" }, { "meters", "m" },{ "m", "m" }, { "meter", "m" } ,{ "deg C", "C" },{ "decibars", "dbar" },{"1","1"} ,{"S/m","S/m"} };

	/**
	 * return a mapping the UCUM units. If it the udunits is not found, it will return the udunits
	 * @param UDUnit
	 * @return
	 */
	public static String getUCUM(String UDUnit) {
		for (int i = 0; i < CFtoUCUM.length; i++) {
			String[] map = CFtoUCUM[i];
			if (map[0].equals(UDUnit)) {
				return map[1];
			}

		}
		return UDUnit;
	}

}
