package org.oostethys.smlmor.gwt.client.rpc;


/**
 * Result of getting SensorML; includes a tree dump for testing purposes.
 * 
 * @author Carlos Rueda
 */
public class SmlResult extends BaseResult {
	private static final long serialVersionUID = 1L;

	private String sml;
	private String dump;
	
	public void setSml(String sml) {
		this.sml = sml;
	}
	public String getSml() {
		return sml;
	}
	public void setDump(String dump) {
		this.dump = dump;
	}
	public String getDump() {
		return dump;
	}
}
