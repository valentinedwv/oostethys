package org.oostethys.sos;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.opengis.ows.GetCapabilitiesDocument;
import net.opengis.sos.x10.DescribeSensorDocument;
import net.opengis.sos.x10.GetObservationDocument;
import net.opengis.sos.x10.GetObservationDocument.GetObservation;
import net.opengis.sos.x10.GetObservationDocument.GetObservation.EventTime;

import org.apache.commons.lang.StringUtils;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.impl.values.XmlValueOutOfRangeException;
import org.oostethys.model.VariableQuantity;
import org.oostethys.model.VariablesConfig;
import org.oostethys.model.impl.ObservationNetcdf;
import org.oostethys.model.impl.VariableQuantityImpl;
import org.oostethys.netcdf.util.TimeUtil;
import org.oostethys.schemas.x010.oostethys.OostethysDocument;
import org.oostethys.schemas.x010.oostethys.BoundingBoxDocument.BoundingBox;
import org.oostethys.schemas.x010.oostethys.ComponentsDocument.Components;
import org.oostethys.schemas.x010.oostethys.EnvelopeDocument.Envelope;
import org.oostethys.schemas.x010.oostethys.ExtendDocument.Extend;
import org.oostethys.schemas.x010.oostethys.LastKnownLocationDocument.LastKnownLocation;
import org.oostethys.schemas.x010.oostethys.OostethysDocument.Oostethys;
import org.oostethys.schemas.x010.oostethys.OutputDocument.Output;
import org.oostethys.schemas.x010.oostethys.SourceConfigurationDocument.SourceConfiguration;
import org.oostethys.schemas.x010.oostethys.SystemDocument.System;
import org.oostethys.schemas.x010.oostethys.TimePeriodDocument.TimePeriod;
import org.oostethys.schemas.x010.oostethys.VariableDocument.Variable;
import org.oostethys.schemas.x010.oostethys.VariablesDocument.Variables;
import org.oostethys.schemas.x010.oostethysNetcdf.OostethysNetcdfDocument.OostethysNetcdf;
import org.oostethys.voc.Voc;

public class DataTurbine2SOS100 {

	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger
			.getLogger(DataTurbine2SOS100.class.getName());

	private URL urlOostethys;
	private OostethysDocument oostDoc;
	private String tempDir = "xml/oostethys/0.1.0/tmp/";
	private String xsltDir = "xml/oostethys/0.1.0/xslt/";
	private String getCapabilitiesXSLT = "oostethys2getCapabilities.xsl";
	private String descibeSensorXSLT = "oostethys2describeSensor.xsl";
	private String getObservationXSLT = "oostethys2getObservation.xsl";

	// query parameters

	private final static String EVENT_TIME = "EVENT_TIME";
	private final static String BBOX = "BBOX";
	private final static String OBSERVED_PROPERTY = "OBSERVED_PROPERTY";
	private final static String PROCEDURE = "PROCEDURE";
	private final static String SERVICE = "SERVICE";
	private final static String REQUEST = "REQUEST";
	private final static String SRS_NAME = "SRSNAME";
	private final static String RESPONSE_MODE = "RESPONSEMODE";

	private final static String GET_CAPABILITIES = "GetCapabilities";
	private final static String DESCRIBE_SENSOR = "DescribeSensor";
	private final static String GET_OBSERVATION = "GetObservation";
	private final static String VERSION = "VERSION";
	private final static String VERSION_NUMBER_SOS = "1.0.0";
	private final static String RESPONSE_FORMAT = "RESPONSEFORMAT";
	private static final String OUTPUT_FORMAT = "OUTPUTFORMAT";

	private static final String OFFERING = "OFFERING";

	private String value_PROCEDURE = null;
	private String value_OBSERVED_PROPERTY = null;
	private String value_BBOX = null;
	private String value_EVENT_TIME = null;
	private String value_BEGIN = null;
	private String value_END = null;

	public final static String default2dsrsName = "urn:ogc:def:crs:EPSG:6.17:4326";
	public final static String responseFormat = "text/xml;subtype=\"sensorML/1.0.1\"";
	public final static String resultModel = "om:Observation";
	public final static String responseMode = "inline";

	private static final String RESULT_MODEL = "RESULTMODEL";

	private Map<String, String> parameterMap = null;

	private OostethysDocument oostDocTemp = null;
	private String requestURL;
	private String requestSensorID = null;
	private String offeringID = null;
	private InputStream postInputStream;
	private URL xsltUrl;
	private URL tempUrl;

	private int numberOfRecordsToProcess = 100;

	/**
	 * Constructor. Instantiates the logger, and xslt path and temporary
	 * directories.
	 * 
	 */
	public DataTurbine2SOS100() {
		xsltUrl = Thread.currentThread().getContextClassLoader().getResource(
				xsltDir);
		tempUrl = Thread.currentThread().getContextClassLoader().getResource(
				tempDir);
		log.debug("URLS: " + xsltUrl + ":" + tempUrl);
		xsltDir = (xsltUrl != null ? xsltUrl.getPath() : null);
		tempDir = (tempUrl != null ? tempUrl.getPath() : null);
		log.debug("start logging");

	}

	/**
	 * Processes request post xml. Gets a stream that encapsulates an XML
	 * request. It guesses which type of SOS operation it is and call the
	 * appropriate method. The methods are all passing an output stream where he
	 * respond will be streamed. It gets the error if any and throws it. Errors
	 * for example if a parameter is missing
	 * 
	 * @param inputStream
	 * @throws Exception
	 */
	public void process(InputStream inputStream, OutputStream os)
			throws Exception {
		log.debug("processing");

		BufferedReader in = new BufferedReader(new InputStreamReader(
				inputStream));
		StringBuffer buffer = new StringBuffer();
		String line = null;
		while ((line = in.readLine()) != null) {
			buffer.append(line);
		}

		String stream = buffer.toString();

		boolean requestProcessed = false;

		try {
			GetCapabilitiesDocument.Factory.parse(new StringReader(stream));

			getCapabilities(os);
			requestProcessed = true;
		} catch (XmlException e) {
			// this is not a GetObservation request
		}

		if (!requestProcessed) {
			try {
				GetObservationDocument getObs = GetObservationDocument.Factory
						.parse(stream);

				if (!processParametersGetObservation(getObs, os)) {
					return;
				}
				getObservation(os);

				requestProcessed = true;
			} catch (XmlException e) {
				// this is not a GetObservation request
			}
		}
		if (!requestProcessed) {
			try {
				DescribeSensorDocument describeSensor = DescribeSensorDocument.Factory
						.parse(new StringReader(stream));
				processParametersDescribeSensor(describeSensor);
				getDescribeSensor(os);
				requestProcessed = true;

			} catch (XmlException e) {
				// this is not a GetObservation request
			}
		}
		if (!requestProcessed)
			report(
					"InvalidRequest",
					null,
					"Not able to understand the operation. This service supports the following operations: "
							+ GET_CAPABILITIES
							+ ", "
							+ DESCRIBE_SENSOR
							+ " and, " + GET_OBSERVATION, os);

	}

	public void getCapabilities(OutputStream outputStream) {

		boolean isGood = okGetCapabilitiesParameters(outputStream);
		if (!isGood) {
			return;
		}
		ExceptionReporter reporter = new ExceptionReporter();

		if (requestURL == null) {

			String report = reporter.create(ExceptionReporter.NoApplicableCode,
					SERVICE,
					"Missing request URL in the OOSTethys configuration file.");

			try {
				outputStream.write(report.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			try {
				process();
			} catch (Exception e) {
				e.printStackTrace();
				report(ExceptionReporter.NoApplicableCode, null, e.toString(),
						outputStream);
				return;
			}

			StringReader reader = new StringReader(oostDocTemp.xmlText());
			File xsltGetCapabilitiesFile = getXSLTFile(getCapabilitiesXSLT);
			getXML(outputStream, xsltGetCapabilitiesFile, reader);
			try {
				saveOOSTethysTempFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void getDescribeSensor(OutputStream outputStream) {

		if (!okDescribeSensorParameters(outputStream)) {
			return;
		}

		try {
			process();
		} catch (Exception e) {
			e.printStackTrace();
			report(ExceptionReporter.NoApplicableCode, null, e.toString(),
					outputStream);
			return;
		}

		String queryExpression = "declare namespace oost='http://www.oostethys.org/schemas/0.1.0/oostethys';"
				+ "$this//oost:system";
		OostethysDocument oostDoc = OostethysDocument.Factory.newInstance();

		Oostethys oostTemp = (Oostethys) oostDocTemp.getOostethys().copy();

		System[] systems = (System[]) oostTemp.selectPath(queryExpression);

		if (systems.length == 0) {
			report(ExceptionReporter.InvalidParameterValue, null,
					"Not able to find any system", outputStream);

		}

		List<System> systemsTodAdd = new ArrayList<System>();

		String procedures = parameterMap.get(PROCEDURE);

		// find systems to add
		for (int i = 0; i < systems.length; i++) {
			String systemId = systems[i].getMetadata().getSystemIdentifier();
			StringTokenizer st = new StringTokenizer(procedures, ",");
			while (st.hasMoreElements()) {
				String token = st.nextToken();
				if (systemId.equalsIgnoreCase(token)) {
					systemsTodAdd.add((System) systems[i].copy());
				}
			}

		}

		if (systemsTodAdd.size() == 0) {
			report(ExceptionReporter.InvalidParameterValue, PROCEDURE,
					"Not able to find procedures: " + procedures, outputStream);
			return;

		}

		oostTemp.getComponents().setNil();

		System[] sysArray = new System[systemsTodAdd.size()];
		int i = 0;
		for (Iterator<System> iterator = systemsTodAdd.iterator(); iterator
				.hasNext();) {
			System system = iterator.next();
			sysArray[i] = system;
			i = i + 1;

		}

		oostTemp.getComponents().setSystemArray(sysArray);

		File xsltGetCapabilitiesFile = getXSLTFile(descibeSensorXSLT);

		oostDoc.setOostethys(oostTemp);

		String xmlText = oostDoc.xmlText();

		if (log.isDebugEnabled()) {
			try {
				saveOOSTethysTempFile();
			} catch (IOException e) {
				// just logging
			}
		}

		getXML(outputStream, xsltGetCapabilitiesFile, new StringReader(xmlText));
	}

	public void getObservation(OutputStream outputStream) {

		if (!okGetObservationParameters(outputStream)) {
			printMap(parameterMap);
			return;
		}

		try {
			// process the stream !
			process();
		} catch (Exception e) {
			e.printStackTrace();
			report(ExceptionReporter.NoApplicableCode, null, e.toString(),
					outputStream);
			return;
		}

		// check result model

		Oostethys oost = getOOSTethysObservationFilter(outputStream);
		if (oost != null) {

			File xsltGetCapabilitiesFile = getXSLTFile(getObservationXSLT);
			getXML(outputStream, xsltGetCapabilitiesFile, new StringReader(
					oostDocTemp.xmlText()));
		}

	}

	/**
	 * Processes OOSTethys config file.
	 * 
	 * @throws Exception
	 */
	private void process() throws Exception {

		if (urlOostethys == null) {
			throw new Exception("Need URL of OOSTethys config file");
		}

		// temporary object to store the results
		oostDocTemp = OostethysDocument.Factory.newInstance();

		try {
			// oostethys config file
			oostDoc = OostethysDocument.Factory.parse(urlOostethys);

			// now validated !
		XmlOptions validationOptions = new XmlOptions();
			ArrayList<?> validationErrors = new ArrayList<Object>();
			validationOptions.setErrorListener(validationErrors);
			boolean isValid = oostDoc.validate(validationOptions);
			if (!isValid) {
				Iterator<?> iter = validationErrors.iterator();
				StringBuffer buffy = new StringBuffer();
				buffy.append("XML Instance is not valid:");
				while (iter.hasNext()) {
					buffy.append(iter.next().toString() + ".\n");
				}
			throw new Exception(buffy.toString());
			}

			Oostethys oost = oostDoc.getOostethys();

			// copy general metadata in new document
			Oostethys oostTemp = oostDocTemp.addNewOostethys();
			oostTemp.setWebServerURL(getServletURL());

			oostTemp.setServiceContact(oost.getServiceContact());

			// process componenets // variables etc..

			doComponents(oost.getComponents(), oostTemp.addNewComponents());

		} catch (XmlException e) {
			throw new Exception("Not able to read the oostethys config file: "
					+ urlOostethys, e);
		} catch (IOException e) {
			throw new Exception("Not able to open the oostethys config file: "
					+ urlOostethys, e);
		} catch (Exception e) {
			throw e;
		}

	}

	/**
	 * Process the components of system by copying in a temp Component all the
	 * components of the original config file
	 * 
	 * @param components
	 * @param componentsTemp
	 * @throws Exception
	 */
	public void doComponents(Components components, Components componentsTemp)
			throws Exception {
		if (components != null) {
			System[] systems = components.getSystemArray();

			for (int i = 0; i < systems.length; i++) {
				if (checkConditionSystem(systems[i].getMetadata()
						.getSystemIdentifier())) {

					System sys = componentsTemp.addNewSystem();
					processSystem(systems[i], sys);

					// now process sub components
					doComponents(systems[i].getComponents(), sys
							.addNewComponents());

				}
			}

		}

	}

	// public void resetValues() {
	// value_PROCEDURE = value_OBSERVED_PROPERTY = null;
	// }
	
	
	/**
	 * Processes a system declared in the OOST model based on a netcdf configuration 
	 * @param oosn
	 * @param systemTemp
	 * @throws Exception
	 */
	private void processSystemsNetCDFConfiguration(OostethysNetcdf oosn, System systemTemp) throws Exception{
		
		
		String fileNCURL = oosn.getFileURL();
		if (fileNCURL.length()==0){
			throw new Exception("NetCDF URL was not provided");
		}
		 
		URL url = null;
		try {
			url = new URL(fileNCURL);
			if (url == null) {
				throw new Exception();
			}

		} catch (Exception e) {
			try {
				// then... try to find the resource locally.. this
				// is in WEB-INF/classes/
				url = Thread.currentThread()
						.getContextClassLoader().getResource(
								fileNCURL);
				// url.openConnection();
			} catch (Exception e2) {
				throw new Exception("Not found netCDF file "
						+ fileNCURL, e2);
			}

		}

		ObservationNetcdf obsNC = new ObservationNetcdf();
		// set the dataset
		obsNC.setURL(url.toString());
		obsNC.setNumberOfRecords(numberOfRecordsToProcess);

		if (value_BBOX != null) {
			obsNC.setValue_BBOX(value_BBOX);
		}
		if (value_EVENT_TIME != null) {
			obsNC.setValue_TIME(value_EVENT_TIME);
		}

		// set variables in a collection of variables config - only
		// using the short name
		obsNC.setVariablesConfig(getVariablesConfig(oosn));

		// process
		obsNC.process();
		// update the modelXML

		// updateTime
		TimePeriod tp = TimePeriod.Factory.newInstance();
		tp
				.setStart(TimeUtil.getISOFromMillisec(obsNC
						.getMinTime()));
		tp.setEnd(TimeUtil.getISOFromMillisec(obsNC.getMaxTime()));
		Extend extend = systemTemp.addNewExtend();
		extend.setTimePeriod(tp);

		// update BBOX
		BoundingBox bbox = BoundingBox.Factory.newInstance();
		Envelope env = bbox.addNewEnvelope();
		env.setLowerCorner(obsNC.getLowerCornerEPSG());
		env.setUpperCorner(obsNC.getUpperCornerEPSG());
		extend.setBoundingBox(bbox);

		// adds latest known position -true for in-situ sensors
		LastKnownLocation loc = LastKnownLocation.Factory
				.newInstance();
		loc.addNewSrsName().setStringValue(default2dsrsName);
		loc.setStringValue(obsNC.getLastKnownPositionEPSG());
		systemTemp.getMetadata().setLastKnownLocation(loc);

		// update Variables

		List<Variable> variablesList = new ArrayList<Variable>();

		List<VariableQuantity> vars = obsNC.getVariables();

		for (VariableQuantity var : vars) {
			if (checkConditionVariable(var.getURI())) {
				// create new variable
				Variable varTemp = Variable.Factory.newInstance();
				varTemp.setUri(var.getURI());
				varTemp.setName(var.getStandardName());

				// some variables in a nc file may not have units..
				if (var.getUnits().getLabel() != null
						&& !var.getUnits().getLabel().trim()
								.equals("")) {
					varTemp.setUom(var.getUnits().getLabel());
				} else {
				}
				varTemp.setIsCoordinate(var.isCoordinate());

				if (var.getReferenceFrame() != null) {
					varTemp.setReferenceFrame(var
							.getReferenceFrame());
				}

				if (var.isTime()) {
					varTemp.setIsTime(true);
					// even if it has a CF standard name - we will
					// converted t ISO 8601
					varTemp.setUri(Voc.time);
				}

				log.info("units set for " + varTemp.getUri() + " "
						+ varTemp.getUom());

				// add to temp sensor
				variablesList.add(varTemp);
			}

		}

		Variable[] varArray = new Variable[variablesList.size()];
		int i = 0;
		for (Iterator<Variable> iterator = variablesList.iterator(); iterator
				.hasNext();) {
			varArray[i] = iterator.next();
			i = i + 1;
		}

		Variables variablesTemp = Variables.Factory.newInstance();
		variablesTemp.setVariableArray(varArray);

		Output outputTemp = Output.Factory.newInstance();
		outputTemp.setVariables(variablesTemp);
		outputTemp.setValues(obsNC.getAsRecords());
		systemTemp.setOutput(outputTemp);

	
		
	}

	/**
	 * process a netcdf
	 * 
	 * @param system
	 * @param systemTemp
	 * @throws Exception
	 */
	private void processSystem(System system, System systemTemp)
			throws Exception {
		systemTemp.setMetadata(system.getMetadata());
	
		// check if the system meets the requirements
	
		Output output = system.getOutput();
		if (output != null) {
			SourceConfiguration sourceConfiguration = output
					.getSourceConfiguration();
			if (sourceConfiguration != null) {
				
				OostethysNetcdf oosn = sourceConfiguration.getOostethysNetcdf();
				if (oosn != null) {
					// configuration is netcdf file
					processSystemsNetCDFConfiguration(oosn ,systemTemp);
				}else{
					// txt configuration
					//processSystemsCSVConfiguration(null ,systemTemp);
				}
					
			}
	
		}
	
		// return systemTemp;
	
	}

	public boolean checkConditionSystem(String uri) {
		return true;
		// return value_PROCEDURE == null ? true : value_PROCEDURE.equals(uri);
	}

	public boolean checkConditionVariable(String uri) {
		return true;
	}

	private void printMap(Map<String, ?> map) {
		if (log.isDebugEnabled()) {
			if (map == null)
				return;
			Set<?> set = map.keySet();
			log.debug("Parameter map ");
			Iterator<?> iterator = set.iterator();
			while (iterator.hasNext()) {
				Object key = iterator.next();

				Object value = map.get(key);
				if (value instanceof String[]) {
					log.debug(key + ", " + Arrays.toString((String[]) value));
				} else {
					log.debug(key + ", " + map.get(key));
				}
			}
		}
	}

	/**
	 * process requests http get
	 * 
	 * @param map
	 * @param outputStream
	 * @throws Exception
	 */
	public void process(Map<String, String[]> map, OutputStream os)
			throws Exception {
		printMap(map);
		// assign parameters
		parameterMap = new HashMap<String, String>();

		// change all the value to be upper case since parameter names are case
		// insensitive
		if (map != null) {
			Set<String> keys = map.keySet();
			for (Iterator<String> iterator = keys.iterator(); iterator
					.hasNext();) {
				String key = iterator.next();
				String[] values = map.get(key);
				if (values.length > 0) {
					String value = map.get(key)[0];
					parameterMap.put(key.toUpperCase(), value);
				}
			}
		}
		// guess which operation its being called

		// validate request parameters

		String operation = parameterMap.get(REQUEST);

		if (operation == null) {
			getCapabilities(os);
		} else {

			if (operation.equalsIgnoreCase(GET_CAPABILITIES)) {
				getCapabilities(os);
			} else if (operation.equalsIgnoreCase(GET_OBSERVATION)) {
				getObservation(os);
			} else if (operation.equals(DESCRIBE_SENSOR)) {
				getDescribeSensor(os);
			} else {
				report(
						"InvalidParameterValue",
						REQUEST,
						"Not able to understand the operation. This service supports the following operations: "
								+ GET_CAPABILITIES
								+ ", "
								+ DESCRIBE_SENSOR
								+ " and, " + GET_OBSERVATION, os);
			}
		}

	}

	/**
	 * This is only for testing purpose
	 * 
	 * @throws Exception
	 * @throws Exception
	 */

	public void processForTest() throws Exception {
		process();
	}

	public String saveOOSTethysTempFile() throws IOException {
		File tempFile = File.createTempFile("oostethysTemp", ".xml");

		tempFile.createNewFile();
		oostDocTemp.save(tempFile);
		log.info("saved file " + tempFile);
		tempFile.deleteOnExit();

		return tempFile.getAbsolutePath();

	}

	public String getOOSTethysDoc() {

		StringWriter sw = new StringWriter();
		try {
			oostDocTemp.save(sw);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sw.toString();

	}

	private VariablesConfig getVariablesConfig(OostethysNetcdf oosnc) {

		org.oostethys.schemas.x010.oostethysNetcdf.VariableDocument.Variable[] variables = oosnc
				.getVariables().getVariableArray();
		VariablesConfig config = new VariablesConfig();

		for (int i = 0; i < variables.length; i++) {
			org.oostethys.schemas.x010.oostethysNetcdf.VariableDocument.Variable var = variables[i];
			String shortName = var.getShortName();
			String dim = var.getDimension().toString();
			String uri = var.getUri();
			VariableQuantity varQ = new VariableQuantityImpl();
			varQ.setURI(uri);
			varQ.setLabel(shortName);
			if (dim.equals("no")) {
				varQ.setCoordinate(false);
			} else {
				varQ.setCoordinate(true);
				if (dim.equals("time")) {
					varQ.setIsTime(true);
				}
			}

			config.addVariable(varQ);
			
		}

		return config;
	}

	/**
	 * @return the oostDoc
	 */
	public OostethysDocument getOostDoc() {
		return oostDoc;
	}

	public void updateURLServices() {

		oostDocTemp.getOostethys().setWebServerURL(this.requestURL);
	}

	private File getXSLTFile(String xslt) {
		// String file = ResourceLoader.getPath("xml");
		URL url = Thread.currentThread().getContextClassLoader().getResource(
				"xml");
		String file = url.getPath();

		File xsltF = new File(file + "/oostethys/0.1.0/xslt/", xslt);
		log.debug("XSLT FILE: " + file + " ---  " + xsltF);
		return xsltF;

	}

	/**
	 * returns the observation from oostethys doc that matches the
	 * systemShortName and the observation offering
	 * 
	 * @param observationOffering
	 * @return
	 */
	public Oostethys getOOSTethysObservationFilter(OutputStream outputStream) {
		// the xslts create observation offering id from the
		// oost:metadata/oost:systemShortName, and
		// add "observationOffering_"

		// do after it has been processsed

		// /oost:oostethys/oost:components[1]/oost:system[1]/oost:components[1]/oost:system[1]/oost:components[1]/oost:system[1]/oost:metadata[1]/oost:systemShortName[1]

		// create copy oostethys
		Oostethys oostTemp = (Oostethys) oostDocTemp.getOostethys().copy();

		// get all systems

		String queryExpression = "declare namespace oost='http://www.oostethys.org/schemas/0.1.0/oostethys';"
				+ "$this//oost:system";

		System[] systems = (System[]) oostTemp.selectPath(queryExpression);

		// add systems found to generate the getObsColllection
		List<System> systemsTodAdd = new ArrayList<System>();

		String requestedOffering = parameterMap.get(OFFERING);

		// find systems to add
		for (System system : systems) {
			final String systemShortName = system.getMetadata()
					.getSystemShortName();

			String requestedSystemShort = StringUtils.substringAfter(
					requestedOffering, "_");
			if (StringUtils.equals(requestedSystemShort, systemShortName)) {
				systemsTodAdd.add((System) system.copy());
			}
		}

		// check if there nothing was found - return exception

		if (systemsTodAdd.size() == 0) {
			report(ExceptionReporter.InvalidParameterValue, OFFERING,
					"Not able to find any observation with id: "
							+ requestedOffering, outputStream);
			return null;

		}

		// for each system get oost:metadata/oost:systemShortName

		oostTemp.getComponents().setNil();

		System[] sysArray = new System[systemsTodAdd.size()];
		int i = 0;
		for (Iterator<System> iterator = systemsTodAdd.iterator(); iterator
				.hasNext();) {
			System system = iterator.next();
			sysArray[i] = system;
			i = i + 1;

		}

		oostTemp.getComponents().setSystemArray(sysArray);

		// if it matches (lastPrt of ofering string )
		// add to xml that system
		// return new oostethys document

		return oostTemp;

	}

	public void getOOSTethys(OutputStream outputStream) {
		try {
			process();
		} catch (Exception e) {
			e.printStackTrace();
			report(ExceptionReporter.NoApplicableCode, null, e.toString(),
					outputStream);
			return;
		}
		try {
			oostDocTemp.save(outputStream);
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	public void getKML(OutputStream outputStream) {

	}

	private void getXML(OutputStream outputStream, File xsltFile,
			Reader xmlFileReader) {
		java.lang.System.setProperty("javax.xml.transform.TransformerFactory",
				"net.sf.saxon.TransformerFactoryImpl");

		TransformerFactory tfactory = TransformerFactory.newInstance();
		InputStream xslIS;
		try {
			xslIS = new BufferedInputStream(new FileInputStream(xsltFile));

			StreamSource xslSource = new StreamSource(xslIS);

			// The following line would be necessary if the stylesheet contained
			// an xsl:include or xsl:import with a relative URL
			// xslSource.setSystemId(xslID);

			// Create a transformer for the stylesheet.
			Transformer transformer = tfactory.newTransformer(xslSource);
			// InputStream xmlIS = new BufferedInputStream(new FileInputStream(
			// xmlFile));
			//			

			StreamSource xmlSource = new StreamSource(xmlFileReader);

			// The following line would be necessary if the source document
			// contained
			// a call on the document() function using a relative URL
			// xmlSource.setSystemId(sourceID);

			// Transform the source XML to System.out.
			transformer.transform(xmlSource, new StreamResult(outputStream));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// private void getXML(OutputStream outputStream, File xsltFile, File
	// xmlFile) {
	// java.lang.System.setProperty("javax.xml.transform.TransformerFactory",
	// "net.sf.saxon.TransformerFactoryImpl");
	//
	// TransformerFactory tfactory = TransformerFactory.newInstance();
	// InputStream xslIS;
	// try {
	// xslIS = new BufferedInputStream(new FileInputStream(xsltFile));
	//
	// StreamSource xslSource = new StreamSource(xslIS);
	//
	// // The following line would be necessary if the stylesheet contained
	// // an xsl:include or xsl:import with a relative URL
	// // xslSource.setSystemId(xslID);
	//
	// // Create a transformer for the stylesheet.
	// Transformer transformer = tfactory.newTransformer(xslSource);
	// InputStream xmlIS = new BufferedInputStream(new FileInputStream(
	// xmlFile));
	// StreamSource xmlSource = new StreamSource(xmlIS);
	//
	// // The following line would be necessary if the source document
	// // contained
	// // a call on the document() function using a relative URL
	// // xmlSource.setSystemId(sourceID);
	//
	// // Transform the source XML to System.out.
	// transformer.transform(xmlSource, new StreamResult(outputStream));
	// } catch (FileNotFoundException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (TransformerConfigurationException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (TransformerException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }

	/**
	 * @return the urlOostethys
	 */
	public URL getUrlOostethys() {
		return urlOostethys;
	}

	/**
	 * URL of the config file
	 * 
	 * @param ys
	 *            the urlOostethys to set
	 */
	public void setUrlOostethys(URL url) {
		this.urlOostethys = url;
	}

	public String getValue_PROCEDURE() {
		return value_PROCEDURE;
	}

	/**
	 * @param value_PROCEDURE
	 *            the value_PROCEDURE to set
	 */
	public void setValue_PROCEDURE(String value_PROCEDURE) {
		this.value_PROCEDURE = value_PROCEDURE;
	}

	/**
	 * @return the value_OBSERVED_PROPERTY
	 */
	public String getValue_OBSERVED_PROPERTY() {
		return value_OBSERVED_PROPERTY;
	}

	/**
	 * @param value_OBSERVED_PROPERTY
	 *            the value_OBSERVED_PROPERTY to set
	 */
	public void setValue_OBSERVED_PROPERTY(String value_OBSERVED_PROPERTY) {
		this.value_OBSERVED_PROPERTY = value_OBSERVED_PROPERTY;
	}

	/**
	 * @return the value_BBOX
	 */
	public String getValue_BBOX() {
		return value_BBOX;
	}

	/**
	 * @param value_BBOX
	 *            the value_BBOX to set
	 */
	public void setValue_BBOX(String value_BBOX) {
		this.value_BBOX = value_BBOX;
	}

	/**
	 * @return the oostDocTemp
	 */
	public OostethysDocument getOostDocTemp() {
		return oostDocTemp;
	}

	/**
	 * @return the parameterMap
	 */
	public Map<String, String> getParameterMap() {
		return parameterMap;
	}

	/**
	 * @param parameterMap
	 *            the parameterMap to set if this is call by http get
	 */
	// public void setParameterMap(Map<String, String> parameterMap) {
	// this.parameterMap = parameterMap;
	// // doCheck();
	// }
	public boolean doCheck() {
		boolean isOK = false;

		Set<String> set = parameterMap.keySet();

		for (String key : set) {
			if (key.equalsIgnoreCase("service")) {
				if (!parameterMap.get(key).equalsIgnoreCase("SOS")) {
					// set message error
					return false;
				}
			}
		}

		for (String key : set) {
			if (key.equalsIgnoreCase("version")) {
				if (!parameterMap.get(key).equalsIgnoreCase("1.0.0")) {
					// set message error
					return false;
				}
			}
		}

		return isOK;

	}

	private boolean checkCommonParameters(OutputStream os) {
		final String service = parameterMap.get(SERVICE);
		final String version = parameterMap.get(VERSION);

		if (version != null) {
			// report(ExceptionReporter.MissingParameterValue, VERSION,
			// "Need parameter: " + VERSION + " ", os);
			// return false;
			// } else {
			if (!StringUtils.equalsIgnoreCase(version, VERSION_NUMBER_SOS)) {
				report(ExceptionReporter.InvalidParameterValue, VERSION,
						"The version: " + version
								+ " is not supported. Service should be "
								+ VERSION_NUMBER_SOS, os);
				return false;
			}
		}

		if (service == null) {
			report(ExceptionReporter.MissingParameterValue, SERVICE,
					"Need parameter: " + SERVICE + " ", os);
			return false;
		} else {
			if (!StringUtils.equalsIgnoreCase(service, "SOS")) {

				report(
						ExceptionReporter.InvalidParameterValue,
						SERVICE,
						"The service: " + service
								+ " is not supported. Service should be 'SOS'.",
						os);
				return false;
			}
		}

		if (parameterMap.get("ACCEPTVERSIONS") != null
				&& !StringUtils.equals(VERSION_NUMBER_SOS, parameterMap
						.get("ACCEPTVERSIONS"))) {
			report("VersionNegotiationFailed", "AcceptVersions",
					"The parameter 'AcceptVersions' does not contain the version of this SOS: '"
							+ VERSION_NUMBER_SOS + "'", os);
			return false;
		}

		return true;
	}

	private boolean okDescribeSensorParameters(OutputStream os) {
		if (!hasParameters(os)) {
			return false;
		}

		if (!checkCommonParameters(os))
			return false;

		String outputFormat = parameterMap.get(OUTPUT_FORMAT);
		String sensorId = parameterMap.get(PROCEDURE);

		if (outputFormat == null) {
			report(ExceptionReporter.MissingParameterValue, "outputFormat",
					"Need parameter: " + OUTPUT_FORMAT + " ", os);
			return false;
		} else {
			if (!StringUtils.equalsIgnoreCase(responseFormat, outputFormat)) {
				report(ExceptionReporter.InvalidParameterValue, "outputFormat",
						"The output format supported is: " + responseFormat
								+ " And you are asking for:" + outputFormat, os);
				return false;
			}
		}

		if (sensorId == null) {
			report(ExceptionReporter.MissingParameterValue, PROCEDURE,
					"Need parameter: " + PROCEDURE + " ", os);
			return false;
		}

		return true;
	}

	private boolean okGetObservationParameters(OutputStream os) {

		if (!checkCommonParameters(os))
			return false;

		boolean hasOffering = false;

		String supportedResponseFormat = "text/xml; subtype=\"om/1.0.0\"";

		final String offering = parameterMap.get(OFFERING);
		final String procedure = parameterMap.get(PROCEDURE);
		final String responesFormat = parameterMap.get(RESPONSE_FORMAT);
		final String eventType = parameterMap.get(EVENT_TIME);
		final String boundingBox = parameterMap.get(BBOX);
		final String observedProperty = parameterMap.get(OBSERVED_PROPERTY);

		if (responesFormat != null) {
			if (!StringUtils.equalsIgnoreCase(responesFormat,
					supportedResponseFormat)) {
				report(ExceptionReporter.InvalidParameterValue,
						RESPONSE_FORMAT, "The responseFormat: "
								+ responesFormat
								+ " is not supported. It should be "
								+ responseFormat, os);
				return false;
			}

		}

		if (offering != null) {
			hasOffering = true;
			offeringID = offering;
		}
		if (procedure != null) {
			requestSensorID = procedure;
		}

		if (eventType != null) {
			setValue_EventTime(eventType);
			String[] values = value_EVENT_TIME.split("/");

			try {
				// long start =
				TimeUtil.getMillisec(values[0]);

				// long end =
				TimeUtil.getMillisec(values[1]);
			} catch (Exception e) {
				report(
						ExceptionReporter.InvalidParameterValue,
						EVENT_TIME,
						"time should be giving following this format: \"yyyy-MM-dd'T'HH:mm:ss'Z'\" ",
						os);
				e.printStackTrace();
				return false;
			}

		}

		if (boundingBox != null)
			setValue_BBOX(boundingBox);
		if (observedProperty != null)
			setValue_OBSERVED_PROPERTY(observedProperty);

		if (!hasOffering) {
			report(ExceptionReporter.MissingParameterValue, OFFERING,
					"Need parameter: " + OFFERING + " ", os);
			return false;
		}
		// relax this condition - not sure if it is used in oostethys - default
		// is swe common

		// if (!hasResponseFormat){
		// report(ExceptionReporter.MissingParameterValue,
		// RESPONSEFORMAT, "Need parameter: "+RESPONSEFORMAT
		// +" "
		// , os);
		// return false;
		// }

		if (parameterMap.containsKey(RESULT_MODEL)) {
			final String resultModel = parameterMap.get(RESULT_MODEL);
			if (!("{http://www.opengis.net/om/1.0}Observation"
					.equals(resultModel)
					|| "{http://www.opengis.net/om/1.0}Measurement"
							.equals(resultModel) || "{http://www.opengis.net/om/1.0}CategoryObservation"
					.equals(resultModel))) {
				report(
						ExceptionReporter.InvalidParameterValue,
						"resultModel",
						"The value ("
								+ resultModel
								+ ") of the parameter 'resultModel' is invalid! It could be 'Observation' or 'Measurement' for numerical observations or 'CategoryObservation' for categorical observations.",
						os);
				return false;
			}
		}

		if (parameterMap.containsKey(SRS_NAME)) {
			if (!StringUtils.startsWith(parameterMap.get(SRS_NAME),
					"urn:ogc:crs:epsg:")) {
				report(
						ExceptionReporter.InvalidParameterValue,
						"srsName",
						"The parameter 'srsName' has to match pattern 'urn:ogc:crs:epsg:' with appended EPSG code number.",
						os);
				return false;
			}
		}

		return true;
	}

	private void setValue_EventTime(String eventTime) {
		value_EVENT_TIME = eventTime;

	}

	private boolean hasParameters(OutputStream os) {
		ExceptionReporter reporter = new ExceptionReporter();
		if (parameterMap == null) {
			String report = reporter.create(
					ExceptionReporter.MissingParameterValue, null,
					"The service: should have service and request parameters");

			try {
				os.write(report.getBytes());
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return true;
	}

	private void report(String code, String locator, String text,
			OutputStream os) {
		if (os == null)
			return;

		ExceptionReporter reporter = new ExceptionReporter();

		String report = reporter.create(code, locator, text);
		try {
			os.write(report.getBytes());
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	private boolean okGetCapabilitiesParameters(OutputStream os) {
		boolean hasService = false;

		String value = null;
		// if (!hasParameters(os)) {
		// return false;
		// }
		if (!checkCommonParameters(os))
			return false;

		Set<String> set = parameterMap.keySet();
		for (String key : set) {
			log.debug("key " + key);
			log.debug("value " + parameterMap.get(key));
			value = parameterMap.get(key) + "";
			if (key.equalsIgnoreCase(SERVICE)) {
				if (!value.equalsIgnoreCase("SOS")) {
					report(
							ExceptionReporter.InvalidParameterValue,
							SERVICE,
							"The service: "
									+ value
									+ " is not supported. Service should be 'SOS'.",
							os);
					return false;

				}
				hasService = true;
			} else if (key.equalsIgnoreCase(VERSION)) {

				if (!value.equalsIgnoreCase(VERSION_NUMBER_SOS)) {
					report(ExceptionReporter.InvalidParameterValue, VERSION,
							"The version: " + value
									+ " is not supported. Service should be "
									+ VERSION_NUMBER_SOS, os);
					return false;

				}
			}
		}

		if (!hasService) {
			report(
					ExceptionReporter.MissingParameterValue,
					SERVICE,
					"The mandatory parameter value 'service' was not found in the request.",
					os);
			return false;
		}

		return true;

	}

	/**
	 * Sets the webservice of the servlet rendering this service.
	 * (getRequestURL) with no parameteres
	 * 
	 * @param requestURL
	 */
	public void setServletURL(String requestURL) {
		this.requestURL = requestURL;

	}

	public String getServletURL() {
		return requestURL;

	}

	public String createStringSeparatedCommas(String[] values) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < values.length; i++) {
			buffer.append(values[i]);
			if (i <= values.length - 1) {
				buffer.append(",");
			}
		}

		return buffer.toString();
	}

	private void processParametersDescribeSensor(
			DescribeSensorDocument describeSensor) {

		String service = describeSensor.getDescribeSensor().getService();
		String version = describeSensor.getDescribeSensor().getVersion();
		String procedure = describeSensor.getDescribeSensor().getProcedure();
		String format = describeSensor.getDescribeSensor().getOutputFormat();

		parameterMap = new HashMap<String, String>();

		if (service != null)
			parameterMap.put(SERVICE, service);
		if (version != null)
			parameterMap.put(VERSION, version);
		if (procedure != null)
			parameterMap.put(PROCEDURE, procedure);
		if (format != null)
			parameterMap.put(OUTPUT_FORMAT, format);

	}

	private boolean processParametersGetObservation(
			GetObservationDocument getObsDocument, OutputStream os) {

		parameterMap = new HashMap<String, String>();
		GetObservation getObservation = getObsDocument.getGetObservation();

		if (getObservation.getService() != null)
			parameterMap.put(SERVICE, getObservation.getService());
		if (getObservation.getVersion() != null)
			parameterMap.put(VERSION, getObservation.getVersion());
		if (getObservation.getOffering() != null)
			parameterMap.put(OFFERING, getObservation.getOffering());
		if (getObservation.getProcedureArray() != null)
			parameterMap.put(PROCEDURE,
					createStringSeparatedCommas(getObservation
							.getProcedureArray()));
		if (getObservation.getObservedPropertyArray() != null)
			parameterMap.put(OBSERVED_PROPERTY,
					createStringSeparatedCommas(getObservation
							.getObservedPropertyArray()));

		if (getObservation.getSrsName() != null)
			parameterMap.put(SRS_NAME, getObservation.getSrsName());
		if (getObservation.getResponseFormat() != null)
			parameterMap.put(RESPONSE_FORMAT, getObservation
					.getResponseFormat());

		try {
			if (getObservation.getResponseMode() != null)
				parameterMap.put(RESPONSE_MODE, getObservation
						.getResponseMode().toString());
		} catch (XmlValueOutOfRangeException e) {
			report(ExceptionReporter.InvalidParameterValue, "responseMode",
					"XML validation error: " + e.getMessage(), os);
			return false;
		}

		if (getObservation.getEventTimeArray().length > 0) {
			try {

				EventTime eventTime = getObservation.getEventTimeArray(0);
				String queryExpression = "declare namespace ogc='http://www.opengis.net/ogc'; declare namespace  gml='http://www.opengis.net/gml'; "
						+ "$this//ogc:TM_During/gml:TimePeriod/gml:beginPosition";

				XmlObject[] obj = eventTime.selectPath(queryExpression);
				value_BEGIN = obj[0].newCursor().getTextValue();

				queryExpression = "declare namespace ogc='http://www.opengis.net/ogc'; declare namespace  gml='http://www.opengis.net/gml'; "
						+ "$this//ogc:TM_During/gml:TimePeriod/gml:endPosition";

				obj = eventTime.selectPath(queryExpression);
				value_END = obj[0].newCursor().getTextValue();

				parameterMap.put(EVENT_TIME, value_BEGIN + "/" + value_END);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (getObservation.getResultModel() != null) {
			parameterMap.put(RESULT_MODEL, getObservation.getResultModel()
					.toString());
		}

		if (getObservation.getSrsName() != null) {
			parameterMap.put(SRS_NAME, getObservation.getSrsName());
		}

		return true;
	}

	/**
	 * @return the postInputStream
	 */
	public InputStream getPostInputStream() {
		return postInputStream;
	}

	public static String observationOfferingCreator(String id) {
		return "observationOffering_" + id;
	}

	/**
	 * @return the numberOfRecordsToProcess
	 */
	public int getNumberOfRecordsToProcess() {
		return numberOfRecordsToProcess;
	}

	/**
	 * @param numberOfRecordsToProcess
	 *            the numberOfRecordsToProcess to set
	 */
	public void setNumberOfRecordsToProcess(int numberOfRecordsToProcess) {
		this.numberOfRecordsToProcess = numberOfRecordsToProcess;

	}

}
