package org.oostethys.netcdf.util;

import java.util.regex.Pattern;

import org.mmi.util.StringManipulationUtil;
import org.oostethys.voc.Voc;

public class VariableMapper {

	/**
	 * Returns a URI based on regex mapping rules for a label
	 * 
	 * @param label
	 * @return
	 */

	private static String[] regex = { ".*lat.*",".*lon.*", ".*time.*", ".*depth.*" };
	private static String[] uris = { Voc.latitude,   Voc.longitude,  Voc.time,  Voc.depth };
	
	private static String baseMMI = "urn:mmisw.org:tmp";

	public static   String getBestURI(String label) {
		for (int i = 0; i < regex.length; i++) {
			String reg = regex[i];
			Pattern pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
			if (pattern.matcher(label).find()) {
				return uris[i];
			}

		}
		return null;

	}
	
	public static String getMMIURN(String shortNameOrganization, String label){
		return baseMMI+":"+shortNameOrganization+":"+StringManipulationUtil.transformValidQName(label);
		
	}
	
	public static String getMMIURIforCFParam(String standardName){
		return Voc.CF+standardName;
		
	}

}
