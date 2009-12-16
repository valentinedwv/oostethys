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
	private String configFile = "config/oostethys.xml";


	public String getOostethysURL() {
		return oostethysURL;
	}

	public void setOostethysURL(String oostethysURL) {
		this.oostethysURL = oostethysURL;
	}

	public String getConfigFile() {
		return configFile;
	}

	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}

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



	private URL getOOSTethysConfigFile() {
		return Thread.currentThread().getContextClassLoader().getResource(
				configFile);

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
