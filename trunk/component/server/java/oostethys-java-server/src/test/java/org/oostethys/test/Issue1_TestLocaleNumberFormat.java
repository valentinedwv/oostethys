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
		myFormatter.format(val);
		fail();
		}catch(	java.lang.NumberFormatException e){
			// ok
		}
	}
	
	public void testNumberFormatLocale_aparent_wrong_fix() throws ParseException{
// using the default locale	(test in USA)
		String valS = "36,23";
		Locale locale= Locale.getDefault();
		NumberFormat nf = NumberFormat.getNumberInstance(locale);
		DecimalFormat df = (DecimalFormat)nf;
	
			Number val = df.parse(valS);

			NumberFormat myFormatter = NumberFormat.getInstance(locale);
			
			myFormatter.setMaximumFractionDigits(6);
			String output = myFormatter.format(val);
			assertEquals("36,23", output);
		
		
	}
	


	
	public void testNumberFormatLocale_fix() throws ParseException{
	 
		String valS = "36,23";
//		Locale locale= new Locale("spa", "ES");
		Locale locale = Locale.GERMAN;
	DecimalFormat df = (DecimalFormat)DecimalFormat.getNumberInstance(locale);

			Number val = df.parse(valS);
			double doub = val.doubleValue();
			df.setMaximumFractionDigits(6);
			String s = df.format(doub);
			
			assertEquals("36,23", s);
	}
	

}
