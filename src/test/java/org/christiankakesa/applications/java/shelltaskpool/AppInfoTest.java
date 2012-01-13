package org.christiankakesa.applications.java.shelltaskpool;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class AppInfoTest {
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Test
	public void testAppInfo() {
		assertNotNull(AppInfo.APP_NAME);
		assertNotNull(AppInfo.AUTHOR_NAME);
		assertNotNull(AppInfo.AUTHOR_EMAIL);
		assertNotNull(AppInfo.APP_COPYRIGHT);
		assertTrue(AppInfo.APP_COPYRIGHT.endsWith(String.valueOf(Calendar.getInstance().get(Calendar.YEAR))));
	}
	
	@Test
	public void AppInfoConstructorTest() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		thrown.expect(IllegalAccessException.class);
		Class<?> cls = Class.forName("org.christiankakesa.applications.java.shelltaskpool.AppInfo");
		cls.newInstance();
	}
}
