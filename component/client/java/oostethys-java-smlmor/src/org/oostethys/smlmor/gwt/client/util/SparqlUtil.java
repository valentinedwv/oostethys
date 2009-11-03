package org.oostethys.smlmor.gwt.client.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mmisw.iserver.gwt.client.rpc.SparqlQueryInfo;
import org.mmisw.iserver.gwt.client.rpc.SparqlQueryResult;
import org.mmisw.iserver.gwt.client.util.QueryUtil;
import org.oostethys.smlmor.gwt.client.Main;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author Carlos Rueda
 */
public class SparqlUtil {
	
	public interface ResponseHandler {
		public void onFailure(Throwable caught);
		public void onError(String error);
		public void responseObtained(SparqlQueryResult sparqlQueryResult);
	}
	
	public static void runQuery(String queryString, final ResponseHandler responseHandler) {
		
		final SparqlQueryInfo sparqlQueryInfo = new SparqlQueryInfo();
		sparqlQueryInfo.setQuery(queryString);
		sparqlQueryInfo.setFormat("json");
		sparqlQueryInfo.setParseResult(true);
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
					QueryUtil.parseResult(sparqlQueryInfo, sqResult);
					responseHandler.responseObtained(sqResult);
				}
			}
			
		};
		
		Main.smlmorService.runSparqlQuery(sparqlQueryInfo, callback);
	}
	
}
