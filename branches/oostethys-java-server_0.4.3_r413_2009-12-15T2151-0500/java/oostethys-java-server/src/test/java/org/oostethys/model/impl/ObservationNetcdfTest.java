package org.oostethys.model.impl;

import java.net.URL;
import java.text.DecimalFormat;

import junit.framework.TestCase;

import org.oostethys.model.VariablesConfig;
import org.oostethys.model.impl.ObservationNetcdf;
import org.oostethys.netcdf.util.TimeUtil;

public class ObservationNetcdfTest extends TestCase {
	public void testCTDMBARI() throws Exception {
		URL url = getClass().getResource("/ctd0010.nc");
		VariablesConfig config = new VariablesConfig();

		config.addVariable("esecs", "urn:ogc:phenomenon:time:iso8601",
				"seconds since 1970-01-01 00:00:00",
				"http://mmisw.org/ont/cf/parameter/milliseconds_since_Epoch");
		config.addVariable("Longitude", "urn:ogc:phenomenon:longitude:wgs84",
				null, null);
		config.addVariable("Latitude", "urn:ogc:phenomenon:latitude:wgs84",
				null, null);
		config.addVariable("NominalDepth",
				"http://mmisw.org/ont/cf/parameter/depth", null, null);
		config.addVariable("Temperature",
				"http://mmisw.org/ont/cf/parameter/sea_water_temperature",
				null, null);

		config.addVariable("Conductivity",
				"http://mmisw.org/ont/cf/parameter/conductivity", null, null);
		config.addVariable("Salinity",
				"http://mmisw.org/ont/cf/parameter/sea_water_salinity", null,
				null);
		config.addVariable("Pressure",
				"http://mmisw.org/ont/cf/parameter/pressure", null, null);

		ObservationNetcdf oni = new ObservationNetcdf();
		oni.setURL(url.toString());
		oni.setVariablesConfig(config);
		oni.setDepthIsGiven(true);
		oni.setValue_TIME("2008-04-11/2008-10-11");
		oni.setNumberOfRecords(100);

		oni.process();
		String records = oni.getAsRecords();

		assertTrue(records.startsWith("2"));
		String s = "2008-06-09T09:36:19Z,36.69623,-122.39965,10,1,213,004,179,-122.39965,36.69623,10,12.1338,3.82432,33.17029,10.052 2008-06-09T09:46:20Z,36.69623,-122.39965,10,1,213,004,780,-122.39965,36.69623,10,12.1324,3.82408,33.169254,9.956 ";
		assertTrue(records.startsWith(s));
		assertEquals(-122.39965, oni.getMinLon());
		assertEquals(-122.39965, oni.getMaxLon());
		assertEquals(36.69623, oni.getMinLat());
		assertEquals(36.69623, oni.getMaxLat());

		DecimalFormat df = new DecimalFormat("#0");
		long lminTime = Long.parseLong(df.format(oni.getMinTime()));
		long lmaxTime = Long.parseLong(df.format(oni.getMaxTime()));

		assertEquals("2008-06-09T09:36:19Z", TimeUtil
				.getISOFromMillisec(lminTime));
		assertEquals("2008-06-10T02:06:21Z", TimeUtil
				.getISOFromMillisec(lmaxTime));
	}
}
