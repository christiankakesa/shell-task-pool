package org.christiankakesa.applications.java.shelltaskpool;

import static org.junit.Assert.assertNotNull;

public class AppInfoTest {
	public void testAppInfo(){
		assertNotNull(AppInfo.APP_NAME);
		assertNotNull(AppInfo.AUTHOR_NAME);
		assertNotNull(AppInfo.AUTHOR_EMAIL);
		assertNotNull(AppInfo.APP_COPYRIGHT);
	}

}
