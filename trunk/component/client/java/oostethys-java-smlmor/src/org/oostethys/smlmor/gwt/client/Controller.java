package org.oostethys.smlmor.gwt.client;

import java.util.ArrayList;
import java.util.List;

import org.oostethys.smlmor.gwt.client.rpc.model.AttrGroupModel;
import org.oostethys.smlmor.gwt.client.rpc.model.AttrGroupValues;
import org.oostethys.smlmor.gwt.client.rpc.model.AttributeModel;
import org.oostethys.smlmor.gwt.client.rpc.model.MetadataModel;
import org.oostethys.smlmor.gwt.client.rpc.model.MetadataValues;
import org.oostethys.smlmor.gwt.client.rpc.model.OostethysModel;
import org.oostethys.smlmor.gwt.client.rpc.model.OostethysValues;
import org.oostethys.smlmor.gwt.client.rpc.model.SystemModel;
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
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class Controller {
	
	private OostethysModel oostModel;
	
	private Widget widget;
	
	private OostethysValues oostValues;
	
	

	public Controller(OostethysModel oostModel) {
		this.oostModel = oostModel;
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

		VerticalPanel vp = new VerticalPanel();
		
		vp.add(createWidgetForAttributes(null, null, oostModel.getWebServerUrlAttribute()));
		
		AttrGroupValues serviceContactValues = new AttrGroupValues();
		oostValues.setServiceContactValues(serviceContactValues);
		vp.add(createWidgetForAttrGroup(oostModel.getServiceContact(), serviceContactValues));
		
		SystemModel systemModel = oostModel.getSystemModel();
		
		List<SystemValues> systemValuesList = oostValues.getSystemValuesList();
		vp.add(createWidgetForSystems(systemModel, systemValuesList));
		
		widget = vp;
	}

	// groups of systems
	private static Widget createWidgetForSystems(final SystemModel systemModel, final List<SystemValues> systemValuesList) {
		
		DisclosurePanel dp = new DisclosurePanel("Systems");
		
		final VerticalPanel vp0 = new  VerticalPanel();
		dp.add(vp0);
		
		final VerticalPanel vp1 = new  VerticalPanel();
		
		vp0.add(vp1);
		
		SystemValues systemValues = new  SystemValues();
		systemValuesList.add(systemValues);
		vp1.add(createWidgetForSystem("", systemModel, systemValues));
		
		vp0.add(new PushButton("Add system", new ClickListener() {
			public void onClick(Widget sender) {
				SystemValues systemValues = new  SystemValues();
				systemValuesList.add(systemValues);
				vp1.add(createWidgetForSystem("", systemModel, systemValues));
			}
		}));
		
		return dp;
	}
	
	private static Widget createWidgetForSystem(String name, SystemModel systemModel, SystemValues systemValues) {
		
		DisclosurePanel dp = new DisclosurePanel(name, true);
		
		VerticalPanel vp = new  VerticalPanel();
		dp.add(vp);
		
		MetadataValues metadataValues = new MetadataValues();
		systemValues.setMetadataValues(metadataValues);
		vp.add(createWidgetForMetadata(null, systemModel.getMetadataModel(), metadataValues));
		
		return dp;
	}



	private static Widget createWidgetForMetadata(Object object,
			MetadataModel metadataModel, MetadataValues metadataValues) {
		
		VerticalPanel vp = new VerticalPanel();
		
		AttrGroupValues systemContactValues = new AttrGroupValues();
		metadataValues.setSystemContactValues(systemContactValues);
		vp.add(createWidgetForAttrGroup(metadataModel.getSystemContact(), systemContactValues));
		
		AttrGroupValues systemMetadataValues = new AttrGroupValues();
		metadataValues.setSystemMetadataValues(systemMetadataValues);
		vp.add(createWidgetForAttrGroup(metadataModel.getSystemMetadata(), systemMetadataValues));

		return vp;
	}

	private static Widget createWidgetForAttrGroup(
			final AttrGroupModel attrGroup, final AttrGroupValues attrGroupValues
	) {
		
		ChangeListener cl = new ChangeListener() {
			public void onChange(Widget sender) {
				TextBoxBase tb = (TextBoxBase) sender;
				String name = tb.getName();
				String value = tb.getText().trim();
				attrGroupValues.getValues().put(name, value);
				
				Main.log("onChange: " +attrGroup.getName()+ ": " +name+ " <- \"" +value+ "\"");
			}
		};
		List<AttributeModel> list = attrGroup.getAttributes();
		AttributeModel[] attributes = list.toArray(new AttributeModel[list.size()]);
		Widget ww = createWidgetForAttributes(attrGroup.getHtmlInfo(), cl, attributes);
		
		for (AttributeModel attributeModel : attributes) {
			String value = attributeModel.getDefaultValue();
			if ( value != null ) {
				attrGroupValues.getValues().put(attributeModel.getBeanAttributeName(), value);
			}
		}
		
		VerticalPanel container = new VerticalPanel();
//		container.setSpacing(4);
		DecoratorPanel decPanel = new DecoratorPanel();
	    decPanel.setWidget(container);
	    container.add(ww);
	    
	    return decPanel;
	}
	
	private static Widget createWidgetForAttributes(
			String preamble, ChangeListener cl, AttributeModel... attributes
	) {
		
		ArrayList<Elem> elems = new ArrayList<Elem>();
		for (int i = 0; i < attributes.length; i++) {
			AttributeModel attrDef = attributes[i];
			TextBoxBase tbb = Util.createTextBoxBase(1, "200px", cl);
			
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
		int row = 0;
		
		for ( Elem elem : elems ) {
			AttributeModel attrDef = elem.attrDef;
			Widget widget = elem.widget;
			
			String label = attrDef.getLabel();
			String tooltip = "<b>" +label+ "</b>:<br/>" + 
					attrDef.getTooltip() +
					"<br/>";
			flexTable.setWidget(row, 0, new TLabel(label, false, tooltip ));
			
			flexTable.setWidget(row, 1, widget);
			flexTable.getFlexCellFormatter().setWidth(row, 0, "250px");
			flexTable.getFlexCellFormatter().setAlignment(row, 0, 
					HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_MIDDLE
			);
			flexTable.getFlexCellFormatter().setAlignment(row, 1, 
					HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_MIDDLE
			);
			row++;
		}
		
		if ( preamble != null ) {
			VerticalPanel vp = new VerticalPanel();
			vp.setSpacing(5);
			vp.clear();
			vp.add(new HTML(preamble));
			vp.add(flexTable);

			return vp;
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
