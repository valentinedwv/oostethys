package org.oostethys.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OOSTethys {
	
	public static final List<Observation> observations = new ArrayList<Observation>();

	public static List<Observation> getObservations() {
		return observations;
	}
	
	public void updateObservation(Observation obs){
		Observation observation = findObservationByName(obs.getName());
		observations.remove(observation);
		observation=null;
		observations.add(obs);
	
	}
	
	public Observation findObservationByName(String name){
		for (Iterator iterator = observations.iterator(); iterator.hasNext();) {
			Observation obs = (Observation) iterator.next();
			if (obs.getName().equals(name)){
				return obs;
			}
			
		}
		return null;
	}
	
	
	

}
