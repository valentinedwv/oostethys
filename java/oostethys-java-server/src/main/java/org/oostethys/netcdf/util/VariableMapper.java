package org.oostethys.netcdf.util;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

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
	    private static String clean(String s)
	    {
	        String rep = s;
	        Pattern p_ = Pattern.compile("_{2,}");
	        Matcher m_ = p_.matcher(rep);
	        rep = m_.replaceAll("_");
	        return rep;
	    }

	    private static String patterns[] = {
	        "[^a-zA-Z0-9-_]+", "(_+)$"
	    };
	    private static String replace[] = {
	        "_", ""
	    };

	
	    public static String transformValidQName(String s)
	    {
	        String rep = s;
	        for(int i = 0; i < patterns.length; i++)
	        {
	            Pattern p = Pattern.compile(patterns[i]);
	            Matcher m = p.matcher(rep);
	            rep = m.replaceAll(replace[i]);
	        }

	        return clean(appendUnderScoreStart(rep));
	    }

	    private static String appendUnderScoreStart(String s)
	    {
	        String rep = s;
	        Pattern p_ = Pattern.compile("[^a-zA-Z_]");
	        Matcher m_ = p_.matcher(rep);
	        if(m_.lookingAt())
	            rep = (new StringBuilder("_")).append(rep).toString();
	        return rep;
	    }
	
	public static String getMMIURN(String shortNameOrganization, String label){
		return baseMMI+":"+shortNameOrganization+":"+transformValidQName(label);
		
	}
	
	public static String getMMIURIforCFParam(String standardName){
		return Voc.CF+standardName;
		
	}

}
