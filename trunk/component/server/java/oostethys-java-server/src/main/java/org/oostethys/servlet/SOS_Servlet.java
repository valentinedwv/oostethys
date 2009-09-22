package org.oostethys.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.oostethys.sos.Netcdf2sos100;

public class SOS_Servlet extends HttpServlet {
	public String oostethysURL = null;
	private static final long serialVersionUID = 1L;


	private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger
		.getLogger(SOS_Servlet.class.getName());

	public void destroy() {
		super.destroy();
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		InputStream postInputStream = request.getInputStream();
		Netcdf2sos100 ns = new Netcdf2sos100();
		ns.setUrlOostethys(getOOSTethysConfigFile());
		ns.setServletURL(request.getRequestURL().toString());
		response.setContentType("text/xml");
		try {
			ns.process(postInputStream, response.getOutputStream());
		} catch (Exception e) {
		
			e.printStackTrace();
		}

	}
	
	

	private void printParameters(HttpServletRequest request,
			HttpServletResponse response)  {

		Map<?,?> map = request.getParameterMap();
		Set<?> set = map.keySet();
		logger.debug("Parameter map: ");
		Iterator<?> iterator = set.iterator();
		while (iterator.hasNext()) {
			Object key =  iterator.next();
			map.get(key);
			logger.debug(key + " ");
			Object value = map.get(key);
			logger.debug("type: " + value.getClass());

			String[] list = (String[]) map.get(key);
			for (int i = 0; i < list.length; i++) {
				logger.debug("value: " + list[i]);
			}

		}
		

	}

	

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		printParameters(request, response);


		Netcdf2sos100 ns = new Netcdf2sos100();
		response.setContentType("text/xml");
		ns.setUrlOostethys(getOOSTethysConfigFile());
		ns.setServletURL(request.getRequestURL().toString());

		try {
			@SuppressWarnings("unchecked")
			Map<String,String[]> parameterMap = request.getParameterMap();
			/* as a reminder that the types are correct: 
			 * from the Javadoc of ServletRequest: 
			 *  "The keys in the parameter map are of type String. 
			 *   The values in the parameter map are of type String array."
			 */
			
			ns.process(parameterMap, response.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
			// response.sendRedirect("check.jsp");
			return;
		}

	}

//	private void throwError_(Exception e, OutputStream outputStream) {
//		StringBuffer buffy = new StringBuffer(100);
//		buffy.append("<html>");
//		buffy.append("<h2>");
//		buffy.append("<p><b>Errors found: </b></p>");
//		StackTraceElement[] ele = e.getStackTrace();
//		for (int i = 0; i < ele.length; i++) {
//			buffy.append(ele.toString() + "<br>");
//		}
//		buffy.append("<\\html>");
//		try {
//			outputStream.write(buffy.toString().getBytes());
//			outputStream.close();
//		} catch (IOException e1) {
//		
//			e1.printStackTrace();
//		}
//
//	}

	private URL getOOSTethysConfigFile() {
		return Thread.currentThread().getContextClassLoader().getResource(
				"config/oostethys.xml");

	}
	
	

	
	public String getValueCaseInsensitive(HttpServletRequest request,
			String parameter) {
	    @SuppressWarnings("unchecked")
		java.util.Enumeration<String> e = request.getParameterNames();
		while (e.hasMoreElements()) {
			String key =  e.nextElement();
			if (key.equalsIgnoreCase(parameter)) {
				return request.getParameter(key);
			}
		}
		return null;

	}

	public void init() throws ServletException {

	}

}
