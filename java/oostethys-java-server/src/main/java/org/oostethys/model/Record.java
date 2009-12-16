package org.oostethys.model;

import java.util.ArrayList;
import java.util.List;

public class Record {
	private double lat;
	private double lon;
	private double elevation;
	private long millisec;
	private List<String> values;

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public long getMillisec() {
		return millisec;
	}

	public void setMillisec(long millisec) {
		this.millisec = millisec;
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	public void addValue(String value) {
		if (values == null) {
			values = new ArrayList<String>();

		}
		values.add(value);
	}

	public void setElevation(double elevation) {
		this.elevation = elevation;
	}

	public double getElevation() {
		return elevation;
	}

}
