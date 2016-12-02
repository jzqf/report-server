package com.qfree.bo.report.apps;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathsFilesTests {

	private static final Logger logger = LoggerFactory.getLogger(PathsFilesTests.class);

	public static void main(String[] args) {

		Path rootPath = Paths.get("/a/b");
		logger.info("rootPath = {}", rootPath.toString());

		Path directoryPath = Paths.get("webcontent/blah");
		logger.info("directoryPath = {}", directoryPath.toString());

		File assetDirectory = rootPath.resolve(directoryPath).toFile();
		logger.info("assetDirectory = {}", assetDirectory.toString());


	}

}
