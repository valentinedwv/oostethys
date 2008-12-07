package org.oostethys.other.test;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import ucar.ma2.Array;
import ucar.ma2.Index;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import ucar.nc2.dataset.NetcdfDataset;

public class TestReadNetCDFAUV {
	public static void main(String[] args) {
		String m0 = "http://dods.mbari.org/cgi-bin/nph-nc/data/auvctd/surveys/2006/netcdf/Dorado389_2006_324_11_324_11_decim.nc";
		String salinity = "salinity"; //psu
		String depth = "depth"; //m +down
		String longitude = "longitude"; //deg
		String latitude = "latitude"; //deg
		String time = "time";// millis
		
//GRID trajectory
		
		NetcdfDataset file;
		try {
			file = NetcdfDataset.openDataset(m0);

			List list = file.getVariables();
			System.out.println(list);

			Array varArr = getArray(file,salinity);
			Array timeArr = getArray(file,time);
			Array latitudeArr = getArray(file,latitude);
			Array longitudeArr = getArray(file,longitude);
			Array depthArr = getArray(file,depth);
			
			
			
			
			for (int i = 0; i < timeArr.getShape()[0]; i++) {
				
				System.out.print ( " time " + timeArr.getDouble(timeArr.getIndex().set(i)));
				System.out.print ( " depth " + depthArr.getDouble(depthArr.getIndex().set(i)));
				System.out.print ( " lat " + latitudeArr.getDouble(latitudeArr.getIndex().set(i)));
				System.out.print ( " lon " + longitudeArr.getDouble(longitudeArr.getIndex().set(i)));
				System.out.println ( " var " + varArr.getFloat(varArr.getIndex().set(i)));
				i=timeArr.getShape()[0];
				
			}
			
			
			// float SEA_WATER_SALINITY_HR(HR_TIME_TS=2879, DEPTH_TS_HR=5,
			// LATITUDE_HR=1, LONGITUDE_HR1=1);

			

//			Variable lat = file.findVariable(latitude);
//			a = lat.read();
//			float latF = a.getFloat(a.getIndex().set(0));
//			System.out.println("latitude " + latF);
//			
//			Variable lon = file.findVariable(longitude);
//			a = lon.read();
//			float lonF = a.getFloat(a.getIndex().set(0));
//			System.out.println("longitude " + lonF);
//			
//			Variable timeVar = file.findVariable(time);
//			a = timeVar.read();
//			index = a.getIndex();
//			for (int i = 0; i < index.getShape()[0]; i++) {
//				System.out.println( "time "+ i+" " + a.getDouble(index.set(i)));
//			}
//			
//			Variable depthVar = file.findVariable(depth);
//			a = depthVar.read();
//			index = a.getIndex();
//			for (int i = 0; i < index.getShape()[0]; i++) {
//				System.out.println("depth "+ i+" " + a.getDouble(index.set(i)));
//			}

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
		return null;
	}

}
