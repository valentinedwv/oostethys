package org.oostethys.sos.reader.test;
import java.net.URL;

import org.oostethys.sos.reader.SOSObservationSweCommonReader;

import junit.framework.TestCase;

public class SOSObservationSweCommonReaderTest extends TestCase {

	private SOSObservationSweCommonReader reader;

	// time
	// Longitude
	// Latitude
	// NominalDepth
	// Temperature
	// Conductivity
	// Salinity
	// Pressure
	private String asciidata = "2008-06-09T09:36:19Z,36.69623,-122.39965,10,12.1338,3.82432,33.17029,10.052 2008-06-09T09:46:20Z,36.69623,-122.39965,10,12.1324,3.82408,33.169254,9.956";

	protected void setUp() throws Exception {

		super.setUp();

		reader = new SOSObservationSweCommonReader();
	}

	public void testSetArrayTime() {

		reader.setASCIIString(asciidata);
		reader.setBlockSeparator(" ");
		reader.setTokenSeparator(",");

		reader.setIndexTime(0);
		reader.setIndexVariable(6); // salinity
		reader.doArrayTime();
		long[] times = reader.getPlotData().getTimes();
		assertEquals(1213004179000l, times[0]);
		assertEquals(1213004780000l, times[1]);

	}
	
	public void testSetArrayValues() {

		reader.setASCIIString(asciidata);
		reader.setBlockSeparator(" ");
		reader.setTokenSeparator(",");

		reader.setIndexTime(0);
		reader.setIndexVariable(6); // salinity
		reader.doArrayValues();
		double[] values = reader.getPlotData().getValues();
		assertEquals(33.17029, values[0]);
		assertEquals(33.169254, values[1]);

	}
	
	

}
