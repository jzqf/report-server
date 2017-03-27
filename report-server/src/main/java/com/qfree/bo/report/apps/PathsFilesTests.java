package com.qfree.bo.report.apps;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathsFilesTests {

	private static final Logger logger = LoggerFactory.getLogger(PathsFilesTests.class);

	public static void main(String[] args) {

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
