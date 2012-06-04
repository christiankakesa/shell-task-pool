# shell-task-pool

A Java tool to parallelize command lines with java ThreadPoolExecutor class. [![Build Status](https://secure.travis-ci.org/fenicks/shell-task-pool.png?branch=master)](http://travis-ci.org/fenicks/shell-task-pool).

## Usage

	Usage: shelltaskpool.jar -n "Batch name" -j'/path/to/shell.sh > /var/log/shell.sh.log;/path/to/job.sh' [OPTIONS]
	   or: shelltaskpool.jar -n "Batch name" -f/path/to/file.job [OPTIONS]
	   or: shelltaskpool.jar -h
	   [-h,--help]
	   	Show this help screen
	
	   [-n,--batchname=]
	   	Set the name of the entire batch
	   	example : -n "Alimentation différentiel des omes"
	
	   [-c,--corepoolsize=]
	   	Set number of thread processor
	   	example : -c5
	
	   [-j,--jobslist=]
	   	List of jobs seperated by ';'
	   	example : -j'nslookup google.fr; /path/script2.sh > /tmp/script2.log'
	
	   [-f,--jobsfile=]
	   	Path to the jobs plain text file. Jobs are separated by new line
	   	example : -f/home/me/test.job
	
	    [-p,--jobsparam=]
	   	Set global params to add for each job
	   	example : -p'-x 2011/05/05 -m 1024'
