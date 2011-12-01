package org.christiankakesa.applications.java.shelltaskpool;

//import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UtilsTest {
	
	@Test
	public void testFalseStringToSHA1() {
		assertFalse(Utils.hexSHA1("toto").equals("11112625dc21ef05f6ad4ddf47c5f203837a1111"));
	}
	
	@Test
	public void testTotoStringToSHA1() {
		assertEquals("0b9c2625dc21ef05f6ad4ddf47c5f203837aa32c", Utils.hexSHA1("toto"));
	}
}
