package com.qfree.bo.report.apps;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.bo.report.util.ReportUtils;

import ch.qos.logback.classic.LoggerContext;

public class PathsFilesTests {

	private static final Logger logger = LoggerFactory.getLogger(PathsFilesTests.class);

	public static void main(String[] args) {

		/*
		 * 
		 * If this code is placed in a standalone Java application, the
		 * result is currently appContextPath.toString() =
		 * 
		 * /home/jeffreyz/git/qfree-report-server/report-server
		 * 
		 * If this code is executed when this application is installed as a
		 * WAR in Tomcat on my PC, appContextPath.toString() 
		 * currently evaluates to:
		 * 
		 * /home/jeffreyz/Applications/java/apache-tomcat/apache-tomcat-8.0.17/webapps/report-server
		 */
		Path appContextPath = ReportUtils.getApplicationContextPath();
		logger.info("appContextPath = {}", appContextPath);

		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		String LOG_FOLDER = lc.getProperty("LOG_FOLDER");
		logger.info("LOG_FOLDER = {}", LOG_FOLDER);

		String logPath = appContextPath.resolve(LOG_FOLDER).toString();
		logger.info("logPath = {}", logPath);

		String classesPath = PathsFilesTests.class.getClassLoader().getResource("").getPath();
		logger.info("classesPath = {}", classesPath);
		Path absoluteContextPath = null;
		try {
			/*
			 * On my PC, this currently evaluates to:
			 * 
			 * /home/jeffreyz/git/qfree-report-server/report-server/target/classes/
			 */
			absoluteContextPath = Paths.get(classesPath).resolve("..").resolve("..").toRealPath();
			logger.info("absoluteContextPath = {}", absoluteContextPath);
		} catch (IOException e) {
			logger.error("Exception thrown while obtaining application context directory Path", e);
		}

		//Path rootPath = Paths.get("/");
		//logger.info("rootPath = {}", rootPath.toString());

		//Path blankPath = Paths.get("/");
		//logger.info("blankPath = {}", blankPath.toString());

		Path absolutePath = Paths.get("/a/b");
		logger.info("absolutePath = {}", absolutePath.toString());

		Path directoryPath = Paths.get("webcontent/blah");
		logger.info("directoryPath = {}", directoryPath.toString());

		File assetDirectory = absolutePath.resolve(directoryPath).toFile();
		logger.info("assetDirectory = {}", assetDirectory.toString());


	}

}
