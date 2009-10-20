package org.oostethys.netcdf.util.test;

import org.oostethys.netcdf.util.UnitsMapper;

import junit.framework.TestCase;

public class UnitsMapperTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void testgetUCUM(){
		String udunit  = "meters";
		String ucum = UnitsMapper.getUCUM(udunit);
		assertEquals("m", ucum);
	}

}
