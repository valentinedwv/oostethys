package org.oostethys.test;

import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.TestCase;

public abstract class GeneralTest extends TestCase {

	public GeneralTest() {
		super();
	}

	public GeneralTest(String name) {
		super(name);
	}

	public URL getURL(String fileName) {
		URL file = Thread.currentThread().getContextClassLoader().getResource(
				"test/");
		URL url =null;
		try {
			url = new URL(file+ fileName);
		} catch (MalformedURLException e) {
		
			e.printStackTrace();
		}
	
	
		return url;
	}

}