package org.christiankakesa.applications.java.shelltaskpool;

//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Shell Task Pool utility class
 */
public final class Utils {
	/**
	 * Static logger
	 */
	private static final Log LOG = LogFactory.getLog(Utils.class.getName());
	public static final String APP_NAME = "shelltaskpool.jar";
	public static final String AUTHOR_NAME = "Christian Kakesa";
	public static final String AUTHOR_EMAIL = "christian.kakesa@gmail.com";
	public static final String APP_COPYRIGHT = "Christian Kakesa (c) "
			+ Calendar.getInstance().get(Calendar.YEAR);
	

	/**
	 * private constructor
	 */
	private Utils() {
	}

	/**
	 * Print help screen
	 */
	public static void printHelp() {
		System.out.printf("%s", Utils.getHelp());
		System.out.flush();
	}

	/**
	 * Print help screen and exit the program
	 */
	public static void printHelpAndExit() {
		Utils.printHelp();
		System.exit(0);
	}

	/**
	 * Application help string
	 * @return Help screen string content
	 */
	public static String getHelp() {
		final String dynamicSpaceAppend = Utils.getSpace(Utils.APP_NAME.length()
				+ "Usage: ".length());
		String result = "Usage: "
				+ Utils.APP_NAME
				+ " [-h,--help]\n"
				+ dynamicSpaceAppend
				+ "\tShow this help screen\n\n"
				+ dynamicSpaceAppend
				+ " [-n,--batchname=]\n"
				+ dynamicSpaceAppend
				+ "\tSet the name of the entire batch\n"
				+ dynamicSpaceAppend
				+ "\texample : -n \"Alimentation différentiel des omes\"\n\n"
				+ dynamicSpaceAppend
				+ " [-c,--corepoolsize=]\n"
				+ dynamicSpaceAppend
				+ "\tSet number of thread processor\n"
				+ dynamicSpaceAppend
				+ "\texample : -c5\n\n"
				+ dynamicSpaceAppend
				+ " [-j,--jobslist=]\n"
				+ dynamicSpaceAppend
				+ "\tList of jobs seperated by ';'\n"
				+ dynamicSpaceAppend
				+ "\texample : -l'nslookup google.fr; /path/script2.sh > /tmp/script2.log'\n\n"
				+ dynamicSpaceAppend
				+ " [-f,--jobsfile=]\n"
				+ dynamicSpaceAppend
				+ "\tPath to the jobs plain text file. Jobs are separated by new line\n"
				+ dynamicSpaceAppend + "\texample : -f /home/me/test.job\n\n"
				+ dynamicSpaceAppend + " [-p,--jobsparam=]\n"
				+ dynamicSpaceAppend
				+ "\tSet global params to add for each job\n"
				+ dynamicSpaceAppend
				+ "\texample : -p'-x 2011/05/05 -m 1024'\n"
				+ "--------------\n" + "Author name  : " + Utils.AUTHOR_NAME
				+ "\n" + "Author email : " + Utils.AUTHOR_EMAIL + "\n"
				+ "Copyright    : " + Utils.APP_COPYRIGHT + "\n";
		return result;
	}

	/**
	 * Build string of number space in parameter
	 * @param nbSpace
	 * @return String of space
	 */
	public static String getSpace(final int nbSpace) {
		if (nbSpace <= 0) {
			return "";
		}
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < nbSpace; ++i) {
			sb.append(" ");
		}
		return sb.toString();
	}

	/**
	 * Build string duration between two dates :
	 * <ul>
	 * <li>format : HH:MM:SS</li>
	 * </ul>
	 * @param end
	 * @param start
	 * @return String representation of the duration
	 */
	public static String buildDurationFromDates(final Date end, final Date start) {
		if (end != null && start != null) {//Build the duration only if both parameters are not null
			final long milliInSecond = 1000;
			final long secondsInHour = 3600;
			final long secondsInMinute = 60;
			final long tsTime = (end.getTime() - start.getTime())
					/ milliInSecond;
			//tsTime / 3600, (tsTime % 3600) / 60, (tsTime % 60)
			return String.format("%02d:%02d:%02d", tsTime / secondsInHour,
					(tsTime % secondsInHour) / secondsInMinute,
					(tsTime % secondsInMinute));
		}
		LOG.debug("Can't determine duration : endDate = " + end
				+ " - startDate = " + start);
		return "00:00:00";
	}

	/**
	 * Build String Array of command line.
	 * @param commandLine
	 * @return String array of the command line string
	 */
	public static String[] parseCommandLineToStringArray(
			final String commandLine) {
		final Pattern p = Pattern.compile("(\"[^\"]*?\"|'[^']*?'|\\S+)");
		final Matcher m = p.matcher(commandLine);
		final List<String> tokens = new ArrayList<String>();
		while (m.find()) {
			tokens.add(m.group(1));
		}
		return tokens.toArray(new String[tokens.size()]);
	}
	
	/**
	 * Detect and return the number of the available Processor
	 * @return Number of available cores
	 */
	public static int defaultCorePoolSize(){
		return Runtime.getRuntime().availableProcessors();
	}

//	/**
//	 * Build an hexadecimal SHA1 hash for string.
//	 * 
//	 * @param plainText
//	 * @return An hexadecimal string hash
//	 */
//	public static String hexSHA1(final String plainText) {
//		/**
//		 * Exit the method when parameter is <code>null</code> or empty.
//		 */
//		if (plainText == null || plainText.isEmpty()) {
//			return "";
//		}
//		final StringBuilder sb = new StringBuilder();
//		try {
//
//			final MessageDigest sha1 = MessageDigest.getInstance("SHA1");
//			byte[] digest = sha1.digest((plainText).getBytes());
//			String hexString;
//			final int hexPadSHA1 = 0x00FF;
//			for (byte b : digest) {
//				hexString = Integer.toHexString(hexPadSHA1 & b);
//				sb.append(hexString.length() == 1 ? "0" + hexString : hexString);
//			}
//		} catch (NoSuchAlgorithmException e) {
//			LOG.error("Can't build a SHA1 MessageDigest object", e);
//		}
//		return sb.toString();
//	}

	/**
	 * Generate UUID string without '-' character.
	 * @return String UUID without '-'
	 */
	public static String buildUUID() {
		UUID newUUID = UUID.randomUUID();
		return Utils.removeCharFromString(newUUID.toString(), '-');
	}

	/**
	 * Remove character from given string
	 * @param givenString
	 * @param c
	 * @return String without given character
	 */
	public static String removeCharFromString(final String givenString, final char c) {
		final StringBuffer r = new StringBuffer(givenString.length());
		r.setLength(givenString.length());
		int current = 0;
		char cur;
		for (int i = 0; i < givenString.length(); ++i) {
			cur = givenString.charAt(i);
			if (cur != c) {
				r.setCharAt(current++, cur);
			}
		}
		return r.toString().trim();
	}
}
