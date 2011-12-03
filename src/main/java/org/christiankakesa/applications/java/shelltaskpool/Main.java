package org.christiankakesa.applications.java.shelltaskpool;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Main class of <b>Shell Task Pool</b>
 */
public final class Main {
	public static final String APP_NAME = "shelltaskpool.jar";
	public static final String AUTHOR_NAME = "Christian Kakesa";
	public static final String AUTHOR_EMAIL = "christian.kakesa@gmail.com";
	public static final String APP_COPYRIGHT = "Christian Kakesa (c) "
			+ Calendar.getInstance().get(Calendar.YEAR);
	public static final int DEFAULT_CORE_POOL_SIZE = 2;
	public static final long THREAD_KEEP_ALIVE_TIME = 30L;
	public static final int MAX_JOBS = 5120;
	public static final int MAX_LINE_LENGTH = 2048;
	public static final char JOB_SEPARATOR = ';';
	public static final int DEFAULT_ERROR_CODE = -42;

	private static final List<String> JOBS_ARRAY_LIST = new ArrayList<String>(
			MAX_JOBS);

	private static int corePoolSize = DEFAULT_CORE_POOL_SIZE;
	private static String jobsFile;
	private static String jobsList;
	private static String jobsParam;

	private static final Log LOG = LogFactory.getLog(Main.class);

	private Main() {
	}

	public static void main(String[] args) {
		Main.CmdLineParser clp = new Main.CmdLineParser(args);
		clp.parse(args);
		MyThreadPoolExecutor mtpe = new MyThreadPoolExecutor(corePoolSize,
				corePoolSize, THREAD_KEEP_ALIVE_TIME);
		for (String cmd : JOBS_ARRAY_LIST) {
			mtpe.addTask(new ShellTaskWorker(cmd));
		}
		mtpe.shutdown();
	}

	private static class CmdLineParser {
		private String[] pParams;
		
		public CmdLineParser(String[] params){
			this.pParams = params;
		}
		
		private void parse(String[] params) {
			if (this.pParams == null || this.pParams.length == 0) {
				LOG.error("No argument found");
				Utils.printHelpAndExit();
			}
			this.parseCmdLine();
		}
		
		public void parseCmdLine() {
			String[] params = this.pParams;
			LOG.debug("Command line args : " + StringUtils.join(params, " "));
			int c;
			String arg;
			final LongOpt[] opts = {
					new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h'),
					new LongOpt("batchname", LongOpt.REQUIRED_ARGUMENT, null, 'n'),
					new LongOpt("corepoolsize", LongOpt.OPTIONAL_ARGUMENT, null,
							'c'),
					new LongOpt("jobslist", LongOpt.OPTIONAL_ARGUMENT, null, 'l'),
					new LongOpt("jobsfile", LongOpt.OPTIONAL_ARGUMENT, null, 'f'),
					new LongOpt("jobsparam", LongOpt.OPTIONAL_ARGUMENT, null, 'p'),
			};
			Getopt g = new Getopt(APP_NAME, params, "hn:c::l::f::p::", opts, false);
			g.setOpterr(true);
			while ((c = g.getopt()) != -1) {
				switch (c) {
				case 'h':
					Utils.printHelpAndExit();
				case 'n':
					arg = g.getOptarg();
					Batch.getInstance().setBatchName(arg);
					LOG.debug("Param [batchname]: "
							+ Batch.getInstance().getBatchName());
					break;
				case 'c':
					arg = g.getOptarg();
					try {
						corePoolSize = Integer.valueOf(arg);
					} catch (NumberFormatException e) {
						LOG.error("Numeric value expected", e);
						corePoolSize = DEFAULT_CORE_POOL_SIZE;
					}
					LOG.debug("Param [corepoolsize]: " + corePoolSize);
					break;
				case 'l':
					arg = g.getOptarg();
					jobsList = arg;
					LOG.debug("Param [jobslist]: " + jobsList);
					break;
				case 'f':
					arg = g.getOptarg();
					jobsFile = arg;
					LOG.debug("Param [jobsfile]: " + jobsFile);
					break;
				case 'p':
					arg = g.getOptarg();
					jobsParam = arg;
					LOG.debug("Param [jobsparam]: " + jobsParam);
					break;
				default:
					LOG.error("Unknown parameter : " + Character.toString((char) c));
					break;
				}
			}
			if (Batch.getInstance().getBatchName() == null) {
				LOG.error("Name of the batch are required. Set the \"n\" parameter");
				Utils.printHelpAndExit();
			}
			/** Add job in <b>JOBS_ARRAY_LIST<b> */
			prepareJobListToExecute();
		}

		private void prepareJobListToExecute() {
			if (jobsFile == null && jobsList == null) {
				LOG.error("No jobs specified.");
				Utils.printHelpAndExit();
			}
			if (jobsList != null) {
				String[] jl = StringUtils.split(jobsList, JOB_SEPARATOR);
				for (String s : jl) {
					addJob(s.trim());
				}
			}
			if (jobsFile != null) {
				try {
					BufferedReader br = new BufferedReader(new FileReader(jobsFile));
					String jobsFileLine;
					while ((jobsFileLine = br.readLine()) != null) {
						addJob(jobsFileLine.trim());
					}
					br.close();
				} catch (FileNotFoundException e) {
					LOG.error("jobsFile not exists : " + jobsFile, e);
				} catch (IOException e) {
					LOG.error("Problem whith the jobs file : " + jobsFile, e);
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
		public void addJob(String jobCommandLine) {
			String jcl = jobCommandLine;
			if (jcl.isEmpty()) {
				LOG.error("Cannot add empty job");
				return;
			}
			if (jobsParam != null && jobsParam.length() > 0) {
				jcl = jcl + " " + jobsParam;
			}
			if (JOBS_ARRAY_LIST.size() < MAX_JOBS) {
				if (jcl.length() < MAX_LINE_LENGTH) {
					JOBS_ARRAY_LIST.add(jcl);
				} else {
					LOG.error("Length of the jobs command line is too high : "
							+ jcl.length() + "!!!. Maximum is " + MAX_LINE_LENGTH);
				}
			} else {
				LOG.error("Maximum of jobs is " + MAX_JOBS);
				LOG.error("Reduce the number of jobs");
			}
		}
	}
}
