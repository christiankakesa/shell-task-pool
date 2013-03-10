package org.christiankakesa.applications.java.shelltaskpool;

//import static org.junit.Assert.assertNull;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.security.Permission;
import java.util.Calendar;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class UtilTest extends TestCase {
    private static final Log LOG = LogFactory.getLog(UtilTest.class.getName());

    //@Test
    public void testUtilConstructorTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        /**
         * @see http://www.stupidplebs.com/testing-private-constructors
         */
        // get the constructor that takes no parameters
        Constructor<?> constructor = Util.class.getDeclaredConstructor();
        // the modifiers int can tell us metadata about the constructor
        int constructorModifiers = constructor.getModifiers();
        // but we're only interested in knowing that it's private
        assertThat(Modifier.isPrivate(constructorModifiers), is(true));
        // constructor is private so we first have to make it accessible
        constructor.setAccessible(true);
        // now construct an instance
        constructor.newInstance();
    }

    //@Test
    /*public void testPrintHelp() {
        StringBuilder res = new StringBuilder();
        try {
            final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));
            Util.printHelp();
            res.append(outContent.toString());
        } finally {
            System.setOut(null);
        }
        assertTrue(res.toString().length() > 0);
    }*/

    //@Test
    public void testPrintHelpAndExit() {
        /**
         * @see http://stackoverflow.com/questions/309396/java-how-to-test-methods-that-call-system-exit
         */
        int resPrintHelpAndExit = 0;
        int resSystemExit = 0;
        try {
            Util.printHelpAndExit();
        } catch (ExitException e) {
            resPrintHelpAndExit = e.status;
        }
        try {
            System.exit(0);
        } catch (ExitException e) {
            resSystemExit = e.status;
        }
        assertTrue(resPrintHelpAndExit == resSystemExit);
    }

    //@Test
    public void testGetHelp() {
        assertNotNull(Util.getHelp());
        assertTrue(Util.getHelp().length() > 0);
    }

    //@Test
    public void testGetSpace() {
        final int NO_SPACE = 0;
        final int NEGATIVE_SPACE = -42;
        final int POSITIVE_SPACE = 42;
        assertTrue(Util.getSpace(NO_SPACE).equals(""));
        assertTrue(Util.getSpace(NEGATIVE_SPACE).equals(""));
        assertEquals(Util.getSpace(POSITIVE_SPACE).length(), POSITIVE_SPACE);
    }

    //@Test
    public void testBuildDurationFromDates() {
        final int SLEEP_TIME = 1000;
        final String DEFAULT_DURATION = "00:00:00.000";
        assertEquals(Util.buildDurationFromDates(null, null), DEFAULT_DURATION);
        final java.util.Date BEGIN_DATE = Calendar.getInstance().getTime();
        try {
            Thread.sleep(SLEEP_TIME);
        } catch (InterruptedException e) {
            LOG.warn("Can invoke Thread.sleep(" + String.valueOf(SLEEP_TIME) + ") in UtilsTest.testBuildDurationFromDates()", e);
        }
        final java.util.Date END_DATE = Calendar.getInstance().getTime();
        assertFalse(Util.buildDurationFromDates(BEGIN_DATE, END_DATE).equals(DEFAULT_DURATION));
        final long elapsedTime = 9762042L; //2 hours, 42 minutes, 42 seconds, 42 milliseconds.
        END_DATE.setTime(BEGIN_DATE.getTime() + elapsedTime);
        assertEquals(Util.buildDurationFromDates(BEGIN_DATE, END_DATE), "02:42:42.042");
    }

    //@Test
    public void testParseCommandLineToStringArray() {
        final String CMD_LINE_PARAMS = "'-p xx-xx-xx' \"-x toto\" '-42 -42' --Tester testerSize";
        final int NB_TOKEN = 5;
        assertEquals(Util.parseCommandLineToStringArray(CMD_LINE_PARAMS).length, NB_TOKEN);
    }

    //@Test
    public void testDefaultCorePoolSize() {
        final int MINIMUM_CORE = 1;
        assertTrue(Util.defaultCorePoolSize() >= MINIMUM_CORE);
    }

    //@Test
    public void testUUID() {
        final int UUID_LENGTH = 32; //Type 4 UUID is 36 chars but we removes the 4 '-' separator characters.
        assertFalse(Util.buildUUID().length() == 0);
        assertNotNull(Util.buildUUID());
        assertEquals(Util.buildUUID().length(), UUID_LENGTH);
        final String UUID1 = Util.buildUUID();
        final String UUID2 = Util.buildUUID();
        assertFalse(UUID1.equals(UUID2));
    }

    //@Test
    public void testRemoveChar() {
        final String ONE_S = "-titi-toto-";
        final String TWO_S = "!titi!toto!";
        final String THREE_S = "+titi+toto+";
        final String RESULT = "tititoto";
        assertEquals(Util.removeCharFromString(ONE_S, '-'), RESULT);
        assertEquals(Util.removeCharFromString(TWO_S, '!'), RESULT);
        assertEquals(Util.removeCharFromString(THREE_S, '+'), RESULT);
    }

    static class ExitException extends SecurityException {
        public final int status;

        public ExitException(int status) {
            super("There is no escape!");
            this.status = status;
        }
    }

    private static class NoExitSecurityManager extends SecurityManager {
        @Override
        public void checkPermission(Permission perm) {
            // allow anything.
        }

        @Override
        public void checkPermission(Permission perm, Object context) {
            // allow anything.
        }

        @Override
        public void checkExit(int status) {
            super.checkExit(status);
            throw new ExitException(status);
        }
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.setSecurityManager(new NoExitSecurityManager());
    }

    @Override
    protected void tearDown() throws Exception {
        System.setSecurityManager(null); // or save and restore original
        super.tearDown();
    }
}
