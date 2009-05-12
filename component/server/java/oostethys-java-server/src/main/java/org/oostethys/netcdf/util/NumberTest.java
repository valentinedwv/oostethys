package org.oostethys.netcdf.util;

import java.text.DecimalFormat;

public class NumberTest {
	
	public static void main(String[] args) {
		double d = 	1231231222;
		 DecimalFormat df = new DecimalFormat("#0");
		long l = Long.parseLong(df.format(d));
		System.out.println(l);
	}

}
