package org.oostethys.sos.test;

import java.net.URL;
import java.util.Map;

import org.oostethys.sos.Netcdf2sos100;
import org.oostethys.test.OOSTethysTest;

public class Netcdf2sos_config_errorsTest extends OOSTethysTest {
	Netcdf2sos100 ns = null;

	public void testNotFoundConfigXML() {

		Netcdf2sos100 ns = new Netcdf2sos100();
		try {
			ns.getParameterMap().put("version", "1.0.0");
			ns.getParameterMap().put("service", "SOS");
			ns.getParameterMap().put("request", "GetCapabilities");
			ns.setUrlOostethys(new URL("file:/ddhdhdhd"));
			ns.process((Map<String,String[]>)null, null);
			fail();
			
		} catch (Exception e) {
// TODO more specific exception class
		}
	}
	
	public void testNotFoundNCFile() {

		Netcdf2sos100 ns = new Netcdf2sos100();
		try {
			ns.getParameterMap().put("version", "1.0.0");
			ns.getParameterMap().put("service", "SOS");
			ns.getParameterMap().put("request", "GetCapabilities");
			ns.setUrlOostethys(getURL("oostethys_wrongurlnc.xml"));
			ns.process((Map<String,String[]>)null, null);
			fail();
		} catch (Exception e) {
			// TODO more specific exception class
		}
	}

	public void testMalformedXML() {

		Netcdf2sos100 ns = new Netcdf2sos100();
		try {
			ns.getParameterMap().put("version", "1.0.0");
			ns.getParameterMap().put("service", "SOS");
			ns.getParameterMap().put("request", "GetCapabilities");
			ns.setUrlOostethys(new URL("file:test/oostethys_malformed.xml"));
			ns.process((Map<String,String[]>)null, null);
			fail();
		} catch (Exception e) {
			// TODO more specific exception class
		}
	}

	
	public void testNotValidXML() {
// todo - need to check for validation !!
		Netcdf2sos100 ns = new Netcdf2sos100();
		try {
			ns.getParameterMap().put("version", "1.0.0");
			ns.getParameterMap().put("service", "SOS");
			ns.getParameterMap().put("request", "GetCapabilities");
			ns.setUrlOostethys(new URL("file:test/oostethys_notvalid.xml"));
			ns.process((Map<String,String[]>)null, null);
			fail();
		} catch (Exception e) {
			// TODO more specific exception class
		}
	}
	
	public void testNotFoundVariable(){
		Netcdf2sos100 ns = new Netcdf2sos100();
		try {
			ns.getParameterMap().put("version", "1.0.0");
			ns.getParameterMap().put("service", "SOS");
			ns.getParameterMap().put("request", "GetCapabilities");
			ns.setUrlOostethys(new URL("file:test/oostethys_notfoundvars.xml"));
			ns.process((Map<String,String[]>)null, null);
			fail();
		} catch (Exception e) {
		 // TODO more specific exception class
		}
	}
//	public void testFoundVariableDifficultSemantics(){
//		Netcdf2sos100 ns = new Netcdf2sos100();
//		try {
//			ns.setUrlOostethys(getURL("oostethys_foundVarsLat.xml"));
//			
//			ns.process((Map)null, null);
//			assertTrue("should not reach this point", 1 == 2);
//		} catch (Exception e) {
//			e.printStackTrace();
////			System.out.println("message "+e.getMessage());
//			assertTrue(e.getMessage().contains("The following variable was not found "));
//			assertNotNull(e);
//			e.printStackTrace();
//		}
//	}

}
