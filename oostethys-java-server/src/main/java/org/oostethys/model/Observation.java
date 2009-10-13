package org.oostethys.model;

import java.util.List;

/**
 *<p> An observation is a data offering of a process part of an observing system. </p><hr>
 * @author  : $Author: luisbermudez $
 * @version : $Revision: 1.4 $
 * @since   : Feb 22, 2007
*/

public interface Observation extends Resource{
	
	/**
	 * @link composition
	 */
		
	 VariablesConfig variablesConfig = null;
	
	/**
	 * Returns data in ASCII in tabular form, following the order of the declare
	 * variables in the variablesConfig list and the defined tokenSeparator and blockSeparator. 
	 */
	public String getAsRecords();

	public List<VariableQuantity> getVariables();
	public double getMinTime();
	/**
	 * @return the maxTime
	 */
	public double getMaxTime();
	/**
	 * @return the minLon
	 */
	public double getMinLon();
	/**
	 * @return the maxLon
	 */
	public double getMaxLon();
	/**
	 * @return the minLat
	 */
	public double getMinLat();

	/**
	 * @return the maxLat
	 */
	public double getMaxLat();

	/**
	 * @return the minZ
	 */
	public double getMinZ();

	/**
	 * @return the maxZ
	 */
	public double getMaxZ();

	/**
	 * @return the lastKnownPosition
	 */
	public String getLastKnownPositionEPSG();
	
	public String getLowerCornerEPSG();
	
	public String getUpperCornerEPSG();
	public String getDescription();
	public void setDescription(String description);
	public String getName();
	/**
	 * Opens the source of the data and creates and instantiates all the componenets of this  Observation object
	 * @throws Exception
	 */
	public void process() throws Exception;
	

	/**
	 * @return the tokenSeparator
	 */
	public String getTokenSeparator();


	/**
	 * @param tokenSeparator the tokenSeparator to set
	 */
	public void setTokenSeparator(String tokenSeparator);


	/**
	 * @return the blockSeparator
	 */
	public String getBlockSeparator();


	/**
	 * @param blockSeparator the blockSeparator to set
	 */
	public void setBlockSeparator(String blockSeparator);


	/**
	 * @param config the config to set
	 */
	public void setConfig(VariablesConfig config);

	public VariablesConfig getVariablesConfig();

	public void setVariablesConfig(VariablesConfig variablesConfig);

	public void process(boolean depthIsGiven);

}