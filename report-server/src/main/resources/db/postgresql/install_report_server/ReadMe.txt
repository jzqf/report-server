===== Extracting the files from the installation package archive =====

The report server is distributed in a Debian package named:

	report-server-<version>.deb

Install this package on the report server host machine with:

	sudo dpkg -i report-server-<version>.deb

This will install the following tree of files in the /tmp directory:

	report-server-<version>/
		report-server.war
		install/
			ReadMe.txt
			create-report_server_db.sh
			sql/
				init_db.sql
		upgrade/
			ReadMe.txt
			upgrade_report_server_db.sh
			db_version_upgrade_deltas/
				upgrade_report_server_db_to_v2.sql
				upgrade_report_server_db_to_v3.sql
				upgrade_report_server_db_to_v4.sql
				...

Note: The install script that is packaged in this archive:

	create-report_server_db.sh ,

is written to be executed by the "postgres" operating system user. The reason 
for this approach is that this "postgres" user automatically has super-user 
access to the PostgreSQL cluster. This simplifies creating and initializing the 
report server database since no special super-user needs to first be created in 
order to bootstrap the process. A operating system account for the "postgres" 
user is created automatically when PostgreSQL is installed, and this user can 
log into the local PostgreSQL cluster using the PostgreSQL role of the same 
name, "postgres".


===== Installation instructions =====

Before the report server can be installed, both a Tomcat 8.x server and a 
PostgreSQL 9.x server must be installed in a Debian or Ubuntu GNU/Linux 
operating system. This document assumes that standard installations of both of
these servers have already been performed. However, the customization details 
(beyond a standard installation) that are necessary to support the report server 
are covered here. In particular:

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
will be necessary which is not documented here.

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
	be at least 150 MB. If this is not done, it might not be possible to upload 
	the report server WAR file. This setting can be changed by editing the 
	max-file-size and max-request-size XML elements in the file:

		<CATALINA_HOME>/webapps/manager/WEB-INF/web.xml .
		
3.	Install PostgreSQL JDBC driver:

	In order to use container-managed database connections, it is necessary to 
	install the latest JDBC driver for PostgreSQL. The latest JDBC v4.2 driver 
	for PostgreSQL v9.x can be downloaded from:

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

	From a shell on the database host, execute the following:

		cd /tmp/report-server-<version>/install/
		sudo -u postgres ./create-report_server_db.sh

	To see a list of all options that this script provides, type:

		./create-report_server_db.sh -h

	For example, this script can be run with the -D option. This will first drop 
	an *existing* report server database and then recreate a new one. If an 
	existing report server application is running when this option is used, it 
	must first be shut down because it maintains an open connection to the 
	database, which will cause this script to fail.

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
	also occurs when a new WAR file is uploaded, as is done during this step. If 
	the report server database is not available during startup, this validation 
	will fail. To check for validation errors, examine the log file. On Tomcat, 
	the log file can be found here:

		<CATALINA_HOME>/logs/obo-report-server/obo-report-server.log

2.	Test that the report server is running by visiting the following URL:

		<host>/report-server/rest/appversion

	where <host> identifies the server hosting the report server application, 
	the protocol for accessing it, as well as the port number if a non-standard 
	port number is used. You will need to authenticate with a username and 
	password for a role that has the authority "USE_RESTAPI" granted, e.g., the
	role with username = "reportserver-restadmin". This can be done using a web
	browser or it can be done in a bash shell by executing:

		curl -X GET -u <username>:<password> <host>/report-server/rest/appversion

	Either way, a string should be returned that represents the version number 
	for the running report server application.