package org.oostethys.smlmor.gwt.server;

import org.oostethys.smlmor.gwt.client.rpc.model.AttrGroupModel;
import org.oostethys.smlmor.gwt.client.rpc.model.AttributeModel;
import org.oostethys.smlmor.gwt.client.rpc.model.BasicModels;

class ModelsCreator {

	static BasicModels createModels() {
		BasicModels basicModels = new BasicModels();

		
		basicModels.setSystemContact(createContactModel("System contact", "Contact"));
		basicModels.setServiceContact(createContactModel("Service contact", "Service contact"));
		
		basicModels.setSystemMetadata(createSystemMetadataModel("Metadata", "Metadata"));
		
		basicModels.setOutput(createOutputModel());
		
		return basicModels;
	}
	
	
	
	private static AttrGroupModel createContactModel(String name, String htmlInfo) {
		AttrGroupModel contact = new AttrGroupModel(name, htmlInfo);
		contact.addAttributes(
				new AttributeModel("urlOrganization", "Organization URL", "http://rbnXYZ.org"),
				new AttributeModel("longNameOrganization", "Organization long name", "r b and n"),
				new AttributeModel("shortNameOrganization", "Organization short name", "RBN"),
				new AttributeModel("individualName", "Individual name", "John Smith"),
				new AttributeModel("individualEmail", "Individual email", "smith@rbn.com")
		);
		return contact;
	}

	
	private static AttrGroupModel createSystemMetadataModel(String name, String htmlInfo) {
		AttrGroupModel contact = new AttrGroupModel(name, htmlInfo);
		contact.addAttributes(
				new AttributeModel("systemType", "type", "urn:xxx:sytem:type"),
				new AttributeModel("systemShortName", "Short name", "seabird-ctd"),
				new AttributeModel("sytemLongName", "Long name", "Seabird XXX CTD Sensor"),
				new AttributeModel("systemIdentifier", "Identifier", "id1:idx2")
		);
		return contact;
	}

	private static AttrGroupModel createOutputModel() {
		AttrGroupModel contact = new AttrGroupModel("Output", null);
		contact.addAttributes(
				new AttributeModel("name", "Name", "Temperature"),
				new AttributeModel("uri", "URI", "urn:xyz:temp"),
				new AttributeModel("uom", "UOM", "F"),
				new AttributeModel("isCoordinate", "Is coordinate", "false"),
				new AttributeModel("isTime", "Is time", "false"),
				new AttributeModel("referenceFrame", "Reference frame", "")
		);
		return contact;
	}


}
