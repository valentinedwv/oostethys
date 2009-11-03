package org.oostethys.smlmor.gwt.client.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mmisw.iserver.gwt.client.rpc.SparqlQueryInfo;
import org.mmisw.iserver.gwt.client.rpc.SparqlQueryResult;
import org.oostethys.smlmor.gwt.client.Main;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author Carlos Rueda
 */
public class SparqlUtil {
	
	
	public static class Result {
		public SparqlQueryResult sqResult;
		public List<String> keys = new ArrayList<String>();
		public List<Map<String,String>> values = new  ArrayList<Map<String,String>>();
		
	}
	
	public interface ResponseHandler {
		public void onFailure(Throwable caught);
		public void onError(String error);
		public void responseObtained(Result result);
	}
	
	public static void runQuery(String queryString, final ResponseHandler responseHandler) {
		
		final SparqlQueryInfo sparqlQueryInfo = new SparqlQueryInfo();
		sparqlQueryInfo.setQuery(queryString);
		sparqlQueryInfo.setFormat("json");
		sparqlQueryInfo.setAcceptEntries("text/plain");
		sparqlQueryInfo.setEndPoint("http://mmisw.org/ont/");


		
		AsyncCallback<SparqlQueryResult> callback = new  AsyncCallback<SparqlQueryResult>() {

			public void onFailure(Throwable caught) {
				Main.log("---failure---");
				responseHandler.onFailure(caught);
			}

			public void onSuccess(SparqlQueryResult sqResult) {
				Main.log("---sparql completed---");
				if ( sqResult.getError() != null ) {
					String error = sqResult.getError();
					responseHandler.onError(error);
				}
				else {
					String text = sqResult.getResult();
					Result result = new Result();
					result.sqResult = sqResult;

					JSONValue jsonValue = JSONParser.parse(text);
					dispatchJsonResponse(jsonValue, sqResult, result);
					
					responseHandler.responseObtained(result);
				}
			}
			
		};
		
		Main.smlmorService.runSparqlQuery(sparqlQueryInfo, callback);
	}

	private static void dispatchJsonResponse(JSONValue jsonValue, SparqlQueryResult sqResult, Result result) {
		
		JSONArray vars = jsonValue.isObject().get("head").isObject().get("vars").isArray();
		for ( int i = 0, cnt = vars.size(); i < cnt; i++ ) {
			String key = valueOfNoQuotes(vars.get(i));
			result.keys.add(key);
		}
		
		JSONArray bindings = jsonValue.isObject().get("results").isObject().get("bindings").isArray();
		
		for ( int i = 0, cnt = bindings.size(); i < cnt; i++ ) {
			JSONObject tuple = bindings.get(i).isObject();
			Map<String,String> record = new HashMap<String,String>();
			for ( String key : result.keys ) {
				JSONValue val = tuple.get(key);
				// the following check because there may be OPTIONAL variables with no value
				String value = val == null ? "" : valueOfNoQuotes(val.isObject().get("value"));
				record.put(key, value);
			}
			result.values.add(record);
		}
	}
	
	private static String valueOfNoQuotes(Object obj) {
		return String.valueOf(obj).replaceAll("^\"+|\"+$", "");
	}
	
}
