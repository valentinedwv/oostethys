package org.oostethys.smlmor.gwt.client;

import java.util.ArrayList;
import java.util.List;

import org.oostethys.smlmor.gwt.client.SvContainer.IElement;
import org.oostethys.smlmor.gwt.client.rpc.model.AttrGroupModel;
import org.oostethys.smlmor.gwt.client.rpc.model.AttrGroupValues;
import org.oostethys.smlmor.gwt.client.rpc.model.AttributeModel;
import org.oostethys.smlmor.gwt.client.rpc.model.BasicModels;
import org.oostethys.smlmor.gwt.client.rpc.model.MetadataValues;
import org.oostethys.smlmor.gwt.client.rpc.model.OostethysValues;
import org.oostethys.smlmor.gwt.client.rpc.model.SystemValues;
import org.oostethys.smlmor.gwt.client.util.TLabel;
import org.oostethys.smlmor.gwt.client.util.Util;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/** 
 * Main controller: it creates the interface and maintains the
 * resulting OostethysValues
 * 
 * @author Carlos Rueda
 */
public class Controller {
	
	
	private  BasicModels basicModels;
	
	private VerticalPanel widget;
	
	private OostethysValues oostValues;
	
	
	Controller(BasicModels basicModels) {
		this.basicModels = basicModels;
	}
	
	public OostethysValues getOostValues() {
		if ( widget == null ) {
			create();
		}
		return oostValues;
	}

	public Widget getWidget() {
		if ( widget == null ) {
			create();
		}
		return widget;
	}
	
	private void create() {
		
		oostValues = new OostethysValues();
		widget = new VerticalPanel();
		
		if ( false ) {  // false= oostethys URL removed.
			ChangeListener cl = new ChangeListener() {
				public void onChange(Widget sender) {
					TextBoxBase tb = (TextBoxBase) sender;
					String value = tb.getText().trim();
					oostValues.setWebServerUrl(value);
					Main.log("onChange: setWebServerUrl <- \"" +value+ "\"");
				}
			};

			widget.add(createWidgetForAttributes(null, cl, true,
					new AttributeModel("webServiceURL", "OOSTethys Web service URL", "http://zzzz"))
			);
		}
		
		AttrGroupValues serviceContactValues = new AttrGroupValues();
		oostValues.setServiceContactValues(serviceContactValues);
		widget.add(createWidgetForAttrGroup(
				null,
				true,
				basicModels.getServiceContact(),
				serviceContactValues));
		
		List<SystemValues> systemValuesList = oostValues.getSystemValuesList();
		widget.add(createWidgetForSystems(systemValuesList, true));
	}
	

	// groups of systems
	private Widget createWidgetForSystems(
			final List<SystemValues> systemValuesList,
			boolean createDisclosure
	) {

		assert systemValuesList.size() == 0;
		
		final SvContainer sysContainer = SvContainer.createTabPanel();
		
		sysContainer.setListener(new SvContainer.Listener() {
			public void addElement() {
				createSystem(systemValuesList, sysContainer);
			}

			public void elementRemoved(int idx) {
				assert systemValuesList.size() == sysContainer.getWidgetCount();
				systemValuesList.remove(idx);
				sysContainer.remove(idx);
				assert systemValuesList.size() == sysContainer.getWidgetCount();
			}
		});

		
		// add first element:
		createSystem(systemValuesList, sysContainer);
		
		if ( createDisclosure ) {
			DisclosurePanel dp = new DisclosurePanel("Systems", true);
			dp.add(sysContainer.getPanel());
			return dp;
		}
		else {
			return sysContainer.getPanel();
		}
	}
	
	// temporary way to name systems to facilitate testing
	private int sysId = 1;
	
	private void createSystem(final List<SystemValues> systemValuesList, final SvContainer sysContainer) {
		assert systemValuesList.size() == sysContainer.getWidgetCount();
		
		SystemValues systemValues = new SystemValues();
		systemValuesList.add(systemValues);
		
		MyChangeListener mcl = new MyChangeListener() {
			void valueChanged(String name, String value) {
				if ( name.equals("systemShortName") ) {
					element.setName(prefix + value);
				}				
			}
		};
		
		String name = "System " +(sysId++);
		
		Widget sysWidget = createWidgetForSystem(name, true, systemValues, mcl);
		
		mcl.prefix = "System: ";
		mcl.element = sysContainer.add(sysWidget, name);
		
		assert systemValuesList.size() == sysContainer.getWidgetCount();
	}
	
	
	static abstract class MyChangeListener implements ChangeListener {
		String prefix = "";
		IElement element;
		public void onChange(Widget sender) {
			if ( element == null ) {
				return;
			}
			TextBoxBase tb = (TextBoxBase) sender;
			String name = tb.getName();
			String value = tb.getText().trim();

			Main.log("onChange: attribute name: " +name+ " <- \"" +value+ "\"");
			if ( value.length() == 0 ) {
				value = "?";
			}
			valueChanged(name, value);
		}
		
		abstract void valueChanged(String name, String value);
	}
	
	
	
	/**
	 * 
	 * @param open
	 * @param systemValues
	 * @return
	 */
	private Widget createWidgetForSystem(
			String name,
			boolean open, SystemValues systemValues,
			ChangeListener clSysMd
	) {
		
		VerticalPanel vp = new VerticalPanel();
		
		MetadataValues metadataValues = new MetadataValues();
		systemValues.setMetadataValues(metadataValues);
		
		vp.add(createWidgetForMetadata(clSysMd, open, metadataValues));
		
		VerticalPanel selectionPanel = new VerticalPanel();
		vp.add(createOutputOrComponentsChoice(selectionPanel, systemValues));
		
		vp.add(selectionPanel);
		
		
		if ( false ) {
			DecoratorPanel decPanel = new DecoratorPanel();
			decPanel.add(vp);
			return decPanel;
		}
		else {
			return vp;
		}
	}

	
	// group of variables
	private Widget createWidgetForVariables(
			final List<AttrGroupValues> outputValuesList,
			boolean createDisclosure
	) {
		final VerticalPanel vp0 = new  VerticalPanel();
		
		final SvContainer varContainer = SvContainer.createTabPanel();
		
		vp0.add(varContainer.getPanel());
		
		varContainer.setListener(new SvContainer.Listener() {
			public void addElement() {
				createVariable(outputValuesList, varContainer);
			}

			public void elementRemoved(int idx) {
				assert outputValuesList.size() == varContainer.getWidgetCount();
				outputValuesList.remove(idx);
				varContainer.remove(idx);
				assert outputValuesList.size() == varContainer.getWidgetCount();
			}
		});

		
//		add first element:
		createVariable(outputValuesList, varContainer);
		
		if ( createDisclosure ) {
			DisclosurePanel dp = new DisclosurePanel("Variables", true);
			dp.add(vp0);
			return dp;
		}
		else {
			return vp0;
		}
	}
	
	
	// temporary way to name systems to facilitate testing
	private int varId = 1;
	
	private void createVariable(final List<AttrGroupValues> outputValuesList, final SvContainer varContainer) {
		assert outputValuesList.size() == varContainer.getWidgetCount();
		
		AttrGroupValues outputValues = new AttrGroupValues();
		outputValuesList.add(outputValues);
		
		MyChangeListener mcl = new MyChangeListener() {
			void valueChanged(String name, String value) {
				if ( name.equals("name") ) {
					element.setName(prefix + value);
				}				
			}
		};
		
		String name = "variable " +(varId++);
		
		Widget varWidget = createWidgetForVariable(
				true, outputValues, mcl);
		
		mcl.prefix = "Variable: ";
		mcl.element = varContainer.add(varWidget, name);
		
		assert outputValuesList.size() == varContainer.getWidgetCount();
	}
	

	private Widget createWidgetForVariable(
			boolean open, AttrGroupValues outputValues,
			ChangeListener clSysMd
	) {
		
		return createWidgetForAttrGroup(clSysMd, open, basicModels.getOutput(), outputValues);
	}


	
	
	
	
	private Widget createOutputOrComponentsChoice(
			final VerticalPanel selectionPanel, 
			final SystemValues systemValues
	) {
		
		final RadioButton outputRb = new RadioButton("oc", "Output");
		final RadioButton compsRb = new RadioButton("oc", "Components");
		
		ClickListener cl = new ClickListener() {
			public void onClick(Widget sender) {
				if ( sender == outputRb ) {
					selectionPanel.clear();
					systemValues.setSystemValuesList(null);
					List<AttrGroupValues> outputValuesList = systemValues.getOutputValuesList();
					selectionPanel.add(createWidgetForVariables(outputValuesList, true));
				}
				else if ( sender == compsRb ) {
					selectionPanel.clear();
					systemValues.setOutputValuesList(null);
					List<SystemValues> systemValuesList = systemValues.getSystemValuesList();
					selectionPanel.add(createWidgetForSystems(systemValuesList, true));
				}
			}
		};
		outputRb.addClickListener(cl);
		compsRb.addClickListener(cl);
		
		HorizontalPanel hp = new HorizontalPanel();
		hp.add(new HTML("This system has: "));
		hp.add(outputRb);
		hp.add(compsRb);
		
		return hp;
		
	}


	private Widget createWidgetForMetadata(
			ChangeListener clSysMd,
			boolean open, MetadataValues metadataValues) {
		
		VerticalPanel vp = new VerticalPanel();
		
		AttrGroupValues systemContactValues = new AttrGroupValues();
		metadataValues.setSystemContactValues(systemContactValues);
		vp.add(createWidgetForAttrGroup(
				null,
				open,
				basicModels.getSystemContact(),
				systemContactValues)
		);
		
		AttrGroupValues systemMetadataValues = new AttrGroupValues();
		metadataValues.setSystemMetadataValues(systemMetadataValues);
		vp.add(createWidgetForAttrGroup(
				clSysMd, 
				open,
				basicModels.getSystemMetadata(),
				systemMetadataValues)
		);

		return vp;
	}
	
	
	private static Widget createWidgetForAttrGroup(
			final ChangeListener cl,
			boolean open,
			final AttrGroupModel attrGroup, final AttrGroupValues attrGroupValues
	) {
		
		ChangeListener mycl = new ChangeListener() {
			public void onChange(Widget sender) {
				TextBoxBase tb = (TextBoxBase) sender;
				String name = tb.getName();
				String value = tb.getText().trim();
				attrGroupValues.getValues().put(name, value);
				
				Main.log("onChange: " +attrGroup.getName()+ ": " +name+ " <- \"" +value+ "\"");
				
				// also notify given ChangeListener, if any:
				if ( cl != null ) {
					cl.onChange(sender);
				}
			}
		};
		List<AttributeModel> list = attrGroup.getAttributes();
		AttributeModel[] attributes = list.toArray(new AttributeModel[list.size()]);
		Widget ww = createWidgetForAttributes(attrGroup.getHtmlInfo(), mycl, open, attributes);
		
		for (AttributeModel attributeModel : attributes) {
			String value = attributeModel.getDefaultValue();
			if ( value != null ) {
				attrGroupValues.getValues().put(attributeModel.getBeanAttributeName(), value);
			}
		}
		
//		VerticalPanel container = new VerticalPanel();
////		container.setSpacing(4);
//		DecoratorPanel decPanel = new DecoratorPanel();
//	    decPanel.setWidget(container);
//	    container.add(ww);
//	    
//	    return decPanel;
	    return ww;
	}
	
	private static Widget createWidgetForAttributes(
			String preamble, ChangeListener cl, boolean open, AttributeModel... attributes
	) {
		
		ArrayList<Elem> elems = new ArrayList<Elem>();
		for (int i = 0; i < attributes.length; i++) {
			AttributeModel attrDef = attributes[i];
			TextBoxBase tbb = Util.createTextBoxBase(1, "350px", cl);
			
			tbb.setName(attrDef.getBeanAttributeName());
			
			String defaultValue = attrDef.getDefaultValue();
			if ( defaultValue != null ) {
				tbb.setText(defaultValue);
			}
			
			Elem elem = new Elem(attrDef, tbb);

			elems.add(elem);
		}

		
		// crdeate form
		FlexTable flexTable = new FlexTable();
//		flexTable.setBorderWidth(1);
		int row = 0;
		
		for ( Elem elem : elems ) {
			AttributeModel attrDef = elem.attrDef;
			Widget widget = elem.widget;
			
			String label = attrDef.getLabel();
			String tooltip = null; // null = no tooltip for now 
			flexTable.setWidget(row, 0, new TLabel(label, false, tooltip ));
			
			flexTable.setWidget(row, 1, widget);
//			flexTable.getFlexCellFormatter().setWidth(row, 1, "300px");
			flexTable.getFlexCellFormatter().setAlignment(row, 0, 
					HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_MIDDLE
			);
			flexTable.getFlexCellFormatter().setAlignment(row, 1, 
					HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_MIDDLE
			);
			row++;
		}
		
		if ( preamble != null ) {
			if ( false ) {
				VerticalPanel vp = new VerticalPanel();
				vp.setSpacing(5);
				vp.add(new HTML(preamble));
				vp.add(flexTable);
				return vp;
			}
			else {
				DisclosurePanel dp = new DisclosurePanel(preamble);
				dp.add(flexTable);
				dp.setOpen(open);
				return dp;
			}
		}
		else {
			return flexTable;
		}
	}

	
	static class Elem {
		AttributeModel attrDef;
		Widget widget;
		Elem(AttributeModel attrDef, Widget widget) {
			assert attrDef != null;
			assert widget != null;
			this.attrDef = attrDef;
			this.widget = widget;
		}
	}

}
