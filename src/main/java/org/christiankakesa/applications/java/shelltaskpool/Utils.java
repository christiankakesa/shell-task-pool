package org.christiankakesa.applications.java.shelltaskpool;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 */
public class Utils {
    private static final Log LOG = LogFactory.getLog(Utils.class);

    public static void printHelp() {
        System.out.print(getHelp());
    }

    public static CharSequence getHelp() {
        final int nbSpace = Main.APP_NAME.length() + 7; // 7 == "Usage: ".length();
        final StringBuilder sb = new StringBuilder();
        sb.append("Usage: ").append(Main.APP_NAME).append(" [-h,--help]\n");
        sb.append(getSpace(nbSpace)).append("\tShow this help screen\n");
        sb.append("\n");
        sb.append(getSpace(nbSpace)).append(" [-n,--batchname=]\n");
        sb.append(getSpace(nbSpace)).append("\tSet the name of the entire batch\n");
        sb.append(getSpace(nbSpace)).append("\texample : -n \"Alimentation différentiel des omes\"\n");
        sb.append("\n");
        sb.append(getSpace(nbSpace)).append(" [-c,--corepoolsize=]\n");
        sb.append(getSpace(nbSpace)).append("\tSet number of thread processor\n");
        sb.append(getSpace(nbSpace)).append("\texample : -c5\n");
        sb.append("\n");
        sb.append(getSpace(nbSpace)).append(" [-l,--jobslist=]\n");
        sb.append(getSpace(nbSpace)).append("\tList of jobs seperated by ';'\n");
        sb.append(getSpace(nbSpace)).append("\texample : -l'nslookup google.fr; /path/script2.sh > /tmp/script2.log'\n");
        sb.append("\n");
        sb.append(getSpace(nbSpace)).append(" [-f,--jobsfile=]\n");
        sb.append(getSpace(nbSpace)).append("\tPath to the jobs plain text file. Jobs are separated by new line\n");
        sb.append(getSpace(nbSpace)).append("\texample : -f /home/me/test.job\n");
        sb.append("\n");
        sb.append(getSpace(nbSpace)).append(" [-p,--jobsparam=]\n");
        sb.append(getSpace(nbSpace)).append("\tSet global params to add for each job\n");
        sb.append(getSpace(nbSpace)).append("\texample : -p'-x 2011/05/05 -m 1024'\n");
        sb.append("--------------\n");
        sb.append("Author name  : ").append(Main.AUTHOR_NAME).append("\n");
        sb.append("Author email : ").append(Main.AUTHOR_EMAIL).append("\n");
        sb.append("Copyright    : ").append(Main.APP_COPYRIGHT).append("\n");
        sb.append("\n");
        return sb.toString();
    }

    public static String getSpace(int nbSpace) {
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
     * Build String Array of command line
     * 
     * @param commandLine
     * @return
     */
    public static String[] buildCommandLineArray(final String commandLine) {
        final Pattern p = Pattern.compile("(\"[^\"]*?\"|'[^']*?'|\\S+)");
        final Matcher m = p.matcher(commandLine);
        final List<String> tokens = new ArrayList<String>();
        while (m.find()) {
            tokens.add(m.group(1));
        }
        return tokens.toArray(new String[tokens.size()]);
    }
    
    public static String stringToSHA1(String plainText) {
    	//TODO: Removes unneeded test
		if ("" == plainText || null == plainText || plainText.isEmpty() || plainText.equals(null)) {
			return null;
		}
		final StringBuilder sb = new StringBuilder();
		try {
			final MessageDigest sha1 = MessageDigest.getInstance("SHA1");
			byte[] digest = sha1.digest((plainText).getBytes());
			String hexString;
			for (byte b : digest) {
				hexString = Integer.toHexString(0x00FF & b);
				sb.append(hexString.length() == 1 ? "0" + hexString : hexString);
			}
		} catch (NoSuchAlgorithmException e) {
			LOG.error(e);
			return null;
		}

		return sb.toString();
	}
}
