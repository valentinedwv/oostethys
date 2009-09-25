package org.oostethys.smlmor.gwt.client;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.KeyboardListenerAdapter;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * TODO
 * 
 * @author Carlos Rueda
 */
public class MainPanel {
	private static final String[] FIELDS = {
		"field1", "Field 1",
		"field2", "Field 2",
		"field3", "Field 3",
		"field4", "Field 4",
	};

	private final HorizontalPanel widget = new HorizontalPanel();
	
	private final CellPanel container = new VerticalPanel();
	
	
	private static class Entry {
		String label;
		TextBox tb;
		Entry(String label, TextBox tb) {
			super();
			this.label = label;
			this.tb = tb;
		}
		
	}
	private final Map<String,Entry> tbs = new LinkedHashMap<String, Entry>();
	
	
	
	private final PushButton generateButton = new PushButton("Generate", new ClickListener() {
		public void onClick(Widget sender) {
			createUpdate();
		}
	});

	
	private final HTML statusLabel = new HTML("");
	
	
	private void _addTb(String name, String label) {
		TextBox tb = new TextBox();
		tb.setWidth("200px");
		tbs.put(name, new Entry(label, tb));
	}
	
	public MainPanel() {
		
		for ( int i = 0; i < FIELDS.length; i += 2 ) {
			_addTb(FIELDS[i], FIELDS[i +1]);
		}		
		
		widget.setWidth("700px");
		widget.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		container.setSpacing(4);
		DecoratorPanel decPanel = new DecoratorPanel();
	    decPanel.setWidget(container);
	    widget.add(decPanel);

	    container.add(createForm());
	    
	    statusLabel.setHeight("20px");
	    
	    KeyboardListener kl = new KeyboardListenerAdapter() {
	    	@Override
			public void onKeyUp(Widget sender, char keyCode, int modifiers) {
	    		statusLabel.setText("");
	    		if ( keyCode == KeyboardListener.KEY_ENTER ) {
	    			_cancelKey();
	    			justCheck();
	    		}
			}
	    };
	    
	    for ( Entry entry : tbs.values() ) {
	    	entry.tb.addKeyboardListener(kl);
	    }
	}
	
	public Widget getWidget() {
		return widget;
	}
	
	public void dispatch() {

		dispatchCreate();

		final TextBox tb2Focus = tbs.get("firstname").tb;
		
		// use a timer to make the userPanel focused (there must be a better way)
		new Timer() {
			public void run() {
				tb2Focus.setFocus(true);
				tb2Focus.selectAll();
			}
		}.schedule(700);

	}
	
	
	private void dispatchCreate() {
		_enable(true);
	}

	private Widget createForm() {
		FlexTable panel = new FlexTable();
		panel.setCellSpacing(5);
		
		int row = 0;
		
		panel.getFlexCellFormatter().setColSpan(row, 0, 3);
		panel.setWidget(row, 0, new HTML("<strong>Sensor properties</strong>"));
		panel.getFlexCellFormatter().setAlignment(row, 0, 
				HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE
		);
		row++;

		
		for ( String name : tbs.keySet() ) {
			Entry entry = tbs.get(name); 
			TextBox tb = entry.tb;
			
			panel.setWidget(row, 0, new Label(entry.label+ ":"));

			panel.setWidget(row, 1, tb);
			panel.getFlexCellFormatter().setAlignment(row, 0, 
					HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_MIDDLE
			);
			panel.getFlexCellFormatter().setAlignment(row, 1, 
					HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_MIDDLE
			);
			
			row++;
			
		}

		panel.getFlexCellFormatter().setColSpan(row, 0, 3);
		panel.setWidget(row, 0, statusLabel);
		panel.getFlexCellFormatter().setAlignment(row, 0, 
				HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_MIDDLE
		);
		row++;
		
		HorizontalPanel loginCell = new HorizontalPanel();
		loginCell.add(generateButton);
		panel.getFlexCellFormatter().setColSpan(row, 0, 3);
		panel.setWidget(row, 0, loginCell);
		panel.getFlexCellFormatter().setAlignment(row, 0, 
				HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_MIDDLE
		);
		row++;
		
		
		tbs.get(FIELDS[0]).tb.setFocus(true);
		return panel;
	}
	
	
	private void statusMessage(String msg) {
		statusLabel.setHTML("<font color=\"green\">" +msg+ "</font>");
	}

	public void getFocus() {
		tbs.get(FIELDS[0]).tb.setFocus(true);
		tbs.get(FIELDS[0]).tb.selectAll();
	}
	
	private void statusError(String error) {
		statusLabel.setHTML("<font color=\"red\">" +error+ "</font>");
	}

	
	private Map<String,String> checkFields() {
		Map<String,String> values = new HashMap<String,String>();
		for (  String name : tbs.keySet() ) {
			Entry entry = tbs.get(name);
			TextBox tb = entry.tb;
			String value = tb.getText().trim();
			values.put(name, value);
			
			if ( value.length() == 0 ) {
				statusError("Missing value for field: " +entry.label);
				tb.setFocus(true);
				tb.selectAll();
				return null;
			}
			else if ( name.equals("email") ) {
				// basic check:  something@something:
				String[] toks = value.split("@");
				if ( toks.length != 2 ) {
					statusError("Malformed email address. Expected name and domain");
					tb.setFocus(true);
					tb.selectAll();
					return null;
				}
			}
		}

		return values;
	}
	
	
	private void justCheck() {
		
		checkFields();
	}
	
	private void createUpdate() {
		
		Map<String, String> values = checkFields();
		
		if ( values != null ) {
			doCreateUpdate(values);
		}
	}
	
	
	private void doCreateUpdate(Map<String,String> values) {

	}

	
	private void _enable(boolean enable) {
		generateButton.setEnabled(enable);
	}
	
	
	private void _cancelKey() {
	    for ( Entry entry : tbs.values() ) {
	    	entry.tb.cancelKey();
	    }
	}


}
