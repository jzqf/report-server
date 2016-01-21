package com.qfree.obo.report.service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.qfree.obo.report.dto.AssetSyncResource;
import com.qfree.obo.report.dto.ReportSyncResource;

//@Component  <- not needed because bean is explicitly created in ApplicationConfig.java
@PropertySource("classpath:config.properties")
public class StartupService {

	private static final Logger logger = LoggerFactory.getLogger(StartupService.class);

	@Autowired
	private ReportSyncService reportSyncService;

	@Autowired
	private AssetSyncService assetSyncService;

	//	@Autowired
	//	private BirtService birtService;

	//@Autowired
	//ServletContext servletContext;

	/*
	 * The injected "env" object here will contain key/value pairs for each 
	 * property in the properties files specified above in the @PropertySource
	 * annotation.
	 */
	@Autowired
	private Environment env;

	@PostConstruct
	public void initialize() {

		//try {
		//	String path1 = new File(".").getCanonicalPath();
		//	logger.info("path1 = {}", path1);  // /home/jeffreyz/Applications/java/apache-tomcat/apache-tomcat-8.0.17/bin
		//} catch (IOException e) {
		//	// TODO Auto-generated catch block
		//	e.printStackTrace();
		//}
		//String path2 = System.getProperty("user.dir");
		//logger.info("path2 = {}", path2);  // /home/jeffreyz/Applications/java/apache-tomcat/apache-tomcat-8.0.17/bin

		/*
		 * Synchronizing the reports or assets stored in the database with
		 * the file system only makes sense if there is a context where the
		 * files can be written. 
		 * 
		 * If this application is started via:
		 * 
		 *     $ mvn clean spring-boot:run
		 * 
		 * there will not be such a context. In this case, 
		 * "absoluteContextPath" will be the 
		 * ".../obo-birt-viewer/birt-viewer/src" directory of the Maven
		 * project from which "mvn clean spring-boot:run" is executed.
		 * 
		 * If, however, we are running in a non-embedded servlet container
		 * such as Tomcat, "absoluteContextPath" will refer to the servlet
		 * context path of the web application.
		 * 
		 * We test which environment we are in by testing if the JNDI
		 * initial context "java:comp/env" is available:
		 */
		try {
			new InitialContext().lookup("java:comp/env");

			/*
			 * If no exception is thrown, we are probably running in a servlet
			 * container environment, e.g., Tomcat. Therefore, it is safe to
			 * synchronize the reports and assets stored in the database with
			 * the file system. For that, we need to determine the context path
			 * for the application.
			 * 
			 * On my PC, this currently evaluates to:
			 * 
			 * /home/jeffreyz/Applications/java/apache-tomcat/apache-tomcat-8.0.17/webapps/report-server/WEB-INF/classes/
			 */
			String classesPath = this.getClass().getClassLoader().getResource("").getPath();
			logger.info("classesPath = {}", classesPath);
			Path absoluteContextPath = null;
			try {

				/*
				 * On my PC, this currently evaluates to:
				 * 
				 * /home/jeffreyz/Applications/java/apache-tomcat/apache-tomcat-8.0.17/webapps/report-server
				 */
				absoluteContextPath = Paths.get(classesPath).resolve("..").resolve("..").toRealPath();
				logger.info("absoluteContextPath = {}", absoluteContextPath);

				/*
				 * If configured, synchronize reports stored in the database
				 * with the file system.
				 */
				logger.info("startup.sync.reports = {}", env.getProperty("startup.sync.reports"));
				if (env.getProperty("startup.sync.reports").equals("true")) {
					Boolean showInactiveReports = false;
					ReportSyncResource reportSyncResource = reportSyncService.syncReportsWithFileSystem(
							absoluteContextPath, showInactiveReports);
					logger.debug("reportSyncResource = {}", reportSyncResource);
				}

				/*
				 * If configured, synchronize assets stored in the database
				 * with the file system.
				 */
				logger.info("startup.sync.assets = {}", env.getProperty("startup.sync.assets"));
				if (env.getProperty("startup.sync.assets").equals("true")) {
					Boolean syncInactiveAssets = false;
					AssetSyncResource assetSyncResource = assetSyncService.syncAssetsWithFileSystem(
							absoluteContextPath, syncInactiveAssets);
					logger.debug("assetSyncResource = {}", assetSyncResource);
				}

			} catch (IOException e) {
				logger.error("Exception thrown during startup while obtaining application context directory", e);
			}
		} catch (NamingException ex) {
			/*
			 * We are probably running via:
			 * 
			 *     $ mvn clean spring-boot:run
			 * 
			 * so we do not attempt to synchronize the reports or assets
			 * stored in the database with the file system.
			 */
		}

		//		/* 
		//		 * This is the default version for the endpoint to which the request is
		//		 * sent.
		//		 */
		//		String defaultVersion = "1";
		//
		//		Response response;
		//
		//		/*
		//		 * IMPORTANT:
		//		 * 
		//		 * It is necessary here to specify that "reportCategory" be expanded;
		//		 * otherwise, 
		//		 * responseEntity.getReportCategoryResource().getReportCategoryId() will
		//		 * be null, since the JSON object returned by the server will not have
		//		 * included an attribute/value pair for it. As a result, the assert for 
		//		 * it below will fail, even though the POST will still correctly create 
		//		 * the new Report object. 
		//		 */
		//		response = webTarget
		//				// .path(ResourcePath.REPORTS_PATH)
		//				.path("/reportSyncs")
		//				// See comments above why these query parameters are necessary here:
		//				.queryParam(ResourcePath.EXPAND_QP_NAME,
		//						ResourcePath.REPORT_EXPAND_PARAM,
		//						ResourcePath.REPORTCATEGORY_EXPAND_PARAM)
		//				.request()
		//				.header("Accept", MediaType.APPLICATION_JSON + ";v=" + defaultVersion)
		//				.post("");
		//		assertThat(response.getStatus(), is(Response.Status.CREATED.getStatusCode()));
		//
		//		/*
		//		 * The HTTP "Location" header should have been set in the response. It
		//		 * should contain the URI of the resource created. The resource at this
		//		 * URI is loaded below for additional tests.
		//		 */
		//		MultivaluedMap<String, Object> headers = response.getHeaders();
		//		logger.info("headers = {}", headers);
		//		List<Object> createdEntityLocations = headers.get("Location");
		//		//assertThat(createdEntityLocations, is(not(nullValue())));
		//		//assertThat(createdEntityLocations.size(), is(greaterThan(0)));
		//
		//		ReportResource responseEntity = response.readEntity(ReportResource.class);

	}

	//	@PreDestroy
	//	public void shutdown() {
	//		birtService.shutdownBirt();
	//	}

}
