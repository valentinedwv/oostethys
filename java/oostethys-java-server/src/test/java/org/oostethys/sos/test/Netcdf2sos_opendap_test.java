package org.oostethys.sos.test;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.opengis.sensorML.x101.SensorMLDocument;

import org.oostethys.sos.Netcdf2sos100;
import org.oostethys.sos.reader.SOSObservationSweCommonReader;
import org.oostethys.sos.reader.SOSSimpleGeneralReader;
import org.oostethys.test.OOSTethysTest;
import org.oostethys.testutils.LocalResourceServer;
import org.oostethys.sos.reader.PlotData;

/**
 * 
 * This test has been disabled
 * 
 * @author bermudez
 * 
 */
public class Netcdf2sos_opendap_test extends OOSTethysTest {
	Netcdf2sos100 ns = null;

	private LocalResourceServer server = new LocalResourceServer();

	protected void setUp() throws Exception {
		super.setUp();
		ns = new Netcdf2sos100();

		URL url = Thread.currentThread().getContextClassLoader().getResource(
				"oostethys-roym.xml");
		ns.setUrlOostethys(url);

		URL urlService = new URL("http://localhost:8080/oostethys/sos");
		ns.setServletURL(urlService.toString());
		
		// start test server
		server.startServer();
	}

	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		server.stopServer();
		super.tearDown();
	}

	public void testGetCapabilities() throws Exception {
		Map<String, String[]> map = new HashMap<String, String[]>();
		map.put("REQUEST", createArray("GetCapabilities"));
		map.put("SERVICE", createArray("SOS"));
		map.put("VERSION", createArray("1.0.0"));

		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ns.process(map, baos);

		
		final String result = baos.toString();
		
		assertDoesNotContain(result, "ExceptionReport");

	}
	
	public void atestGetData()throws Exception{
		SOSSimpleGeneralReader simpleReader = new SOSSimpleGeneralReader();
		
		simpleReader
				.processCapabilities("http://localhost:8080/oostethys/sos?VERSION=1.0.0&SERVICE=SOS&REQUEST=GetCapabilities");

		URL urlGetObservation = new URL(simpleReader.getObservationURL());
		SOSObservationSweCommonReader reader = new SOSObservationSweCommonReader();
		reader.setURLGetObservation(urlGetObservation);

		String var = simpleReader.getVariableURL();

		String varURI = var;
		reader.process(varURI);
	
		PlotData plotData = reader.getPlotData();
		String title = plotData.getTitle();
		int rows =plotData.getValues().length;
		System.out.println(plotData.getValues());
	}

	public void testDescribeSensor() throws Exception {
		final Map<String, String[]> map = new HashMap<String, String[]>();
		map.put("Request", new String[] { "describeSensor" });
		map.put("procedure", new String[] { "urn:xxx:org:model1" });
		map.put("SERVICE", createArray("SOS"));
		map.put("VERSION", createArray("1.0.0"));
		map.put("outputformat",
				createArray("text/xml;subtype=\"sensorML/1.0.1\""));

		final ByteArrayOutputStream baos = new ByteArrayOutputStream();

		ns.process(map, null);
		ns.getDescribeSensor(baos);

		final String result = baos.toString();

		SensorMLDocument.Factory.parse(result);
	}

	public void testgetObservation() throws Exception {
		// 42.20551 -70.7238
		String minLon = "-71";
		String minLat = "40";
		String maxLon = "-69";
		String maxlat = "45";
		String bbox = minLon + "," + minLat + "," + maxLon + "," + maxlat;

		Map<String, String[]> map = new HashMap<String, String[]>();
		map.put("Request", new String[] { "getObservation" });
		map.put("bbox", new String[] { bbox });
		map.put("SERVICE", createArray("SOS"));
		map.put("VERSION", createArray("1.0.0"));
		map.put("offering",
				createArray("observationOffering_um"));

		// ns.setValue_BBOX(bbox);

		final ByteArrayOutputStream baos = new ByteArrayOutputStream();

		ns.process(map, null);
		ns.getObservation(baos);

		final String result = baos.toString();
		
		
		

		assertDoesNotContain(result, "ExceptionReport");
	}

}
