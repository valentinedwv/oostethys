package org.oostethys.model.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Level;
import org.oostethys.model.Observation;
import org.oostethys.model.Units;
import org.oostethys.model.VariableQuantity;
import org.oostethys.model.VariablesConfig;
import org.oostethys.netcdf.util.TimeUtil;
import org.oostethys.netcdf.util.UnitsMapper;
import org.oostethys.netcdf.util.VariableMapper;
import org.oostethys.sos.ParserException;
import org.oostethys.voc.Voc;

import ucar.ma2.Array;
import ucar.ma2.Index;
import ucar.ma2.InvalidRangeException;
import ucar.ma2.Section;
import ucar.nc2.Attribute;
import ucar.nc2.NCdump;
import ucar.nc2.NCdumpW;
import ucar.nc2.dataset.NetcdfDataset;
import ucar.nc2.units.DateUnit;

public class ObservationNetcdf extends ResourceImpl implements Observation {

	private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger
			.getLogger(ObservationNetcdf.class.getName());

	private int numberOfRecords = 10000; // max number of records to read

	private VariablesConfig variablesConfig;

	private NetcdfDataset netcdfdataset;

	private List<VariableQuantity> varsToProcess;

	private String url;

	private boolean depthIsGiven = true;

	private long minTime;

	private long maxTime;

	private double minZ;

	private double maxZ;

	private String lastKnownPosition;

	private String data;

	private String tokenSeparator = ",";

	private String blockSeparator = " ";

	private TimeUtil timeUtil;

	private String value_BBOX = null;
	private double minLonBBOX = Long.MAX_VALUE;
	private double minLatBBOX = Long.MAX_VALUE;
	private double maxLonBBOX = Long.MIN_VALUE;
	private double maxLatBBOX = Long.MIN_VALUE;

	private double minLon = minLonBBOX;

	private double maxLon = maxLonBBOX;

	private double minLat = minLatBBOX;

	private double maxLat = maxLatBBOX;

	private String value_TIME = null;
	private long timeStart = Long.MIN_VALUE;
	private long timeEnd = Long.MAX_VALUE;

	/**
	 * if getCapabilites or DescribeSensor, which do not requires to get all the
	 * data, only the extends
	 */
	private boolean parseAllData = false;

	private boolean isOpendapQuery;

	private String query;

	public ObservationNetcdf() {

		super("id");
		// logger.setLevel(Level.DEBUG);

	}

	/**
	 * Sets the URL location ( file or OpenDAP for the NC dataset
	 * 
	 * @param url
	 */
	public void setURL(String url) {
		this.url = url;

	}

	public VariablesConfig newVariablesConfig() {
		this.variablesConfig = new VariablesConfig();
		return variablesConfig;
	}

	/**
	 * Creates an Observation from a netcdf, opendap link
	 * 
	 * @param fileURL
	 *            : NETCDF of OPENDAP link
	 * @param identifierOfThisObservation
	 *            : unique id that identifies this observation
	 * @param config
	 *            : variables configuration which contains information about
	 *            what variables to parse, the units and the mapping to URIs
	 */
	public ObservationNetcdf(String fileURL,
			String identifierOfThisObservation, VariablesConfig config) {
		super(identifierOfThisObservation);

		this.url = fileURL;

		this.variablesConfig = config;

	}

	/**
	 * Opens an opendap or netcdf URL. Tries to open a URL if it is an opendap
	 * query it strips the query part and flags this netcdf as isOpendapQuery.
	 * The query part s store in a field.
	 * 
	 * @throws ParserException
	 */
	private void openNetcdfDataSet() throws ParserException {
		netcdfdataset = null;
		String surl = null;
		if (this.url == null) {
			throw new ParserException(ParserException.NOT_ABLE_TO_OPEN_FILE,
					this.url.toString());
		}
		try {
			int index = url.indexOf("?");
			if (index > 0) {
				 query = url.substring(url.indexOf("?"));
			}else{
				query = null;
			}
			 surl = url.toString();
			if (query != null) {
				surl = surl.substring(0, surl.indexOf("?"));
				// remove the 'dods'
				surl = surl.replace(".dods", "");
				surl = surl.replace(".ascii", "");

				isOpendapQuery = true;
				this.query = query;

			}

			netcdfdataset = NetcdfDataset.openDataset(surl);

			if (logger.isDebugEnabled()){

				logger.info(netcdfdataset.toString());
			}

		} catch (IOException e) {
			e.printStackTrace();

			throw new ParserException(ParserException.NOT_ABLE_TO_OPEN_FILE,
					surl);

		}

	}

	/**
	 * 
	 * @see org.oostethys.model.impl.Observation#getAsRecords(java.lang.String,
	 *      java.lang.String)
	 */
	public String getAsRecords() {
		return data.trim();
	}

	private boolean isGood(ucar.nc2.Variable var, double value) {
		@SuppressWarnings("unchecked")
		List<Attribute> atts = var.getAttributes();
		for (Attribute attribute : atts) {
			if (attribute.getName().equalsIgnoreCase("missing_value")) {
				Number n = attribute.getNumericValue();
				logger.debug(n.toString());
			}
		}
		return false;

	}

	/**
	 * Find variable in a netcdf data set either in the standard name or log
	 * name attribute
	 * 
	 * @param standardName
	 * @return
	 */
	private ucar.nc2.Variable findncfVariableByShortName(String shortName) {
		@SuppressWarnings("unchecked")
		List<ucar.nc2.Variable> vars = netcdfdataset.getVariables();

		for (ucar.nc2.Variable variable : vars) {
			if (variable.getShortName().equalsIgnoreCase(shortName)) {
				return variable;
			}
		}

		return null;
	}

	private VariableQuantity getVarQ(ucar.nc2.Variable var) {

		Attribute att = null;
		String label = null;
		att = var.findAttribute("standard_name");
		if (att != null) {
			label = att.getStringValue();
			if (label != null) {
				VariableQuantity varConfig = variablesConfig.getVariable(label);
				if (varConfig != null) {
					return varConfig;
				}
			}
		} else {
			att = var.findAttribute("long_name");
			if (att != null) {
				label = att.getStringValue();
				if (label != null) {
					VariableQuantity varConfig = variablesConfig
							.getVariable(label);
					if (varConfig != null) {
						return varConfig;
					}
				}
			}

		}

		return null;
	}

	/**
	 * Processes the variables in the vasrsToProcess. It reads the nc array and
	 * adds the array read to the list of arrays. It checks if it is an opendap
	 * query and reads the variable using sections.
	 * 
	 * @param varsToProcess
	 * @param arraysVar
	 * @param uriOfVariable
	 */
	private void processReadVar(List<VariableQuantity> varsToProcess,
			List<Array> arraysVar, String uriOfVariable) throws Exception {
		logger.info("Processing " + uriOfVariable);
		VariableQuantity varQuantity = variablesConfig
				.getVariableByURI(uriOfVariable);

		if (varQuantity != null) {
			String varLabel = varQuantity.getLabel();
			if (varLabel != null) {
				ucar.nc2.Variable varnc = findncfVariableByShortName(varLabel);

				// VariableQuantity varQuantity = getVarQ(varnc);
				// VariableQuantity varQuantity =
				// variablesConfig.getVariable(varnc.getShortName());

				try {
					varsToProcess.add(varQuantity);

					if (isOpendapQuery) {
						Section section = getSection(varLabel);
						arraysVar.add(varnc.read(section));

					} else {
						arraysVar.add(varnc.read());
					}

					logger.info("processed succesfully" + varnc);
				} catch (IOException e) {
					logger.warn("problems parsing: " + uriOfVariable);
					throw new Exception("not able to parse " + varnc
							+ " corresponding to " + varLabel, e);
				}

			} else {

				logger.warn("Variable has no label: " + uriOfVariable);
			}
		} else {
			// throw new Exception("not able to find the variable " +
			// varQuantity);
		}
	}

	private Section getSection(String varLabel) {
		int indexVarFrom = query.indexOf(varLabel + "[");
		int indexVarTo = query.indexOf(",", indexVarFrom);
		if (indexVarTo == -1) {
			indexVarTo = query.length();
		}
		String sel = query.substring(indexVarFrom, indexVarTo);
		Section sec = new Section();

		int ind = sel.indexOf("[");
		String subsel = sel;
		try {
			while (ind > -1) {
				subsel = subsel.substring(ind);
				int indexColon = subsel.indexOf(":");
				int indexofClosingParan = subsel.indexOf("]");
				int lower = -1;
				int upper = -1;
				if (indexColon > -1) {
					if (indexColon < indexofClosingParan) {
						lower = Integer.parseInt(subsel
								.substring(1, indexColon));
						upper = Integer.parseInt(subsel.substring(
								indexColon + 1, indexofClosingParan));
					}
				} else {
					lower = Integer.parseInt(subsel.substring(1,
							indexofClosingParan));
					upper = lower;
				}

				sec.appendRange(lower, upper);
				ind = subsel.indexOf("[", 1);

			}

		} catch (InvalidRangeException e1) {

			e1.printStackTrace();

		}

		return sec;
	}

	/*
	 * reads content from netcdf. : 1) reads time 2) read lat 3) reads lon 4)
	 * read depth (optional) 5) reads all the other variables 5.1 ) check that
	 * the have as dimension 1-3 or 1 -4
	 * 
	 * (non-Javadoc)
	 * 
	 * @see org.oostethys.model.Observation#process()
	 */
	public boolean checkHasDepth() {
		return variablesConfig.getVariableByURI(Voc.depth) != null;
	}

	private void checkBBOX() {
		// if not min lat lon are set replace min and max to accept all
		if (minLatBBOX == Long.MAX_VALUE) {
			minLatBBOX = Long.MIN_VALUE;
		}

		if (minLonBBOX == Long.MAX_VALUE) {
			minLonBBOX = Long.MIN_VALUE;
		}

		if (maxLatBBOX == Long.MIN_VALUE) {
			maxLatBBOX = Long.MAX_VALUE;
		}

		if (maxLonBBOX == Long.MIN_VALUE) {
			maxLonBBOX = Long.MAX_VALUE;
		}

	}

	// TODO - need to change this - the mapping of variables to URIS should be
	// done manually
	// A default could exist that guesses all the variables

	public void process() throws Exception {
		openNetcdfDataSet();
		setDepthFlag();

		// process the variables in the configuration file
		processVarConfig();
		if (url == null) {
			throw new Exception("Need URL to process NETCDF ");

		}

		if (variablesConfig == null) {
			throw new Exception("Need variables configuration ");

		}

		@SuppressWarnings("unchecked")
		List<ucar.nc2.Variable> varsNC = netcdfdataset.getVariables();

		// will hold the variables found in the appropriate order
		varsToProcess = new ArrayList<VariableQuantity>(varsNC.size());

		// try {

		// contains all the Array of data related to a variable

		List<Array> arraysVar = new ArrayList<Array>(varsNC.size());
		// ucar.nc2.Variable varnc = null;

		processReadVar(varsToProcess, arraysVar, Voc.time);
		if ( varsToProcess.size()==0){
			throw new Exception("time variable is missing - cannot process");
			
		}
		processReadVar(varsToProcess, arraysVar, Voc.latitude);
		if ( varsToProcess.size()==1){
			throw new Exception("latitude variable is missing - cannot process");
			
		}
		
		processReadVar(varsToProcess, arraysVar, Voc.longitude);
		if ( varsToProcess.size()==2){
			throw new Exception("longitude variable is missing - cannot process");
			
		}
		

		VariableQuantity timeVar = variablesConfig.getVariableByURI(Voc.time);

		if (checkHasDepth()) {
			processReadVar(varsToProcess, arraysVar, Voc.depth);
		}

		logger.info("Processing variables ---");

		List<VariableQuantity> vars = variablesConfig.getVariables();
		for (VariableQuantity varQ : vars) {
			if (!varQ.isCoordinate()) {
				processReadVar(varsToProcess, arraysVar, varQ.getURI());
			}
		}

		// gets info time
		// get first variable array shape
		int varArrSize = arraysVar.get(arraysVar.size() - 1).getShape()[0];

		StringBuffer buffy = new StringBuffer();

		int numberOfFields = arraysVar.size();

		if (varsToProcess.size() > numberOfFields) {
			logger
					.error("not all the variables have been processed correctly ! ");
		}

		logger.info("numberOfFields " + numberOfFields);

		// if no time is given - then send the last record
		// if (timeStart == Long.MIN_VALUE && timeEnd == Long.MAX_VALUE) {
		// numberOfRecords = 1;
		// }

		// set up min max lon lat
		checkBBOX();

		int temp = 0;

		// this is updated every time it reads the variable info. It
		// should
		// net be calculated only on the time min and time max, but from
		// the
		// min and max available for a particular set of given variables

		minTime = Long.MAX_VALUE;
		maxTime = Long.MIN_VALUE;

		// out of bound limits

		// minLat = 500;
		// maxLat = -500;
		// minLon = 500;
		// maxLon = -500;

		minZ = Double.parseDouble(getValue(varArrSize, arraysVar.get(arraysVar
				.size() - 1), 0));
		maxZ = Double.parseDouble(getValue(varArrSize, arraysVar.get(arraysVar
				.size() - 1), 0));

		String val = "";

		int latest = numberOfRecords;
		int start = 0;
		int last = 0;
		if (varArrSize > latest) {
			start = varArrSize - latest;

		} else {
			start = 0;

		}
		last = varArrSize;

		// StringBuffer lastknown = new StringBuffer();

		long millis = Long.MIN_VALUE;
		// String time = "";
		String lat = "";
		String lon = "";
		// String z = "";

		// this is where the data will be stored

		StringBuffer temBuffy = new StringBuffer();
		int j = 0;

		for (int i = start; i < last; i++) {
			for (Array array : arraysVar) {
				j = j + 1;
				// first array is time
				val = getValue(varArrSize, array, i);

				switch (temp) {
				case 0:

					String units_ = val + " " + timeVar.getUnits().getLabel();

					millis = DateUnit.getStandardDate(units_).getTime();
					minTime = Math.min(millis, minTime);
					maxTime = Math.max(millis, maxTime);

					val = TimeUtil.getISOFromMillisec(millis);

					break;

				case 1:
					double dd = Double.parseDouble(val);

					minLat = Math.min(dd, minLat);
					maxLat = Math.max(dd, maxLat);
					lat = val = getValueFormatted(dd);

					break;
				case 2:
					dd = Double.parseDouble(val);
					minLon = Math.min(dd, minLon);
					maxLon = Math.max(dd, maxLon);
					lon = val = getValueFormatted(dd);

					break;

				case 3:
					if (depthIsGiven) {
						dd = Double.parseDouble(val);
						minZ = Math.min(dd, minZ);
						maxZ = Math.max(dd, maxZ);
						val = getValueFormatted(dd);

						break;
					} else {
						dd = Double.parseDouble(val);
						val = getValueFormatted(dd);
						break;
					}
				default:
					dd = Double.parseDouble(val);
					val = getValueFormatted(dd);
				}

				temBuffy.append(val);

				temp = temp + 1;
				//				

				if (temp == numberOfFields) {
					temp = 0;
					buffy.append(blockSeparator);
				} else {
					temBuffy.append(tokenSeparator);
				}
			}

			boolean isOK = false;

			if (millis >= this.timeStart && millis <= this.timeEnd) {
				isOK = true;
			} else {
				isOK = false;
			}

			if (isOK) {

				double lat_ = Double.parseDouble(lat);
				double lon_ = Double.parseDouble(lon);

				if (lat_ >= minLatBBOX && lat_ <= maxLatBBOX) {
					if (lon_ >= minLonBBOX && lon_ <= maxLonBBOX) {

						buffy.append(temBuffy);

						// if it is the last record - use this as the last know
						// position
						if (i == last - 1) {
							lastKnownPosition = lat + " " + lon;
						}

					}
				}

			}
			temBuffy = new StringBuffer();

			temp = 0;
			data = buffy.toString();
		}

	}

	// TODO create a URI for units or transform to UCUM units, currently is NULL
	/**
	 * Processes the variables config. Adds URIs and units
	 * 
	 */

	private void setDepthFlag() {
		// find if there is a depth coordinate , other than lat lon and time
		@SuppressWarnings("unchecked")
		List<ucar.nc2.Variable> vars = netcdfdataset.getVariables();
		for (Iterator<ucar.nc2.Variable> iterator = vars.iterator(); iterator
				.hasNext();) {
			ucar.nc2.Variable variable = iterator.next();

			if (ncVariableIsCoordinate(variable)) {

				Attribute att2 = variable
						.findAttributeIgnoreCase("standardName");
				if (att2 != null) {
					if (att2.getStringValue().matches("[D,d]epth.*")) {
						setDepthIsGiven(true);
						return;
					}
				}
			}

		}
		setDepthIsGiven(false);

	}

	private static boolean ncVariableIsCoordinate(ucar.nc2.Variable var) {
		Attribute att = var.findAttributeIgnoreCase("_CoordinateAxisType");
		if (att == null) {
			var.findAttributeIgnoreCase("axis");
		}

		return att != null;
	}

	/**
	 * Process the variables in the configuration file. For every variable it
	 * checks in the netcdfdataset and extracts the metadata related to that
	 * variable, e.g. units, short name etc. It doesn't read the variables data.
	 * Only update the variable metadata for each variable in the configuration.
	 * 
	 * @throws Exception
	 */
	private void processVarConfig() throws Exception {

		Iterator<VariableQuantity> iter = variablesConfig.getIterator();
		VariableQuantity variableQuantity = null;
		while (iter.hasNext()) {

			variableQuantity = iter.next();

			ucar.nc2.Variable var = findncfVariableByShortName(variableQuantity
					.getLabel());

			if (var == null) {

				throw new Exception("The following variable was not found : '"
						+ variableQuantity + "' in "
						+ netcdfdataset.getLocation());

			} else {
				logger.info(" Found nc Variable for: " + variableQuantity
						+ " --> " + var.getShortName());
			}

			// variableQuantity.

			// // set coordinate attribute
			// if (ncVariableIsCoordinate(var)) {
			// variableQuantity.setCoordinate(true);
			// } else {
			// variableQuantity.setCoordinate(false);
			// }

			// set URI // mapping
			// no in new version - of netcdf configuration - URIS are provided
			// in the config file
			// if( variableQuantity.getURI() == null) {
			// assignURItoVarConfig2(variableQuantity);
			// }

			String unitsS = var.getUnitsString();
			Units units = new UnitsImpl();

			if (!Voc.time.equals(variableQuantity.getURI())) {
				String ucum = UnitsMapper.getUCUM(unitsS);
				units.setLabel(ucum);

				//				
			}
			// leave units as it is form nc var
			else {
				units.setLabel(unitsS);
			}
			variableQuantity.setUnits(units);

		}

	}

	// /**
	// * It guesses what is lat, long depth and others and those the mappings
	// *
	// * @throws Exception
	// */
	// private void processVarConfig2() throws Exception {
	//
	// Iterator<VariableQuantity> iter = variablesConfig.getIterator();
	// VariableQuantity variableQuantity = null;
	// while (iter.hasNext()) {
	//
	// variableQuantity = (VariableQuantity) iter.next();
	// ucar.nc2.Variable var = findncfVariableByShortName(variableQuantity
	// .getLabel());
	//
	// if (var == null) {
	//
	// throw new Exception("The following variable was not found : '"
	// + variableQuantity + "' in "
	// + netcdfdataset.getLocation());
	//
	// } else {
	// logger.info(" Found nc Variable for: " + variableQuantity
	// + " --> " + var.getShortName());
	// }
	//
	// // set coordinate attribute
	// if (ncVariableIsCoordinate(var)) {
	// variableQuantity.setCoordinate(true);
	// } else {
	// variableQuantity.setCoordinate(false);
	// }
	//
	// // set URI // mapping
	// assignURItoVarConfig(variableQuantity);
	//
	// String unitsS = var.getUnitsString();
	// Units units = new UnitsImpl();
	//
	// if (!variableQuantity.getURI().equals(Voc.time)) {
	// String ucum = UnitsMapper.getUCUM(unitsS);
	// units.setLabel(ucum);
	//
	// //
	// }
	// // leave units as it is form nc var
	// else {
	// units.setLabel(unitsS);
	// }
	// variableQuantity.setUnits(units);
	//
	// }
	//
	// }

	/**
	 * Maps the variable names provided in the configuration file, with the
	 * internal OOSTethys mappings ( at least lat, lon and time should be
	 * provided)
	 * 
	 * @param variableQuantity
	 */

	// TODO main problem - !!!!!!!
	// why don't we assume that the variables are mapped before hand !

	public void assignURItoVarConfig(VariableQuantity variableQuantity) {

	}

	private void assignURItoVarConfig2(VariableQuantity variableQuantity) {
		String label = variableQuantity.getLabel();

		// find variable by shortname
		ucar.nc2.Variable var = findncfVariableByShortName(label);
		String standardName = null;
		String uri = null;
		// boolean isTime = false;

		if (var != null) {
			Attribute att = var.findAttribute("standard_name");
			// check first standard names !
			if (att != null) {
				standardName = att.getStringValue();

				// check first time
				Attribute coordType = var.findAttribute("_CoordinateAxisType");
				if (coordType != null) {
					String value = coordType.getStringValue();
					if (value.equalsIgnoreCase("Time")) {
						uri = Voc.time;
						variableQuantity.setURI(uri);
						logger.info("mapping set: " + label + "  -->  " + uri);
						return;
					} else {

						if (value.toLowerCase().contains("height")
								|| value.toLowerCase().contains("elev")) {
							uri = Voc.depth;
							variableQuantity.setURI(uri);
							logger.info("mapping set:: " + label + "  -->  "
									+ uri);
							return;
						}
					}
				}
				//

				if (standardName.equalsIgnoreCase("latitude")) {
					uri = Voc.latitude;

				} else if (standardName.equalsIgnoreCase("longitude")) {
					uri = Voc.longitude;
				} else {
					uri = VariableMapper.getMMIURIforCFParam(standardName);
				}
			}

			else {

				// if there is not attribute standard
				// name, then try with coordinate types -

				Attribute coordType = var.findAttribute("_CoordinateAxisType");
				if (coordType != null) {
					String value = coordType.getStringValue();
					if (value.equalsIgnoreCase("Time")) {
						// time
						uri = Voc.time;

					} else if (value.equals("Lat")) {
						uri = Voc.latitude;

					} else if (value.equals("Lon")) {
						uri = Voc.longitude;
					}

				} else {

					// if it reaches this point then it has no standard name, it
					// is
					// not a coord
					uri = VariableMapper.getMMIURN(label, "mmi");
				}
			}
		}
		variableQuantity.setURI(uri);
		logger.info("mapping set: " + label + "  -->  " + uri);

	}

	private String getValueFormatted(Number val) {
		// DecimalFormat myFormatter = new DecimalFormat();
		NumberFormat myFormatter = NumberFormat.getInstance(Locale.ENGLISH);
		myFormatter.setMaximumFractionDigits(6);
		String output = myFormatter.format(val);
		return output;
	}

	private String getValue(int varArraySize, Array ma, int index) {
		Index ima = ma.getIndex();
		int ind = getIndex(index, varArraySize, ma.getShape()[0]);
		Object obj = ma.getObject(ima.set(ind));
		return obj.toString();

	}

	// private double getDoubleValue(int varArraySize, Array ma, int index) {
	// Index ima = ma.getIndex();
	// int ind = getIndex(index, varArraySize, ma.getShape()[0]);
	// double obj = ma.getDouble(ima.set(ind));
	// return obj;
	// }

	private int getIndex(int index, int varArraySize, int thisArraySize) {
		double d = index / ((double) varArraySize / (double) thisArraySize);
		return (int) d;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.oostethys.model.impl.Observation#getConfig()
	 */
	public VariablesConfig getConfig() {
		return variablesConfig;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.oostethys.model.impl.Observation#getNetcdfdataset()
	 */
	public NetcdfDataset getNetcdfdataset() {
		return netcdfdataset;
	}

	public String getLocation() {
		return netcdfdataset.getLocation();
	}

	/**
	 * @return the minTime
	 */
	public double getMinTime() {
		return minTime;
	}

	/**
	 * @return the maxTime
	 */
	public double getMaxTime() {
		return maxTime;
	}

	/**
	 * @return the minLon
	 */
	public double getMinLon() {
		return minLon;
	}

	/**
	 * @return the maxLon
	 */
	public double getMaxLon() {
		return maxLon;
	}

	/**
	 * @return the minLat
	 */
	public double getMinLat() {
		return minLat;
	}

	/**
	 * @return the maxLat
	 */
	public double getMaxLat() {
		return maxLat;
	}

	/**
	 * @return the minZ
	 */
	public double getMinZ() {
		return minZ;
	}

	/**
	 * @return the maxZ
	 */
	public double getMaxZ() {
		return maxZ;
	}

	/**
	 * @return the lastKnownPosition
	 */
	public String getLastKnownPositionEPSG() {
		return lastKnownPosition;
	}

	public List<VariableQuantity> getVariables() {
		return varsToProcess;

	}

	public String getDescription() {
		return getLabel();
	}

	public String getName() {
		return getLabel();
	}

	public void setDescription(String description) {

	}

	public String getLowerCornerEPSG() {
		return getValueFormatted(getMinLat()) + " "
				+ getValueFormatted(getMinLon());
	}

	public String getUpperCornerEPSG() {
		return getValueFormatted(getMaxLat()) + " "
				+ getValueFormatted(getMaxLon());
	}

	/**
	 * @return the tokenSeparator
	 */
	public String getTokenSeparator() {
		return tokenSeparator;
	}

	/**
	 * @param tokenSeparator
	 *            the tokenSeparator to set
	 */
	public void setTokenSeparator(String tokenSeparator) {
		this.tokenSeparator = tokenSeparator;
	}

	/**
	 * @return the blockSeparator
	 */
	public String getBlockSeparator() {
		return blockSeparator;
	}

	/**
	 * @param blockSeparator
	 *            the blockSeparator to set
	 */
	public void setBlockSeparator(String blockSeparator) {
		this.blockSeparator = blockSeparator;
	}

	/**
	 * @param config
	 *            the config to set
	 */
	public void setConfig(VariablesConfig config) {
		this.variablesConfig = config;
	}

	/**
	 * @param netcdfdataset
	 *            the netcdfdataset to set
	 */
	public void setNetcdfdataset(NetcdfDataset netcdfdataset) {
		this.netcdfdataset = netcdfdataset;
	}

	public VariablesConfig getVariablesConfig() {
		if (variablesConfig == null) {
			return newVariablesConfig();
		}
		return variablesConfig;
	}

	public void setVariablesConfig(VariablesConfig variablesConfig) {
		this.variablesConfig = variablesConfig;
	}

	public void process(boolean depthIsGiven) {
		this.depthIsGiven = depthIsGiven;

	}

	/**
	 * @return the URL use to get the dataset of thei NetCDF observation
	 */
	public String getURL() {
		return url;
	}

	/**
	 * @return the depthIsGiven
	 */
	public boolean isDepthIsGiven() {
		return depthIsGiven;
	}

	/**
	 * @param depthIsGiven
	 *            the depthIsGiven to set
	 */
	public void setDepthIsGiven(boolean depthIsGiven) {
		this.depthIsGiven = depthIsGiven;
	}

	/**
	 * @return the timeUtil
	 */
	public TimeUtil getTimeUtil() {
		return timeUtil;
	}

	/**
	 * @param timeUtil
	 *            the timeUtil to set
	 */
	public void setTimeUtil(TimeUtil timeUtil) {
		this.timeUtil = timeUtil;
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
		if (value_BBOX != null) {
			String[] values = this.value_BBOX.split(",");
			minLon = minLonBBOX = Double.parseDouble(values[0]);
			minLat = minLatBBOX = Double.parseDouble(values[1]);
			maxLon = maxLonBBOX = Double.parseDouble(values[2]);
			maxLat = maxLatBBOX = Double.parseDouble(values[3]);
		}

	}

	/**
	 * @return the value_TIME
	 */
	public String getValue_TIME() {
		return value_TIME;
	}

	/**
	 * @param value_TIME
	 *            the value_TIME to set
	 */
	public void setValue_TIME(String value_TIME) {
		if (value_TIME != null) {
			String[] values = value_TIME.split("/");
			this.timeStart = TimeUtil.getMillisec(values[0]);
			this.timeEnd = TimeUtil.getMillisec(values[1]);
			this.minTime = timeStart;
			this.maxTime = timeEnd;
		}
	}

	/**
	 * @return the numberOfRecords
	 */
	public int getNumberOfRecords() {
		return numberOfRecords;
	}

	/**
	 * @param numberOfRecords
	 *            the numberOfRecords to set
	 */
	public void setNumberOfRecords(int numberOfRecords) {
		this.numberOfRecords = numberOfRecords;
	}

	/**
	 * @return the parseAllData
	 */
	public boolean isParseAllData() {
		return parseAllData;
	}

	/**
	 * @param parseAllData
	 *            the parseAllData to set
	 */
	public void setParseAllData(boolean parseAllData) {
		this.parseAllData = parseAllData;
	}
}
