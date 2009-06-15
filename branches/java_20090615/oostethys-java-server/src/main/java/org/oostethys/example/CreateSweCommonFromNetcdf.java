package org.oostethys.example;

import java.util.ArrayList;
import java.util.List;

import org.oostethys.model.Observation;
import org.oostethys.model.VariableQuantity;
import org.oostethys.model.VariablesConfig;
import org.oostethys.model.impl.ObservationNetcdf;
import org.oostethys.model.impl.VariableQuantityImpl;
import org.oostethys.voc.Voc;

public class CreateSweCommonFromNetcdf {

	public static void main(String[] args) {

		// create url of netcdf, opendap etc...
		String url = "http://dods.mbari.org/cgi-bin/nph-nc/data/ssdsdata/deployments/m2/current_netCDFs/ctd0000.nc";

		// create a variables to parse with information about units and the
		// mapping to URIs
		VariablesConfig varConfig = createVariblesConfigM2();

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

	public static VariablesConfig createVariblesConfigM2() {

		// the Voc class contains constants from MMI ontology, containing
		// concepts mostly used in
		// ocean observing systems
		List<VariableQuantity> variablesConfig = new ArrayList<VariableQuantity>();

		variablesConfig.add(new VariableQuantityImpl("esecs", Voc.time,
				"seconds since 1970-01-01 00:00:00", Voc.seconds_since_Epoch,
				true));

		variablesConfig.add(new VariableQuantityImpl("Latitude", Voc.latitude,
				"degrees_north", Voc.degree, true));

		variablesConfig.add(new VariableQuantityImpl("Longitude",
				Voc.longitude, "degrees_east", Voc.degree, true));

		variablesConfig.add(new VariableQuantityImpl("NominalDepth", Voc.depth,
				"m", Voc.meter, false));

		variablesConfig.add(new VariableQuantityImpl("Salinity", Voc.salinity,
				"", "1", false));

		variablesConfig.add(new VariableQuantityImpl("Conductivity",
				Voc.conductivity, "S/m", Voc.siemens_per_metre, false));

		variablesConfig.add(new VariableQuantityImpl("Temperature",
				Voc.sea_water_temperature, "deg C", Voc.celsius, false));

		VariablesConfig config = new VariablesConfig();
		config.setVariables(variablesConfig);

		return config;

	}

}
