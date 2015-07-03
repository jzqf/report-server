#!/usr/bin/env bash
#
# Create and prepare an empty report server database in a PostgreSQL cluster,
# and create a PostgreSQL role to own this database and that will be used by the 
# report server applicatiom to connect to this database.
#
# Usage:
#   sudo -u postgres create-report_server_db-and-role.sh [-Dhlv]] 
#                                [-s server] [-p port] [-d database] [-U user]
#
#   This should be executed from a directory where the OS user "postgres" has 
#   the permission to write, because the script writes errors and other messages
#   to a log file named "errors" in the script's directory.
#
# Prerequisites:
#
#     PostgreSQL's pg_hba.conf file must be configured to allow the PostgreSQL
#     superuser "postgres" to connect to the cluster via peer authentication.
#     This is normally enabled by default.
#
#     In order to be able to created the 'uuid-ossp' extension here, the 
#     apropriate PostgreSQL support must be installed for it. For Debian or
#     Ubuntu Linux, this requires that the 'postgresql-contrib-9.x' package be
#     installed.
#
# Version history:
#
# Version:  1.0
# Updated:  2015.06.20
# Author:   Jeffrey Zelt
# Changes:  Initial version

#set -x

IFS='
        '  # explicitly set IFS for security reasons

OLDPATH="$PATH"    # in case I want to restore this path below
PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin
export PATH

LOGFILE=$(dirname $0)/logfile
PROGRAM=$(basename $0)
VERSION=1.0
USAGE="$PROGRAM [OPTIONS]"

# Defaults (to simplify running this script manually):
DBUSER=report_server_app  # name of role to create that will be used by report server application to connect to the report server database
#DBSERVER=localhost       # PostgreSQL server to log into
#DBPORT=5432              # TCP port on which the PostgreSQL server is listening
DBNAME=report_server_db   # database name that will be created and initialized

# This script should be run as OS user "postgres", but it may also be run as 
# other OS users if only the "-h" or "-l" or "-v" options are used. Setting the
# umask to 0000 means that if the log file is created by one user, it will still
# be writable by another user. If this umask "fiddling" is not done, the write
# permission for "other" users will not be set. 
CURRENTUMASK=$(umask)
umask 0000           # only needed when the log file is first created
printf "\n$(date)-INFO -$PROGRAM: Script started...\n" >> "$LOGFILE"
umask $CURRENTUMASK  # reset

error()
{
    printf "$(date)-ERROR-$PROGRAM: $@\n" | tee -a "$LOGFILE" 1>&2  # send msg to both stderr and log file
    usage_and_exit 1
}

warning()
{
    printf "$(date)-WARN -$PROGRAM: $@\n" | tee -a "$LOGFILE" 1>&2  # send msg to both stderr and log file
}

info()
{
    printf "$(date)-info -$PROGRAM: $@\n" | tee -a "$LOGFILE" 1>&2  # send msg to both stderr and log file
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
    Creates a report server database and owner role in a PostgreSQL cluster.

OPTIONS
    -D    Drop report server database, if it exists, before creating it.

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
          Specifies the name of the report server database that will be created.

    -U user
          Specifies the PostgreSQL role name that will own the report server
          database.

" "$PROGRAM" "$USAGE"
}

printlog()
{
if [ -e "$LOGFILE" ]; then
    printf "tail -20 $LOGFILE :\n%s\n" "$(tail -20 $LOGFILE)"
else
    printf "The log file $LOGFILE does not exist. This is normal if there have been no log entries written since the file was last deleted.\n"
fi
}

# Check that the directory containing the specified log file exists (the log 
# file itself may not exist because it may be created here). The characters
# %/* strip the shortest match of /* from the end of $LOGFILE, i.e., it removes
# "/" and the log file itself from $LOGFILE, leaving just the log file 
# directory.
if [ ! -d "${LOGFILE%/*}" ]; then
    error "The log file directory \"${LOGFILE%/*}\" does not exist"
fi

while getopts 'hlvDp:s:d:U:' OPTION
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
    D)  DROPDB=1
        ;;
    s)  DBSERVER=$OPTARG
        ;;
    p)  DBPORT=$OPTARG
        ;;
    d)  DBNAME=$OPTARG
        ;;
    U)  DBUSER=$OPTARG
        ;;
    *)  usage
        exit 2
        ;;
    esac
done
shift $(($OPTIND - 1))

#echo "DBSERVER = $DBSERVER"
#echo "DBPORT   = $DBPORT"
#echo "DBNAME   = $DBNAME"
#echo "DBUSER   = $DBUSER"

if [ $(whoami) != "postgres" ]; then
    error "This command must be run by the OS user 'postgres'"
fi


# If there is currently no role named '$DBUSER', it is created here:
result=$(psql -tAc "SELECT 1 FROM pg_roles WHERE rolname='$DBUSER'" -d postgres -U postgres 2>> "$LOGFILE") 
if [ $? -eq 0 ]
then
    if [ "$result" = "1" ]
    then
        info "Role '$DBUSER' already exists and will not be created here."
    else
        # The role does not exist, so we create it here.
        # This will connect to a local PostgreSQL server using a Unix domain socket
        # and peer authentication. If there is no local PostgreSQL server or there
        # is one but it is not the server which will be used for the report server 
        # database, then this will either fail or create the role on the wrong server.
        psql -tAc "CREATE ROLE $DBUSER PASSWORD 'sA5gfH7tng*125Z6f' LOGIN INHERIT" -d postgres -U postgres 2>> "$LOGFILE"
        if [ $? -eq 0 ]
        then
            info "Created role '$DBUSER'"
        else
            error "Could not create role '$DBUSER'"
        fi 
    fi
else
    error "Could not determine if role '$DBUSER' already exists. Can superuser 'postgres' connect to local cluster over Unix-domain socket with peer authentication?"
fi


# If the user asked that the database to be created be dropped first, the drop
# is done here.
if [ ! -z $DROPDB ]
then
    psql -tAc "DROP DATABASE IF EXISTS $DBNAME" -d postgres -U postgres 2>> "$LOGFILE"
    if [ $? -eq 0 ]
    then
        info "Dropped existing database '$DBNAME'"
    else
        error "Could not drop existing database '$DBNAME'"
    fi    
fi


# If there is currently no database named '$DBNAME', it is created here:
result=$(psql -tAc "SELECT 1 FROM pg_database WHERE datname='$DBNAME'" -d postgres -U postgres 2>> "$LOGFILE") 
if [ $? -eq 0 ]
then
    if [ "$result" = "1" ]
    then
        info "Database '$DBNAME' already exists and will not be created here."
    else
        # The database does not exist, so we create it here.
        # This will connect to a local PostgreSQL server using a Unix domain socket
        # and peer authentication. If there is no local PostgreSQL server or there
        # is one but it is not the server which will be used for the report server 
        # database, then this will either fail or create the databse on the wrong server.
        psql -tAc "CREATE DATABASE $DBNAME OWNER $DBUSER ENCODING 'UTF8'" -d postgres -U postgres 2>> "$LOGFILE"
        if [ $? -eq 0 ]
        then
            info "Created database '$DBNAME'"
        else
            error "Could not create database '$DBNAME'"
        fi 
    fi
else
    error "Could not determine if database '$DBNAME' already exists. Can superuser 'postgres' connect to local cluster over Unix-domain socket with peer authentication?"
fi


# All SQL commands above are executed while connected to the "postgres" database
# as role "postgres".
#
# All SQL commands below must be executed while connected to the $DBNAME database
# as role "postgres".


psql -tAc "CREATE SCHEMA IF NOT EXISTS reporting AUTHORIZATION $DBUSER" -d $DBNAME -U postgres 2>> "$LOGFILE"
if [ $? -eq 0 ]
then
    info "The 'reporting' schema was successfully created, or it already exists and it was not necessary to create it."
else
    error "Could not create schema 'reporting' in database '$DBNAME'"
fi 

psql -tAc "ALTER ROLE $DBUSER IN DATABASE $DBNAME SET search_path TO reporting, \"\$user\", public" -d $DBNAME -U postgres 2>> "$LOGFILE"
if [ $? -eq 0 ]
then
    info "The schema search path was successfully set for role $DBUSER in database '$DBNAME'."
else
    error "Could not set the schema search path for role $DBUSER in database '$DBNAME'"
fi 

# This requires Debian package postgresql-contrib-9.x to be installed first:
psql -tAc "CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\" WITH SCHEMA reporting" -d $DBNAME -U postgres 2>> "$LOGFILE"
if [ $? -eq 0 ]
then
    info "The 'uuid-ossp' extension was successfully created in the 'reporting' schema of database '$DBNAME', or it already exists and it was not necessary to create it."
else
    error "Could not create the 'uuid-ossp' extension in the 'reporting' schema of database '$DBNAME'."
fi 


#set +x
exit 0
