/**
 *
 */
package org.oostethys.testutils;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;


/**
 * @author Jesper Zedlitz &lt;jze@informatik.uni-kiel.de&gt;
 *
 */
public class SimpleTypeCheckTest extends TestCase {
    public void testAssertIsValidURN_true() throws Exception {
        SimpleTypeCheck.assertIsValidURN(
            "urn:ogc:object:procedure:CITE:WeatherService:JFK");
    }

    public void testAssertIsValidURN_false() throws Exception {
        try {
            SimpleTypeCheck.assertIsValidURN("urn:foobar");
            throw new RuntimeException("not a valid URN");
        } catch (final AssertionFailedError e) {
            // ok
        }
    }

    public void testAssertIsValidDefinitionURN_true() throws Exception {
        SimpleTypeCheck.assertIsValidDefinitionURN(
            "urn:ogc:def:phenomenon:OGC:1.0.30:pressure");
    }

    public void testAssertIsValidDefinitionURN_false()
        throws Exception {
        try {
            SimpleTypeCheck.assertIsValidDefinitionURN(
                "urn:ogc:object:procedure:CITE:WeatherService:JFK");
            throw new RuntimeException("not a valid URN");
        } catch (final AssertionFailedError e) {
            // ok
        }
    }

    public void testIsValidTextXMLFormat_true() throws Exception {
        SimpleTypeCheck.assertIsValidTextXMLFormat(
            "text/xml; subtype=\"OM/1.0.0\"");
    }

    public void testIsValidTextXMLFormat_false() throws Exception {
        try {
            SimpleTypeCheck.assertIsValidTextXMLFormat(
                "text/xml;subtype=\"foobar\"");
            throw new RuntimeException("not a text XML format");
        } catch (final AssertionFailedError e) {
            // ok
        }
    }
}
