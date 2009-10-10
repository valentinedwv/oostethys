package org.oostethys.smlmor.gwt.server;

import java.util.List;
import java.util.Map;

import org.oostethys.schemas.x010.oostethys.Contact;
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
import org.oostethys.smlmor.gwt.client.rpc.model.AttrGroupValues;
import org.oostethys.smlmor.gwt.client.rpc.model.AttributeModel;
import org.oostethys.smlmor.gwt.client.rpc.model.AttrGroupModel;
import org.oostethys.smlmor.gwt.client.rpc.model.MetadataModel;
import org.oostethys.smlmor.gwt.client.rpc.model.MetadataValues;
import org.oostethys.smlmor.gwt.client.rpc.model.OostethysModel;
import org.oostethys.smlmor.gwt.client.rpc.model.OostethysValues;
import org.oostethys.smlmor.gwt.client.rpc.model.SystemModel;
import org.oostethys.smlmor.gwt.client.rpc.model.SystemValues;

/**
 * 
 * @author Carlos Rueda
 */
public class SOostethys2Doc {

	static OostethysDocument getoostethysDocument(
			OostethysModel oostModel,
			OostethysValues oostValues
	) {
		OostethysDocument oostethysDocument = OostethysDocument.Factory.newInstance();
		Oostethys oostethys = oostethysDocument.addNewOostethys();

		oostethys.setWebServerURL(oostValues.getWebServerUrl());
		setOostethysValues(oostModel.getServiceContact(), 
				oostValues.getServiceContactValues(), oostethys);
		
		AttrGroupModel sserviceContact = oostModel.getServiceContact();
		AttrGroupValues serviceContactValues = oostValues.getServiceContactValues();
		if ( serviceContactValues != null ) {
			ServiceContact serviceContact = oostethys.addNewServiceContact();
			setContactValues(sserviceContact, serviceContactValues, serviceContact);
		}
		
		
		SystemModel systemModel = oostModel.getSystemModel();
		MetadataModel metadataModel = systemModel.getMetadataModel();
		
		List<SystemValues> systemValuesList = oostValues.getSystemValuesList();
		
		if ( systemValuesList != null ) {
			Components components = oostethys.addNewComponents();
			
			for ( SystemValues systemValues : systemValuesList ) {
	
				org.oostethys.schemas.x010.oostethys.SystemDocument.System system = components.addNewSystem();
				
				
				MetadataValues metadataValues = systemValues.getMetadataValues();
	
				if ( metadataValues != null ) {
					Metadata metadata = system.addNewMetadata();
					SystemContacts systemContacts = metadata.addNewSystemContacts();
					SystemContact systemContact = systemContacts.addNewSystemContact();
					
					AttrGroupValues systemContactValues = metadataValues.getSystemContactValues();
					if ( systemContactValues != null ) {
						setContactValues(metadataModel.getSystemContact(), systemContactValues, systemContact);
					}
					
					AttrGroupValues systemMetadataValues = metadataValues.getSystemMetadataValues();
					if ( systemMetadataValues != null ) {
						setSystemMetadataValues(metadataModel.getSystemMetadata(), systemMetadataValues, metadata);
					}
				}
	
	
				Output output = system.addNewOutput();
				Variables vars = output.addNewVariables();
				Variable temp = vars.addNewVariable();
				temp.setName("Temperature");
				temp.setUom("F");
				temp.setUri("urn:xyz:temp");
	
			}
		}
		return oostethysDocument;
	}

	
	
	private static void setOostethysValues(AttrGroupModel contactDefNode, AttrGroupValues attrGroupValues, Oostethys oostethys) {
		
		if ( attrGroupValues == null ) {
			return;
		}
		
		List<AttributeModel> attributeModels = contactDefNode.getAttributes();
		Map<String, String> values = attrGroupValues.getValues();

		for ( AttributeModel attributeModel : attributeModels ) {
			String beanAttributeName = attributeModel.getBeanAttributeName();
			String value = values.get(beanAttributeName);
			if ( value != null && value.trim().length() > 0 ) {
				
				// TODO some reflection can simplify the following, but this is a quick test
				
				if ( beanAttributeName.equals("webServerlURL") ) {
					oostethys.setWebServerURL(value);
				}
			}
		}
	}
	
	
	private static void setContactValues(AttrGroupModel contactDefNode, AttrGroupValues contactValues, Contact contact) {
		
		List<AttributeModel> attributeModels = contactDefNode.getAttributes();
		Map<String, String> values = contactValues.getValues();

		for ( AttributeModel attributeModel : attributeModels ) {
			String beanAttributeName = attributeModel.getBeanAttributeName();
			String value = values.get(beanAttributeName);
			if ( value != null && value.trim().length() > 0 ) {
				
				// TODO some reflection can simplify the following, but this is a quick test
				
				if ( beanAttributeName.equals("individualEmail") ) {
					contact.setIndividualEmail(value);
				}
				else if ( beanAttributeName.equals("individualName") ) {
					contact.setIndividualName(value);
				}
				else if ( beanAttributeName.equals("shortNameOrganization") ) {
					contact.setShortNameOrganization(value);
				}
				else if ( beanAttributeName.equals("urlOrganization") ) {
					contact.setUrlOrganization(value);
				}
			}
		}
	}

	private static void setSystemMetadataValues(AttrGroupModel attrGroupModel, AttrGroupValues attrGroupValues, Metadata metadata) {
		
		List<AttributeModel> attributeModels = attrGroupModel.getAttributes();
		Map<String, String> values = attrGroupValues.getValues();

		for ( AttributeModel attributeModel : attributeModels ) {
			String beanAttributeName = attributeModel.getBeanAttributeName();
			String value = values.get(beanAttributeName);
			if ( value != null && value.trim().length() > 0 ) {
				
				// TODO some reflection can simplify the following, but this is a quick test

				if ( beanAttributeName.equals("systemType") ) {
					metadata.setSystemType(value);
				}
				else if ( beanAttributeName.equals("systemShortName") ) {
					metadata.setSystemShortName(value);
				}
				else if ( beanAttributeName.equals("systemLongName") ) {
					metadata.setSytemLongName(value);
				}
				else if ( beanAttributeName.equals("systemIdentfier") ) {
					metadata.setSystemIdentifier(value);
				}
			}
		}
	}

}
