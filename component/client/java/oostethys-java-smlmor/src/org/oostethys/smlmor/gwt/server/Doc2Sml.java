package org.oostethys.smlmor.gwt.server;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.oostethys.schemas.x010.oostethys.OostethysDocument;

/**
 * OostethysDocument to SensorML
 * 
 * @author Carlos Rueda
 */
public class Doc2Sml {
	
	static void getSensorML(OostethysDocument oostethysDocument, Writer outputStream) throws Exception {
		InputStream xslIS = Thread.currentThread().getContextClassLoader().getResourceAsStream("oostethys2describeSensor.xsl");
		
		String xmlText = oostethysDocument.xmlText();
		transformXML(outputStream, xslIS, new StringReader(xmlText));
	}

	private static void transformXML(Writer outputStream, InputStream xslIS,
			Reader xmlFileReader) throws Exception {
		java.lang.System.setProperty("javax.xml.transform.TransformerFactory",
				"net.sf.saxon.TransformerFactoryImpl");

		TransformerFactory tfactory = TransformerFactory.newInstance();
		StreamSource xslSource = new StreamSource(xslIS);
		Transformer transformer = tfactory.newTransformer(xslSource);
		StreamSource xmlSource = new StreamSource(xmlFileReader);
		transformer.transform(xmlSource, new StreamResult(outputStream));

	}

}
