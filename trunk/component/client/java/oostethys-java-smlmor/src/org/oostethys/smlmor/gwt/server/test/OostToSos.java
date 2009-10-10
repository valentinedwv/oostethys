package org.oostethys.smlmor.gwt.server.test;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.oostethys.schemas.x010.oostethys.OostethysDocument;
import org.oostethys.schemas.x010.oostethys.ComponentsDocument.Components;
import org.oostethys.schemas.x010.oostethys.MetadataDocument.Metadata;
import org.oostethys.schemas.x010.oostethys.OostethysDocument.Oostethys;
import org.oostethys.schemas.x010.oostethys.OutputDocument.Output;
import org.oostethys.schemas.x010.oostethys.ServiceContactDocument.ServiceContact;
import org.oostethys.schemas.x010.oostethys.SystemContactDocument.SystemContact;
import org.oostethys.schemas.x010.oostethys.SystemContactsDocument.SystemContacts;
import org.oostethys.schemas.x010.oostethys.VariableDocument.Variable;
import org.oostethys.schemas.x010.oostethys.VariablesDocument.Variables;

/**
 * Example that shows easy sensorML creation using the OOStethys model.  The only jar needed is oostethys-xmlbeans jar
 * 
 */
public class OostToSos {
	
	public static void main(String[] args) throws Exception {

		InputStream xslIS = Thread.currentThread().getContextClassLoader().getResourceAsStream("oostethys2describeSensor.xsl");
		
		
		OostToSos app = new OostToSos();

		// create an oostethys document
		OostethysDocument doc = app.getoostethysDocument();
		
		// create the stream to send the xml file
		Writer os = new StringWriter();
		
		// transform to SensorML
		app.getSensorML(doc, os, xslIS);
		
		System.out.println("RESULTING SML:\n" +os.toString());
		

	}

	public OostethysDocument getoostethysDocument() {

		OostethysDocument doc = OostethysDocument.Factory.newInstance();
		Oostethys oostethys = doc.addNewOostethys();

		oostethys.setWebServerURL("http://zzzz");
		
		// create service contact metadata

		ServiceContact serviceContact = oostethys.addNewServiceContact();

		serviceContact.setIndividualEmail("bermudez@sura.org");
		serviceContact.setIndividualName("Luis Bermudez");
		serviceContact.setShortNameOrganization("SURA");
		serviceContact.setUrlOrganization("http://sura.org");


		Components components = oostethys.addNewComponents();

		// create a new system
		org.oostethys.schemas.x010.oostethys.SystemDocument.System system = components
				.addNewSystem();
		Metadata metadata = system.addNewMetadata();
		SystemContacts systemContacts = metadata.addNewSystemContacts();

		// create contact for the system / probably the same as the service

		SystemContact systemContact = systemContacts.addNewSystemContact();
		systemContact.setIndividualEmail("smith@rbn.com");
		systemContact.setIndividualName("John Smith");
		systemContact.setShortNameOrganization("RBN");
		systemContact.setUrlOrganization("http://rbnXYZ.org");
	
		metadata.setSystemType("urn:xxx:sytem:type");
		metadata.setSystemShortName("seabird-ctd");
		metadata.setSytemLongName("Seabird XXX CTD Sensor");
		metadata.setSystemIdentifier("id1:idx2");
	

		Output output = system.addNewOutput();
		Variables vars = output.addNewVariables();
		Variable temp = vars.addNewVariable();
		temp.setName("Temperature");
		temp.setUom("F");
		temp.setUri("urn:xyz:temp");

		return doc;

		// 

	}

	public void getSensorML(OostethysDocument oostethysDocument,
			Writer outputStream, InputStream xslIS) {

	
		String xmlText = oostethysDocument.xmlText();

		transformXML(outputStream, xslIS, new StringReader(
				xmlText));
	}


	private void transformXML(Writer outputStream, InputStream xslIS,
			Reader xmlFileReader) {
		java.lang.System.setProperty("javax.xml.transform.TransformerFactory",
				"net.sf.saxon.TransformerFactoryImpl");

		TransformerFactory tfactory = TransformerFactory.newInstance();
		try {
			StreamSource xslSource = new StreamSource(xslIS);
			Transformer transformer = tfactory.newTransformer(xslSource);
			StreamSource xmlSource = new StreamSource(xmlFileReader);
			transformer.transform(xmlSource, new StreamResult(outputStream));
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
