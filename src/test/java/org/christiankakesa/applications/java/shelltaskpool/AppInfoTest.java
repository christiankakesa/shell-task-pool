package org.christiankakesa.applications.java.shelltaskpool;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;

public class AppInfoTest {
	public void testAppInfo(){
		assertNotNull(AppInfo.APP_NAME);
		assertNotNull(AppInfo.AUTHOR_NAME);
		assertNotNull(AppInfo.AUTHOR_EMAIL);
		assertNotNull(AppInfo.APP_COPYRIGHT);
		assertTrue(AppInfo.APP_COPYRIGHT.endsWith(String.valueOf(Calendar.getInstance().get(Calendar.YEAR))));
	}
}
