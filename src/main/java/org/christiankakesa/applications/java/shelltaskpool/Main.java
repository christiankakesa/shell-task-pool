package org.christiankakesa.applications.java.shelltaskpool;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <b>Shell Task Pool</b>.
 * 
 * This is the main class of the program. This project aims to parallelize shell
 * command execution efficiently and safely. All the task are logged and could
 * be parsed by a reporting tool.
 * 
 * @author Christian Kakesa (christian.kakesa@gmail.com)
 */
public final class Main {
	private static final int MAX_JOBS = 5120;
	private static final int MAX_LINE_LENGTH = 2048;
	private static final char JOB_SEPARATOR = ';';

	private static final List<String> JOBS_ARRAY_LIST = new ArrayList<String>(
			MAX_JOBS);

	private static int corePoolSize = Util.defaultCorePoolSize();
	private static String jobsFile;
	private static String jobsList;
	private static String jobsParam;
	private static String jobsLogDir;

	private static final Logger LOG = Logger.getLogger(Main.class);

	private Main() {}

	public static void main(String[] args) {
		Main.CmdLineParser clp = new Main.CmdLineParser(args);
		clp.parse();
		Main.prepareJobListToExecute();// Populate JOBS_ARRAY_LIST
		if (Main.JOBS_ARRAY_LIST.isEmpty()) {
			LOG.error("No jobs found.");
			Util.printHelpAndExit();
		}
		if (Main.jobsLogDir != null) {
			File f = new File(Main.jobsLogDir);
			if (f.isDirectory()) {
				Batch.getInstance().setLogDirectory(Main.jobsLogDir);
			} else {
				LOG.error(Main.jobsLogDir + " is not a directory.");
				Util.printHelpAndExit();
			}
			f = null;
		}
		LOG.debug("[BATCH_PARAMETERS] BatchId: " + Batch.getInstance().getId()
				+ " | BatchName: " + Batch.getInstance().getName()
				+ " | BatchParameter: " + StringUtils.join(args, " ")
				+ " | BatchCorePoolSize: " + Main.corePoolSize
				+ " | BatchTotalJobs: " + Main.JOBS_ARRAY_LIST.size());
		MyThreadPoolExecutor mtpe = new MyThreadPoolExecutor(Main.corePoolSize,
				Main.corePoolSize);
		for (String cmd : Main.JOBS_ARRAY_LIST) {
			mtpe.addTask(new ShellTaskWorker(cmd));
		}
		mtpe.shutdown();
	}

	private static class CmdLineParser {
		private final String[] pParams;

		public CmdLineParser(final String[] params) {
			this.pParams = params.clone();
		}

		private void parse() {
			if (this.pParams == null || this.pParams.length == 0) {
				LOG.error("No argument found");
				Util.printHelpAndExit();
			}
			this.parseCmdLine();
		}

		public final void parseCmdLine() {
			String[] params = new String[this.pParams.length];
			System.arraycopy(this.pParams, 0, params, 0, this.pParams.length);
			int opt;
			String arg;
			final LongOpt[] opts = {
					new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h'),
					new LongOpt("batchname", LongOpt.REQUIRED_ARGUMENT, null, 'n'),
					new LongOpt("jobslist", LongOpt.OPTIONAL_ARGUMENT, null, 'j'),
					new LongOpt("jobsfile", LongOpt.OPTIONAL_ARGUMENT, null, 'f'),
					new LongOpt("jobsparam", LongOpt.OPTIONAL_ARGUMENT, null, 'p'),
                    new LongOpt("corepoolsize", LongOpt.OPTIONAL_ARGUMENT, null, 'c'),
                    new LongOpt("jobslogdir", LongOpt.OPTIONAL_ARGUMENT, null, 'l')};
			Getopt g = new Getopt(AppInfo.APP_NAME, params, "hn:j::f::p::c::l::", opts, false);
			g.setOpterr(true);
			while ((opt = g.getopt()) != -1) {
				switch (opt) {
				case 'h':
					Util.printHelpAndExit();
					break;
				case 'n':
					arg = g.getOptarg();
					Batch.getInstance().setName(arg);
					LOG.debug("Param [batchname]: " + Batch.getInstance().getName());
					LOG.debug("         Batch Id: " + Batch.getInstance().getId());
					break;
				case 'j':
					arg = g.getOptarg();
					Main.jobsList = arg;
					LOG.debug("Param [jobslist]: " + Main.jobsList);
					break;
				case 'f':
					arg = g.getOptarg();
					Main.jobsFile = arg;
					LOG.debug("Param [jobsfile]: " + Main.jobsFile);
					break;
				case 'p':
					arg = g.getOptarg();
					Main.jobsParam = arg;
					LOG.debug("Param [jobsparam]: " + Main.jobsParam);
					break;
                case 'c':
                    arg = g.getOptarg();
                    try {
                        Main.corePoolSize = Integer.valueOf(arg);
                    } catch (NumberFormatException e) {
                        LOG.warn("Wrong corePoolSize set: " + arg);
                        Main.corePoolSize = Util.defaultCorePoolSize();
                        LOG.warn("Detected free cpu core set: corePoolSize=" + Main.corePoolSize);
                    }
                    LOG.debug("Param [corepoolsize]: " + Main.corePoolSize);
                    break;
                case 'l':
					arg = g.getOptarg();
					Main.jobsLogDir = arg;
					LOG.debug("Param [jobslogdir]: " + Main.jobsLogDir);
					break;
				default:
					LOG.error("Unknown parameter : " + Character.toString((char) opt));
					break;
				}
			}
			if (Batch.getInstance().getName() == null) {
				LOG.error("Name of the batch are required. Set the \"n\" parameter");
				Util.printHelpAndExit();
			}
		}
	}

	private static void prepareJobListToExecute() {
		if (Main.jobsList != null) {
			String[] jl = StringUtils.split(Main.jobsList, Main.JOB_SEPARATOR);
			for (String s : jl) {
				Main.addJob(s.trim());
			}
		}
		if (Main.jobsFile != null) {
			try {
				FileReader fr = new FileReader(Main.jobsFile);
				BufferedReader br = new BufferedReader(fr);
				try {
					String jobsFileLine;
					while ((jobsFileLine = br.readLine()) != null) {
						// Test if line is not a comment (starting with "#") or not empty
						if ((jobsFileLine.trim().length() > 0)
								&& !jobsFileLine.trim().startsWith("#")) {
							Main.addJob(jobsFileLine.trim());
						}
					}
				} catch (IOException e) {
					LOG.error("Problem with the jobs file : " + Main.jobsFile,
							e);
				} finally {
					try {
						br.close();
					} catch (IOException e) {
						LOG.warn("Can't close jobsFile reader stream : "
								+ e.getLocalizedMessage());
					}
					fr = null;
				}
			} catch (FileNotFoundException fne) {
				LOG.error("jobsFile not exists : " + Main.jobsFile, fne);
				Util.printHelpAndExit();
			}
		}
	}

	/**
	 * Add job in the list of the jobs <b>Main.allJobs</b>. If jobsParam is set,
	 * jobsParam is added to the jobCommandLine.
	 * 
	 * @param jobCommandLine
	 * @return true if job is correctly added
	 */
	private static void addJob(final String jobCommandLine) {
		String jcl = jobCommandLine;
		if (jcl.length() == 0) { // Exit the method if jobCommandLine is empty
			LOG.warn("Can't add empty job");
			return;
		}
		if (Main.jobsParam != null && Main.jobsParam.length() > 0) {
			// Add global job parameter if <code>jobsParam</code> is not null
			// and <code>jobsParam</code> contains parameter.
			jcl = jcl + " " + Main.jobsParam;
		}
		if (Main.JOBS_ARRAY_LIST.size() < Main.MAX_JOBS) {
			if (jcl.length() < Main.MAX_LINE_LENGTH) {
				Main.JOBS_ARRAY_LIST.add(jcl);
			} else {
				LOG.warn("Length of the jobs command line is too long: \n"
						+ "           Command line: " + jcl + "\n"
						+ "    Command line length: " + jcl.length() + "\n"
						+ "             Maximum is: " + Main.MAX_LINE_LENGTH
						+ " !!!");
			}
		} else {
			LOG.error("Maximum of jobs is " + Main.MAX_JOBS);
			LOG.error("Reduce the number of jobs");
			Util.printHelpAndExit();
		}
	}
}
