package org.oostethys.other.test;

import ucar.nc2.Attribute;
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
import ucar.nc2.Variable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.io.IOException;

import org.oostethys.netcdf.util.TimeUtil;

public class NCtestWrite {
	
	public static void main(String[] args) {
		String url = "http://dods.ndbc.noaa.gov/thredds/fileServer/data/adcp2/42377/42377a9999nc/i1b020.nc";
		
		try {

			NetcdfDataset nc = NetcdfDataset.openDataset(url);
			NetcdfDataset.debugDump(System.out, nc);
			Attribute titleAtt = nc.findGlobalAttribute("notes");
			String title  = titleAtt.getStringValue();
			System.out.println("att value "+title);
		
//
//			List<Variable> vars = nc.getVariables();
//			for (Iterator iterator = vars.iterator(); iterator.hasNext();) {
//				ucar.nc2.Variable var = (ucar.nc2.Variable) iterator.next();
//				
//				System.out.println(var.getName());
//				
//			}


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void mainA(String[] args) {

		// when dods using no ending
		// NetcdfFile doesnt work

		// http://thredds1.pfeg.noaa.gov:8080/thredds/dodsC/satellite/BA/ssta/mday.dods?BAssta[0:1:20][0:1:0][0:10:0][0:10:0]

		String url = "http://thredds1.pfeg.noaa.gov:8080/thredds/dodsC/satellite/BA/ssta/mday";
		// BAssta[0:1:73][0:1:0][1000:1:1000][2400:1:2400]
		try {

			NetcdfDataset netcdfdataset = NetcdfDataset.openDataset(url);
			generateObs(netcdfdataset);

			List<Variable> vars = netcdfdataset.getVariables();
			for (Iterator iterator = vars.iterator(); iterator.hasNext();) {
				ucar.nc2.Variable var = (ucar.nc2.Variable) iterator.next();
				System.out.println(var.getName());
				
			}

			// doVar(netcdfdataset, "BAssta", "1:73,0,1000:1010,2400");
			// doVar(netcdfdataset, "lon", "0:100");
			// doVar(netcdfdataset, "lat", "0:100");
			// doVar(netcdfdataset, "time", "0:30");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String createString(int minIndexLat, int maxIndexlat,
			int minIndexLon, int maxIndexLon, int minIndexTime, int MaxIndexTime) {
		String s = minIndexTime + ":" + MaxIndexTime + ",0," + minIndexLat
				+ ":" + maxIndexlat + "," + minIndexLon + ":" + maxIndexLon;
		return s;
	}

	public static void generateObs(NetcdfDataset netcdfdataset) {
		try {
			ucar.nc2.Variable lon = netcdfdataset.findVariable("lon");
			ucar.nc2.Variable lat = netcdfdataset.findVariable("lat");
			ucar.nc2.Variable time = netcdfdataset.findVariable("time");
			ucar.nc2.Variable BAssta = netcdfdataset.findVariable("BAssta");

			Array dataLat = lat.read();
			int[] shapeLat = lat.getShape();
			Index indexLat = dataLat.getIndex();

			Array dataLon = lon.read();
			int[] shapeLon = lon.getShape();
			Index indexLon = dataLon.getIndex();
			
			Array dataTime = time.read();
			Index indexTime = dataTime.getIndex();

			// mbari
			double minLat = 34.955;
			double maxLat = 38.260;
			double minLon = -124.780;
			double maxLon = -121.809;
			int minIndexLat = -Integer.MAX_VALUE;
			int minIndexLon = Integer.MAX_VALUE;
			int maxIndexlat = Integer.MIN_VALUE;
			int maxIndexLon = Integer.MIN_VALUE;

			// lon is ginve as +

//			int[][] indexLatLon = new int[shapeLat[0]][shapeLon[0]];
			List lats = new ArrayList<Integer>();
			List lons = new ArrayList<Integer>();

			// get lats

			for (int i = 0; i < shapeLat[0]; i++) {
				Double lat_d = dataLat.getDouble(indexLat.set(i));
				if (lat_d >= minLat && lat_d <= maxLat) {
					minIndexLat = i;
					lats.add(i);
				}
			}

			for (int j = 0; j < shapeLon[0]; j++) {
				Double lon_d = dataLon.getDouble(indexLon.set(j));
				if (lon_d > 180) {
					lon_d = -360 + lon_d;
				}
				if (lon_d >= minLon && lon_d <= maxLon) {
					lons.add(j);
				}
			}

			// get values:

			ucar.nc2.Variable bassta = netcdfdataset.findVariable("BAssta");
			int indMinLat = (Integer) lats.get(0);
			int indMaxLat = (Integer) lats.get(lats.size() - 1);
			int indMinLon = (Integer) lons.get(0);
			int indMaxLon = (Integer) lons.get(lons.size() - 1);
			int indMinTime = 0;
			int indMaxTime = time.getShape()[0] - 1;

			String s = createString(indMinLat, indMaxLat, indMinLon, indMaxLon,
					indMinTime, indMaxTime);
			// doVar(netcdfdataset, "BAssta", s);
			// Array dataVar= bassta.read();
			// bassta.getShape()

			// System.out.println("lats size: " + lats.size()+" "+"lons size:
			// "+lons.size());

			// for a given lat lon get time series
			// 1st lat lon
			s = createString(indMinLat, indMinLat, indMinLon, indMinLon,
					indMinTime, indMaxTime);

			Array arr = BAssta.read(s);
			int[] shapeArr = arr.getShape();
			Index index = arr.getIndex();
			System.out.println(shapeArr);
			// [74, 1, 1, 1]
			for (int i = 0; i<shapeArr[0];i++){
				
				Double d = arr.getDouble(index.set(i,0,0,0));
			
				Double t = dataTime.getDouble(indexTime.set(i));
				
				String timeS = TimeUtil.getISOFromMillisec(t*1000);
				
				System.out.println(t+" "+timeS+" "+d);
			}
			
			
			
			// lat lon are fixed, only varying time
//			basta[time,alt,lat,lon]
			
			
//			NCdump.printArray(arr, "array", System.out, null);

//			doVar(netcdfdataset, "BAssta", s);
//			doVar(netcdfdataset, "time", indMinTime + ":" + indMaxTime);

			// while (i < lon.getSize()) {
			// System.out.println("obs" + i + " " + lat + " " + lon);
			// i++;
			// }

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidRangeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void doVar(NetcdfDataset netcdfdataset, String varS,
			String limits) {
		System.out.println("\r---------\r\r");
		ucar.nc2.Variable var = netcdfdataset.findVariable(varS);
		System.out.println(var.getName() + " " + var.getSize());

		Array arr;
		try {
			arr = var.read(limits);
			NCdump.printArray(arr, "array", System.out, null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidRangeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main4(String[] args) {
		try {
			TimeUnit tu = new TimeUnit("days");
			tu.setValue(1);
			System.out.println(tu.getValueInSeconds());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main2(String args[]) throws Exception {

		final int NLVL = 2;
		final int NLAT = 6;
		final int NLON = 12;
		final int NumberOfRecords = 2;

		final float SAMPLE_PRESSURE = 900.0f;
		final float SAMPLE_TEMP = 9.0f;
		final float START_LAT = 25.0f;
		final float START_LON = -125.0f;

		// Create the file.
		String filename = "pres_temp_4D.nc";
		NetcdfFileWriteable dataFile = null;

		try {
			// Create new netcdf-3 file with the given filename
			dataFile = NetcdfFileWriteable.createNew(filename, false);

			// add dimensions where time dimension is unlimit
			Dimension lvlDim = dataFile.addDimension("level", NLVL);
			Dimension latDim = dataFile.addDimension("latitude", NLAT);
			Dimension lonDim = dataFile.addDimension("longitude", NLON);
			Dimension timeDim = dataFile.addDimension("time", 1000); // should
			// not be
			// need
			// second
			// argument

			ArrayList dims = null;

			// Define the coordinate variables.
			dataFile.addVariable("latitude", DataType.FLOAT,
					new Dimension[] { latDim });
			dataFile.addVariable("longitude", DataType.FLOAT,
					new Dimension[] { lonDim });

			// Define units attributes for data variables.
			dataFile.addVariableAttribute("latitude", "units", "degrees_north");
			dataFile.addVariableAttribute("longitude", "units", "degrees_east");

			// Define the netCDF variables for the pressure and temperature
			// data.
			dims = new ArrayList();
			dims.add(timeDim);
			dims.add(lvlDim);
			dims.add(latDim);
			dims.add(lonDim);
			dataFile.addVariable("pressure", DataType.FLOAT, dims);
			dataFile.addVariable("temperature", DataType.FLOAT, dims);

			// Define units attributes for data variables.
			dataFile.addVariableAttribute("pressure", "units", "hPa");
			dataFile.addVariableAttribute("temperature", "units", "celsius");

			// Create some pretend data. If this wasn't an example program, we
			// would have some real data to write for example, model output.
			ArrayFloat.D1 lats = new ArrayFloat.D1(latDim.getLength());
			ArrayFloat.D1 lons = new ArrayFloat.D1(lonDim.getLength());
			int i, j;

			for (i = 0; i < latDim.getLength(); i++) {
				lats.set(i, START_LAT + 5.f * i);
			}

			for (j = 0; j < lonDim.getLength(); j++) {
				lons.set(j, START_LON + 5.f * j);
			}

			// Create the pretend data. This will write our surface pressure and
			// surface temperature data.
			ArrayFloat.D4 dataTemp = new ArrayFloat.D4(NumberOfRecords, lvlDim
					.getLength(), latDim.getLength(), lonDim.getLength());
			ArrayFloat.D4 dataPres = new ArrayFloat.D4(NumberOfRecords, lvlDim
					.getLength(), latDim.getLength(), lonDim.getLength());

			for (int record = 0; record < NumberOfRecords; record++) {
				i = 0;
				for (int lvl = 0; lvl < NLVL; lvl++)
					for (int lat = 0; lat < NLAT; lat++)
						for (int lon = 0; lon < NLON; lon++) {
							dataPres.set(record, lvl, lat, lon, SAMPLE_PRESSURE
									+ i);
							dataTemp.set(record, lvl, lat, lon, SAMPLE_TEMP
									+ i++);
						}
			}

			// Create the file. At this point the (empty) file will be written
			// to disk
			dataFile.create();

			// A newly created Java integer array to be initialized to zeros.
			int[] origin = new int[4];

			dataFile.write("latitude", lats);
			dataFile.write("longitude", lons);
			dataFile.write("pressure", origin, dataPres);
			dataFile.write("temperature", origin, dataTemp);
			dataFile.close();

		} catch (IOException e) {
			e.printStackTrace(System.err);
		} catch (InvalidRangeException e) {
			e.printStackTrace(System.err);
		} finally {
			// if (dataFile != null) {
			// try {
			// dataFile.close();
			// } catch (IOException ioe) {
			// ioe.printStackTrace();
			// }
			// }
		}
		System.out.println("*** SUCCESS writing example file " + filename);
	}

}
