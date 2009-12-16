package org.oostethys.sos.test;

import junit.framework.TestCase;

import net.opengis.ows.x11.ExceptionReportDocument;
import net.opengis.ows.x11.ExceptionReportDocument.ExceptionReport;

import org.apache.xmlbeans.XmlException;

import org.oostethys.sos.ExceptionReporter;


public class ExceptionReporterTest extends TestCase {
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testExceptionReport() throws XmlException {
        String expectedExceptionText = "operation supported are x y z";
        String expectedExceptionCode = "OperationNotSupported";

        ExceptionReporter reporter = new ExceptionReporter();
        String locator = null;

        String report =
            reporter.create(expectedExceptionCode, locator,
                expectedExceptionText);

        ExceptionReport exception =
            ExceptionReportDocument.Factory.parse(report).getExceptionReport();

        assertEquals("correct version", "1.0.0", exception.getVersion());
        assertEquals("correct namespace", "http://www.opengis.net/ows/1.1",
            exception.getDomNode().getNamespaceURI());
        assertEquals("correct exception code", expectedExceptionCode,
            exception.getExceptionArray()[0].getExceptionCode());
        assertEquals("correct exception text", expectedExceptionText,
            exception.getExceptionArray()[0].getExceptionTextArray()[0]);
    }
}
