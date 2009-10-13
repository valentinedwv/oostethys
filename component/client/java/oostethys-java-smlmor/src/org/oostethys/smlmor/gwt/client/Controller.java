package org.oostethys.smlmor.gwt.client;

import java.util.ArrayList;
import java.util.List;

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
import com.google.gwt.user.client.ui.PushButton;
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
		
		AttrGroupValues serviceContactValues = new AttrGroupValues();
		oostValues.setServiceContactValues(serviceContactValues);
		widget.add(createWidgetForAttrGroup(
				null,
				true,
				basicModels.getServiceContact(),
				serviceContactValues));
		
		List<SystemValues> systemValuesList = oostValues.getSystemValuesList();
		widget.add(createWidgetForSystems(systemValuesList, false, true));
	}
	

	// groups of systems
	private Widget createWidgetForSystems(
			final List<SystemValues> systemValuesList,
			boolean addFirst,
			boolean createDisclosure
	) {
		
		
		final VerticalPanel vp0 = new  VerticalPanel();
		
		final VerticalPanel sysContainer = new  VerticalPanel();
		
		vp0.add(sysContainer);
		
		if ( addFirst ) {
			SystemValues systemValues = new SystemValues();
			systemValuesList.add(systemValues);
			sysContainer.add(createWidgetForSystem(
					systemValuesList, sysContainer, sysContainer.getWidgetCount(), 
					true, systemValues));
		}
		
		HorizontalPanel hp = new HorizontalPanel();
		vp0.add(hp);
		hp.add(new PushButton("Add system", new ClickListener() {
			public void onClick(Widget sender) {
				SystemValues systemValues = new  SystemValues();
				systemValuesList.add(systemValues);
				sysContainer.add(createWidgetForSystem(
						systemValuesList, sysContainer, sysContainer.getWidgetCount(), 
						true, systemValues));
			}
		}));
		
		if ( createDisclosure ) {
			DisclosurePanel dp = new DisclosurePanel("Systems", true);
			dp.add(vp0);
			return dp;
		}
		else {
			return vp0;
		}
	}
	
	/**
	 * 
	 * @param idx Index in system container
	 * @param open
	 * @param systemValues
	 * @return
	 */
	private Widget createWidgetForSystem(
			final List<SystemValues> systemValuesList,
			final VerticalPanel sysContainer, final int idx, 
			boolean open, SystemValues systemValues
	) {
		
		String name = "System: " +"TODO:intial value here";
		final DisclosurePanel dp = new DisclosurePanel(name, true);
		
		VerticalPanel vp = new VerticalPanel();
//		vp.setBorderWidth(1);
		dp.add(vp);
		
		MetadataValues metadataValues = new MetadataValues();
		systemValues.setMetadataValues(metadataValues);
		
		
		ChangeListener clSysMd = new ChangeListener() {
			public void onChange(Widget sender) {
				TextBoxBase tb = (TextBoxBase) sender;
				String name = tb.getName();
				String value = tb.getText().trim();
				
				Main.log("onChange: attribute name: " +name+ " <- \"" +value+ "\"");
				
				if ( name.equals("systemIdentifier") ) {
					dp.getHeaderTextAccessor().setText("System: " +value);
				}				
				
			}
		};
		
		vp.add(createWidgetForMetadata(clSysMd, open, metadataValues));
		
		VerticalPanel selectionPanel = new VerticalPanel();
		vp.add(createOutputOrComponentsChoice(selectionPanel, systemValues));
		
		vp.add(selectionPanel);
		
		
		HorizontalPanel hpr = new HorizontalPanel();
		vp.add(hpr);
		hpr.add(new PushButton("Remove this system", new ClickListener() {
			public void onClick(Widget sender) {
				systemValuesList.remove(idx);
				sysContainer.remove(idx);
			}
		}));
		
		if ( true ) {
			DecoratorPanel decPanel = new DecoratorPanel();
			decPanel.add(dp);
			return decPanel;
		}
		else {
			return dp;
		}
	}

	
	// groups of systems
	private Widget createWidgetForVariables(
			final List<AttrGroupValues> outputValuesList,
			boolean addFirst,
			boolean createDisclosure
	) {
		final VerticalPanel vp0 = new  VerticalPanel();
		
		final VerticalPanel vp1 = new  VerticalPanel();
		
		vp0.add(vp1);
		
		if ( addFirst ) {
			AttrGroupValues outputValues = new AttrGroupValues();
			outputValuesList.add(outputValues);
			vp1.add(createWidgetForVariable(
					outputValuesList, vp1, vp1.getWidgetCount(),
					true, outputValues));
		}
		
		HorizontalPanel hp = new HorizontalPanel();
		vp0.add(hp);
		hp.add(new PushButton("Add variable", new ClickListener() {
			public void onClick(Widget sender) {
				AttrGroupValues outputValues = new AttrGroupValues();
				outputValuesList.add(outputValues);
				vp1.add(createWidgetForVariable(
						outputValuesList, vp1, vp1.getWidgetCount(),
						true, outputValues));
			}
		}));
		
		
		if ( createDisclosure ) {
			DisclosurePanel dp = new DisclosurePanel("Variables", true);
			dp.add(vp0);
			return dp;
		}
		else {
			return vp0;
		}
	}
	
	private Widget createWidgetForVariable(
			final List<AttrGroupValues> outputValuesList,
			final VerticalPanel varContainer, final int idx, 
			boolean open, AttrGroupValues outputValues
	) {
		
		String name = "Variable ";
		DisclosurePanel dp = new DisclosurePanel(name, true);
		
		
		VerticalPanel vp = new VerticalPanel();
		dp.add(vp);
		
		vp.add(createWidgetForAttrGroup(null, open, basicModels.getOutput(), outputValues));
		
		
		HorizontalPanel hpr = new HorizontalPanel();
		vp.add(hpr);
		hpr.add(new PushButton("Remove this variable", new ClickListener() {
			public void onClick(Widget sender) {
				outputValuesList.remove(idx);
				varContainer.remove(idx);
			}
		}));
		
		
		return dp;
	}


	
	
	
	
	private Widget createOutputOrComponentsChoice(
			final VerticalPanel selectionPanel, 
			final SystemValues systemValues
	) {
		HorizontalPanel hp = new HorizontalPanel();
		
		final RadioButton outputRb = new RadioButton("oc", "Output");
		final RadioButton compsRb = new RadioButton("oc", "Components");
		
		ClickListener cl = new ClickListener() {
			public void onClick(Widget sender) {
				if ( sender == outputRb ) {
					selectionPanel.clear();
					systemValues.setSystemValuesList(null);
					List<AttrGroupValues> outputValuesList = systemValues.getOutputValuesList();
					selectionPanel.add(createWidgetForVariables(outputValuesList, false, false));
				}
				else if ( sender == compsRb ) {
					selectionPanel.clear();
					systemValues.setOutputValuesList(null);
					List<SystemValues> systemValuesList = systemValues.getSystemValuesList();
					selectionPanel.add(createWidgetForSystems(systemValuesList, false, false));
				}
			}
		};
		outputRb.addClickListener(cl);
		hp.add(outputRb);
		
		compsRb.addClickListener(cl);
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
			String tooltip = null; 
//				"<b>" +label+ "</b>:<br/>" + 
//					attrDef.getTooltip() +
//					"<br/>";
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
