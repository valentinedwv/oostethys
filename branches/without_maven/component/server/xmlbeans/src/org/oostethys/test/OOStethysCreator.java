package org.oostethys.test;

import java.io.File;
import java.io.IOException;

import org.apache.xmlbeans.XmlException;
import org.oostethys.schemas.x010.oostethys.OostethysDocument;
import org.oostethys.schemas.x010.oostethys.OostethysDocument.Oostethys;

import sun.misc.GC.LatencyRequest;


public class OOStethysCreator {

	public static void main(String[] args) {
		OostethysDocument od = OostethysDocument.Factory.newInstance();
		Oostethys ost = od.addNewOostethys();
	}

	
	

}
