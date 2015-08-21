Upgrading the report server involves updating two components of the report 
server installation:

	I.	The report server database
	II.	The report server application

This document covers upgrading both of these components.


I.	===== Upgrading the report server database =====

1.	Stop the report server application using the administrative HTML interface 
	provided by the application server, Tomcat. 

2.	Perform a backup of the report server database or of the entire PostgresQL 
	cluster.

3.	Log into the Tomcat host machine, either directly or via the ssh command.

4.	Execute the following in a bash shell from the upgrade_report_server 
	directory:

		./upgrade_report_server_db.sh

	The OS user account from which this is run should have the permission to 
	write from the upgrade_report_server directory because this script writes 
	errors and other messages to a log file named "logfile" in the script's 
	directory.

	Unless a different PostgreSQL role (user) is specified with the -U option, 
	this script will attempt to connect to the report server database using the 
	role "report_server_app", which is the role used by the report server 
	application itself to connect to the report server application. To simplify 
	execution of this script, the PostgreSQL password for this role can be 
	placed in the .pgpass file of the OS user's account from which this script 
	is executed; otherwise, the user will be prompted for this password each 
	time this script is run.

	To view a list of options available to the script, execute:

		./upgrade_report_server_db.sh -h

	By default, the script will connect to the local PostgreSQL cluster using 
	the default database name and TCP port, but these can be overridden, if 
	necessary, using command line options.
	
5.	Start the report server application using the administrative HTML interface.


II.	===== Upgrading the report server application =====

1.	Stop and undeploy the current report server application using the 
	administrative HTML interface provided by the application server, Tomcat.

2.	Deploy report-server.war for the new version of the report server 
	application using the same web interface. This step should also start the 
	application. If the application does not start, check the container log 
	file, which for Tomcat can be found here:

		<CATALINA_HOME>/logs/obo-report-server/obo-report-server.log
	
	Important:	The new report-server.war file must be deployed (or at least
				restarted) after the database is upgraded.

3.	Test that the report server is running by visiting the following URL:

		<host>/report-server/rest/appversion

	where <host> identifies the server hosting the report server application, 
	the protocol for accessing it, as well as the port number if a non-standard 
	port number is used. This can be done using a web browser or in a bash shell
	by executing:

		curl -X GET <host>/report-server/rest/appversion

	Either way, a string should be returned that represents the version number 
	for the running report server application.