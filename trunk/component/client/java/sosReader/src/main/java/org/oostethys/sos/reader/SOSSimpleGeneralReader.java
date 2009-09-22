package org.oostethys.sos.reader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.jdom.Attribute;
import org.jdom.Document;

/**
 * @author bermudez Gets data for the first variable of the first observation
 *         offering
 * 
 */
public class SOSSimpleGeneralReader {

	private String offeringID;
	private String variableURL;
	private String capabilitiesURL;

	public String getOfferingID() {
		return offeringID;
	}

	public String getVariableURL() {
		return variableURL;
	}

	public Document createDoc() {
		SAXBuilder builder = new SAXBuilder();

		URL url_ = null;
		try {
			url_ = new URL(capabilitiesURL);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			return builder.build(url_);
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void setCapabilitiesURL(String getCapabilitiesURL) {
		this.capabilitiesURL = getCapabilitiesURL;

	}

	private void process() {
		Document doc = createDoc();
		String pathS = "/sos:Capabilities/sos:Contents[1]/sos:ObservationOfferingList[1]/sos:ObservationOffering[1]";
		String pathToId= pathS+"/@gml:id";
		XPath path;
		try {
			path = XPath.newInstance(pathToId);

			Attribute att = (Attribute) path.selectNodes(doc).get(0);
			
			if (att !=null) {
				String val = att.getValue();
				offeringID = val;
			}
			String pathObservedProperties = pathS + "/sos:observedProperty/@xlink:href";
			XPath pathObsProperties = XPath.newInstance(pathObservedProperties);
			List observedProperties = pathObsProperties.selectNodes(doc);
			int i = 0;

			// set indexes
			Iterator iter = observedProperties.iterator();
			while (iter.hasNext()) {
				Attribute att2 = (Attribute)iter.next();
			
				String val = att2.getValue();
				if (varIsNotDimension(val)) {
					variableURL = val;
				}

			}
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void processCapabilities(String getCapabilitiesURL) {
		setCapabilitiesURL(getCapabilitiesURL);
		process();

	}

	public String getObservationURL() {
		if (capabilitiesURL != null) {
			if (variableURL == null) {
				process();
			}
			String s = capabilitiesURL.replace("GetCapabilities",
					"GetObservation");
			s = s.concat("&offering=" + offeringID);
			return s;
		}
		return null;
	}

	public boolean varIsNotDimension(String value) {
		String val = value.toLowerCase();
		if (val.contains("lat"))
			return false;
		if (val.contains("lon"))
			return false;
		if (val.contains("time"))
			return false;
		if (val.contains("iso8601"))
			return false;
		return true;

	}

}
