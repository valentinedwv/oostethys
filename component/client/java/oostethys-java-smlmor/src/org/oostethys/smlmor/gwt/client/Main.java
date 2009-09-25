package org.oostethys.smlmor.gwt.client;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.mmisw.iserver.gwt.client.rpc.AppInfo;
import org.mmisw.iserver.gwt.client.rpc.RegisteredOntologyInfo;
import org.oostethys.smlmor.gwt.client.img.SmlMorImageBundle;
import org.oostethys.smlmor.gwt.client.rpc.SmlMorService;
import org.oostethys.smlmor.gwt.client.rpc.SmlMorServiceAsync;
import org.oostethys.smlmor.gwt.client.util.Util;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.ButtonBase;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The entry point for the application
 * 
 * @author Carlos Rueda
 */
public class Main implements EntryPoint {
	
	public String footer;
	
	public static final String VERSION_COMMENT = 
		"sensorml-mmiorr mock-up. ";
	
	
	private static String baseUrl;
	
	public static SmlMorImageBundle images = (SmlMorImageBundle) GWT.create(SmlMorImageBundle.class);

	private static AppInfo appInfo;
	
	private static boolean includeLog;
	
	
	static SmlMorServiceAsync smlmorService;
	
	// cached list of all ontologies
	private static List<RegisteredOntologyInfo> allUris = new ArrayList<RegisteredOntologyInfo>();
	
	// selected ontologies to work on:
	// Map: code -> RegisteredOntologyInfo
	private static final Map<String, RegisteredOntologyInfo> workingUris = new LinkedHashMap<String,RegisteredOntologyInfo>();
	
	
	public static List<RegisteredOntologyInfo> getAllUris() {
		return allUris;
	}

	public static void setAllUris(List<RegisteredOntologyInfo> allUris) {
		Main.allUris = allUris;
	}

	public static Map<String, RegisteredOntologyInfo> getWorkingUris() {
		return workingUris;
	}

	public static void addWorkingUri(RegisteredOntologyInfo uri) {
		char code = (char) ((int) 'A' + Main.workingUris.size());
		uri.setCode(code);
		Main.workingUris.put(""+ code, uri);
	}

	public static boolean containsWorkingUri(RegisteredOntologyInfo uri) {
		return workingUris.get("" +uri.getCode()) != null;
	}


	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
      log("Util.getLocationProtocol() = " +Util.getLocationProtocol());
      log("Util.getLocationHost()     = " +Util.getLocationHost());
      log("GWT.getHostPageBaseURL()   = " +GWT.getHostPageBaseURL());
      log("GWT.getModuleBaseURL()     = " +GWT.getModuleBaseURL());
      baseUrl = Util.getLocationProtocol() + "//" + Util.getLocationHost();
      baseUrl = baseUrl.replace("/+$", "");   // remove trailing slashes
      log("baseUrl = " +baseUrl);
      
      Map<String,String> params = Util.getParams();
	  
      if ( params != null ) {
          String _log = (String) params.get("_log");
          if ( _log != null ) {
              includeLog = true;
              params.remove("_log");
          }
          
      }
      
      getSmlMorService();
      getAppInfo(params);
  }
  
  
  private void startGui(final Map<String,String> params) {

	  MainPanel mainPanel = new MainPanel();
	  HorizontalPanel hp = new HorizontalPanel();
	  RootPanel.get().add(hp);
	  // TODO logo
//	  hp.add(Main.images.logo().createImage());
	  hp.add(Util.createHtml("<br/>\n" +VERSION_COMMENT, 11));
	  RootPanel.get().add(mainPanel.getWidget());

      if ( includeLog ) {
          final HTML logLabel = Util.createHtml("", 10);
          ButtonBase buttonLog = Util.createButton("Refresh Log",
                  "Refresh log info", new ClickListener() {
                      public void onClick(Widget sender) {
                          logLabel.setHTML("<pre>" +log.toString()+ "</pre>");
                      }
                  });
          ButtonBase buttonClear = Util.createButton("Clear Log",
                  "Clear log info", new ClickListener() {
                      public void onClick(Widget sender) {
                          log.setLength(0);
                          logLabel.setHTML("");
                      }
                  });
          RootPanel.get().add(buttonLog);
          RootPanel.get().add(buttonClear);
          RootPanel.get().add(logLabel);    
      }
      else {
          log.setLength(0);
      }
      RootPanel.get().add(Util.createHtml("<font color=\"gray\">" +footer+ "</font><br/><br/>", 10));
  }
  
  private static void getSmlMorService() {
	  String moduleRelativeURL = GWT.getModuleBaseURL() + "smlmorService";
      log("Getting " +moduleRelativeURL+ " ...");
      smlmorService = (SmlMorServiceAsync) GWT.create(SmlMorService.class);
      ServiceDefTarget endpoint = (ServiceDefTarget) smlmorService;
      endpoint.setServiceEntryPoint(moduleRelativeURL);
      log("   smlmorService " +smlmorService);
  }
  
  
  
	private void getAppInfo(final Map<String, String> params) {
		AsyncCallback<AppInfo> callback = new AsyncCallback<AppInfo>() {
			public void onFailure(Throwable thr) {
				removeLoadingMessage();
				String error = thr.toString();
				while ( ( thr = thr.getCause()) != null ) {
					error += "\n" + thr.toString();
				}
				RootPanel.get().add(new Label(error));
			}

			public void onSuccess(AppInfo aInfo) {
				removeLoadingMessage();
				appInfo = aInfo;
				footer = appInfo.toString();
				startGui(params);
			}
		};

		log("Getting application info ...");
		smlmorService.getAppInfo(callback);
	}

  
  
  // always write to this buffer, but show contents if includeLog is true
  private static final StringBuffer log = new StringBuffer();
  public static void log(String msg) {
      log.append(msg+ "\n");
      GWT.log(msg, null);
  }

	private void removeLoadingMessage() {
    	Element loadingElement = DOM.getElementById("loading");
		if ( loadingElement != null ) {
			DOM.removeChild(RootPanel.getBodyElement(), loadingElement);
		}
    }

}
