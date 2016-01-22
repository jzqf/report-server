package com.qfree.obo.report.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Semaphore;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.obo.report.domain.Asset;
import com.qfree.obo.report.domain.ReportVersion;

public class ReportUtils {

	private static final Logger logger = LoggerFactory.getLogger(ReportUtils.class);

	/*
	 * This counting semaphore is used to ensure that only one thread is 
	 * performing some sort of synchronization action between the rptdesign
	 * files in the report server application server's file system and the
	 * rptdesign definitions stored in the report server's database.
	 */
	public static final Semaphore reportSyncSemaphore = new Semaphore(1);

	/*
	 * This counting semaphore is used to ensure that only one thread is 
	 * performing some sort of synchronization action between the asset
	 * files in the report server application server's file system and the
	 * asset iles stored in the report server's database.
	 */
	public static final Semaphore assetSyncSemaphore = new Semaphore(1);

	/*
	 * Threads will wait this many seconds to wait for a permit to become
	 * available for the reportSyncSemaphore semaphore.
	 */
	public static final long MAX_WAIT_ACQUIRE_REPORTSYNCSEMAPHORE = 10;

	/*
	 * Threads will wait this many seconds to wait for a permit to become
	 * available for the assetSyncSemaphore semaphore.
	 */
	public static final long MAX_WAIT_ACQUIRE_ASSETSYNCSEMAPHORE = 10;

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

	/*
	 * Directory in /webapp where assets (CSS files, image files, ...) are 
	 * stored. Be careful changing it because the BIRT Viewer servlets expect
	 * this name. If the BIRT "viewservlets" are eliminated from this 
	 * application, then we are free to rename this.
	 */
	public static final String ASSET_FILES_PARENT_FOLDER = "webcontent";

	/**
	 * Returns a {@link Path} corresponding to a file to which a BIRT report
	 * definition will be written. This file contains XML and by convention has
	 * the extension "rptdesign".
	 * 
	 * @param reportVersion
	 * @param absoluteAppContextPath
	 * @return
	 */
	private static Path rptdesignFilePath(ReportVersion reportVersion, String absoluteAppContextPath) {
		return Paths.get(absoluteAppContextPath, ReportUtils.BIRT_VIEWER_WORKING_FOLDER,
				reportVersion.getFileName());
		//reportVersion.getReportVersionId().toString() + ".rptdesign");
	}

	/**
	 * Write BIRT rptdesign file to the file system of the report server,
	 * overwriting a file with the same name if one exists.
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

	/**
	 * Write an asset file to the file system of the report server, overwriting
	 * a file with the same name if one exists.
	 * 
	 * @param asset
	 * @param absoluteAppContextPath
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	public static Path writeAssetFile(Asset asset, String absoluteAppContextPath)
			throws IOException {
		/*
		 * Create directory to contain the asset, if necessary.
		 */
		//Path assetDirectoryPath = assetFilePath(asset, absoluteAppContextPath);
		Path assetDirectoryPath = Paths.get(absoluteAppContextPath)
				.resolve(ReportUtils.ASSET_FILES_PARENT_FOLDER)
				.resolve(asset.getAssetTree().getDirectory())
				.resolve(asset.getAssetType().getDirectory());
		if (!assetDirectoryPath.toFile().isDirectory()) {
			logger.info("Creating directory {}", assetDirectoryPath.toString());
			assetDirectoryPath = Files.createDirectories(assetDirectoryPath);
		}
		/*
		 * Write the asset to the file system. In case a containing directory
		 * was just created, we check again here that it exists.
		 */
		Path assetFilePath = assetDirectoryPath.resolve(asset.getFilename());
		if (assetDirectoryPath.toFile().isDirectory()) {
			logger.info("Writing file \"{}\"...", assetFilePath);
			Files.write(
					assetFilePath,
					asset.getDocument().getContent(),
					StandardOpenOption.CREATE,
					StandardOpenOption.WRITE,
					StandardOpenOption.TRUNCATE_EXISTING);
		}
		return assetFilePath;
	}

	/**
	 * Delete an asset file from the file system of the report server.
	 * 
	 * @param asset
	 * @param absoluteAppContextPath
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	public static Path deleteAssetFile(Asset asset, String absoluteAppContextPath)
			throws IOException {
		/*
		 * Delete the asset from from the file system. We check first that the
		 * file exists and that it is a file, i.e., not a directory. The file
		 * may not exist if the corresponding Asset is inactive; hence, we do
		 * not flag that as an error here.
		 */
		Path assetFilePath = Paths.get(absoluteAppContextPath)
				.resolve(ReportUtils.ASSET_FILES_PARENT_FOLDER)
				.resolve(asset.getAssetTree().getDirectory())
				.resolve(asset.getAssetType().getDirectory())
				.resolve(asset.getFilename());
		if (assetFilePath.toFile().isFile()) {
			logger.info("Deleting file \"{}\"...", assetFilePath);
			Files.delete(assetFilePath);
		} else if (assetFilePath.toFile().isDirectory()) {
			logger.error("Asset file {} is a directory. It cannot be deleted.", assetFilePath.toString());
		}
		return assetFilePath;
	}

	/**
	 * Checks if the application has been packaged as a WAR file and is running
	 * in a full servlet container environment.
	 * 
	 * <p>
	 * If the application is <i>not</i> running in a full servlet container,
	 * e.g., if the application is started via:
	 * 
	 * <p>
	 * <code>$ mvn clean spring-boot:run</code>
	 * 
	 * <p>
	 * then not all the features of a full servlet environment will be
	 * available.
	 * 
	 * <p>
	 * One feature in particular that is important for this application is that
	 * in order to synchronize reports or report assets (CSS files, images, ...)
	 * between the database and file system, there needs to be a directory tree
	 * in the file system where these files should be stored. If this
	 * application is being run from an embedded Tomcat container, such a
	 * directory tree will exist but it will be within the current Eclipse
	 * project, i.e., in my local Git repository for this project. On my machine
	 * (Jeffrey Zelt), this will be within:
	 * 
	 * <p>
	 * <code>/home/jeffreyz/git/obo-birt-viewer/birt-viewer/src/main/webapp</code>
	 * 
	 * <p>
	 * I do not want to write files to this directory tree because any files
	 * that exist in this tree will be deployed when the application is packaged
	 * as a WAR file.
	 * 
	 * @return
	 */
	public static boolean applicationPackagedAsWar() {
		boolean isWar = false;
		/*
		 * We test which environment we are in by testing if the JNDI
		 * initial context "java:comp/env" is available. It is, in fact, 
		 * possible to configure Spring Boot's embedded Tomcat environment to
		 * support a JNDI initial context, but I have not configured this. If
		 * this is configured one day, then we will need to detect whether this 
		 * application has been deployed in a full servlet container or not by
		 * a different test. 
		 */
		try {
			Object object = new InitialContext().lookup("java:comp/env");
			isWar = true;
		} catch (NamingException ex) {
			/*
			 * We are probably running via:
			 * 
			 *   $ mvn clean spring-boot:run
			 */
		}
		return isWar;
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
