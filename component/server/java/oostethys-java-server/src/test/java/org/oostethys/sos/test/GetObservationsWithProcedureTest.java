package org.oostethys.sos.test;

import org.oostethys.sos.Netcdf2sos100;

import org.oostethys.test.OOSTethysTest;

import java.io.ByteArrayOutputStream;

import java.net.URL;

import java.util.HashMap;
import java.util.Map;


public class GetObservationsWithProcedureTest extends OOSTethysTest {
    Netcdf2sos100 ns = null;

    protected void setUp() throws Exception {
        super.setUp();
        ns = new Netcdf2sos100();

        URL file = getURL("oostethysComplex.xml");
        URL url = file;

        ns.setUrlOostethys(url);
    }
    
    public void testGetObservationNoProcedure() throws Exception {
        
        Map<String, String[]> map = new HashMap<String, String[]>();
        map.put("Request", createArray("getObservation"));
        //			map.put("bbox", createArray(bbox));
        map.put("Service", createArray("SOS"));
        map.put("version", createArray("1.0.0"));
        map.put("offering", createArray("observationOffering_1455"));
      

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // ns.setValue_BBOX(bbox);
        ns.process(map, outputStream);

        
        String s = outputStream.toString();
       

        assertTrue(s.contains("<om:ObservationCollection"));
    }

    public void testGetObservationWrongProcedure() throws Exception {
      
        Map<String, String[]> map = new HashMap<String, String[]>();
        map.put("Request", createArray("getObservation"));
        //			map.put("bbox", createArray(bbox));
        map.put("Service", createArray("SOS"));
        map.put("version", createArray("1.0.0"));
        map.put("offering", createArray("observationOffering_1455"));
        map.put("procedure", createArray("sssss"));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // ns.setValue_BBOX(bbox);
        ns.process(map, outputStream);

        
        String s = outputStream.toString();
     

        assertTrue(s.contains("<ExceptionReport"));
    }
    
    
    public void testGetObservationCorrectProcedure() throws Exception {
        
        Map<String, String[]> map = new HashMap<String, String[]>();
        map.put("Request", createArray("getObservation"));
        //			map.put("bbox", createArray(bbox));
        map.put("Service", createArray("SOS"));
        map.put("version", createArray("1.0.0"));
        map.put("offering", createArray("observationOffering_1456"));
        map.put("procedure", createArray("urn:mbari:org:device:1456"));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // ns.setValue_BBOX(bbox);
        ns.process(map, outputStream);

        
        String s = outputStream.toString();
      

        assertTrue(s.contains("<om:ObservationCollection"));
        assertTrue(s.contains("observationOffering_1456"));
        assertFalse(s.contains("observationOffering_1455"));
    }
    
    
}
