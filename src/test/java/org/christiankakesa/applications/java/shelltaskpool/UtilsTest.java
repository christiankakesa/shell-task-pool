package org.christiankakesa.applications.java.shelltaskpool;

//import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

public class UtilsTest {
	private static final Log LOG = LogFactory.getLog(UtilsTest.class.getName());
	
	@Test
	public void testGetHelp() {
		assertNotNull(Utils.getHelp());
		assertTrue(Utils.getHelp().length() > 0);
	}
	
	@Test
	public void testGetSpace() {
		int noSpace = 0;
		int negativeSpace = -42;
		int positiveSpace = 42;
		assertTrue(Utils.getSpace(noSpace).equals(""));
		assertTrue(Utils.getSpace(negativeSpace).equals(""));
		assertEquals(Utils.getSpace(positiveSpace).length(), positiveSpace);
	}
	
	@Test
	public void testBuildDurationFromDates() {
		final String defaultDuration = "00:00:00";
		assertEquals(Utils.buildDurationFromDates(null, null), defaultDuration);
		final java.util.Date begin = Calendar.getInstance().getTime();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			LOG.error("Can invoque Thread.sleep(5000) in UtilsTest.testBuildDurationFromDates()", e);
		}
		final java.util.Date end = Calendar.getInstance().getTime();
		assertFalse(Utils.buildDurationFromDates(end, begin) == defaultDuration);
	}
	
	@Test
	public void testParseCommandLineToStringArray(){
		final String commandLine = "'-p xx-xx-xx' \"-x toto\" '-42 -42' --Tester testerSize";
		final int nbTokens = 5;
		assertEquals(Utils.parseCommandLineToStringArray(commandLine).length, nbTokens);
	}
	
	@Test
	public void testHexSHA1() {
		assertFalse(Utils.hexSHA1("toto").equals("11112625dc21ef05f6ad4ddf47c5f203837a1111"));
		assertEquals(Utils.hexSHA1("toto"), "0b9c2625dc21ef05f6ad4ddf47c5f203837aa32c");
	}
	
	@Test
	public void testUUID() {
		assertNotNull(Utils.buildUUID());
		final int testTime = 10;
		for(int i = 0; i < testTime; ++i) {
			assertFalse(Utils.buildUUID().isEmpty());
		}
	}
}
