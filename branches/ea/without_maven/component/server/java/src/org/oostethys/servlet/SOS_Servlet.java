package org.oostethys.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.oostethys.sos.Netcdf2sos100;

public class SOS_Servlet extends HttpServlet {
	public String oostethysURL = "file:/Users/bermudez/Documents/workspace31/oostethys-xml-luis/oostethys/0.1.0/example/SimpleOostethys.xml";

	private static final long serialVersionUID = 1L;



	public Logger logger = Logger.getLogger(SOS_Servlet.class.getName());

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
			HttpServletResponse response) throws ServletException, IOException {

	
		Map map = request.getParameterMap();
		Set set = map.keySet();
		logger.info("Parameter map: ");
		Iterator iterator = set.iterator();
		while (iterator.hasNext()) {
			Object key = (Object) iterator.next();
			map.get(key);
			java.lang.System.out.println(key + " ");
			Object value = map.get(key);
			logger.info("type: " + value.getClass());

			String[] list = (String[]) map.get(key);
			for (int i = 0; i < list.length; i++) {
				logger.info("value: " + list[i]);
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
			ns.process(request.getParameterMap(), response.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
			// response.sendRedirect("check.jsp");
			return;
		}

	}

	private void throwError_(Exception e, OutputStream outputStream) {
		StringBuffer buffy = new StringBuffer(100);
		buffy.append("<html>");
		buffy.append("<h2>");
		buffy.append("<p><b>Errors found: </b></p>");
		StackTraceElement[] ele = e.getStackTrace();
		for (int i = 0; i < ele.length; i++) {
			buffy.append(ele.toString() + "<br>");
		}
		buffy.append("<\\html>");
		try {
			outputStream.write(buffy.toString().getBytes());
			outputStream.close();
		} catch (IOException e1) {
		
			e1.printStackTrace();
		}

	}

	private URL getOOSTethysConfigFile() {
		return Thread.currentThread().getContextClassLoader().getResource(
				"config/oostethys.xml");

	}
	
	

	public String getValueCaseInsensitive(HttpServletRequest request,
			String parameter) {
		java.util.Enumeration e = request.getParameterNames();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			if (key.equalsIgnoreCase(parameter)) {
				return request.getParameter(key);
			}
		}
		return null;

	}

	public void init() throws ServletException {

	}

}
