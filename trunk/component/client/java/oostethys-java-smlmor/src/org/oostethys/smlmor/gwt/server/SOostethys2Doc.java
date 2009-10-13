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
import org.oostethys.smlmor.gwt.client.rpc.model.AttrGroupModel;
import org.oostethys.smlmor.gwt.client.rpc.model.AttrGroupValues;
import org.oostethys.smlmor.gwt.client.rpc.model.AttributeModel;
import org.oostethys.smlmor.gwt.client.rpc.model.MetadataValues;
import org.oostethys.smlmor.gwt.client.rpc.model.BasicModels;
import org.oostethys.smlmor.gwt.client.rpc.model.OostethysValues;
import org.oostethys.smlmor.gwt.client.rpc.model.SystemValues;

/**
 * 
 * @author Carlos Rueda
 */
public class SOostethys2Doc {

	static OostethysDocument getoostethysDocument(
			BasicModels basicModels,
			OostethysValues oostValues
	) {
		OostethysDocument oostethysDocument = OostethysDocument.Factory.newInstance();
		Oostethys oostethys = oostethysDocument.addNewOostethys();

		oostethys.setWebServerURL(oostValues.getWebServerUrl());
		setOostethysValues(basicModels.getServiceContact(), 
				oostValues.getServiceContactValues(), oostethys);
		
		AttrGroupModel sserviceContact = basicModels.getServiceContact();
		AttrGroupValues serviceContactValues = oostValues.getServiceContactValues();
		if ( serviceContactValues != null ) {
			ServiceContact serviceContact = oostethys.addNewServiceContact();
			setContactValues(sserviceContact, serviceContactValues, serviceContact);
		}
		
		
//		SystemModel systemModel = models.getSystemModel();
//		MetadataModel metadataModel = systemModel.getMetadataModel();
		
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
						setContactValues(basicModels.getSystemContact(), systemContactValues, systemContact);
					}
					
					AttrGroupValues systemMetadataValues = metadataValues.getSystemMetadataValues();
					if ( systemMetadataValues != null ) {
						setSystemMetadataValues(basicModels.getSystemMetadata(), systemMetadataValues, metadata);
					}
				}
	
				// choice: variables or components:
				List<AttrGroupValues> outputValuesList = systemValues.getOutputValuesList();
				List<SystemValues> subsystemValuesList = systemValues.getSystemValuesList();
				
				if ( outputValuesList != null && outputValuesList.size() > 0 ) {
					// variables (ie, output) 
					
					Output output = system.addNewOutput();
					Variables vars = output.addNewVariables();
					
					for ( AttrGroupValues attrGroupValues : outputValuesList ) {
					
						Variable variable = vars.addNewVariable();
						setVariableValues(basicModels.getOutput(), attrGroupValues, variable);
					}
		
				}
				else if ( subsystemValuesList != null && subsystemValuesList.size() > 0 ) {
					// components (ie, systems)
					
//					TODO components
				}
	
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

	
	
	private static void setVariableValues(AttrGroupModel attrGroup, AttrGroupValues attrGroupValues, Variable variable) {
		
		List<AttributeModel> attributeModels = attrGroup.getAttributes();
		Map<String, String> values = attrGroupValues.getValues();

		for ( AttributeModel attributeModel : attributeModels ) {
			String beanAttributeName = attributeModel.getBeanAttributeName();
			String value = values.get(beanAttributeName);
			if ( value != null && value.trim().length() > 0 ) {
				
				// TODO some reflection can simplify the following, but this is a quick test
				
				if ( beanAttributeName.equals("name") ) {
					variable.setName(value);
				}
				else if ( beanAttributeName.equals("uom") ) {
					variable.setUom(value);
				}
				else if ( beanAttributeName.equals("uri") ) {
					variable.setUri(value);
				}
				else if ( beanAttributeName.equals("isCoordinate") ) {
					variable.setIsCoordinate(Boolean.valueOf(value));
				}
				else if ( beanAttributeName.equals("isTime") ) {
					variable.setIsTime(Boolean.valueOf(value));
				}
				else if ( beanAttributeName.equals("referenceFrame") ) {
					variable.setReferenceFrame(value);
				}
			}
		}
	}

}
