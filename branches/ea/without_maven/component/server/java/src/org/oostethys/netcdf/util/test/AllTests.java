package org.oostethys.netcdf.util.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.oostethys.model.impl.test.ObservationNetcdfTest;
import org.oostethys.model.impl.test.VariableQuantityImplTest;
import org.oostethys.netcdf.util.test.TimeUtilTest;
import org.oostethys.netcdf.util.test.UnitsMapperTest;
import org.oostethys.sos.test.SOS_100_Test;
import org.oostethys.sos.test.Netcdf2sos_example_model_output;
import org.oostethys.sos.test.Netcdf2sos_mbari_ctd_test;
import org.oostethys.sos.test.Netcdf2sos_usmass_test;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.oostethys.netcdf.util.test");
		
		
		suite.addTestSuite(VariableMapperTest.class);
		suite.addTestSuite( TimeUtilTest.class);
		suite.addTestSuite( UnitsMapperTest.class);
	
		
		return suite;
	}

}
