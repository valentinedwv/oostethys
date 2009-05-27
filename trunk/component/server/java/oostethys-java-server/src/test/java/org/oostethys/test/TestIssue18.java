package org.oostethys.test;

import java.net.URL;

import org.oostethys.sos.Netcdf2sos100;
import org.oostethys.testutils.LocalResourceServer;

public class TestIssue18 extends GeneralTest {
	private Netcdf2sos100 ns;
//	String file ="http://dods.mbari.org/cgi-bin/nph-nc/data/ssdsdata/deployments/m0/200607/hourlyM0_20060731.nc";
	
	private LocalResourceServer server = new LocalResourceServer();
	
	/**
	* @see junit.framework.TestCase#setUp()
	*/
	@Override
	protected void setUp() throws Exception {
	    server.startServer();
	    super.setUp();
	}
	
	/**
	* @see junit.framework.TestCase#tearDown()
	*/
	@Override
	protected void tearDown() throws Exception {
	    server.stopServer();
	    super.tearDown();
	}
	
	public void testIssue18() throws Exception{
		ns = new Netcdf2sos100();
		URL url =getURL("oostethys-18.xml");
		ns.setUrlOostethys(url);
		ns.processForTest();
		
	}

}
