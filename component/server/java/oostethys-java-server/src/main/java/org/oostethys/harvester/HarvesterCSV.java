package org.oostethys.harvester;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import com.infomata.data.CSVFormat;
import com.infomata.data.DataFile;

/**
 * Harvest records in CSV and stores them in a Result Object,
 * 
 * @author bermudez
 * 
 */
public class HarvesterCSV {
	private URI url = null; // where the CSV file is
	private int numberOfRowsInHeader = 0; // number of header before the values
	private String TIME_ISO = "TIME_ISO";
	private String TIME_MILLISEC = "TIME_MILLISEC";
	private String LAT = "LAT";
	private String LON = "LON";
	private String Z = "Z";
	

	public void harvest() {
		File file = new File(url);
		DataFile datafile = DataFile.createWriter(null, true);
		CSVFormat format = new CSVFormat();
		datafile.setDataFormat(format);
		try {
			datafile.open(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < numberOfRowsInHeader; i++) {
			try {
				datafile.next();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		processData(datafile);

	}

	private void processData(DataFile datafile) {
		
		
	}

}
