package org.oostethys.smlmor.gwt.client.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mmisw.iserver.gwt.client.rpc.SparqlQueryResult;
import org.oostethys.smlmor.gwt.client.Main;
import org.oostethys.smlmor.gwt.client.rpc.model.AttributeModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ChangeListenerCollection;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.SourcesChangeEvents;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestionEvent;
import com.google.gwt.user.client.ui.SuggestionHandler;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author Carlos Rueda
 */
public class FieldWithChoose extends HorizontalPanel implements SourcesChangeEvents {
	AttributeModel attr;
	private TextBoxBase textBox;
	PushButton chooseButton;
	ChangeListener cl;
	
	private ChangeListenerCollection changeListeners;
	
	/**
	 * Creates a field with a choose feature.
	 * @param attr
	 * @param cl
	 */
	public FieldWithChoose(AttributeModel attr, ChangeListener cl) {
		this(attr, cl, "200px");
	}
	
	/**
	 * Creates a field with a choose feature.
	 * @param attr
	 * @param cl
	 * @param textWidth
	 */
	public FieldWithChoose(AttributeModel attr, ChangeListener cl, String textWidth) {
		this.attr = attr;
		this.cl = cl;
		
		addChangeListener(cl);
		
		int nl = 1;    /// attr.getNumberOfLines() is ignored
		textBox = Util.createTextBoxBase(nl, textWidth, cl);
		textBox.addChangeListener(new ChangeListener() {
			public void onChange(Widget sender) {
				_onChange();
			}
		});

		add(textBox);
		
		chooseButton = new PushButton("Choose", new ClickListener() {
			public void onClick(Widget sender) {
				choose();
			}
		});
		
		add(chooseButton);
	}

	/** nothing done here */
	protected void optionSelected(Map<String, String> option) {
	}

	
	private void _onChange() {
		if (changeListeners != null) {
			changeListeners.fireChange(textBox);
		}
	}

	
	/**
	 * dispatches the selection of an option.
	 */
	private void choose() {
		
		String optionsVocab = attr.getOptionsVocabulary();
		
		final MyDialog waitPopup = new MyDialog(Util.createHtml("Getting URIs for " +
				optionsVocab+ " ...", 12),
				false    // No "Close" button
		);   
		waitPopup.setHTML("<img src=\"" +GWT.getModuleBaseURL()+ "images/loading.gif\"> <i>Please wait</i>");
		waitPopup.center();
		waitPopup.show();

		
		String queryString = 				
			" PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
			" PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
			" PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
			" SELECT DISTINCT ?label ?uri \n" +
			" WHERE { \n" +
			"     ?uri rdf:type <" +optionsVocab+ "> . \n" +
			"     ?uri rdfs:label ?label . \n" +
			" } \n" +
			" ORDER BY ASC(?label) "
			;
		
		Main.log("queryString:\n" +queryString);
		
		SparqlUtil.runQuery(queryString , new SparqlUtil.ResponseHandler() {

			public void onError(String error) {
				enable(true);
				Main.log(error);
			}

			public void onFailure(Throwable caught) {
				Main.log("FAILURE");
				String error = caught.getMessage();
				onError(error);
			}

			public void responseObtained(SparqlQueryResult sparqlQueryResult) {
				Main.log("responseObtained");
				enable(true);
//				dispatchResponse(sparqlQueryResult);
				dispatchOptions(sparqlQueryResult.getParsedResult().values, waitPopup);
			}
			
		});

		
	}
	
	private void dispatchOptions(final List<Map<String,String>> options, final MyDialog waitPopup) {
		Main.log("Dispatching options");
		
		final String width = "500px";
		
		final ListBox listBox = createListBox(options, cl);
		listBox.setWidth(width);
		
		VerticalPanel vp = new VerticalPanel();
		
		final MyDialog popup = new MyDialog(vp);
		
		listBox.setVisibleItemCount(Math.min(options.size(), 12));
		// make sure no item is selected so we get an change event on the first item (needed for firefox at least):
		// (see issue #138: Can't select AGU as authority abbreviation)
		listBox.setSelectedIndex(-1);

		listBox.addChangeListener(new ChangeListener () {
			public void onChange(Widget sender) {
				String value = listBox.getValue(listBox.getSelectedIndex());
				textBox.setText(value);
				
				Map<String, String> option = options.get(listBox.getSelectedIndex());
				optionSelected(option);

				_onChange();

				popup.hide();
			}
		});
		
		/////////////////////////////////////////////////////////
		// Use a SuggestBox with a MultiWordSuggestOracle.
		//
		// A map from a suggestion to its corresponding Option:
		final Map<String,Map<String, String>> suggestions = new HashMap<String,Map<String, String>>();
		MultiWordSuggestOracle oracle = new MultiWordSuggestOracle("/ :-_"); 
		for ( Map<String, String> option : options ) {
			String suggestion = option.get("label")+ " - " +option.get("uri");
			suggestions.put(suggestion, option);
			oracle.add(suggestion);

		}
		final SuggestBox suggestBox = new SuggestBox(oracle);
		suggestBox.setLimit(40);
		suggestBox.setWidth(width);
		suggestBox.addEventHandler(new SuggestionHandler() {
			public void onSuggestionSelected(SuggestionEvent event) {
				String suggestion = event.getSelectedSuggestion().getReplacementString();
				Map<String, String> option = suggestions.get(suggestion);
				textBox.setText(option.get("uri"));
				optionSelected(option);
				
				_onChange();
				
				popup.hide();
			}
		});
		////////////////////////////////////////////////////////////
		
		vp.add(suggestBox);
		vp.add(listBox);
		
		waitPopup.hide();
		
		// use a timer to request for focus in the suggest-box:
		new Timer() {
			public void run() {
				suggestBox.setFocus(true);
			}
		}.schedule(700);
		    
		popup.setText("Select " +attr.getLabel());
		popup.center();
		popup.show();

	}
	
	
	
    public static ListBox createListBox(final List<Map<String,String>> options, ChangeListener cl) {
		final ListBox lb = new ListBox();
		for ( Map<String, String> option : options ) {
			lb.addItem(option.get("label")+ " - " +option.get("uri"), option.get("uri"));
		}
		if ( cl != null ) {
			lb.addChangeListener(cl);
		}
		return lb;
	}


	public void enable(boolean enabled) {
		textBox.setReadOnly(!enabled);
//		lb.setEnabled(enabled);
		chooseButton.setEnabled(enabled);
	}

	public void setValue(String value) {
		textBox.setText(value);
//		lb.setSelectedIndex(0);
	}
	
	public String getValue() {
		return textBox.getText();
	}

	public TextBoxBase getTextBox() {
		return textBox;
	}

	public void addChangeListener(ChangeListener listener) {
	    if (changeListeners == null) {
	        changeListeners = new ChangeListenerCollection();
	        sinkEvents(Event.ONCHANGE);
	      }
	      changeListeners.add(listener);
	}

	public void removeChangeListener(ChangeListener listener) {
		if (changeListeners != null) {
			changeListeners.remove(listener);
		}
	}
	
}
