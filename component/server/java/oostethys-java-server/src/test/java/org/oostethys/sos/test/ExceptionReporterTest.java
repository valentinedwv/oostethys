package org.oostethys.sos.test;

import org.oostethys.sos.ExceptionReporter;

import junit.framework.TestCase;

public class ExceptionReporterTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testExceptionReport() {
		ExceptionReporter reporter = new ExceptionReporter();
		String exceptionCode = "OperationNotSupported";
		String locator = null;
		String exceptionText = "operation supported are x y z";

		String report = reporter.create(exceptionCode, locator, exceptionText);

		assertTrue(report.contains("<ExceptionReport version=\"1.0\">"));
		assertTrue(report.contains("<ExceptionText>operation supported are x y z</ExceptionText>"));

	}

}
