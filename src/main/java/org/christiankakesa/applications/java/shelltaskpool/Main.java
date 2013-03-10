package org.christiankakesa.applications.java.shelltaskpool;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;

/**
 * <b>Shell Task Pool</b>.
 * <p>
 * This is the main class of the program. This project aims to parallelized shell
 * command execution efficiently and safely. All the task are logged and could
 * be parsed by a reporting tool.
 * </p>
 *
 * @author Christian Kakesa (christian.kakesa@gmail.com)
 */
public final class Main {

    /**
     * Additional jobs added from command line.
     */
    private static String jobsList;

    /**
     * Additional params added to each jobs from command line.
     */
    private static String jobsParam;

    /**
     * Logger for Main class.
     */
    private static final Logger LOG = Logger.getLogger(Main.class);

    /**
     * Private constructor.
     */
    private Main() {
    }

    /**
     * Main method of the application.
     *
     * @param args Program arguments.
     */
    public static void main(String[] args) {
        Batch.getInstance().setParameters(args);
        Main.CmdLineParser clp = new Main.CmdLineParser(args);
        clp.parse();
        Main.prepareJobListToExecute();// Populate JOBS_STORE
        if (Batch.JOBS_STORE.isEmpty()) {
            LOG.error("No jobs found.");
            Util.printHelpAndExit();
        }
        MyThreadPoolExecutor mtpe = new MyThreadPoolExecutor(Batch.getInstance().getNumberOfWorkers(),
                Batch.getInstance().getNumberOfWorkers());
        for (String cmd : Batch.JOBS_STORE) {
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
                    new LongOpt("batchname", LongOpt.OPTIONAL_ARGUMENT, null, 'n'),
                    new LongOpt("jobslist", LongOpt.OPTIONAL_ARGUMENT, null, 'j'),
                    new LongOpt("jobsfile", LongOpt.OPTIONAL_ARGUMENT, null, 'f'),
                    new LongOpt("jobsparam", LongOpt.OPTIONAL_ARGUMENT, null, 'p'),
                    new LongOpt("corepoolsize", LongOpt.OPTIONAL_ARGUMENT, null, 'c'),
                    new LongOpt("jobslogdir", LongOpt.OPTIONAL_ARGUMENT, null, 'l')};
            Getopt g = new Getopt(AppInfo.APP_NAME, params, "hn::j::f::p::c::l::", opts, false);
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
                        break;
                    case 'j':
                        arg = g.getOptarg();
                        Main.jobsList = arg;
                        LOG.debug("Param [jobslist]: " + Main.jobsList);
                        break;
                    case 'f':
                        arg = g.getOptarg();
                        Batch.getInstance().setJobsFile(arg);
                        LOG.debug("Param [jobsfile]: " + Batch.getInstance().getJobsFile());
                        break;
                    case 'p':
                        arg = g.getOptarg();
                        Main.jobsParam = arg;
                        LOG.debug("Param [jobsparam]: " + Main.jobsParam);
                        break;
                    case 'c':
                        arg = g.getOptarg();
                        try {
                            Batch.getInstance().setNumberOfWorkers(Integer.valueOf(arg));
                        } catch (NumberFormatException e) {
                            LOG.warn("Wrong corePoolSize set: " + arg);
                            Batch.getInstance().setNumberOfWorkers(Util.defaultCorePoolSize());
                            LOG.warn("Detected free cpu core set: corePoolSize=" + Batch.getInstance().getNumberOfWorkers());
                        }
                        LOG.debug("Param [corepoolsize]: " + Batch.getInstance().getNumberOfWorkers());
                        break;
                    case 'l':
                        arg = g.getOptarg();
                        Batch.getInstance().setLogDirectory(arg);
                        File d = null;
                        try {
                            d = new File(Batch.getInstance().getLogDirectory());
                        } catch (NullPointerException e) {
                            LOG.error("Le répertoire des logs n'est pas valide", e);
                            Util.printHelpAndExit();
                        }
                        if (null != d && !d.isDirectory()) {
                            LOG.error(Batch.getInstance().getLogDirectory() + " is not a directory.");
                            Util.printHelpAndExit();
                        }
                        LOG.debug("Param [jobslogdir]: " + Batch.getInstance().getLogDirectory());
                        break;
                    default:
                        LOG.error("Unknown parameter : " + Character.toString((char) opt));
                        break;
                }
            }
        }
    }

    private static void prepareJobListToExecute() {
        if (Main.jobsList != null) {
            String[] jl = StringUtils.split(Main.jobsList, Batch.JOB_SEPARATOR);
            for (String s : jl) {
                Main.addJob(s.trim());
            }
        }
        if (Batch.getInstance().getJobsFile() != null) {
            try {
                FileReader fr = new FileReader(Batch.getInstance().getJobsFile());
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
                    LOG.error("Problem with the jobs file : " + Batch.getInstance().getJobsFile(),
                            e);
                } finally {
                    try {
                        br.close();
                    } catch (IOException e) {
                        LOG.warn("Can't close jobsFile reader stream : "
                                + e.getLocalizedMessage());
                    }
                }
            } catch (FileNotFoundException fne) {
                LOG.error("jobsFile not exists : " + Batch.getInstance().getJobsFile(), fne);
                Util.printHelpAndExit();
            }
        }
    }

    /**
     * Add job in the list of the jobs <b>Main.allJobs</b>. If jobsParam is set,
     * jobsParam is added to the jobCommandLine.
     *
     * @param jobCommandLine The job command line.
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
        if (Batch.JOBS_STORE.size() < Batch.MAX_JOBS) {
            if (jcl.length() < Batch.MAX_LINE_LENGTH) {
                Batch.JOBS_STORE.add(jcl);
            } else {
                LOG.warn("Length of the jobs command line is too long: \n"
                        + "           Command line: " + jcl + "\n"
                        + "    Command line length: " + jcl.length() + "\n"
                        + "             Maximum is: " + Batch.MAX_LINE_LENGTH
                        + " !!!");
            }
        } else {
            LOG.error("Maximum of jobs is " + Batch.MAX_JOBS);
            LOG.error("Reduce the number of jobs");
            Util.printHelpAndExit();
        }
    }
}
