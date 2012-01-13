package org.christiankakesa.applications.java.shelltaskpool;

import java.util.Calendar;

public final class AppInfo {
	/**
	 * Application name.
	 * TODO: Try to retrieve dynamically the program name.
	 */
	public static final String APP_NAME = "shelltaskpool.jar";
	/**
	 * Application author name.
	 */
	public static final String AUTHOR_NAME = "Christian Kakesa";
	/**
	 * Application author email.
	 */
	public static final String AUTHOR_EMAIL = "christian.kakesa@gmail.com";
	/**
	 * Application copyright.
	 */
	public static final String APP_COPYRIGHT = "Christian Kakesa (c) "
			+ Calendar.getInstance().get(Calendar.YEAR);

	/**
	 * Private default constructor
	 */
	private AppInfo(){
	}
}
