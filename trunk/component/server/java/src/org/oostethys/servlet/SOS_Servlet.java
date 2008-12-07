package org.oostethys.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.RedirectException;
import org.mmi.util.ResourceLoader;
import org.oostethys.sos.Netcdf2sos100;

import HTTPClient.Response;

public class SOS_Servlet extends HttpServlet {
	public String oostethysURL = "file:/Users/bermudez/Documents/workspace31/oostethys-xml-luis/oostethys/0.1.0/example/SimpleOostethys.xml";

	private static final long serialVersionUID = 1L;

	private String errorMessage;

	public Logger logger = Logger.getLogger(SOS_Servlet.class.getName());

	public void destroy() {
		super.destroy();
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO
		InputStream postInputStream = request.getInputStream();
		Netcdf2sos100 ns = new Netcdf2sos100();
		ns.setUrlOostethys(getOOSTethysConfigFile());
		ns.setServletURL(request.getRequestURL().toString());
		response.setContentType("text/xml");
		try {
			ns.process(postInputStream, response.getOutputStream());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	

	private void printParameters(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		StringBuffer buffy = new StringBuffer();
		buffy.append("parameters are:\r");
		Map map = request.getParameterMap();
		printMap(map);
		// Set set = map.keySet();
		// java.lang.System.out.println("Parameter map ");
		// for (Iterator iterator = set.iterator(); iterator.hasNext();) {
		// Object key = (Object) iterator.next();
		// java.lang.System.out.println(key + " ");
		// List list = (List) map.get(key);
		// for (Iterator iterator2 = list.iterator(); iterator2.hasNext();) {
		// Object object = (Object) iterator2.next();
		// java.lang.System.out.println("value: " + object);
		// buffy.append("value: " + (String)object);
		// }
		// }
		// logger.info(buffy.toString());
		// logger.info("URL config file" + getOOSTethysConfigFile());

	}

	private void printMap(Map map) {
		Set set = map.keySet();
		java.lang.System.out.println("Parameter map ");
		Iterator iterator = set.iterator();
		while (iterator.hasNext()) {
			Object key = (Object) iterator.next();
			map.get(key);
			java.lang.System.out.println(key + " ");
			Object value = map.get(key);
			System.out.println("type: " + value.getClass());

			String[] list = (String[]) map.get(key);
			for (int i = 0; i < list.length; i++) {
				java.lang.System.out.println("value: " + list[i]);
			}

		}

	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		printParameters(request, response);
		String value = getValueCaseInsensitive(request, "REQUEST");

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
			// TODO Auto-generated catch block
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
