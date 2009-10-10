package org.oostethys.smlmor.gwt.client;

import org.oostethys.smlmor.gwt.client.rpc.model.OostethysValues;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PushButton;
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
	
//	private Widget thePanel = new Wizard(this).getWidget();
	
	private Controller controller;
	private Widget thePanel;
	
	
	private final HTML statusLabel = new HTML("");

	
	
	
	public MainPanel() {
		controller = new Controller(Main.oostethysModel);
		thePanel = controller.getWidget();
		
		widget.setWidth("700px");
		widget.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		container.setSpacing(4);
		DecoratorPanel decPanel = new DecoratorPanel();
	    decPanel.setWidget(container);
	    widget.add(decPanel);

	    container.add(thePanel);
	    
	    statusLabel.setHeight("20px");
	    
	    
	    container.add(new PushButton("Generate SensorML", new ClickListener() {
			public void onClick(Widget sender) {
				doGenerate();
			}
		}));


	}
	
	private void doGenerate() {

		OostethysValues soostValues = controller.getOostValues();
		
		AsyncCallback<String> callback = new  AsyncCallback<String>() {

			public void onFailure(Throwable caught) {
				String error = caught.getMessage();
				statusLabel.setHTML(error);
				Window.alert("ERROR: " +error);
			}

			public void onSuccess(String result) {
				statusLabel.setHTML("OK");
				Main.log(result);
				Window.alert(result);
				
			}
			
		};
		
		Main.smlmorService.getSensorML(soostValues, callback);
	}

	
	
	
	public Widget getWidget() {
		return widget;
	}
	

}
