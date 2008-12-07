package org.oostethys.test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.oostethys.sos.Netcdf2sos100;

public class OOSTethysTest extends TestCase {
	
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

	protected URL getURL(String fileName) {
		URL file = Thread.currentThread().getContextClassLoader().getResource(
				"test/");
		URL url =null;
		try {
			url = new URL(file+ fileName);
		} catch (MalformedURLException e) {
		
			e.printStackTrace();
		}
		logger.info("URL "+url);

		return url;
	}

}
