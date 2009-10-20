package org.oostethys.sos.reader;
public class PlotData {

	private long[] times;
	private double[] values;
	private String title;
	private String unitsShort;
	private String unitsURI;
	private String variableURI;

	public long[] getTimes() {
		return times;
	}

	public void setTimes(long[] times) {
		this.times = times;
	}

	public double[] getValues() {
		return values;
	}

	public void setValues(double[] values) {
		this.values = values;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUnitsShort() {
		return unitsShort;
	}

	public void setUnitsShort(String unitsShort) {
		this.unitsShort = unitsShort;
	}

	public String getUnitsURI() {
		return unitsURI;
	}

	public void setUnitsURI(String unitsURI) {
		this.unitsURI = unitsURI;
	}

	public String getVariableURI() {
		return variableURI;
	}

	public void setVariableURI(String variableURI) {
		this.variableURI = variableURI;
	}

}
