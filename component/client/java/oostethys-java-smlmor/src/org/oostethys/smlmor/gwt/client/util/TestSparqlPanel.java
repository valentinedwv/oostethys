package org.oostethys.smlmor.gwt.client.util;

import java.util.Map;

import org.oostethys.smlmor.gwt.client.Main;

import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * TODO
 * 
 * @author Carlos Rueda
 */
public class TestSparqlPanel {

	private final HorizontalPanel widget = new HorizontalPanel();
	
	private final CellPanel container = new VerticalPanel();
	
	private TextArea queryTextArea = createTextArea(false);
	private TextArea outputTextArea = createTextArea(true);
	
	private TabPanel tabPanel = createTabPanel();
	
	
	private final HTML statusLabel = new HTML("");

	
	private final CellPanel formattedOutput = new VerticalPanel();
	
	private PushButton searchButton = new PushButton(Main.images.search().createImage(), new ClickListener() {
		public void onClick(Widget sender) {
			_doQuery();
		}
	});
	
	
	private TextBox formatTextBox = new TextBox();
	
	
	public TestSparqlPanel() {
		
		queryTextArea.setText(
				" PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
				" PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
				" PREFIX rt: <http://mmisw.org/ont/mmi/resourcetype/> \n" +
				" SELECT ?description ?uri ?label\n" +
				" WHERE { \n" +
				"     ?uri rdf:type rt:ResourceType . \n" +
				"     ?uri <http://www.w3.org/2000/01/rdf-schema#label> ?label . \n" +
				"     OPTIONAL { ?uri rt:description ?description } \n" +
				" } \n" +
				" ORDER BY ASC(?description) "
		);
		
		formatTextBox.setText("json");
		
		widget.setWidth("700px");
		widget.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		
		container.setSpacing(4);
	    widget.add(container);

	    container.add(new HTML(
	    		"<h2>SPARQL test panel</h2>" 
	    ));
	    
	    HorizontalPanel hp = new HorizontalPanel();
	    hp.setSpacing(5);
	    container.add(hp);
	    hp.add(new HTML("Fill out the definitions section and then click: "));
	    hp.add(searchButton);
	    
	    hp.add(new HTML("Format:"));
	    hp.add(formatTextBox);
	    
	    hp.add(statusLabel);

	    tabPanel.add(queryTextArea, "SPARQL");
		tabPanel.add(outputTextArea, "Result");
		tabPanel.add(formattedOutput, "Formatted result");
		
		tabPanel.selectTab(0);
	    container.add(tabPanel);
	    
	    statusLabel.setHeight("20px");
	}
	
	
	TabPanel createTabPanel() {
		TabPanel tabPanel = new TabPanel();	
		tabPanel.setWidth("800px");
		return tabPanel;
	}
	
	TextArea createTextArea(boolean readOnly) {
		TextArea textArea = new TextArea();
		textArea.setSize("700px", "600px");
		textArea.setVisibleLines(30);
		textArea.setReadOnly(readOnly);
		
		return textArea;
	}
	
	private void _doQuery() {
		
		final String queryString = queryTextArea.getText();
		if ( queryString.length() == 0) {
			return;
		}
		
		enable(false);
		tabPanel.selectTab(1);
		outputTextArea.setText("Submitting query ...");
		Main.log("Submitting query ...");
//		resultsPanel.searching();

		statusLabel.setHTML("querying ...");

		SparqlUtil.runQuery(queryString, new SparqlUtil.ResponseHandler() {

			public void onError(String error) {
				enable(true);
				Main.log(error);
				statusLabel.setHTML(error);
				outputTextArea.setText("ERROR: " +error);
				tabPanel.selectTab(1);
			}

			public void onFailure(Throwable caught) {
				Main.log("FAILURE");
				String error = caught.getMessage();
				onError(error);
			}

			public void responseObtained(SparqlUtil.Result result) {
				Main.log("responseObtained");
				enable(true);
				statusLabel.setHTML("");
				String text = result.sqResult.getResult();
				outputTextArea.setText(text);
				tabPanel.selectTab(1);
				
				if ( result.values != null ) { 
					dispatchResponse(result);
				}
			}
			
		});
		
	}

	
	protected void dispatchResponse(SparqlUtil.Result result) {
		FlexTable flexTable = new FlexTable();
		flexTable.setBorderWidth(1);
		int row = 0;
		
		for ( int i = 0, cnt = result.keys.size(); i < cnt; i++ ) {
			String key = result.keys.get(i);
			flexTable.setWidget(row, i, new Label(key));
		}
		row++;
		
		for ( int i = 0, cnt = result.values.size(); i < cnt; i++, row++ ) {
			Map<String, String> record = result.values.get(i);
			int col = 0;
			for ( String key : result.keys ) {
				String value = record.get(key);
				flexTable.setWidget(row, col++, new Label(value));
			}
		}
		
		formattedOutput.clear();
		formattedOutput.add(flexTable);
		tabPanel.selectTab(2);
	}
	
	private void enable(boolean enabled) {
//		textBox.setReadOnly(!enabled);  
		searchButton.setEnabled(enabled);
	}

	
	public Widget getWidget() {
		return widget;
	}
	

}
