package org.christiankakesa.applications.java.shelltaskpool;

import org.apache.commons.logging.LogFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * Created by IntelliJ IDEA. 
 * User: christian 
 * Date: 13/05/11 
 * Time: 17:13 To change
 * this template use File | Settings | File Templates.
 */
public class JobStats {
	private static final org.apache.commons.logging.Log LOG = LogFactory
			.getLog(JobStats.class);

	private String shellUID; // SHA1 of the command line parameters
	private String shellCommand;
	private Date startDate;
	private Date endDate;
	private String jobDuration;
	private int exitStatus;

	public JobStats(String shellCommand) {
		this.shellCommand = shellCommand;
		shellUID = stringToSHA1(this.shellCommand);
		if (shellUID == null)
			shellUID = "42";
	}

	private static String stringToSHA1(String plainText) {
		if (plainText.isEmpty() || plainText.equals(null)) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		try {
			MessageDigest sha1 = MessageDigest.getInstance("SHA1");
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
	
	public String getShellUID() {
		return shellUID;
	}

	public String getShellCommand() {
		return shellCommand;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * Get the string representation of job duration.
	 *  - format : "00:00:00" <==> "hours:minutes:seconds" 
	 * 
	 * @return
	 */
	public String getJobDuration() {
		if (endDate != null && startDate != null) {
			final long tsTime = (endDate.getTime() - startDate.getTime()) / 1000;
			this.jobDuration = String.format("%02d:%02d:%02d", tsTime / 3600, (tsTime % 3600) / 60, (tsTime % 60));
		} else {
			this.jobDuration = "00:00:00";
		}
		return jobDuration;
	}

	public int getExitStatus() {
		return exitStatus;
	}

	public void setExitStatus(int exitStatus) {
		this.exitStatus = exitStatus;
	}
}
