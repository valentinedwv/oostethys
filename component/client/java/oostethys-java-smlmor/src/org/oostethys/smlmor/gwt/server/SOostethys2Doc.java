package org.oostethys.smlmor.gwt.server;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.oostethys.smlmor.gwt.client.rpc.model.BasicModels;
import org.oostethys.smlmor.gwt.client.rpc.model.MetadataValues;
import org.oostethys.smlmor.gwt.client.rpc.model.OostethysValues;
import org.oostethys.smlmor.gwt.client.rpc.model.SystemValues;

/**
 * 
 * @author Carlos Rueda
 */
public class SOostethys2Doc {
	
	private final Log log = LogFactory.getLog(SOostethys2Doc.class);
	
	private BasicModels basicModels;
	private OostethysValues oostValues;
	
	private StringWriter sw = new StringWriter();
	private PrintWriter pw = new PrintWriter(sw);

	private OostethysDocument oostethysDocument = OostethysDocument.Factory.newInstance();
	private Oostethys oostethys = oostethysDocument.addNewOostethys();

	
	SOostethys2Doc(BasicModels basicModels, OostethysValues oostValues) {
		super();
		this.basicModels = basicModels;
		this.oostValues = oostValues;
	}

	OostethysDocument getOostethysDocument() {
		int indent = 0;
		oostethys.setWebServerURL(oostValues.getWebServerUrl());
		setOostethysValues(indent, basicModels.getServiceContact(), 
				oostValues.getServiceContactValues(), oostethys);
		
		AttrGroupModel sserviceContact = basicModels.getServiceContact();
		AttrGroupValues serviceContactValues = oostValues.getServiceContactValues();
		if ( serviceContactValues != null ) {
			ServiceContact serviceContact = oostethys.addNewServiceContact();
			printString(indent, "Service contact:");
			setValues(indent+1, sserviceContact, serviceContactValues, serviceContact);
		}
		
		
		List<SystemValues> systemValuesList = oostValues.getSystemValuesList();
		printString(indent, "Systems:");
		traverseSystemValuesList(indent+1, systemValuesList);
		
		if ( log.isDebugEnabled() ) {
			log.debug(sw);
		}
		System.out.println(sw);
		return oostethysDocument;
	}
	
	String getDump() {
		return sw.toString();
	}
	
	private void traverseSystemValuesList(int indent, List<SystemValues> systemValuesList) {
		Components components = oostethys.addNewComponents();

		int ii = 1;
		for ( SystemValues systemValues : systemValuesList ) {

			printString(indent, "System " + (ii++) + ":");
			
			org.oostethys.schemas.x010.oostethys.SystemDocument.System system = components.addNewSystem();


			MetadataValues metadataValues = systemValues.getMetadataValues();

			if ( metadataValues != null ) {
				Metadata metadata = system.addNewMetadata();
				SystemContacts systemContacts = metadata.addNewSystemContacts();
				SystemContact systemContact = systemContacts.addNewSystemContact();

				AttrGroupValues systemContactValues = metadataValues.getSystemContactValues();
				if ( systemContactValues != null ) {
					printString(indent+1, "System contact:");
					setValues(indent+2, basicModels.getSystemContact(), systemContactValues, systemContact);
				}

				AttrGroupValues systemMetadataValues = metadataValues.getSystemMetadataValues();
				if ( systemMetadataValues != null ) {
					printString(indent+1, "System metadata:");
					setValues(indent+2, basicModels.getSystemMetadata(), systemMetadataValues, metadata);
				}
			}

			// choice: variables or components:
			List<AttrGroupValues> outputValuesList = systemValues.getOutputValuesList();
			List<SystemValues> subsystemValuesList = systemValues.getSystemValuesList();

			if ( outputValuesList != null && outputValuesList.size() > 0 ) {
				// variables (ie, output) 

				Output output = system.addNewOutput();
				Variables vars = output.addNewVariables();

				printString(indent+1, "Variables:");
				int jj = 1;
				for ( AttrGroupValues attrGroupValues : outputValuesList ) {
					printString(indent+2, "Variable " + (jj++) + ":");
					Variable variable = vars.addNewVariable();
					setValues(indent+3, basicModels.getOutput(), attrGroupValues, variable);
				}

			}
			else if ( subsystemValuesList != null && subsystemValuesList.size() > 0 ) {
				// components (ie, systems)
				printString(indent+1, "Subsystems:");
				traverseSystemValuesList(indent+2, subsystemValuesList);
			}
		}
	}

	
	
	private void setOostethysValues(int indent, AttrGroupModel contactDefNode, AttrGroupValues attrGroupValues, Oostethys oostethys) {
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
				else {
					continue;
				}
				printString(indent, beanAttributeName+ " : " +value);
			}
		}
	}
	
	
	private void printString(int indent, String str) {
		// +1 to avoid zero width
		String format = "%" +(4*(indent+1))+ "s%s%n";
		pw.printf(format , "", str);
	}

	private void setValues(int indent, AttrGroupModel attrGroupModel, AttrGroupValues attrGroupValues, Object obj) {
		
		List<AttributeModel> attributeModels = attrGroupModel.getAttributes();
		Map<String, String> values = attrGroupValues.getValues();

		for ( AttributeModel attributeModel : attributeModels ) {
			String beanAttributeName = attributeModel.getBeanAttributeName();
			String value = values.get(beanAttributeName);
			if ( value != null && value.trim().length() > 0 ) {
				try {
					assignValue(obj, beanAttributeName, value);
					printString(indent, beanAttributeName+ " : " +value);
				}
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	
	/**
	 * return true iff the assignment was carried out.
	 * @param bean
	 * @param beanAttributeName
	 * @param value
	 * @return true iff the assignment was carried out.
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private boolean assignValue(Object bean, String beanAttributeName, String value) throws Exception {
		
		String methodName = getMethodName(beanAttributeName);
		
		Class<? extends Object> clazz = bean.getClass();
		
		// try the following argument types until we find the method to be applied:
		Object[] argClasses = { String.class, Boolean.class };
		
		for (Object object : argClasses) {
			Class<? extends Object> argClass = (Class<? extends Object>) object;
			try {
				Method method = clazz.getMethod(methodName, argClass);
				method.invoke(bean, value);
				return true;
			}
			catch (NoSuchMethodException e) {
				// continue
			}
		}
		return false;
	}

	private String getMethodName(String beanAttributeName) {
		char first = Character.toUpperCase(beanAttributeName.charAt(0));
		String methodName = "set" + first + beanAttributeName.substring(1);
		return methodName;
	}

}
