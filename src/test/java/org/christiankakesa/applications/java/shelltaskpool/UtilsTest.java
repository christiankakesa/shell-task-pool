package org.christiankakesa.applications.java.shelltaskpool;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UtilsTest {
	
	@Test
	public void testIsEmptyStringToSHA1() {
		assertNull(Utils.stringToSHA1(""));
	}
	
	@Test
	public void testEqualsNullStringToSHA1() {
		String test = null;
		assertNull(Utils.stringToSHA1(test));
	}
	
	@Test
	public void testNullStringToSHA1() {
		assertNull(Utils.stringToSHA1(null));
	}
	
	@Test
	public void testTotoStringToSHA1() {
		assertEquals("0b9c2625dc21ef05f6ad4ddf47c5f203837aa32c", Utils.stringToSHA1("toto"));
	}
}
