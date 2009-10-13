package org.oostethys.voc;

public interface Voc {
	
	//TODO check that these terms comply with OOSTethys 1.0
	
	//variable names
	public static  String latitude = "urn:ogc:phenomenon:latitude:wgs84";
	public static  String longitude = "urn:ogc:phenomenon:longitude:wgs84";
	public static String depth = "http://mmisw.org/ont/cf/parameter/depth";
	public static String echo_intensity_beam_1 = "http://mmisw.org/ont/mmi/parameter/echo_intensity_beam_1";
	public static String echo_intensity_beam_2 = "http://mmisw.org/ont/mmi/parameter/echo_intensity_beam_2";
	public static String sea_surface_height_above_geoid = "http://mmisw.org/ont/cf/parameter/sea_surface_height_above_geoid";
	
	public static String salinity = "http://mmisw.org/ont/cf/parameter/sea_water_salinity";
	public static String sea_water_temperature = "http://mmisw.org/ont/cf/parameter/sea_water_temperature";
	public static String conductivity = "http://mmisw.org/ont/cf/parameter/conductivity";
	public static String pressure = "http://mmisw.org/ont/cf/parameter/pressure";
	public static String time = "urn:ogc:phenomenon:time:iso8601";
	public static String bioluminesence = "http://mmisw.org/ont/cf/parameter/bioluminescent_photon_rate_in_sea_water";
	public static String northward_sea_water_velocity = "http://mmisw.org/ont/cf/parameter/northward_sea_water_velocity";
	public static String eastward_sea_water_velocity = "http://mmisw.org/ont/cf/parameter/eastward_sea_water_velocity";
	
	//units
	public static  String seconds_since_Epoch = "http://mmisw.org/ont/cf/parameter/seconds_since_Epoch";
	public static  String milliseconds_since_Epoch = "http://mmisw.org/ont/cf/parameter/milliseconds_since_Epoch";
	public static  String ISO19118Time = "http://mmisw.org/ont/cf/parameter/iso_19118_time";
	
	public static String degree = "urn:ogc:unit:degree";
	public static String psu = "urn:mmi.def:units#psu";
	public static String celsius = "urn:mmi.def:units#celsius";
	public static String  meter = "urn:ogc:unit:meter";
	public static String  decibars = "urn:ogc:unit:decibars";
	public static String  siemens_per_metre = "urn:mmi.def:units#siemens_per_metre";
	public static String meter3_per_second = "http://mmisw.org/ont/cf/parameter/meterCubePerSecond"; //for biolum
	public static String cm_per_sec = "http://mmisw.org/ont/cf/parameter/cm_per_sec"; //for biolum
	public static String CF = "http://mmisw.org/ont/cf/parameter/";
	public static String counts_per_45db = "http://mmisw.org/ont/unit/counts_per_45db";
	
	
	//platform types
	public static String  mooring = "urn:mmi:org:platform#mooring";
	
	//data types
	public static String netcdf ="urn:mmi:org:filetype:netcdf";
	
	
	


}
