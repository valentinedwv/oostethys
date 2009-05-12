package org.oostethys.test;

import java.net.URL;

import org.oostethys.sos.Netcdf2sos100;

public class TestIssue18 extends GeneralTest {
	Netcdf2sos100 ns;
//	String file ="http://dods.mbari.org/cgi-bin/nph-nc/data/ssdsdata/deployments/m0/200607/hourlyM0_20060731.nc";
	
	
	
	public void testIssue18() throws Exception{
		ns = new Netcdf2sos100();
		URL url =getURL("oostethys-18.xml");
		ns.setUrlOostethys(url);
		ns.processForTest();
		
	}

}
