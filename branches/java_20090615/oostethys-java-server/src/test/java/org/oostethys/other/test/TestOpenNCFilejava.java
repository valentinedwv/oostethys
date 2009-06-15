package org.oostethys.other.test;

import java.io.IOException;

import ucar.nc2.NCdump;
import ucar.nc2.dataset.NetcdfDataset;

public class TestOpenNCFilejava {

	public static void main(String[] args) {

			String url = "file:/Users/bermudez/Documents/workspace31/org.oostethys/WebRoot/WEB-INF/classes/test/umassb_scituate.nc";
		try {

			NetcdfDataset netcdfdataset = NetcdfDataset.openDataset(url);
			
			NCdump.print(netcdfdataset, "", System.out, null);
			

		} catch (IOException e) {
		
			e.printStackTrace();
		}
	}


}
