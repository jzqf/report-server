package com.qfree.obo.report.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Semaphore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.obo.report.domain.ReportVersion;

public class ReportUtils {

	private static final Logger logger = LoggerFactory.getLogger(ReportUtils.class);

	/*
	 * This counting semaphore is used to ensure that only one thread is 
	 * performing some sort of synchronization action between the rptdesign
	 * files in the report server application server's file system and the
	 * rptdesign definition stored in the report server's database.
	 */
	public static final Semaphore reportSyncSemaphore = new Semaphore(1);

	/*
	 * Threads will wait this many seconds to wait for a permit to become
	 * available for the reportSyncSemaphore semaphore.
	 */
	public static final long MAX_WAIT_ACQUIRE_REPORTSYNCSEMAPHORE = 5;

	/*
	 * Directory in /webapp where reports are stored. This must be set to the 
	 * same value as the <context-param> with the same name in web.xml. This 
	 * value can actually be extracted from the ServletContext that can be 
	 * obtained in a ReST controller method (e.g., see the logging code in
	 * ReportVersionController.createByUpload()), but this does not seem to work
	 * when this application is run via "mvn clean spring-boot:run". For that
	 * reason I hardwire it here.
	 */
	public static final String BIRT_VIEWER_WORKING_FOLDER = "reports";

	/**
	 * Returns a {@link Path} corresponding to a file to which a BIRT report
	 * definition will be written. This file contains XML and by convention has
	 * the extension "rptdesign". 
	 * 
	 * @param reportVersion
	 * @param absoluteAppContextPath
	 * @return
	 */
	public static Path rptdesignFilePath(ReportVersion reportVersion, String absoluteAppContextPath) {
		return Paths.get(absoluteAppContextPath, ReportUtils.BIRT_VIEWER_WORKING_FOLDER,
				reportVersion.getFileName());
				//reportVersion.getReportVersionId().toString() + ".rptdesign");
	}

	/**
	 * Write BIRT rptdesign file to the file system of the report 
	 * server, overwriting a file with the same name if one exists.
	 * 
	 * @param reportVersion
	 * @param absoluteAppContextPath
	 * @throws IOException 
	 * @throws UnsupportedEncodingException 
	 */
	public static Path writeRptdesignFile(ReportVersion reportVersion, String absoluteAppContextPath)
			throws UnsupportedEncodingException, IOException {
		Path rptdesignFilePath = rptdesignFilePath(reportVersion, absoluteAppContextPath);
		logger.info("Writing file \"{}\"...", rptdesignFilePath);
		Files.write(
				rptdesignFilePath,
				reportVersion.getRptdesign().getBytes("utf-8"),
				StandardOpenOption.CREATE,
				StandardOpenOption.WRITE,
				StandardOpenOption.TRUNCATE_EXISTING);
		return rptdesignFilePath;
	}
}
