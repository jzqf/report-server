===== Extracting the files from the installation package archive =====

The script that is packaged in this archive is written to be executed by the 
"postgres" operating system user. The reason for this approach is that this 
"postgres" user automatically has super-user access to the PostgreSQL cluster. 
This simplifies creating and initializing the report server database since no 
special super-user needs to first be created in order to bootstrap the process.
A operating system account for the "postgres" user is created automatically when
PostgreSQL is installed, and this user can log into the local PostgreSQL cluster
using the PostgreSQL role of the same name, "postgres".

In order for the installation script to be executed successfully by the 
"postgres" user, the files in this archive must be extracted to a location where
the "postgres" user has the appropriate permissions to access these files. This
can be done easily by following these simple steps:

1.	Move the installation archive:

		install_report_server-***.tar.gz
	
	to a directory that the "postgres" user has access to. The simplest choice 
	is:
	
		/tmp
	
	Do not use your own user account because it is unlikely that the "postgres"
	user will have the necessary permissions to access files there.

2.	Extract the files from this archive using:

		tar -xvzpf install_report_server-***.tar.gz

The "-p" option is essential. If you do not follow these instructions, the 
installation may fail. You may also see lines similar to the following in the
 log file:	
	
		could not change directory to "...": Permission denied


===== Installation instructions =====

Before the report server can be installed, both a Tomcat 8.x server and a 
PostgreSQL 9.x server must be installed in a Debian or Ubuntu GNU/Linux 
operating system. This document assumes that standard installation of both of
these servers have been performed. However, the customization details (beyond a
standard installation) that are necessary to support the report server are 
covered here. In particular:

	A.	Customizing the PostgreSQL 9.x environment
	B.	Customizing the Tomcat 8.x environment

After the Tomcat and PostgreSQL environments are installed and configured, there
are two components to be installed for a new report server deployment:

	I.	The report server database
	II.	The report server application

This document covers the installation of both of these components below. 


A.	===== Customizing the PostgreSQL 9.x environment =====

The OBO Report Server database has been developed and tested to run on a 
PostgreSQL 9.x server. It is convenient to install this database server on the 
same host as the Tomcat 8.x application server that hosts the OBO Report Server 
web application, although this is not an installation requirement. However, if 
Tomcat and the database are installed on different hosts, extra configuration 
will be necessary which is not yet documented.

The only extra configuration that must be made to a standard PostgreSQL 
installation is to install the "contrib" package for the particular version of 
PostgreSQL that is installed. For for a Debian or Ubuntu Linux GNU/Linux 
operating system, this can be done with:

	$ sudo apt-get install postgresql-contrib-9.x

where "x" corresponds to the particular version of PostgreSQL that is installed.

This package is needed for the uuid_generate_v4() function that it provides that
generates a version 4 UUID. This is needed because most primary keys of the 
database tables in the report server database are of data type uuid.


B.	===== Customizing the Tomcat 8.x environment =====

1.	Configure Tomcat user for "manager-gui" role:

	Configure a Tomcat username/password associated with the "manager-gui" role. 
	This will provide access to Tomcat's HTML interface for uploading WAR files. 
	See https://tomcat.apache.org/tomcat-8.0-doc/manager-howto.html for details.

2.	Increase maximum file upload size:

	Configure the maximum file upload size for the Tomcat manager application to
	be at least 100 MB. If this is not done, it might not be possible to upload 
	the report server WAR file. This setting can be changed by editing the 
	max-file-size and max-request-size XML elements in the file:

		<CATALINA_HOME>/webapps/manager/WEB-INF/web.xml .
		
3.	Install PostgreSQL JDBC driver:

	In order to use container-managed database connections, it is necessary to 
	install the latest JDBC driver for PostgreSQL. The latest JDBC v4.1 driver 
	for PostgreSQL v9.4 can be downloaded from:

		https://jdbc.postgresql.org/download.html .
		
	This driver should be copied into the directory:

		<CATALINA_HOME>/lib .


I.	===== Installing the report server database =====

It is assumed here that the user performing this installation has a basic 
knowledge of how to run shell scripts. For example, the path to the script must 
be specified when running the script if the directory containing the script is 
not listed in the PATH environment variable, the script must have its 
appropriate "execute" access permission set, ...

After the PostgreSQL environment is in place, perform the following tasks in the
order they appear here:

1.	Close all connections to the report server database. 

	If this is the first time that the create-report_server_db.sh script is run
	for the PostgreSQL cluster, then the report server database will not yet 
	exist and there is nothing to do here. 
	
	However, this script can be run to also drop an existing report server 
	database and then recreate a fresh, empty one (using the -D option). Under 
	this condition, there could be existing connections to the database. If
	the report server application is running, it must be shut down because it
	maintains an open connection to the database.

2.	Configure psql so that it does not prompt for a password when connecting to 
	the report server database. This step is optional and only for convenience:
	
	Add an entry for "report_server_app" in the .pgpass file in the home 
	directory on the database host where connections to the report server 
	database will be made, e.g.,

		localhost:5432:*:report_server_app:sA5gfH7tng*125Z6f

3.	Configure PostgreSQL security to allow role "report_server_app" to log in 
	and connect to the "report_server_db" database:
	
	Edit PostgreSQL's pg_hba.conf file and make the following changes:

	a.	To enable local connections, change the existing line:
			local all all peer
		to:
			local all all md5
			
	b.	If you also want to connect to the database remotely via TCP/IP, there 
		are two additional changes to make:

		i.	Add a line to pg_hba.conf. For example, if the machine you want to 
			connect from is on the 192.168.xxx.yyy subnet, you can use:

				host all all 192.168.0.1/16 md5

		ii.	Edit postgresql.conf so that it contains the following line:

				listen_addresses = '*'
	
	After editing pg_hba.conf,  PostgreSQL must be signaled to reload its
	configuration settings. With a standard install of PostgreSQL on 
	Ubuntu/Debian this can be done with:
	
		sudo -u postgres /usr/lib/postgresql/9.x/bin/pg_ctl -D /var/lib/postgresql/9.x/main reload
		
	where "x" refers to the version of PostgreSQL that is being used. If
	listen_addresses is modified, then PostgreSQL must be restarted because this
	parameter is read only on startup. On Ubuntu/Debian this can be done with:
	
		sudo service postgresql restart

4.	Create the report server database. This will also:

		* Create a PostgreSQL role "report_server_app" to own the database.
		* Create the database tables and other database objects.
		* Populate the database with initial data.

	From a shell on the database host, execute the following from the directory
	containing the script create-report_server_db.sh:

		sudo -u postgres ./create-report_server_db.sh

	To see a list of all options that this script provides, type:

		./create-report_server_db.sh -h
	
It should now be possible to make a local connection from the PostgreSQL server
host to the database "report_server_db" using the PostgreSQL role 
"report_server_app" by executing in a shell (on the PostgreSQL server host):
	
	psql report_server_db report_server_app

If you have not placed an entry for "report_server_app" in your .pgpass file,
you will be prompted for the password here.
																	

II.	===== Installing the report server application =====

1.	Upload report-server.war to the Tomcat server via Tomcat's administrative 
	HTML interface. 

	Important: This should be done after the PostgreSQL database is installed 
	and populated because the report server attempts to validate the database 
	schema when the report server web application starts up. This startup phase 
	also occurs when a new WAR file is uploaded. If the report server database 
	is not available during startup, this validation will fail. To check for 
	validation errors, examine the log file. On Tomcat, the log file can be 
	found here:

		<CATALINA_HOME>/logs/obo-report-server/obo-report-server.log

2.	Test that the report server is running by visiting the following URL:

		<host>/report-server/rest/appversion

	where <host> identifies the server hosting the report server application, 
	the protocol for accessing it, as well as the port number if a non-standard 
	port number is used. This can be done using a web browser or in a bash shell
	by executing:

		curl -X GET <host>/report-server/rest/appversion

	Either way, a string should be returned that represents the version number 
	for the running report server application.