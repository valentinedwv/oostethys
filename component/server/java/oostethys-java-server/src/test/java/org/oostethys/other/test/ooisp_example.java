package org.oostethys.other.test;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import ucar.nc2.Attribute;
import ucar.nc2.Variable;
import ucar.nc2.dataset.NetcdfDataset;

public class ooisp_example {

	public static void main(String[] args) {
		String file = "http://dods.mbari.org/cgi-bin/nph-nc/data/ssdsdata/deployments/m2/current_netCDFs/ctd0010.nc";
		NetcdfDataset netcdfdataset;
		try {
			netcdfdataset = NetcdfDataset.openDataset(file);
			
			
		

			List<Variable> vars = netcdfdataset.getVariables();
			for (Iterator iterator = vars.iterator(); iterator.hasNext();) {
				ucar.nc2.Variable var = (ucar.nc2.Variable) iterator.next();
				
				Attribute standardNameAttribute  = var.findAttribute("standard_name");
			
				String value="";
				if (standardNameAttribute !=null){
					 value = standardNameAttribute.getStringValue();
				}
				
				System.out.println(var.getName()+" has stdr Name "+value);

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}

}
