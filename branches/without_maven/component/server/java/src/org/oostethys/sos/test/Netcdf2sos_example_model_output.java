package org.oostethys.sos.test;

import java.net.URL;
import java.util.Map;

import org.oostethys.sos.Netcdf2sos100;
import org.oostethys.test.OOSTethysTest;

import junit.framework.TestCase;

public class Netcdf2sos_example_model_output extends OOSTethysTest {
// checks taht could read the file
	
	
	public void testFVCOM(){
		Netcdf2sos100 ns = new Netcdf2sos100();
		try {
			ns.setUrlOostethys(getURL("oostethys_emptyString.xml"));
			ns.processForTest();
			assertTrue("should  reach this point", 1 == 1);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("message: "+e.getMessage());
			fail("should show not expection");
		
		}
	}

}
