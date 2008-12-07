package org.oostethys.sos.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.oostethys.model.impl.test.ObservationNetcdfTest;
import org.oostethys.model.impl.test.VariableQuantityImplTest;
import org.oostethys.netcdf.util.test.TimeUtilTest;
import org.oostethys.netcdf.util.test.UnitsMapperTest;
import org.oostethys.sos.test.SOS_100_Test;
import org.oostethys.sos.test.Netcdf2sos_mbari_ctd_test;
import org.oostethys.sos.test.Netcdf2sos_usmass_test;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Tests for org.oostethys.sos.test");
		suite.addTestSuite( ExceptionRerporterTest.class);
		suite.addTestSuite( Netcdf2sos_capabilitiesparameterIssue.class);
		suite.addTestSuite( SOS_100_Test.class);
		suite.addTestSuite( Netcdf2sos_config_test_errors.class);
		suite.addTestSuite( Netcdf2sos_example_model_output.class);
		suite.addTestSuite( Netcdf2sos_mbari_ctd_test.class);
		suite.addTestSuite( Netcdf2sos_mbari_ctd_xmlPost_test.class);
		suite.addTestSuite( Netcdf2sos_roym_test.class);
		suite.addTestSuite( Netcdf2sos_usmass_test.class);
		suite.addTestSuite( Netcdf2sos_unc_test.class);
		suite.addTestSuite( Netcdf2sos_unc_test2.class);
		
		suite.addTestSuite( TestOM_NG.class);
		suite.addTestSuite( TestPost.class);
	
		return suite;
	}

}
