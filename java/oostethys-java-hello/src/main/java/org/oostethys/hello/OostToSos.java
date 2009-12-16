package org.oostethys.hello;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;

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
	
	//change the location of the xslt fle
	private String xslt ="/Users/bermudez/Documents/workspace_maven/oostethys-java/oostethys-java-server/src/main/resources/xml/oostethys/0.1.0/xslt/oostethys2describeSensor.xsl"; 

	public static void main(String[] args) {

		OostToSos app = new OostToSos();

		// create an oostethys document
		OostethysDocument doc = app.getoostethysDocument();
		
		// create the stream to send the xml file
		OutputStream os = System.out;
		
		// transform to SensorML
		app.getSensorML(doc, os);
		

	}

	public OostethysDocument getoostethysDocument() {

		OostethysDocument doc = OostethysDocument.Factory.newInstance();
		Oostethys oostethys = doc.addNewOostethys();

		// create service contact metadata

		ServiceContact serviceContact = oostethys.addNewServiceContact();

		serviceContact.setIndividualEmail("bermudez@sura.org");
		serviceContact.setIndividualName("Luis Bermudez");
		serviceContact.setShortNameOrganization("SURA");
		serviceContact.setUrlOrganization("http://sura.org");

		oostethys.setWebServerURL("http://zzzz");

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
			OutputStream outputStream) {

	
		File xsltoostethys2sensorml = new File(xslt);
		String xmlText = oostethysDocument.xmlText();

		transformXML(outputStream, xsltoostethys2sensorml, new StringReader(
				xmlText));
	}


	private void transformXML(OutputStream outputStream, File xsltFile,
			Reader xmlFileReader) {
		java.lang.System.setProperty("javax.xml.transform.TransformerFactory",
				"net.sf.saxon.TransformerFactoryImpl");

		TransformerFactory tfactory = TransformerFactory.newInstance();
		InputStream xslIS;
		try {
			xslIS = new BufferedInputStream(new FileInputStream(xsltFile));

			StreamSource xslSource = new StreamSource(xslIS);
			Transformer transformer = tfactory.newTransformer(xslSource);
			StreamSource xmlSource = new StreamSource(xmlFileReader);
			transformer.transform(xmlSource, new StreamResult(outputStream));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
