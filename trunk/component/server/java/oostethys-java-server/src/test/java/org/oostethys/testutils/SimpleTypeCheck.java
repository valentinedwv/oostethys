/**
 *
 */
package org.oostethys.testutils;

import junit.framework.AssertionFailedError;

import org.apache.commons.lang.StringUtils;

import java.util.regex.Pattern;


/**
 * @author Jesper Zedlitz &lt;jze@informatik.uni-kiel.de&gt;
 *
 */
public class SimpleTypeCheck {
    private static final Pattern PATTERN_DEFINITION_URN_NO_VERSION =
        Pattern.compile(
            "^urn:ogc:def(:(\\w|\\(|\\)|\\+|,|-|\\.|=|\\$|_|!|\\*|''|@|;)+)+$");
    private static final Pattern PATTERN_DEFINITION_URN_WITH_VERSION =
        Pattern.compile(
            "^urn:ogc:def(:(\\w|\\(|\\)|\\+|,|-|\\.|=|\\$|_|!|\\*|''|@|;)+)+:(([0-9])+(\\.([0-9]+))*)*(:(\\w|\\(|\\)|\\+|,|-|\\.|=|\\$|_|!|\\*|''|@|;)+)+$");
    private static final Pattern PATTERN_URN_NO_VERSION =
        Pattern.compile(
            "^urn:ogc(:(\\w|\\(|\\)|\\+|,|-|\\.|=|\\$|_|!|\\*|''|@|;)+)+$");
    private static final Pattern PATTERN_URN_WITH_VERSION =
        Pattern.compile(
            "^urn:ogc(:(\\w|\\(|\\)|\\+|,|-|\\.|=|\\$|_|!|\\*|''|@|;)+)+:(([0-9])+(\\.([0-9]+))*)*(:(\\w|\\(|\\)|\\+|,|-|\\.|=|\\$|_|!|\\*|''|@|;)+)+$");
    private static final Pattern PATTERN_MIME_TYPE =
        Pattern.compile("^(application|audio|image|text|video|message|multipart|model)/.+(;\\s*.+=.+)*",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern PATTERN_XML_TEXT_FORMAT =
        Pattern.compile("^text/xml;\\s*subtype=\"?.+/[0-9\\.]+\"?",
            Pattern.CASE_INSENSITIVE);

    public static void assertIsValidURN(final String input) {
        assertIsValidURN(null, input);
    }

    public static void assertIsValidURN(final String message, final String input) {
        if (!(PATTERN_URN_NO_VERSION.matcher(input).matches() ||
                PATTERN_URN_WITH_VERSION.matcher(input).matches())) {
            throw new AssertionFailedError(StringUtils.defaultString(message) +
                "'" + input + "' is not a valid URN.");
        }
    }

    public static void assertIsValidDefinitionURN(final String input) {
        assertIsValidDefinitionURN(null, input);
    }

    public static void assertIsValidDefinitionURN(final String message,
        final String input) {
        if (!(PATTERN_DEFINITION_URN_NO_VERSION.matcher(input).matches() ||
                PATTERN_DEFINITION_URN_WITH_VERSION.matcher(input).matches())) {
            throw new AssertionFailedError(StringUtils.defaultString(message) +
                "'" + input + "' is not a valid definition URN.");
        }
    }

    public static void assertIsValidTextXMLFormat(final String input) {
        assertIsValidTextXMLFormat(null, input);
    }

    public static void assertIsValidTextXMLFormat(final String message,
        final String input) {
        if (!PATTERN_XML_TEXT_FORMAT.matcher(input).matches()) {
            throw new AssertionFailedError(StringUtils.defaultString(message) +
                "'" + input + "' is not a valid text XML format.");
        }
    }

    public static void assertIsValidMIMETypeFormat(final String input) {
        assertIsValidMIMETypeFormat(null, input);
    }

    public static void assertIsValidMIMETypeFormat(final String message,
        final String input) {
        if (StringUtils.startsWith(input, "text/xml")) {
            assertIsValidTextXMLFormat(message, input);
        } else {
            if (!PATTERN_MIME_TYPE.matcher(input).matches()) {
                throw new AssertionFailedError(StringUtils.defaultString(
                        message) + "'" + input +
                    "' is not a valid MIME type format.");
            }
        }
    }
}
