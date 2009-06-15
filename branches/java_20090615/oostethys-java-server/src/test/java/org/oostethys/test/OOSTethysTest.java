package org.oostethys.test;

import org.oostethys.sos.Netcdf2sos100;

import java.util.logging.Level;
import java.util.logging.Logger;


public abstract class OOSTethysTest extends GeneralTest {
    Logger logger = Logger.getLogger("OOSTethysTest");

    protected void setUp() throws Exception {
        super.setUp();

        Logger logger = Logger.getLogger(Netcdf2sos100.class.getName());
        logger.setLevel(Level.ALL);
    }

    public static String[] createArray(final String value) {
        String[] array = { value };

        return array;
    }

    protected static void assertContains(final String message, final String haystack,
        final String needle) {
        if (!haystack.contains(needle)) {
            fail(message);
        }
    }

    protected static void assertContains(final String haystack, final String needle) {
        if (!haystack.contains(needle)) {
            fail("expected text to contain:<" + needle + "> was not:<" +
                haystack + ">");
        }
    }

    protected static void assertDoesNotContain(final String haystack, final String needle) {
        if (haystack.contains(needle)) {
            fail("expected text not to contain:<" + needle + "> was:<" +
                haystack + ">");
        }
    }

}
