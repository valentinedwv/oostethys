package org.oostethys.sos.test;

import org.oostethys.sos.ExceptionReporter;

import junit.framework.TestCase;

public class ExceptionRerporterTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testExceptionReport() {
		ExceptionReporter reporter = new ExceptionReporter();
		String exceptionCode = "OperationNotSupported";
		String locator = null;
		String exceptionText = "operation supported are x y z";

		String report = reporter.create(exceptionCode, locator, exceptionText);
		System.out.println(report);

		String s = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r"
				+ "<ExceptionReport version=\"1.0\">"
				+ "<Exception exceptionCode=\"OperationNotSupported\">"
				+ "<ExceptionText>operation supported are x y z</ExceptionText>"
				+ "</Exception></ExceptionReport>\r";
		
		assertTrue(report.contains("<ExceptionReport version=\"1.0\">"));
		
		assertTrue(report.contains("<ExceptionText>operation supported are x y z</ExceptionText>"));

	}

}
