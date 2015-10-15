#!/usr/bin/env bash
#
# Upgrade a report server database to the most recent version.
#
# Usage:
#   ./upgrade_report_server_db.sh [-h] [-l] [-v] 
#                                [-s server] [-p port] [-d database] [-U user]
#
#   This should be executed from a directory where the OS user has 
#   the permission to write, because the script writes errors and other messages
#   to a log file named "logfile" in the script's directory.
#
#   Unless a different role (user) is specified with the -U option, this will 
#   attempt to connect to the report server database using the role 
#   "report_server_app", which is the role used by the report server application
#   itself to connect to the report server application. To simplify execution
#   of this script, the PostgreSQL password for this user can be placed in the
#   .pgpass file of the OS user's account from which this script is executed; 
#   otherwise, the user will be prompted for this password each time this script
#   is run.
#
# Prerequisites:
#
#     The report server database must already exist.
#
# Version history:
#
# Version:  1.0
# Updated:  2015.07.15
# Author:   Jeffrey Zelt
# Changes:  Initial version

#set -x

IFS=' 	
'  # explicitly set IFS for security reasons (space, tab, newline)

OLDPATH="$PATH"    # in case I want to restore this path below
PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin
export PATH

LOGFILE=$(dirname $0)/logfile
PROGRAM=$(basename $0)
VERSION=1.0
USAGE="$PROGRAM [OPTIONS]"

# Defaults (to simplify running this script manually):
DBUSER=report_server_app  # name of role with which to connect to the report server database
DBSERVER=localhost        # PostgreSQL server to log into
DBPORT=5432               # TCP port on which the PostgreSQL server is listening
DBNAME=report_server_db   # name of the report server database that will be upgraded

log_error()
{
	printf "$(date) ERROR $PROGRAM: $*\n" | tee -a "$LOGFILE" 1>&2  # send msg to both stderr and log file
	usage_and_exit 1
}

log_warning()
{
	printf "$(date) WARN  $PROGRAM: $*\n" | tee -a "$LOGFILE" 1>&2  # send msg to both stderr and log file
}

log_info()
{
	printf "$(date) INFO  $PROGRAM: $*\n" | tee -a "$LOGFILE" 1>&2  # send msg to both stderr and log file
}

usage_and_exit()
{
	usage
	exit $1
}

usage()
{
	printf "Usage: $USAGE  (Use option -h for help)\n"
}

version()
{
	printf "$PROGRAM $VERSION\nQ-Free ASA, Back Office Projects\nWritten by Jeffrey Zelt, jeffrey.zelt@q-free.com\n"
}

printhelp()
{
	printf "
NAME
    %s -- Create a report server database and owner role in a PostgreSQL cluster.

SYNOPSIS
    %s

DESCRIPTION
    Upgrades an existing report server database.

OPTIONS
    -h    Prints a help message and then quits.

    -l    Prints out last few lines of log file. The command to do this is
          also displayed in order to show the location of the log file.

    -v    Prints the version and then quits.

    -s server
          Specifies the hostname of the PostgreSQL server to connect to.

    -p port
          Specifies the TCP port number that the PostgreSQL server is listening
          on.

    -d database
          Specifies the name of the report server database that will be 
          upgraded.

    -U user
          Specifies the PostgreSQL role name to use for connecting to the report
          server database.

" "$PROGRAM" "$USAGE"
}

printlog()
{
if [ -e "$LOGFILE" ]; then
	printf "tail -20 $LOGFILE :\n%s\n" "$(tail -20 $LOGFILE)"
else
	log_error "The log file $LOGFILE does not exist. This is normal if there have been no log entries written since the file was last deleted."
fi
}

# This ensures that the log file created by this script is writable by everyone.
# This is done to allow this script to write to its log file even if it is run
# by more than one OS user (the first user that runs it will create the log 
# file). If this umask "fiddling" is not done, the write permission for "other"
# users will not be set. 
CURRENTUMASK=$(umask)
umask 0000           # only needed when the log file is first created
log_info "Script started..."
umask $CURRENTUMASK  # reset

# Check that the directory containing the specified log file exists (the log 
# file itself may not exist because it may be created here). The characters
# %/* strip the shortest match of /* from the end of $LOGFILE, i.e., it removes
# "/" and the log file itself from $LOGFILE, leaving just the log file 
# directory.
if [ ! -d "${LOGFILE%/*}" ]; then
	log_error "The log file directory \"${LOGFILE%/*}\" does not exist"
fi

# Specifying ":" at the start of the list of legal options here means that the
# shell variable $OPTARG will be set to the value of an illegal option 
# encountered and no error message will be sent to standard error. For an 
# illegal option, $OPTION will be set to a "?" character.
while getopts ':hlvp:s:d:U:' OPTION
do
	case $OPTION in
	h)  printhelp
		exit 0
		;;
	l)  printlog
		exit 0
		;;
	v)  version
		exit 0
		;;
	s)  DBSERVER=$OPTARG
		;;
	p)  DBPORT=$OPTARG
		;;
	d)  DBNAME=$OPTARG
		;;
	U)  DBUSER=$OPTARG
		;;
	*)  log_warning "Illegal option:" "$OPTARG"
		usage
		exit 2
		;;
	esac
done
shift $(($OPTIND - 1))

#echo "DBSERVER = $DBSERVER"
#echo "DBPORT   = $DBPORT"
#echo "DBNAME   = $DBNAME"
#echo "DBUSER   = $DBUSER"

CONFIG_PARAM_NAME="DB_VERSION"

# If there is currently no global (role_id=null) [configuration] record for 
# 'DB_VERSION', it is created here. This should only be necessary for the
# first upgrade for the MassDot project delivered to Open Roads Consulting, 
# because aree future installs will create a 'DB_VERSION' global configuration
# record that is set to the appropriate value. Therefore, it is OK to delete
# this block of code after we deliver the first upgrade to Open Roads for the
# MassDot project.
#
# It is important *not* to redirect the output of the psql command with
# ">> "$LOGFILE" 2>&1" here because command substitution catches stdout and,
# hence, $result will not be set here.
result=$(psql -tAc "SELECT 1 FROM reporting.configuration WHERE param_name='$CONFIG_PARAM_NAME' AND role_id IS NULL LIMIT 1" \
          -h $DBSERVER -p $DBPORT -d $DBNAME -U $DBUSER 2>> "$LOGFILE")
if [ $? -eq 0 ]; then
	if [ "$result" != "1" ]; then
		# A global configuration record does not exist for holding the current
		# report server database version, so one is inserted here with the 
		# value of 1.
		psql -tAc "INSERT INTO reporting.configuration (param_name, role_id, param_type, integer_value, string_value , created_on) \
		           VALUES ('$CONFIG_PARAM_NAME', null, 'INTEGER', 1, '1', '$(date --utc +%FT%T)')" \
		           -h $DBSERVER -p $DBPORT -d $DBNAME -U $DBUSER >> "$LOGFILE" 2>&1
		if [ $? -eq 0 ]; then
			log_info "Created global configuration record for current report server database version"
		else
			log_error "Could not create global configuration record for current report server database version"
		fi 
	fi
else
	log_error "Could not determine if a global configuration record already exists for the current database version."
fi


# Get the current version number of the reporting server database.
#
# It is important *not* to redirect the output of the psql command with
# ">> "$LOGFILE" 2>&1" here because command substitution catches stdout and,
# hence, $dbversion will not be set here.
dbversion=$(psql -tAc "SELECT integer_value FROM reporting.configuration WHERE param_name='$CONFIG_PARAM_NAME' AND role_id IS NULL LIMIT 1" \
            -h $DBSERVER -p $DBPORT -d $DBNAME -U $DBUSER 2>> "$LOGFILE")
if [ $? -ne 0 ]; then
	log_error "Could not get the current database version."
fi

#echo "dbversion = $dbversion"
if [ "$dbversion" -lt 1 ]; then
	log_error "dbversion = $dbversion. This should not be possible. Execution aborted."
fi

# This is the path to the directory where the SQL upgrade scripts must be found.
# If this path does not start with "/", i.e., it is a relative path, then the
# must be in the *same* same directory where this upgrade shell script is 
# located.
SQL_UPGRADE_DIR="db_version_upgrade_deltas"

if [ -d "$SQL_UPGRADE_DIR" ]; then
	# It is important *not* to redirect the output of the psql command with
	# ">> "$LOGFILE" 2>&1" here because command substitution catches stdout and, 
	# hence, $ls_output will not be set here.
	ls_output=$(ls -1 $SQL_UPGRADE_DIR/upgrade_report_server_db_to_v*.sql 2>> "$LOGFILE")
	if [ $? -eq 0 ]; then
		
		# Here "$ls_output" preserves whitespace exactly, i.e, newlines, tabs, 
		# multiple blanks, etc. Without the quotes, each sequence of one or more
		# blanks, tabs and newlines will be replaced with a single space, which 
		# is not what we want. We need to preserve newlines because we use "wc" 
		# to count the lines (number of scripts available).
		#
		# The logic here *assumes* that there is one upgrade script for each 
		# new version and the script names include version numbers starting from
		# 2 and that increase by 1 for each file, e.g.,
		#
		#	upgrade_report_server_db_to_v2.sql
		#	upgrade_report_server_db_to_v3.sql
		#	upgrade_report_server_db_to_v4.sql
		#
		# In this simple example with three upgrade scripts, $current_db_version
		# will be equal to 4.
		# 
		# It is necessary to test if "$ls_output" is an empty string here
		# because "echo"ing an empty string returns a newline, which "wc" will
		# interpret as one line. This will lead to an incorrect result because
		# $num_upgrade_scripts should be zero if $ls_output holds an empty 
		# string. In fact, if there are no SQL upgrade scripts, we will not even
		# each this point because the "ls" command above will return a non-zero
		# exit status.
		if [ -z "$ls_output" ]; then
			num_upgrade_scripts=0
		else		
			num_upgrade_scripts=$(echo "$ls_output" | wc -l)  # Double quotes are important. See comments.
		fi
		current_db_version=$(( $num_upgrade_scripts + 1 ))
		#echo "num_upgrade_scripts = $num_upgrade_scripts"
		#echo "current_db_version = $current_db_version"
				
		# Execute each SQL upgrade script to bring the DB version up to version
		# $current_db_version. But it is necessary to skip upgrades that are for
		# DB versions earlier than the current DB version, i.e., only execute 
		# upgrade scripts that will *increase* the database version number.
		for i in $(seq $(($dbversion+1)) $current_db_version);
		do
			if [ -r "$SQL_UPGRADE_DIR/upgrade_report_server_db_to_v$i.sql" ]; then
				log_info "Upgrading database to version $i..."
				psql -f $SQL_UPGRADE_DIR/upgrade_report_server_db_to_v$i.sql -h $DBSERVER -p $DBPORT -d $DBNAME -U $DBUSER >> "$LOGFILE" 2>&1
				if [ $? -eq 0 ]; then
					log_info "Successfully upgraded database to version $i"
				else
					log_error "Could not upgrade database to version $i"
				fi 
			else
				log_error "The SQLupgrade script '$SQL_UPGRADE_DIR/upgrade_report_server_db_to_v$i.sql' does not exist or is not readable."
			fi
		done
	else
		log_error "There are no SQL upgrade scripts available. The database was not upgraded."
	fi
else
	log_error "There must be a '$SQL_UPGRADE_DIR' directory in the directory where this script is located. The database was not upgraded."
fi

# If the DB upgrade modifies or introduces new reports, or report versions, it
# will be necessary to re-synchronize the file system with the reports stored in
# the database. The easiest way to do this is to restart the application running
# on Tomcat (or perhaps another application server).

#set +x
exit 0
