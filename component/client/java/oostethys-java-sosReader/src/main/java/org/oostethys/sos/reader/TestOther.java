package org.oostethys.sos.reader;

import java.net.MalformedURLException;
import java.net.URL;

public class TestOther {
	
	public static void main(String[] args) {
		SOSSimpleGeneralReader simpleReader = new SOSSimpleGeneralReader();
		simpleReader
				.processCapabilities("http://localhost:8080/oostethys/sos?VERSION=1.0.0&SERVICE=SOS&REQUEST=GetCapabilities");

		URL urlGetObservation;
		try {
			urlGetObservation = new URL(simpleReader.getObservationURL());
		
		SOSObservationSweCommonReader reader = new SOSObservationSweCommonReader();
		reader.setURLGetObservation(urlGetObservation);

		String var = simpleReader.getVariableURL();

		String varURI = var;
		reader.process(varURI);
		PlotData plotData = reader.getPlotData();
		String title = plotData.getTitle();
		int rows =plotData.getValues().length;
		System.out.println(title);
		
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
