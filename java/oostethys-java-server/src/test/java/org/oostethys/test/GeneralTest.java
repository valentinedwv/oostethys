package org.oostethys.test;

import java.net.URL;

import junit.framework.TestCase;

public abstract class GeneralTest extends TestCase {

	public GeneralTest() {
		super();
	}

	public GeneralTest(String name) {
		super(name);
	}

	public URL getURL(String fileName)  {
	    return getClass().getResource("/"+fileName);
	}

}