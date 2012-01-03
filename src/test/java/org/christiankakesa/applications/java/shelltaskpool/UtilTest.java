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

public class UtilTest {
	private static final Log LOG = LogFactory.getLog(UtilTest.class.getName());
	
	@Test
	public void testGetHelp() {
		assertNotNull(Util.getHelp());
		assertTrue(Util.getHelp().length() > 0);
	}
	
	@Test
	public void testGetSpace() {
		int noSpace = 0;
		int negativeSpace = -42;
		int positiveSpace = 42;
		assertTrue(Util.getSpace(noSpace).equals(""));
		assertTrue(Util.getSpace(negativeSpace).equals(""));
		assertEquals(Util.getSpace(positiveSpace).length(), positiveSpace);
	}
	
	@Test
	public void testBuildDurationFromDates() {
		final int sleepTime = 1000;
		final String defaultDuration = "00:00:00";
		assertEquals(Util.buildDurationFromDates(null, null), defaultDuration);
		final java.util.Date begin = Calendar.getInstance().getTime();
		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			LOG.error("Can invoque Thread.sleep(" + String.valueOf(sleepTime) + ") in UtilsTest.testBuildDurationFromDates()", e);
		}
		final java.util.Date end = Calendar.getInstance().getTime();
		assertFalse(Util.buildDurationFromDates(end, begin) == defaultDuration);
	}
	
	@Test
	public void testParseCommandLineToStringArray(){
		final String commandLine = "'-p xx-xx-xx' \"-x toto\" '-42 -42' --Tester testerSize";
		final int nbTokens = 5;
		assertEquals(Util.parseCommandLineToStringArray(commandLine).length, nbTokens);
	}
	
	@Test
	public void testDefaultCorePoolSize(){
		final int minimumCore = 1;
		assertTrue(Util.defaultCorePoolSize() >= minimumCore);
	}
	
	@Test
	public void testUUID() {
		final int uuidLength = 32; //Type 4 UUID is 36 chars but we removes the 4 '-' separator characters.
		assertFalse(Util.buildUUID().isEmpty());
		assertNotNull(Util.buildUUID());
		assertEquals(Util.buildUUID().length(), uuidLength);
		final String uuid1 = Util.buildUUID();
		final String uuid2 = Util.buildUUID();
		assertFalse(uuid1.equals(uuid2));
	}
	
	@Test
	public void testRemoveChar() {
		final String oneS = "titi-toto";
		final String twoS = "titi!toto";
		final String threeS = "titi+toto";
		final String result = "tititoto";
		assertEquals(Util.removeCharFromString(oneS, '-'), result);
		assertEquals(Util.removeCharFromString(twoS, '!'), result);
		assertEquals(Util.removeCharFromString(threeS, '+'), result);
	}
}
