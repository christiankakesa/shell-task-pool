package org.christiankakesa.applications.java.shelltaskpool;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Shell Task Pool utility class.
 * This class implements helpers methods.
 */
public final class Util {
	/**
	 * Static logger.
	 */
	private static final Logger LOG = Logger.getLogger(Util.class);

	/**
	 * Util private constructor.
	 */
	private Util() {
	}

	/**
	 * Print help screen.
	 */
	public static void printHelp() {
		Logger.getLogger("STDOUT").log(Level.INFO, Util.getHelp());
	}

	/**
	 * Print help screen and exit the program
	 */
	public static void printHelpAndExit() {
		Util.printHelp();
		System.exit(0);
	}

	/**
	 * Application help string
	 * @return Help screen string content
	 */
	public static String getHelp() {
		String dynamicSpaceAppend = Util.getSpace("Usage: ".length() + AppInfo.APP_NAME.length());
		String result = "Usage: " + AppInfo.APP_NAME + " -n \"Batch name\" -j'/path/to/shell.sh > /var/log/shell.sh.log;/path/to/job.sh' [OPTIONS]\n"
				+ "   or: " + AppInfo.APP_NAME + " -n \"Batch name\" -f/path/to/file.job [OPTIONS]\n"
				+ "   or: " + AppInfo.APP_NAME + " -h\n";
		dynamicSpaceAppend = Util.getSpace("   ".length());
		result += dynamicSpaceAppend
				+ "[-h,--help]\n"
				+ dynamicSpaceAppend
				+ "\tShow this help screen\n\n"
				+ dynamicSpaceAppend
				+ "[-n,--batchname=]\n"
				+ dynamicSpaceAppend
				+ "\tSet the name of the entire batch\n"
				+ dynamicSpaceAppend
				+ "\texample : -n \"Alimentation différentiel des omes\"\n\n"
				+ dynamicSpaceAppend
				+ "[-c,--corepoolsize=]\n"
				+ dynamicSpaceAppend
				+ "\tSet number of thread processor\n"
				+ dynamicSpaceAppend
				+ "\texample : -c5\n\n"
				+ dynamicSpaceAppend
				+ "[-j,--jobslist=]\n"
				+ dynamicSpaceAppend
				+ "\tList of jobs seperated by ';'\n"
				+ dynamicSpaceAppend
				+ "\texample : -j'nslookup google.fr; /path/script2.sh > /tmp/script2.log'\n\n"
				+ dynamicSpaceAppend
				+ "[-f,--jobsfile=]\n"
				+ dynamicSpaceAppend
				+ "\tPath to the jobs plain text file. Jobs are separated by new line\n"
				+ dynamicSpaceAppend + "\texample : -f/home/me/test.job\n\n"
				+ dynamicSpaceAppend + " [-p,--jobsparam=]\n"
				+ dynamicSpaceAppend
				+ "\tSet global params to add for each job\n"
				+ dynamicSpaceAppend
				+ "\texample : -p'-x 2011/05/05 -m 1024'\n"
				+ "--------------\n" + "Author name  : " + AppInfo.AUTHOR_NAME
				+ "\n" + "Author email : " + AppInfo.AUTHOR_EMAIL + "\n"
				+ "Copyright    : " + AppInfo.APP_COPYRIGHT + "\n";
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
		for (int i = nbSpace; i > 0; --i) {
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
			final long tmTime = end.getTime() - start.getTime();
			final long tsTime = tmTime / milliInSecond;
			//tsTime / 3600, (tsTime % 3600) / 60, (tsTime % 60)
			final int hours = (int) (tsTime / secondsInHour);
			final int minutes = (int) ((tsTime % secondsInHour) / secondsInMinute);
			final int seconds = (int) (tsTime % secondsInMinute);
			final int millis = (int) (tmTime % milliInSecond);
			return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, millis);
		}
		LOG.warn("Can't determine duration : endDate = " + end
				+ " - startDate = " + start);
		return "00:00:00.000";
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

	/**
	 * Generate UUID string without all '-' characters.
	 * 
	 * @return UUID without all '-' characters
	 */
	public static String buildUUID() {
		UUID newUUID = UUID.randomUUID();
		return Util.removeCharFromString(newUUID.toString(), '-');
	}

	/**
	 * Remove character from given string
	 * @param givenString
	 * @param c
	 * @return String without given character
	 */
	public static String removeCharFromString(final String givenString, final char c) {
		final StringBuilder r = new StringBuilder(givenString.length());
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
