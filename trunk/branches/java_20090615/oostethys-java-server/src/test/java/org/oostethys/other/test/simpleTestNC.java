package org.oostethys.other.test;

import ucar.nc2.NCdump;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFileWriteable;
import ucar.nc2.Dimension;
import ucar.nc2.dataset.NetcdfDataset;
import ucar.nc2.units.TimeUnit;
import ucar.ma2.Array;
import ucar.ma2.DataType;
import ucar.ma2.ArrayFloat;
import ucar.ma2.Index;
import ucar.ma2.InvalidRangeException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.io.IOException;

import org.oostethys.netcdf.util.TimeUtil;

public class simpleTestNC {
	public static void main(String[] args) {
		String url = "http://whewell.marine.unc.edu/dods/nccoos/latest_v2.0/nccoos_jpier_met_latest.nc";
		try {
			NetcdfDataset netcdfdataset = NetcdfDataset.openDataset(url);		
			NCdump.print(netcdfdataset, "", System.out, null);
			
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}


}
