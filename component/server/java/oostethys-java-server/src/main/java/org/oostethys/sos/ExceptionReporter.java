package org.oostethys.sos;

import java.io.IOException;
import java.io.StringWriter;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

public class ExceptionReporter {

	private static final String NAMESPACE_OWS = "http://www.opengis.net/ows/1.1";
	public static String OperationNotSupported = "OperationNotSupported";
	public static String MissingParameterValue = "MissingParameterValue";
	public static String InvalidParameterValue = "InvalidParameterValue";
	public static String ResourceNotFound = "ResourceNotFound";
	public static String NoApplicableCode = "NoApplicableCode";
	
	public String create(String exceptionCode, String locator,
			String exceptionText) {
		Document doc = new Document();

		Element root = new Element("ExceptionReport", NAMESPACE_OWS);
		root.setAttribute("version", "1.0.0");
		Element exception = new Element("Exception", NAMESPACE_OWS);
		exception.setAttribute("exceptionCode", exceptionCode);
		root.addContent(exception);

		if (locator != null) {
			exception.setAttribute("locator", locator);
		}

		if (exceptionCode != null) {
			Element exceptionTextElement = new Element("ExceptionText", NAMESPACE_OWS);
			exceptionTextElement.setText(exceptionText);
			exception.addContent(exceptionTextElement);
		}

		doc.setRootElement(root);

		return getDocumentInXML(doc);
	}

	private String getDocumentInXML(Document doc) {

		XMLOutputter outputter = new XMLOutputter();

		StringWriter sw = new StringWriter();
		try {
			outputter.output(doc, sw);

		} catch (IOException e) {

			e.printStackTrace();
		}
		String xml = sw.getBuffer().toString();
		return xml;

	}

}
