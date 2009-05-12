package org.oostethys.sos.reader;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.*;
import org.joda.time.DateTime;



public class SOSObservationSweCommonReader {

	private PlotData plotData;
	int indexTime;
	int indexVariable;
	Document doc;
	private String asciiString;
	private String tokenSeparator;
	private String blockSeparator;
	private String decimalSeparator;
	private URL URLGetObservation;

	public SOSObservationSweCommonReader() {
		plotData = new PlotData();

	}

	public void process(String varURI) {
		plotData.setVariableURI(varURI);

		doIndexes(varURI);
		doASCIIString();
		doDelimeters();
		doArrayTime();
		doArrayValues();
		doName();

	}

	private String getValueAttributeXPath(String pathElement, String attribute) {
		String att = null;
		try {
			XPath xpath = XPath.newInstance(pathElement);
			Element element = (Element) xpath.selectNodes(doc).get(0);
			att = element.getAttribute(attribute).getValue();

		} catch (JDOMException e) {

			e.printStackTrace();
		}

		return att;
	}

	private void doName() {
		int index = indexVariable + 1;
		String fieldPath = "//om:ObservationCollection/om:member/om:Observation/om:result/swe:DataArray/swe:elementType/swe:DataRecord/swe:field["
				+ index + "]";
		String varShortNamePath = fieldPath + "/@name";
		String uomPath = fieldPath + "/*[1]/swe:uom/@code";
		String name = null;

		try {
			XPath xpath = XPath.newInstance(varShortNamePath);
			String varShortName = ((Attribute) xpath.selectNodes(doc).get(0))
					.getValue();
			xpath = XPath.newInstance(uomPath);
			String uom = ((Attribute) xpath.selectNodes(doc).get(0)).getValue();
			name = varShortName + "(" + uom + ")";
			plotData.setTitle(name);
			plotData.setUnitsShort(uom);

		} catch (JDOMException e) {

			e.printStackTrace();
		}

	}

	private void doDelimeters() {
		String pathS = "om:ObservationCollection/om:member/om:Observation/om:result/swe:DataArray/swe:encoding/swe:TextBlock";
		try {
			XPath path = XPath.newInstance(pathS);
			Element element = (Element) path.selectNodes(doc).get(0);
			tokenSeparator = element.getAttribute("tokenSeparator").getValue();
			blockSeparator = element.getAttribute("blockSeparator").getValue();
			decimalSeparator = element.getAttribute("decimalSeparator")
					.getValue();

		} catch (JDOMException e) {

			e.printStackTrace();
		}

	}

	private void doIndexes(String varURI) {
		try {
			String pathDefinitions = "//om:ObservationCollection/om:member/om:Observation/om:result/swe:DataArray/swe:elementType/swe:DataRecord/swe:field/*[1]/@definition";
			XPath path = XPath.newInstance(pathDefinitions);
			List definitions = path.selectNodes(doc);

			int i = 0;

			// set indexes
			Iterator iter = definitions.iterator();
			while (iter.hasNext()) {
				Attribute att = (Attribute) iter.next();
				String val = att.getValue();
				if (val.contains("iso8601")) {
					indexTime = i;

				}
				if (val.equals(varURI)) {
					indexVariable = i;

				}

				i++;
			}

		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void doArrayTime() {

		List timeList = new ArrayList();
		StringTokenizer st = new StringTokenizer(asciiString, blockSeparator);
		while (st.hasMoreElements()) {
			String blockRecord = st.nextToken();
			StringTokenizer fieldTokenizer = new StringTokenizer(blockRecord,
					tokenSeparator);
			String field = null;
			for (int i = 0; i <= indexTime; i++) {
				field = fieldTokenizer.nextToken();
			}

			DateTime datetime = new DateTime(field);
			timeList.add(datetime.getMillis());

		}

		long[] times = new long[timeList.size()];
		int i = 0;
		for (Iterator iterator = timeList.iterator(); iterator.hasNext();) {
			long object = (Long) iterator.next();
			times[i] = object;
			i++;

		}
		plotData.setTimes(times);

	}

	public void doArrayValues() {

		List varList = new ArrayList();
		StringTokenizer st = new StringTokenizer(asciiString, blockSeparator);
		while (st.hasMoreElements()) {
			String blockRecord = st.nextToken();
			StringTokenizer fieldTokenizer = new StringTokenizer(blockRecord,
					tokenSeparator);
			String field = null;
			for (int i = 0; i <= indexVariable; i++) {
				field = fieldTokenizer.nextToken();
			}

			varList.add(Double.parseDouble(field));

		}

		double[] values = new double[varList.size()];
		int i = 0;
		for (Iterator iterator = varList.iterator(); iterator.hasNext();) {
			double object = (Double) iterator.next();
			values[i] = object;
			i++;

		}
		plotData.setValues(values);

	}

	public void setASCIIString(String s) {
		asciiString = s;

	}

	private void doASCIIString() {
		String pathS = "om:ObservationCollection/om:member/om:Observation/om:result/swe:DataArray/swe:values";
		asciiString = null;
		try {
			XPath path = XPath.newInstance(pathS);
			Element element = (Element) path.selectNodes(doc).get(0);
			asciiString = element.getText();
			System.out.println(asciiString);
		} catch (JDOMException e) {

			e.printStackTrace();
		}

	}

	public PlotData getPlotData() {
		return plotData;
	}

	public void setPlotData(PlotData plotData) {
		this.plotData = plotData;
	}

	public int getIndexTime() {
		return indexTime;
	}

	public void setIndexTime(int indexTime) {
		this.indexTime = indexTime;
	}

	public int getIndexVariable() {
		return indexVariable;
	}

	public void setIndexVariable(int indexVariable) {
		this.indexVariable = indexVariable;
	}

	public String getAsciiString() {
		return asciiString;
	}

	public void setAsciiString(String asciiString) {
		this.asciiString = asciiString;
	}

	public String getTokenSeparator() {
		return tokenSeparator;
	}

	public void setTokenSeparator(String tokenSeparator) {
		this.tokenSeparator = tokenSeparator;
	}

	public String getBlockSeparator() {
		return blockSeparator;
	}

	public void setBlockSeparator(String blockSeparator) {
		this.blockSeparator = blockSeparator;
	}

	public String getDecimalSeparator() {
		return decimalSeparator;
	}

	public void setDecimalSeparator(String decimalSeparator) {
		this.decimalSeparator = decimalSeparator;
	}

	public URL getURLGetObservation() {
		return URLGetObservation;
	}

	public void setURLGetObservation(URL getObservation) {
		SAXBuilder builder = new SAXBuilder();
		URLGetObservation = getObservation;
		try {
			doc = builder.build(URLGetObservation);
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
