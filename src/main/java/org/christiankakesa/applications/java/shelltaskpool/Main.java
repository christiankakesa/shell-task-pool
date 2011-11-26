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

/**
 * Created by IntelliJ IDEA.
 * User: christian
 * Date: 30/04/11
 * Time: 00:25
 * .
 */
public class Main {
    public static final String APP_NAME = "shelltaskpool.jar";
    public static final String AUTHOR_NAME = "Christian Kakesa";
    public static final String AUTHOR_EMAIL = "christian.kakesa@gmail.com";
    public static final String APP_COPYRIGHT = "Christian Kakesa (c) " + Calendar.getInstance().get(Calendar.YEAR);
    public static final int DEFAULT_CORE_POOL_SIZE = 2;
    public static final long THREAD_KEEP_ALIVE_TIME = 30L;
    public static final int MAX_JOBS = 1024;
    public static final int MAX_LINE_LENGTH = 1024;
    public static final String JOB_SEPARATOR = "|";

    private static final Log LOG = LogFactory.getLog(Main.class);
    private static final int PROGRAM_ERROR = -42;

    private static final ArrayList<String> allJobs = new ArrayList<String>(MAX_JOBS);
    private static int corePoolSize = DEFAULT_CORE_POOL_SIZE;
    private static String jobsFile;
    private static String jobsList;
    private static String jobsParam;

    private Main() {
    }

    public static void main(String[] args) {
        shellTaskPool(args);
    }

    public static void shellTaskPool(String[] args) {
        int paramReturn = paramParser(args);
        if (paramReturn != 0)
            System.exit(paramReturn);

        MyThreadPoolExecutor mtpe = new MyThreadPoolExecutor(corePoolSize, corePoolSize, THREAD_KEEP_ALIVE_TIME);
        for (String cmd : allJobs) {
            mtpe.addTask(new ShellTaskWorker(cmd));
        }
        mtpe.shutdown();
    }

    public static int paramParser(String[] args) {
        if (args.length == 0) {
            LOG.error("No argument found");
            Utils.printHelp();
            return PROGRAM_ERROR;
        }
        LOG.debug("Command line args : " + StringUtils.join(args, " "));
        int c;
        String arg;
        final LongOpt[] opts = {
                new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h'),
                new LongOpt("jobname", LongOpt.REQUIRED_ARGUMENT, null, 'n'),
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
                        Statistics.getInstance().setJobName(arg);
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
                        LOG.debug("CorePoolSize set to : " + corePoolSize);
                    } else {
                        LOG.error("No core pool size specified");
                        return PROGRAM_ERROR;
                    }
                    break;
                case 'l':
                    arg = g.getOptarg();
                    if (arg != null) {
                        jobsList = arg;
                        LOG.debug("JobsList set to : " + jobsList);
                    } else {
                        LOG.error("No jobs list specified");
                        //jobsList = null;
                        return PROGRAM_ERROR;
                    }
                    break;
                case 'f':
                    arg = g.getOptarg();
                    if (arg != null) {
                        jobsFile = arg;
                        LOG.debug("JobsFile set to : " + jobsFile);
                    } else {
                        LOG.error("No jobs file specified");
                        //jobsFile = null;
                        return PROGRAM_ERROR;
                    }
                    break;
                case 'p':
                    arg = g.getOptarg();
                    if (arg != null) {
                        jobsParam = arg;
                        LOG.debug("JobsParam set to : " + jobsParam);
                    } else {
                        LOG.error("No jobs param specified");
                        //jobsParam = null;
                        return PROGRAM_ERROR;
                    }
                    break;
                default:
                    LOG.error("Unknown parameter : " + Character.toString((char) c));
                    return PROGRAM_ERROR;
            }
        }
        if (jobsFile == null && jobsList == null) {
            LOG.error("No jobs specified. Try \"-h\" or \"--help\" parameter to print help screen");
            return PROGRAM_ERROR;
        }
        // Build jobsList
        if (jobsList != null) {
            String[] l = StringUtils.split(jobsList, JOB_SEPARATOR);
            for (String s : l) {
                if (addJob(s) != 0)
                    return PROGRAM_ERROR;
            }
        }
        if (jobsFile != null) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(jobsFile));
                String jobsFileLine;
                while ((jobsFileLine = br.readLine()) != null) {
                    if (addJob(jobsFileLine) != 0) {
                        br.close();
                        return PROGRAM_ERROR;
                    }
                }
                br.close();
            } catch (FileNotFoundException e) {
                LOG.error("jobsFile not exists : " + jobsFile);
                LOG.error(e);
                return PROGRAM_ERROR;
            } catch (IOException e) {
                LOG.error("Problem when reading the file : " + jobsFile);
                LOG.error(e);
                return PROGRAM_ERROR;
            }
        }
        return 0;
    }

    public static int addJob(String job) {
        if (job == null || job.isEmpty()) {
            LOG.error("Cannot add job, ");
            return PROGRAM_ERROR;
        }
        if (jobsParam != null && jobsParam.length() > 0) {
            job = job + " " + jobsParam;
        }
        if (allJobs.size() < MAX_JOBS) {
            if (job.length() < MAX_LINE_LENGTH) {
                allJobs.add(job);
                LOG.debug("Real command added : " + job);
            } else {
                LOG.error("Length of command line jobs is " + MAX_LINE_LENGTH);
                LOG.error("Command line : " + job);
                return PROGRAM_ERROR;
            }
        } else {
            LOG.error("Max jobs is " + MAX_JOBS);
            LOG.error("Reduce the number jobs");
            return PROGRAM_ERROR;
        }
        return 0;
    }
}
