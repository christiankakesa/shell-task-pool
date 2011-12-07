package org.christiankakesa.applications.java.shelltaskpool;

//import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Calendar;

import org.junit.Test;

public class UtilsTest {
	@Test
	public void testBuildDurationFromDates() {
		final String defaultDuration = "00:00:00";
		assertEquals(Utils.buildDurationFromDates(null, null), defaultDuration);
		final java.util.Date begin = Calendar.getInstance().getTime();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	public void testFalseStringToSHA1() {
		assertFalse(Utils.hexSHA1("toto").equals("11112625dc21ef05f6ad4ddf47c5f203837a1111"));
	}
	
	@Test
	public void testTotoStringToSHA1() {
		assertEquals("0b9c2625dc21ef05f6ad4ddf47c5f203837aa32c", Utils.hexSHA1("toto"));
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
