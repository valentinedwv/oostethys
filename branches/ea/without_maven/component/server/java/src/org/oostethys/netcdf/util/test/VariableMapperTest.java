package org.oostethys.netcdf.util.test;

import org.oostethys.netcdf.util.VariableMapper;
import org.oostethys.voc.Voc;

import junit.framework.TestCase;

public class VariableMapperTest extends TestCase {

	String MMI_CF_BASE = "http://mmisw.org/ont/cf/parameter/";
	
	protected void setUp() throws Exception {
		super.setUp();
	}



	public void testGetMMIURN() {
		String uri  = VariableMapper.getMMIURN("org", "risk");
		assertEquals("urn:mmisw.org:tmp:org:risk", uri);
	}

	public void testGetMMIURIforCFParam() {
		String uri  = VariableMapper.getMMIURIforCFParam("sea_surface_height_above_geoid");
		assertEquals(MMI_CF_BASE+"sea_surface_height_above_geoid", uri);
	}

}
