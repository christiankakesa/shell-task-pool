package org.christiankakesa.applications.java.shelltaskpool;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
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
	
	/**
	 * private constructor
	 */
	private Utils() {
	}

	/**
	 * Print help screen
	 */
	public static void printHelp() {
		System.out.printf("%s", getHelp());
		System.out.flush();
	}

	/**
	 * Print help screen and exit the program
	 */
	public static void printHelpAndExit() {
		printHelp();
		System.exit(0);
	}

	/**
	 * Application help string
	 * @return string of the help screen
	 */
	public static String getHelp() {
		final String dynamicSpaceAppend = getSpace(Main.APP_NAME.length() + "Usage: ".length());
		String result = "Usage: " + Main.APP_NAME + " [-h,--help]\n"
				+ dynamicSpaceAppend + "\tShow this help screen\n\n"
				+ dynamicSpaceAppend + " [-n,--batchname=]\n"
				+ dynamicSpaceAppend + "\tSet the name of the entire batch\n"
				+ dynamicSpaceAppend	+ "\texample : -n \"Alimentation différentiel des omes\"\n\n"
				+ dynamicSpaceAppend + " [-c,--corepoolsize=]\n"
				+ dynamicSpaceAppend + "\tSet number of thread processor\n"
				+ dynamicSpaceAppend + "\texample : -c5\n\n"
				+ dynamicSpaceAppend + " [-j,--jobslist=]\n"
				+ dynamicSpaceAppend + "\tList of jobs seperated by ';'\n"
				+ dynamicSpaceAppend	+ "\texample : -l'nslookup google.fr; /path/script2.sh > /tmp/script2.log'\n\n"
				+ dynamicSpaceAppend + " [-f,--jobsfile=]\n"
				+ dynamicSpaceAppend	+ "\tPath to the jobs plain text file. Jobs are separated by new line\n"
				+ dynamicSpaceAppend + "\texample : -f /home/me/test.job\n\n"
				+ dynamicSpaceAppend + " [-p,--jobsparam=]\n"
				+ dynamicSpaceAppend	+ "\tSet global params to add for each job\n"
				+ dynamicSpaceAppend + "\texample : -p'-x 2011/05/05 -m 1024'\n"
				+ "--------------\n"
				+ "Author name  : " + Main.AUTHOR_NAME + "\n"
				+ "Author email : " + Main.AUTHOR_EMAIL + "\n"
				+ "Copyright    : " + Main.APP_COPYRIGHT + "\n\n";
		return result;
	}

	/**
	 * Build string of number space in parameter
	 * @param nbSpace
	 * @return string of space
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
	 * Build string duration between two dates
	 * <ul>
	 * <li>format : HH:MM:SS</li>
	 * </ul>
	 * 
	 * @param end
	 * @param start
	 * @return string representation of the duration
	 */
	public static String buildDurationFromDates(final Date end, final Date start) {
		if (end != null && start != null) {
			final long secondInMilli = 1000;
			final long secondsInHour = 3600;
			final long secondsInMinute = 60;
			final long tsTime = (end.getTime() - start.getTime())
					/ secondInMilli;
			/**
			 * tsTime / 3600, (tsTime % 3600) / 60, (tsTime % 60)
			 */
			return String.format("%02d:%02d:%02d", tsTime / secondsInHour,
					(tsTime % secondsInHour) / secondsInMinute,
					(tsTime % secondsInMinute));
		}
		LOG.debug("Can't determine duration : endDate = " + end
				+ " - startDate = " + start);
		return "00:00:00";
	}

	/**
	 * Build String Array of command line
	 * 
	 * @param commandLine
	 * @return String[] of the string command line
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
	 * Build an hexadecimal SHA1 hash for string
	 * 
	 * @param plainText
	 * @return An hexadecimal string hash
	 */
	public static String hexSHA1(final String plainText) {
		if (null == plainText) {
			return "";
		}
		final StringBuilder sb = new StringBuilder();
		try {

			final MessageDigest sha1 = MessageDigest.getInstance("SHA1");
			byte[] digest = sha1.digest((plainText).getBytes());
			String hexString;
			final int hexPadSHA1 = 0x00FF;
			for (byte b : digest) {
				hexString = Integer.toHexString(hexPadSHA1 & b);
				sb.append(hexString.length() == 1 ? "0" + hexString : hexString);
			}
		} catch (NoSuchAlgorithmException e) {
			LOG.error("Can't build a SHA1 MessageDigest object", e);
		}
		return sb.toString();
	}

	/**
	 * Generate UUID string
	 * 
	 * @return an UUID String
	 */
	public static String buildUUID() {
		return UUID.randomUUID().toString();
	}
}
