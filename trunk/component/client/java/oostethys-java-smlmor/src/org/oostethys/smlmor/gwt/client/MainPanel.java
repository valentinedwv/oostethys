package org.oostethys.smlmor.gwt.client;

import org.oostethys.smlmor.gwt.client.rpc.SmlResult;
import org.oostethys.smlmor.gwt.client.rpc.model.OostethysValues;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * TODO
 * 
 * @author Carlos Rueda
 */
public class MainPanel {

	private final HorizontalPanel widget = new HorizontalPanel();
	
	private final CellPanel container = new VerticalPanel();
	
	private Controller controller;
	private Widget thePanel;
	
	private TextArea textArea = createTextArea();
	
	private TabPanel tabPanel = createTabPanel();
	
	
	private final HTML statusLabel = new HTML("");

	
	
	
	public MainPanel() {
		controller = new Controller(Main.basicModels);
		thePanel = controller.getWidget();
		
		widget.setWidth("700px");
		widget.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		
		container.setSpacing(4);
	    widget.add(container);

	    container.add(new HTML(
	    		"<h2>SensorML/TEDS Generator</h2>" +
	    		"See this <a target=\"_black\" href=\"http://code.google.com/p/oostethys/wiki/SmlMor\">wiki page</a> for current status."
	    ));
	    
	    HorizontalPanel hp = new HorizontalPanel();
	    hp.setSpacing(5);
	    container.add(hp);
	    hp.add(new HTML("Fill out the definitions section and then click: "));
	    hp.add(new PushButton("Generate SensorML", new ClickListener() {
			public void onClick(Widget sender) {
				doGenerate();
			}
		}));
	    hp.add(new PushButton("Generate TEDS", new ClickListener() {
			public void onClick(Widget sender) {
				Window.alert("Not implemented");
			}
		}));
	    
	    hp.add(statusLabel);

	    tabPanel.add(thePanel, "Definitions");
		tabPanel.add(textArea, "SensorML");
		tabPanel.selectTab(0);
	    container.add(tabPanel);
	    
	    statusLabel.setHeight("20px");
	}
	
	
	TabPanel createTabPanel() {
		TabPanel tabPanel = new TabPanel();	
		tabPanel.setWidth("800px");
		return tabPanel;
	}
	
	TextArea createTextArea() {
		TextArea textArea = new TextArea();
		textArea.setSize("700px", "600px");
		textArea.setVisibleLines(30);
		textArea.setText("Click 'Generate SensorML'");
		textArea.setReadOnly(true);
		
		return textArea;
	}
	
	private void doGenerate() {

		OostethysValues soostValues = controller.getOostValues();
		
		AsyncCallback<SmlResult> callback = new  AsyncCallback<SmlResult>() {

			public void onFailure(Throwable caught) {
				String error = caught.getMessage();
				statusLabel.setHTML(error);
				textArea.setText("ERROR: " +error);
				tabPanel.selectTab(1);
			}

			public void onSuccess(SmlResult result) {
				Main.log("---generation completed---");
				Main.log("Tree dump:\n" +result.getDump());
				if ( result.getError() != null ) {
					String error = result.getError();
					statusLabel.setHTML(error);
					textArea.setText("ERROR: " +error);
					tabPanel.selectTab(1);
				}
				else {
					statusLabel.setHTML("");
					textArea.setText(result.getSml());
					tabPanel.selectTab(1);
				}
			}
			
		};
		
		tabPanel.selectTab(1);
		statusLabel.setHTML("Generating...");
		textArea.setText("Generating...");
		Main.smlmorService.getSensorML(soostValues, callback);
	}

	
	
	
	public Widget getWidget() {
		return widget;
	}
	

}
