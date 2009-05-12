package org.oostethys.sos.test;

import org.oostethys.sos.Netcdf2sos100;

import org.oostethys.test.OOSTethysTest;

import java.net.URL;


public class Netcdf2sos_example_model_output extends OOSTethysTest {
    // checks taht could read the file
    public void testFVCOM() throws Exception {
        URL url = getURL("oostethys_emptyString.xml");

        Netcdf2sos100 ns = new Netcdf2sos100();

        ns.setUrlOostethys(url);
        ns.processForTest();
    }
}
