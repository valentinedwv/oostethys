package org.oostethys.test;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import junit.framework.TestCase;


public class Issue1_TestLocaleNumberFormat extends TestCase {
	
	
	
	// issue 1 : http://code.google.com/p/oostethys/issues/detail?id=1
		
		
	public void testNumberFormatLocale_error(){
		// this shows the error
		String valS = "36,23";
		try{
		double val = Double.parseDouble(valS);
		NumberFormat myFormatter = NumberFormat.getInstance(Locale.ENGLISH);
		myFormatter.setMaximumFractionDigits(6);
		String output = myFormatter.format(val);
		
		}catch(	java.lang.NumberFormatException e){
			assertTrue(	"java.lang.NumberFormatException catched",1==1);
		}
	}
	
	


	
	public void testNumberFormatLocale_fix(){
		// ? not sure how to fix it
		String valS = "36,23";
		Locale locale= new Locale("de_DE");
		Locale.setDefault(locale);
		
		NumberFormat nf = NumberFormat.getNumberInstance(locale);
		DecimalFormat df = (DecimalFormat)nf;
		String pattern = "###,###.###";
		df.applyPattern(pattern);
	
			double val = Double.parseDouble(valS);
			StringBuffer myBuffy = new StringBuffer();
			NumberFormat myFormatter = NumberFormat.getInstance(locale);
			
			myFormatter.setMaximumFractionDigits(6);
			String output = myFormatter.format(val);
			System.out.println(output);
		
		
	}
	

}
