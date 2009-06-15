package org.oostethys.netcdf.util.test;

import java.util.Calendar;
import java.util.TimeZone;

import junit.framework.TestCase;

import org.oostethys.netcdf.util.TimeUtil;

public class TimeUtilTest extends TestCase {
	



	public void testGetMillisec() {
	

		// "yyyy-MM-dd'T'HH:mm:ss'Z'"
		
		long millisec = TimeUtil.getMillisec("2008-06-09T15:00:10Z");
		assertEquals(millisec, 1213023610000l);
		// check here (put seconds) http://www.epochconverter.com/
		System.out.println(millisec);
		
		
		assertEquals("2008-06-09T15:00:10Z", TimeUtil
				.getISOFromMillisec(getLong(2008, 6, 9, 15, 0, 10)));
		
		
	
	}
	
	

	

	// 2008-06-09 15:00:00 in GMT // real month of the year
	public long getLong(int y, int month, int d, int h, int min, int s) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("GMT"));
		cal.set(y, month - 1, d, h, min, s);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTimeInMillis();
	}

}
