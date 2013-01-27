# SHELL-TASK-POOL

A Java tool to run in parallel, command lines with java ThreadPoolExecutor paradigm. [![Build Status](https://secure.travis-ci.org/fenicks/shell-task-pool.png?branch=master)](http://travis-ci.org/fenicks/shell-task-pool).

## Use cases

                        MAPPER                      REDUCER

    +------------------+
    |  List of jobs    |           +-------------+
    |__________________+---------->|Worker 1     xx
    |job1              |           +-------------|xxxx
    |job2              |           +-------------+    xxxxx
    |job3              |                                  xxxxxxx+-----------------+
    |job4              |           +-------------+               |                 |
    |job5              +---------->|Worker 2     xxxxxxxxxxxxxxxx|                 |
    |job6              |           |-------------|               |                 |
    |job7              |           +-------------+               | Finalizer       |
    |job8              |                                         |                 |
    |...               |           +-------------+               |                 |
    |                  +---------->|Worker 3     |               |                 |
    |                  |           |-------------xxxxxxxxxxxxxxxxx                 |
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

## Logs format
### Logs data description
The main element for the log data description is **batch\_stats**. This element store all batch information including jobs information.
The structure is described below :

* **batch\_stats**: Main element for the batch information [object (hash map)].
    * **batch\_id**: The id of the batch. Technically this is a UUID without `-` character [string].
    * **batch\_name**: The name of the batch [string].
    * **batch\_parameters**: Batch command line parameters [string].
    * **batch\_workers**: Number of workers to process the jobs [number].
    * **batch\_number\_of\_jobs**: Total number of jobs [number].
    * **batch\_start\_date**:Started date of the batch in milliseconds (Unix timestamp) [number (long)].
    * **batch\_end\_date**: Ended date of the batch in milliseconds (Unix timestamp) [number (long)].
    * **batch\_duration**: Batch duration in format HH:mm:ss.SS [string].
    * **batch\_status**: Bath status [string].
    * **batch\_jobs\_file**: File path of jobs [string].
    * **batch\_log\_dir**: Directory path to store all jobs logs [string].
    * **batch\_jobs\_info**: This element contains jobs information [array]
        * **job\_id**: The id of the job [number].
        * **job\_command\_line**: Job command line [string].
        * **job\_start\_date**: Started date of the job in milliseconds (Unix timestamp) [number (long)].
        * **job\_end\_date**: Ended date of the job in milliseconds (Unix timestamp) [number (long)].
        * **job\_duration**: Job duration in format HH:mm:ss.SS [string].
        * **job\_status**: Job status [string].
        * **job\_exit\_code**: Job exit code [number].

### Standard output logs description
The standard output log are separate in 3 part and are formated as *key:value* separated by pipe *|* :

1. Start batch information

        batch:start|id:bbab79e96aa64becb1587774cf28acf8|name:Retrieve best Java technical talks|parameters:-n YDL -jydl https://www.youtube.com/watch?v=svZRp0QoRCY; ydl https://www.youtube.com/watch?v=IECH5cqDLCE|workers:4|number_of_jobs:2|jobs_file:|log_dir:/home/christian/tmp/log|start_date:1354294165000|status:STARTED


2. Job information

        batch:job|id:bbab79e96aa64becb1587774cf28acf8|job_id:1|command_line:ydl https://www.youtube.com/watch?v=svZRp0QoRCY|start_date:1354294165000|end_date:1354294465000|duration:00:05:00.000|status:COMPLETED|exit_code:0
        batch:job|id:bbab79e96aa64becb1587774cf28acf8|job_id:2|command_line:ydl https://www.youtube.com/watch?v=IECH5cqDLCE|start_date:1354294165000|end_date:1354294665000|duration:00:08:20.000|status:COMPLETED|exit_code:0


3. End batch information


        batch:end|id:bbab79e96aa64becb1587774cf28acf8|name:Retrieve best Java technical talks|start_date:1354294165000|end_date:1354294665000|duration:00:08:20.000|status:COMPLETED


### JSON logs format

TODO(fenicks): Need the great format for http monitoring

A JSON log could be parsed easily, look at the example below:

    {
        "batch_stats": { // Batch statistics
            "batch_id": "bbab79e96aa64becb1587774cf28acf8",
            "batch_name": "Retrieve best Java technical talks",
            "batch_parameters": "-n YDL -jydl https://www.youtube.com/watch?v=svZRp0QoRCY; ydl https://www.youtube.com/watch?v=IECH5cqDLCE",
            "batch_workers": 4,
            "batch_number_of_jobs": 2,
            "batch_start_date": 1354294165000, // Unixtime in milliseconds
            "batch_end_date": 1354294665000,   // Unixtime in milliseconds
            "batch_duration": "00:08:20.000",  // Format : HH:mm:ss.SS (Java DateFormat duration)
            "batch_status": "COMPLETED",
            "batch_jobs_file": null,
            "batch_log_dir": "/home/christian/tmp/log",
            "batch_jobs_info": [
                {"job_id": 1, "job_command_line": "ydl https://www.youtube.com/watch?v=svZRp0QoRCY", "job_start_date": 1354294165000, "job_end_date": 1354294465000, "job_duration": "00:05:00.000", "job_status": "COMPLETED", "job_exit_code": 0},
                {"job_id": 2, "job_command_line": "ydl https://www.youtube.com/watch?v=IECH5cqDLCE", "job_start_date": 1354294165000, "job_end_date": 1354294665000, "job_duration": "00:08:20.000", "job_status": "COMPLETED", "job_exit_code": 0}
            ]
        }
    }


## Usage

    Usage: shelltaskpool.jar -n "Batch name" -j'/path/to/shell.sh > /var/log/shell.sh.log;/path/to/job.sh' [OPTIONS]
       or: shelltaskpool.jar -n "Batch name" -f/path/to/file.job [OPTIONS]
       or: shelltaskpool.jar -h

         [-h,--help]
       	   Show this help screen

         [-n,--batchname=]
       	   Set the name of the entire batch (always needed)
       	   example : -n "Alimentation différentiel des omes"

         [-j,--jobslist=]
       	   List of jobs seperated by ';' (could be omitted if "jobsfile"  contains jobs)
       	   example : -j'nslookup google.fr; /path/script2.sh > /tmp/script2.log'

         [-f,--jobsfile=]
       	   Path to the jobs plain text file. Jobs are separated by new line (could be omitted if "jobslist"  contains jobs)
       	   example : -f/home/me/test.job

         [-p,--jobsparam=]
       	   Set global params to add for all jobs
       	   example : -p'-x 2011/05/05 -m 1024'

         [-c,--corepoolsize=]
       	   Set number of thread processor
       	   example : -c5

         [-l,--jobslogdir=]
       	   Path to the jobs logs directory.
       	   example : -l/home/me/var/log
