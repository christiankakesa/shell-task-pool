package org.christiankakesa.applications.java.shelltaskpool;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Main class of <b>Shell Task Pool</b>
 */
public final class Main {
    public static final String APP_NAME = "shelltaskpool.jar";
    public static final String AUTHOR_NAME = "Christian Kakesa";
    public static final String AUTHOR_EMAIL = "christian.kakesa@gmail.com";
    public static final String APP_COPYRIGHT = "Christian Kakesa (c) " + Calendar.getInstance().get(Calendar.YEAR);
    public static final int DEFAULT_CORE_POOL_SIZE = 2;
    public static final long THREAD_KEEP_ALIVE_TIME = 30L;
    public static final int MAX_JOBS = 5120;
    public static final int MAX_LINE_LENGTH = 2048;
    public static final char JOB_SEPARATOR = ';';
    public static final int DEFAULT_ERROR_CODE = -42;

    private static final List<String> JOBS_ARRAY_LIST = new ArrayList<String>(MAX_JOBS);
    
    private static int corePoolSize = DEFAULT_CORE_POOL_SIZE;
    private static String jobsFile;
    private static String jobsList;
    private static String jobsParam;

    private static final Log LOG = LogFactory.getLog(Main.class);
    
    private Main() {
    }

    public static void main(String[] args) {
        shellTaskPool(args);
    }

    public static void shellTaskPool(String[] args) {
        int paramReturn = paramParser(args);
        if (paramReturn != 0) {
            System.exit(paramReturn);
        }
        MyThreadPoolExecutor mtpe = new MyThreadPoolExecutor(corePoolSize, corePoolSize, THREAD_KEEP_ALIVE_TIME);
        for (String cmd : JOBS_ARRAY_LIST) {
            mtpe.addTask(new ShellTaskWorker(cmd));
        }
        mtpe.shutdown();
    }

    public static int paramParser(String[] args) {
        if (args.length == 0) {
            LOG.error("No argument found");
            Utils.printHelp();
            return DEFAULT_ERROR_CODE;
        }
        LOG.debug("Command line args : " + StringUtils.join(args, " "));
        int c;
        String arg;
        final LongOpt[] opts = {
                new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h'),
                new LongOpt("batchname", LongOpt.REQUIRED_ARGUMENT, null, 'n'),
                new LongOpt("corepoolsize", LongOpt.OPTIONAL_ARGUMENT, null, 'c'),
                new LongOpt("jobslist", LongOpt.OPTIONAL_ARGUMENT, null, 'l'),
                new LongOpt("jobsfile", LongOpt.OPTIONAL_ARGUMENT, null, 'f'),
                new LongOpt("jobsparam", LongOpt.OPTIONAL_ARGUMENT, null, 'p'),

        };
        Getopt g = new Getopt(APP_NAME, args, "hn:c::l::f::p::", opts, false);
        //g.setOpterr(false);
        while ((c = g.getopt()) != -1) {
            switch (c) {
                case 'h':
                    Utils.printHelp();
                    System.exit(0);
                case 'n':
                    arg = g.getOptarg();
                    if (arg != null) {
                        Batch.getInstance().setBatchName(arg);
                        LOG.debug("Param [batchname]: " + Batch.getInstance().getBatchName());
                    } else {
                    	LOG.error("Batch name are required");
                    	return DEFAULT_ERROR_CODE;
                    }
                    break;
                case 'c':
                    arg = g.getOptarg();
                    if (arg != null) {
                        try {
                            corePoolSize = Integer.valueOf(arg);
                        } catch (NumberFormatException e) {
                            LOG.error(e.getMessage());
                            LOG.error(e.getStackTrace().toString());
                            corePoolSize = DEFAULT_CORE_POOL_SIZE;
                        }
                        LOG.debug("Param [corepoolsize]: " + corePoolSize);
                    } else {
                        LOG.error("No core pool size specified");
                        return DEFAULT_ERROR_CODE;
                    }
                    break;
                case 'l':
                    arg = g.getOptarg();
                    if (arg != null) {
                        jobsList = arg;
                        LOG.debug("Param [jobslist]: " + jobsList);
                    } else {
                        LOG.error("No jobs list specified");
                        //jobsList = null;
                        return DEFAULT_ERROR_CODE;
                    }
                    break;
                case 'f':
                    arg = g.getOptarg();
                    if (arg != null) {
                        jobsFile = arg;
                        LOG.debug("Param [jobsfile]: " + jobsFile);
                    } else {
                        LOG.error("No jobs file specified");
                        //jobsFile = null;
                        return DEFAULT_ERROR_CODE;
                    }
                    break;
                case 'p':
                    arg = g.getOptarg();
                    if (arg != null) {
                        jobsParam = arg;
                        LOG.debug("Param [jobsparam]: " + jobsParam);
                    } else {
                        LOG.error("No jobs param specified");
                        //jobsParam = null;
                        return DEFAULT_ERROR_CODE;
                    }
                    break;
                default:
                    LOG.error("Unknown parameter : " + Character.toString((char) c));
                    return DEFAULT_ERROR_CODE;
            }
        }
        if (Batch.getInstance().getBatchName() == null) {
        	LOG.error("Name of the batch are required. Set the \"n\" parameter");
        	return DEFAULT_ERROR_CODE;
        }
        return haveJobToRun() ? 0 :DEFAULT_ERROR_CODE;
    }
    
    private static boolean haveJobToRun() {
    	if (jobsFile == null && jobsList == null) {
            LOG.error("No jobs specified. Try \"-h\" or \"--help\" parameter to print help screen");
            return false;
        }
        // Build jobsList
        if (jobsList != null) {
            String[] l = StringUtils.split(jobsList, JOB_SEPARATOR);
            for (String s : l) {
                if (!addJob(s.trim())) {
                    return false;
                }
            }
        }
        if (jobsFile != null) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(jobsFile));
                String jobsFileLine;
                while ((jobsFileLine = br.readLine()) != null) {
                    if (!addJob(jobsFileLine)) {
                        br.close();
                        return false;
                    }
                }
                br.close();
            } catch (FileNotFoundException e) {
                LOG.error("jobsFile not exists : " + jobsFile, e);
                return false;
            } catch (IOException e) {
                LOG.error("Problem when reading the file : " + jobsFile, e);
                return false;
            }
        }
        return true;
    }

    /**
     * Add job in the list of the jobs <b>Main.allJobs</b>.
     * If jobsParam is set, jobsParam is added to the jobCommandLine.
     * @param jobCommandLine
     * @return true if job is correctly added
     */
    public static boolean addJob(String jobCommandLine) {
        boolean res = false;
        String jcl = jobCommandLine;
    	if (jcl == null || jcl.isEmpty()) {
            LOG.error("Cannot add null or empty job");
            return res;
        }
        LOG.debug("Try to add this command line to the job array : " + jcl);
        if (jobsParam != null && jobsParam.length() > 0) {
        	jcl = jcl + " " + jobsParam;
        }
        if (JOBS_ARRAY_LIST.size() < MAX_JOBS) {
            if (jcl.length() < MAX_LINE_LENGTH) {
                res = JOBS_ARRAY_LIST.add(jcl);
            } else {
                LOG.error("Length of the jobs command line is too high : " + jcl.length() + "!!!. Maximum is " + MAX_LINE_LENGTH);
            }
        } else {
            LOG.error("Maximum of jobs is " + MAX_JOBS);
            LOG.error("Reduce the number of jobs");
        }
        return res;
    }
}
