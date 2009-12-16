package org.oostethys.other.test;

import java.io.IOException;

import ucar.nc2.dataset.NetcdfDataset;

public class simpleTestNC {
	public static void main(String[] args) {
	//	String url = "http://whewell.marine.unc.edu/dods/nccoos/latest_v2.0/nccoos_jpier_met_latest.nc";
		String url="http://coast-enviro.er.usgs.gov/thredds/dodsC/gom_interop/bio/ww3/forecast/fine.dods";
		try {
			NetcdfDataset netcdfdataset = NetcdfDataset.openDataset(url);		
			System.out.print(netcdfdataset);
		
			
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}


}
