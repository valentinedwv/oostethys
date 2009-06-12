package org.oostethys.model.impl;

import org.oostethys.model.Resource;

public class ResourceImpl implements Resource {

	private String URI;

	private String label;

	public ResourceImpl() {

	}

	public ResourceImpl(String URI) {

		this.URI = URI;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.oostethys.data.Resource#getURI()
	 */
	public String getURI() {
		return URI;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.oostethys.data.Resource#setURI(java.lang.String)
	 */
	public void setURI(String uri) {
		this.URI = uri;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.oostethys.data.Resource#getFragment()
	 */
	public String getFragment() {
		if (URI != null) {
			int pos1 = URI.lastIndexOf("#");
			int pos2 = URI.lastIndexOf("/");
			int pos3 = URI.lastIndexOf(":");

			int max = Math.max(Math.max(pos1, pos2), pos3);

			if (max > 1) {
				return URI.substring(max + 1);

			}
		}

		return null;
	}

	public String getLabel() {

		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getNamespace() {
		if (URI != null) {
			int pos1 = URI.lastIndexOf("#");
			int pos2 = URI.lastIndexOf("/");
			int pos3 = URI.lastIndexOf(":");

			int max = Math.max(Math.max(pos1, pos2), pos3);

			if (max > 1) {
				return URI.substring(0, max);

			}
		}

		return null;
	}
}