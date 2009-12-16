package org.oostethys.model.impl;

import org.oostethys.model.VariableQuantity;
import org.oostethys.model.impl.VariableQuantityImpl;

import junit.framework.TestCase;

public class VariableQuantityImplTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void testSetGetURI(){
		VariableQuantity vq = new VariableQuantityImpl();
		vq.setLabel("label");
		assertEquals("label",vq.getLabel());
		vq.setURI("urn:a#a");
		String uri  = vq.getURI();
		assertEquals("urn:a#a",uri);
		
		
	}

}
