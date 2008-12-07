package org.oostethys.model.impl.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for org.oostethys.model.impl.test");
		//$JUnit-BEGIN$
		suite.addTestSuite(ObservationNetcdfTest.class);
		suite.addTestSuite(VariableQuantityImplTest.class);
		//$JUnit-END$
		return suite;
	}

}
