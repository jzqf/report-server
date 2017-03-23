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


===== Upgrade instructions =====

Upgrading the report server involves updating two components of the report 
server installation:

	I.	The report server database
	II.	The report server application

This document covers upgrading both of these components.


I.	==== Upgrading the report server database ====

1.	Stop and then *undeploy* the report server application using the Tomcat
	administrative HTML interface. 

2.	Perform a backup of the report server database or of the entire PostgresQL 
	cluster.

3.	Log into the PostgreSQL host machine, either directly or via the ssh 
	command.

4.	Execute the following in a bash shell from the 
	upgrade_report_server-<version> directory on the PostgreSQL host machine:

		./upgrade_report_server_db.sh

	Unless a different PostgreSQL role (user) is specified with the -U option, 
	this script will attempt to connect to the report server database using the 
	role "report_server_app", which is the role used by the report server 
	application itself to connect to the report server database. To simplify 
	execution of this script, the PostgreSQL password for this role can be 
	placed in the .pgpass file of the OS user's account from which this script 
	is executed; otherwise, the user will be prompted for this password each 
	time this script is run.

	To view a list of options available to the script, execute:

		./upgrade_report_server_db.sh -h

	By default, the script will connect to the local PostgreSQL cluster using 
	the default database name and TCP port, but these can be overridden, if 
	necessary, using command line options.


II.	==== Upgrading the report server application ====

1.	Deploy report-server.war for the new version of the report server 
	application using the Tomcat administrative HTML interface. This step should
	also start the application. If the application does not start, check the
	container log file, which for Tomcat can be found here:

		<CATALINA_HOME>/logs/qfree-report-server/qfree-report-server.log
	
	Important:	The new report-server.war file must be deployed  *after* the 
				database is upgraded.

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