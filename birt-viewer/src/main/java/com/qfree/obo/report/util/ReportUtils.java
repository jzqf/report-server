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

	//	public static Map<String, Map<String, Serializable>> parseReportParams(String rptdesignXml)
	//			throws IOException, BirtException {
	//	
	//		/*
	//		 * A LinkedHashMap is used here so that the order of the parameters 
	//		 * inserted into this map is preserved, i.e., iteration over the 
	//		 * entries in this map will always preserve the order that the entries
	//		 * were originally inserted into the map. 
	//		 */
	//		Map<String, Map<String, Serializable>> parameters = new LinkedHashMap<>();
	//	
	//		IReportEngine engine = null;
	//		try {
	//
	//			EngineConfig config = new EngineConfig();
	//			config.setLogConfig(null, Level.FINE);
	//
	//			Platform.startup(config);
	//			IReportEngineFactory factory = (IReportEngineFactory) Platform
	//					.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
	//			engine = factory.createReportEngine(config);
	//			engine.changeLogLevel(Level.WARNING);
	//
	//			logger.info("Report engine is initialized");
	//
	//			IReportRunnable design = null;
	//
	//			/*
	//			 * Open the report design.
	//			 */
	//			try (InputStream rptdesignStream = new ByteArrayInputStream(
	//					rptdesignXml.getBytes(StandardCharsets.UTF_8))) {
	//				design = engine.openReportDesign(rptdesignStream);
	//			}
	//
	//			/*
	//			 * One can also open a design by passing an absolute path to an 
	//			 * rptdesign file as shown here. This code is commented out and only 
	//			 * used for testing.
	//			 */
	//			//design = engine.openReportDesign(
	//			//	"/home/jeffreyz/git/obo-birt-reports/birt-reports/tests/400-TestReport04_v1.1.rptdesign");
	//			//design = engine.openReportDesign("/home/jeffreyz/Desktop/cascade_v3.2.23.rptdesign");
	//
	//			/*
	//			 * Create an engine task for obtaining report parameter definitions.
	//			 */
	//			IGetParameterDefinitionTask task = engine.createGetParameterDefinitionTask(design);
	//			Collection<IParameterDefnBase> params = (Collection<IParameterDefnBase>) task.getParameterDefns(true);
	//
	//			/*
	//			 * Iterate through all parameter entries in "params". These can be 
	//			 * either IScalarParameterDefn or IParameterGroupDefn objects.
	//			 */
	//			for (IParameterDefnBase param : params) {
	//				if (param instanceof IParameterGroupDefn) {
	//
	//					IParameterGroupDefn group = (IParameterGroupDefn) param;
	//
	//					/*
	//					 *  Iterate over each parameter in the parameter group.
	//					 */
	//					for (IScalarParameterDefn scalarParameter : (ArrayList<IScalarParameterDefn>) group.getContents()) {
	//						logger.info("Scalar parameter '{}' from group '{}'", scalarParameter.getName(),
	//								group.getName());
	//						/*
	//						 * Get details about the parameter as a Map and then insert
	//						 * that Map into the paramDetails Map
	//						 */
	//						parameters.put(scalarParameter.getName(),
	//								ReportUtils.loadParameterDetails(task, scalarParameter, design, group));
	//					}
	//
	//				} else {
	//
	//					IScalarParameterDefn scalarParameter = (IScalarParameterDefn) param;
	//					logger.info("Scalar parameter '{}'", scalarParameter.getName());
	//					/*
	//					 * Get details about the parameter as a Map and then insert
	//					 * that Map into the paramDetails Map
	//					 */
	//					parameters.put(scalarParameter.getName(),
	//							ReportUtils.loadParameterDetails(task, scalarParameter, design, null));
	//
	//				}
	//			}
	//
	//			//} catch (BirtException ex) {
	//			//	logger.error("Exception starting up BIRT platform for parsing parameters in a report:",
	//			//			ex);
	//		} finally {
	//
	//			/*
	//			 * Destroy the engine and shutdown the Platform.
	//			 */
	//			if (engine != null) {
	//				engine.destroy();
	//			}
	//			Platform.shutdown();
	//			engine = null;
	//
	//			logger.info("Finished");
	//		}
	//
	//		return parameters;
	//	}

}
