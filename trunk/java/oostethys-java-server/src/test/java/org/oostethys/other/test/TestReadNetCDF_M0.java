package org.oostethys.other.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ucar.ma2.Array;
import ucar.ma2.Index;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.Group;
import ucar.nc2.NCdump;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import ucar.nc2.dataset.CoordinateSystem;
import ucar.nc2.dataset.NetcdfDataset;

public class TestReadNetCDF_M0 {

	public static void main(String[] args) {
		// String m0 =
		// "http://dods.mbari.org/cgi-bin/nph-nc/data/ssdsdata/deployments/m2/current_netCDFs/ctd0010.nc";
		String m0 = "http://dods.mbari.org/cgi-bin/nph-nc/data/ssdsdata/deployments/m0/current_netCDFs/ctd0000.nc";
		
//		m0 =  "/Users/bermudez/downloads/pacific/2006/12/nodc_R5901506_002.nc";
		NetcdfDataset file;
		try {
			file = NetcdfDataset.openDataset(m0);
			List<CoordinateSystem> cs = file.getCoordinateSystems();
			List<Variable> vars = file.getVariables();
			// contains varibles that are not a cooridnate
			List<Variable> varsNotCoordinate = new ArrayList<Variable>();
			for (Variable variable : vars) {
//				System.out.println(variable);
				Dimension dim = variable.getDimension(0);
//				System.out.println("var ="+variable.getName()+" dim:  "+dim.getName() );
				if (dim!=null && dim.getName().equals(variable.getName())){
						varsNotCoordinate.add(variable);
						
						if (variable.getName().equalsIgnoreCase("Salinity")){
							variable.getDimensionsAll();
						}
						
				}
				
			
			
			}
		
		System.out.println(vars);
		System.out.println("====================");
		NCdump.print(file, "", System.out, null);
		float f = 35f;
		
		// get Salinity Salinity
		
		
		
		
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	

	public static void main2(String[] args) {
		String m0 = "http://dods.mbari.org/cgi-bin/nph-nc/data/ssdsdata/deployments/m2/current_netCDFs/ctd0010.nc";
		List<Variable> magic = new ArrayList<Variable>();

		NetcdfDataset file;
		try {
			file = NetcdfDataset.openDataset(m0);

			List<Variable> vars = file.getVariables();
			// for (Iterator iterator = vars.iterator(); iterator.hasNext();) {
			// Variable var = (Variable) iterator.next();
			// System.out.println("_________");
			// System.out.println(var.getName());
			// System.out.println(" "+var.getDimensionsString());
			//				
			// }

			for (Iterator iterator = vars.iterator(); iterator.hasNext();) {
				Variable var = (Variable) iterator.next();
				System.out.println(var);
				if (var.getRank() > 1) {
					for (Iterator iterator2 = vars.iterator(); iterator2
							.hasNext();) {
						Variable var_ = (Variable) iterator2.next();
						if (var.getDimensions().equals(var_.getDimensions())) {
							if (!magic.contains(var)) {
								magic.add(var);
							}
						}

					}
				}

			}

			// all the variables with more than one dimension and with same
			// dimension
			Variable var = vars.get(0);

			var.read();

			// var.getDimensions()

			System.out.println(magic);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main3(String[] args) {
		String m0 = "http://dods.mbari.org/cgi-bin/nph-nc/data/ssdsdata/deployments/m0/200607/hourlyM0_20060731.nc";
		String salinity = "SEA_WATER_SALINITY_HR"; // psu
		String depth = "HR_DEPTH_TS1"; // m +down
		String longitude = "LONGITUDE_HR1"; // deg
		String latitude = "LATITUDE_HR"; // deg
		String time = "HR_TIME_TS";// millis

		// this is a station file - so the dimensions change fr each variable

		NetcdfDataset file;
		try {
			file = NetcdfDataset.openDataset(m0);

			Array varArr = getArray(file, salinity);
			Array timeArr = getArray(file, time);
			Array latitudeArr = getArray(file, latitude);
			Array longitudeArr = getArray(file, longitude);
			Array depthArr = getArray(file, depth);

			for (int i = 0; i < timeArr.getShape()[0]; i++) {
				for (int j = 0; j < depthArr.getShape()[0]; j++) {
					System.out.print(" time "
							+ timeArr.getDouble(timeArr.getIndex().set(i)));
					System.out.print(" depth "
							+ depthArr.getDouble(depthArr.getIndex().set(j)));
					System.out.println(" var "
							+ varArr.getFloat(varArr.getIndex().set(i, j)));
				}
			}

			// float SEA_WATER_SALINITY_HR(HR_TIME_TS=2879, DEPTH_TS_HR=5,
			// LATITUDE_HR=1, LONGITUDE_HR1=1);

			// Variable lat = file.findVariable(latitude);
			// a = lat.read();
			// float latF = a.getFloat(a.getIndex().set(0));
			// System.out.println("latitude " + latF);
			//			
			// Variable lon = file.findVariable(longitude);
			// a = lon.read();
			// float lonF = a.getFloat(a.getIndex().set(0));
			// System.out.println("longitude " + lonF);
			//			
			// Variable timeVar = file.findVariable(time);
			// a = timeVar.read();
			// index = a.getIndex();
			// for (int i = 0; i < index.getShape()[0]; i++) {
			// System.out.println( "time "+ i+" " + a.getDouble(index.set(i)));
			// }
			//			
			// Variable depthVar = file.findVariable(depth);
			// a = depthVar.read();
			// index = a.getIndex();
			// for (int i = 0; i < index.getShape()[0]; i++) {
			// System.out.println("depth "+ i+" " + a.getDouble(index.set(i)));
			// }

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static Array getArray(NetcdfDataset file, String varString) {
		Variable var = file.findVariable(varString);

		try {
			return var.read();
		} catch (IOException e) {

			e.printStackTrace();
		}
		return null;
	}

	public static Variable findByStandardName(NetcdfDataset file,
			String standardName) {
		List list = file.getVariables();
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			Variable var = (Variable) iterator.next();
			Attribute att = var.findAttribute("standard_name");
			if (att != null) {
				String attValue = att.getStringValue();
				if (attValue.equalsIgnoreCase(standardName)) {
					return var;
				}
			}
		}

		for (Iterator iterator = list.iterator(); iterator.hasNext();) {

			Variable var = (Variable) iterator.next();
			System.out.println(var);
			if (var.getName().equalsIgnoreCase(standardName)) {
				return var;
			}
		}

		return null;
	}

}
