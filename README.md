# SHELL-TASK-POOL
This is my Java tool to run command lines in parallel with Java ThreadPoolExecutor paradigm. [![Build Status](https://secure.travis-ci.org/fenicks/shell-task-pool.png?branch=master)](http://travis-ci.org/fenicks/shell-task-pool).

My inspiration led me to make this code, not really in Java standards, but with other programming language skills and my own experience.

This tool has allowed my company to save an hour in nightly batch processing and easily parse logs reporting.
I admit that the first purpose was to retrieve many videos of programming languages, design pattern etc...

## Examples

        java -jar shelltaskpool-1.2.0.jar -f./jobs_list.job [OPTIONS]
        java -jar shelltaskpool-1.2.0.jar -j'/path/to/shell.sh > /var/log/shell.sh.log;/path/to/job.sh' -f./jobs_list.job [OPTIONS]
        java -jar shelltaskpool-1.2.0.jar -n"Batch name" -f./jobs_list.job [OPTIONS]

## Use Cases

                        MAPPER                      REDUCER

    +------------------+
    |  List of jobs    |           +-------------+
    |__________________+---------->|Worker 1     xx
    |job1              |           +-------------|xxxx
    |job2              |           +-------------+    xxxxx
    |job3              |                                  xxxxxxx+-----------------+
    |job4              |           +-------------+               |     Status      |
    |job5              +---------->|Worker 2     xxxxxxxxxxxxxxxx+-----------------+
    |job6              |           |-------------|               | job5:running    |
    |job7              |           +-------------+               | job2:completed  |
    |job8              |                                         | job3:none       |
    |...               |           +-------------+               | job4:completed  |
    |                  +---------->|Worker 3     |               | job1:running    |
    |                  |           |-------------xxxxxxxxxxxxxxxxx jobx:...        |
    |                  |           +-------------+              x+-----------------+
    |                  |                                     xxxx
    |                  |           +-------------+       xxxxx
    |                  +---------->|Worker 4     |   xxxxx
    |                  |           |-------------xxxxx
    |                  |           +-------------+
    |                  |
    +------------------+

## Limitations
* Maximum job number is **5120** (it's hardcoded, give me some reasons to parameterized this limit).
* Command line length limit is **2048** characters (it's hardcoded, give me some reasons to parameterized this limit).

## Logs Format
### Standard output logs
The standard output log are separate in 3 part and are formatted as *key:value* separated by pipe *|* :

1. Start batch information

        batch:start|id:bbab79e96aa64becb1587774cf28acf8|name:Retrieve best Java technical talks|parameters:-n YDL -jydl https://www.youtube.com/watch?v=svZRp0QoRCY; ydl https://www.youtube.com/watch?v=IECH5cqDLCE|workers:4|number_of_jobs:2|jobs_file:|log_dir:/home/christian/tmp/log|start_date:1354294165000|status:STARTED

2. Job information

        batch:job|id:bbab79e96aa64becb1587774cf28acf8|job_id:1|job_command_line:ydl https://www.youtube.com/watch?v=svZRp0QoRCY|job_start_date:1354294165000|job_end_date:1354294465000|job_duration:00:05:00.000|job_status:COMPLETED|job_exit_code:0
        batch:job|id:bbab79e96aa64becb1587774cf28acf8|job_id:2|job_command_line:ydl https://www.youtube.com/watch?v=IECH5cqDLCE|job_start_date:1354294165000|job_end_date:1354294665000|job_duration:00:08:20.000|job_status:COMPLETED|job_exit_code:0

3. End batch information

        batch:end|id:bbab79e96aa64becb1587774cf28acf8|name:Retrieve best Java technical talks|start_date:1354294165000|end_date:1354294665000|duration:00:08:20.000|status:COMPLETED

### Standard output logs data description

* **batch:start**: The batch start information
    * **id**: The id of the batch. Technically this is a UUID without `-` character [string]
    * **name**: The name of the batch [string]
    * **parameters**: Batch command line parameters [string]
    * **workers**: Number of workers to process the jobs [number]
    * **number\_of\_jobs**: Total number of jobs [number]
    * **jobs\_file**: File path of jobs [string]
    * **log\_dir**: Directory path to store all jobs logs [string]
    * **start\_date**: Started date of the batch in milliseconds (Unix timestamp) [number (long)]
    * **status**: Bath status [string]
* **batch:job**: Jobs information
    * **id**: The id of the batch. Technically this is a UUID without `-` character [string]
    * **job\_id**: The id of the job [number]
    * **job\_command\_line**: Job command line [string]
    * **job\_start\_date**: Started date of the job in milliseconds (Unix timestamp) [number (long)]
    * **job\_end\_date**: Ended date of the job in milliseconds (Unix timestamp) [number (long)]
    * **job\_duration**: Job duration in format HH:mm:ss.SS (Java DateFormat duration) [string]
    * **job\_status**: Job status [string]
    * **job\_exit\_code**: Job exit code [number]
* **batch:end**: The batch end information
    * **id**: The id of the batch. Technically this is a UUID without `-` character [string]
    * **name**: The name of the batch [string]
    * **start\_date**: Started date of the batch in milliseconds (Unix timestamp) [number (long)]
    * **end\_date**: Ended date of the job in milliseconds (Unix timestamp) [number (long)]
    * **duration**: Batch duration in format HH:mm:ss.SS (Java DateFormat duration) [string]
    * **status**: Bath status [string]
* **batch:log**: Specific application log output such as: `error`, `warning`, `debug`, `info`.

## Usage

    Usage: shelltaskpool.jar -j'/path/to/shell.sh > /var/log/shell.sh.log;/path/to/another_job.sh' [OPTIONS]
       or: shelltaskpool.jar -n'Batch name' -f/path/to/file.job [OPTIONS]
       or: shelltaskpool.jar -h

         [-h,--help]
       	   Show this help screen

         [-n,--batchname=]
       	   Set the name of the entire batch (default set to : NO BATCH NAME)
       	   example : -n'All Youtube Mongoid video podcast retrieval'

         [-j,--jobslist=]
       	   List of jobs seperated by ';' (can be omitted if "jobsfile"  contains jobs)
       	   example : -j'nslookup google.fr; /path/script2.sh > /tmp/script2.log'

         [-f,--jobsfile=]
       	   Path to the jobs plain text file. Jobs are separated by new line (can be omitted if "jobslist"  contains jobs)
       	   example : -f/home/me/test.job

         [-p,--jobsparam=]
       	   Set global params to add for all jobs
       	   example : -p'-x 2011/05/05 -m 1024'

         [-c,--corepoolsize=]
       	   Set number of cores (workers)
       	   example : -c5

         [-l,--jobslogdir=]
       	   Path to the jobs logs directory.
       	   example : -l/home/me/var/log
