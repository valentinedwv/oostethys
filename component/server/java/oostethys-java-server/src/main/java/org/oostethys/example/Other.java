package org.oostethys.example;

import java.util.ArrayList;
import java.util.List;

import org.oostethys.model.Observation;
import org.oostethys.model.VariableQuantity;
import org.oostethys.model.VariablesConfig;
import org.oostethys.model.impl.ObservationNetcdf;
import org.oostethys.model.impl.VariableQuantityImpl;
import org.oostethys.voc.Voc;

public class Other {

	public static void main(String[] args) {

		// create url of netcdf, opendap etc...
		String url = null;
//		url = "http://dods.mbari.org/cgi-bin/nph-nc/data/ssdsdata/deployments/m2/current_netCDFs/ctd0000.nc";
		//url = "http://dods.mbari.org/cgi-bin/nph-nc/data/ssdsdata/deployments/m2/current_netCDFs/adcp1353.nc.dods?time[0:1:3],depth[0:1:3],latitude[0:1:0],longitude[0:1:0],echo_intensity_beam1[0:1:8514][0:1:59][0:1:0][0:1:0],echo_intensity_beam2[0:1:8514][0:1:59][0:1:0][0:1:0],echo_intensity_beam3[0:1:8514][0:1:59][0:1:0][0:1:0],echo_intensity_beam4[0:1:8514][0:1:59][0:1:0][0:1:0],xdcr_temperature[0:1:8514][0:1:0][0:1:0][0:1:0]";
//		url ="file:///Users/bermudez/Documents/workspace31/org.oostethys/src/test/adcp1353.nc";
		
		
		// vertical profile ADCP
		url ="http://dods.mbari.org/cgi-bin/nph-nc/data/ssdsdata/deployments/m2/current_netCDFs/adcp1353.nc";
		
		VariablesConfig varConfig = createVariblesConfig();

		// create a new observation, which url, an identifier referrring to this observation and a variable configuration

		Observation obs = new ObservationNetcdf(url, "urn:org:oostethys#obs_1",
				varConfig);
		try {
			obs.process();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(obs.getLastKnownPositionEPSG());
		System.out.println(obs.getAsRecords());
		System.out.println(obs.getVariables());

	}

	public static VariablesConfig createVariblesConfig() {

		// the Voc class contains constants from MMI ontology, containing
		// concepts mostly used in
		// ocean observing systems
		List<VariableQuantity> variablesConfig = new ArrayList<VariableQuantity>();

		variablesConfig.add(new VariableQuantityImpl("time", Voc.time,
				"seconds since 1970-01-01 00:00:00", Voc.seconds_since_Epoch,
				true));

		variablesConfig.add(new VariableQuantityImpl("latitude", Voc.latitude,
				"degrees_north", Voc.degree, true));

		variablesConfig.add(new VariableQuantityImpl("longitude",
				Voc.longitude, "degrees_east", Voc.degree, true));

		variablesConfig.add(new VariableQuantityImpl("depth", Voc.depth,
				"m", Voc.meter, true));

		
		variablesConfig.add(new VariableQuantityImpl("echo_intensity_beam1",
				Voc.echo_intensity_beam_1, "Counts", Voc.counts_per_45db, false));

		variablesConfig.add(new VariableQuantityImpl("echo_intensity_beam2",
				Voc.echo_intensity_beam_2, "Counts", Voc.counts_per_45db, false));

		VariablesConfig config = new VariablesConfig();
		config.setVariables(variablesConfig);

		return config;

	}

}
