package org.oostethys.test;

import java.util.logging.Level;
import java.util.logging.Logger;


import org.oostethys.sos.Netcdf2sos100;

public class OOSTethysTest extends GeneralTest {
	
	Logger logger = Logger.getLogger("OOSTethysTest"); 

	protected void setUp() throws Exception {
		super.setUp();
		Logger logger = Logger.getLogger(Netcdf2sos100.class.getName());
		logger.setLevel(Level.ALL

		);

	}

	public static String[] createArray(String value) {
		String[] array = { value };
		return array;
	}

}
