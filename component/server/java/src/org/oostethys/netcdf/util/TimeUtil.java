/*****************************************************************************
 * Source code information
 * -----------------------
 * Original author    Luis Bermudez, SURA
 * Author email       bermudez@sura.org
 * Package            org.oostethys.netcdf.util
 * Web                http://marinemetadata.org/mmitools
 * Created            Dec 4, 2006
 * Filename           $RCSfile: TimeUtil.java,v $
 * Revision           $Revision: 1.2 $
 *
 * Last modified on   $Date: 2008/06/19 19:47:44 $
 *               by   $Author: luisbermudez $
 *
 * (c) Copyright 2005, 2006 Monterey Bay Aquarium Research Institute
 * Marine Metadata Interoperability (MMI) Project http://marinemetadata.org
 *
 * License Information
 * ------------------------
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, you can download it from 
 *  http://marinementadata.org/gpl or write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *********************************************************************************/
package org.oostethys.netcdf.util;

import java.util.Date;
import java.util.TimeZone;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import ucar.nc2.units.DateUnit;
import ucar.nc2.units.TimeUnit;

/**
 * <p>
 * Utility class to handles UDUNIT time . Sets UDUNITs and converts time units
 * to ISO 8601 format "yyyy-MM-dd'T'HH:mm:ss'Z'"
 * </p>
 * 
 * <p>
 * Example of UDUNITS time:
 * </p>
 * <code>
 * <p>seconds since 1992-10-8 15:15:42.5 -6:00</p><br />

 indicates seconds since October 8th, 1992  at  3  hours,  15
 minutes  and  42.5 seconds in the afternoon in the time zone
 which is six hours to the west of Coordinated Universal Time
 (i.e.  Mountain Daylight Time).  The time zone specification
 can also be written without a colon using one or  two-digits
 (indicating hours) or three or four digits (indicating hours
 and minutes). 
 </code>
 * <p>
 * 
 * <a
 * href="http://www.unidata.ucar.edu/software/udunits/man.php?udunits+3f">Taken
 * from the udunits manual</a>
 * </p>
 * 
 * <hr>
 * 
 * 
 */

// TODO implement time zone if given
public class TimeUtil {

	private String nctimeunits;  // udnits as given by udunits
	private DateUnit dateUnit;
	private long base;
	private SimpleDateFormat format;

	private String formatDates[] = { "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HHmm",
			"yyyy-MM-dd HH:mm", "yyyy-MM-dd Hmm", "yyyy-MM-dd HH:mm 'UTC'", "yyyy-MM-dd HH:mm:ss.mm",};

	public final static int MILLISEC = 0;
	public final static int SECONDS = 1;
	public final static int MINUTES = 2;
	public final static int HOURS = 3;
	public final static int DAYS = 4;

	private int uniTtype; // hr or min, etc..

	private String[] stringMatchesMillisec = { "[M,m]illiseconds?", "millisec" };
	private String[] stringMatchesSeconds = { "[S,s]econds?", "sec", "s" };
	private String[] stringMatchesMinutes = { "[M,m]inutes?", "min" };
	private String[] stringMatchesHours = { "[H,h]ours?", "hr", "h" };
	private String[] stringMatchesDays = {};

	private String[][] stringMatches = { stringMatchesMillisec,
			stringMatchesSeconds, stringMatchesMinutes, stringMatchesHours,
			stringMatchesDays };
	
	private static SimpleDateFormat formatISODefault = new SimpleDateFormat(
	"yyyy-MM-dd'T'HH:mm:ss'Z'");

	java.util.logging.Logger logger = java.util.logging.Logger
			.getLogger("TimeUtil");

//	public TimeUtil(String nctimeunits) {
//		this.nctimeunits = nctimeunits;
//		try {
//			dateUnit = new DateUnit(nctimeunits);
//		} catch (Exception e) {
//			logger.severe("time unit not correct "+ nctimeunits );
//			e.printStackTrace();
//		}

//		setBaseTime();
//		setType();
//	}

	private void setType() {
		String type = nctimeunits.substring(0, nctimeunits.indexOf("since"))
				.trim();
		for (int i = 0; i < stringMatches.length; i++) {
			String[] matchArray = stringMatches[i];
			for (int j = 0; j < matchArray.length; j++) {
				String string = matchArray[j];
				if (type.matches(string)) {
					uniTtype = i; // corresponds to the array position in
					// stringMatches
					logger.info("found match for type: " + type
							+ " unit type: " + uniTtype);
					return;
				}

			}

		}

		logger.warning("Not found type for " + type);

	}

	private void setBaseTime() {
		
		String baseDate = nctimeunits.substring(
				nctimeunits.indexOf("since") + 6).trim();
		Date date = null;
		format = new SimpleDateFormat();
		format.setTimeZone(TimeZone.getTimeZone("GMT"));

		for (int i = 0; i < formatDates.length; i++) {
			try {
				// sets the format with the found pattern
				format.applyLocalizedPattern(formatDates[i]);
				date = format.parse(baseDate);
				base = date.getTime();
				break;
			} catch (ParseException e) {

			}

		}
		if (date != null) {
			logger.info("base time set for: " + getISOFromMillisec(base));

		} else {
			logger.info("base time was not set");
		}

	}
	


	public static long getMillisec(String isoDate){
		DateTime datetime = new DateTime(isoDate);
		return datetime.getMillis();
				
	}
	
	private static long getMillisec_old(String isoDate){
		
		Date date=null;
		formatISODefault.setTimeZone(TimeZone.getTimeZone("GMT"));
		try {
			date = formatISODefault.parse(isoDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date.getTime();
	}

	/**
	 * Return nice given MMILIS since EPOCH
	 * 
	 * @param timeMillisecGMT
	 * @return
	 */
	public static String getNice(long timeMillisecGMT) {
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		format.setTimeZone(TimeZone.getTimeZone("GMT"));
		return format.format(new Date(timeMillisecGMT));

	}

	/**
	 * Returns time form ISO given millisec since EPOCH
	 * 
	 * @param timeMillisecGMT
	 * @return
	 */
	public static String getISOFromMillisec(long timeMillisecGMT) {
		
		formatISODefault.setTimeZone(TimeZone.getTimeZone("GMT"));
		return formatISODefault.format(new Date(timeMillisecGMT));

	}
	
	/**
	 * Returns time form ISO given millisec since EPOCH
	 * 
	 * @param timeMillisecGMT
	 * @return
	 */
	public static String getISOFromMillisec(double timeMillisecGMT) {
		
		formatISODefault.setTimeZone(TimeZone.getTimeZone("GMT"));
		 DecimalFormat df = new DecimalFormat("#0");
		 long millisec = Long.parseLong(df.format(timeMillisecGMT));
		return formatISODefault.format(millisec);

	}


	/**
	 * @return the uniTtype
	 */
	public int getUniTtype() {
		return uniTtype;
	}

	/**
	 * @param uniTtype
	 *            the uniTtype to set
	 */
	public void setUniTtype(int uniTtype) {
		this.uniTtype = uniTtype;
	}

}
