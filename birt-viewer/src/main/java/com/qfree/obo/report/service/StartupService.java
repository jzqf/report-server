package com.qfree.obo.report.service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

//@Component  <- not needed because bean is explicitly created in ApplicationConfig.java
@PropertySource("classpath:config.properties")
public class StartupService {

	private static final Logger logger = LoggerFactory.getLogger(StartupService.class);

	@Autowired
	private ReportSyncService reportSyncService;

	//	@Autowired
	//	ServletContext servletContext;

	/*
	 * The injected "env" object here will contain key/value pairs for each 
	 * property in the properties files specified above in the @PropertySource
	 * annotation above.
	 */
	@Autowired
	private Environment env;

	@PostConstruct
	public void initialize() {
		//logger.info("Synchronizing reports in the file system with the database...");
		//		logger.info("servletContext = {}", servletContext);

		logger.info("startup.syncreports = {}", env.getProperty("startup.syncreports"));

		//try {
		//	String path1 = new File(".").getCanonicalPath();
		//	logger.info("path1 = {}", path1);  // /home/jeffreyz/Applications/java/apache-tomcat/apache-tomcat-8.0.17/bin
		//} catch (IOException e) {
		//	// TODO Auto-generated catch block
		//	e.printStackTrace();
		//}
		//String path2 = System.getProperty("user.dir");
		//logger.info("path2 = {}", path2);  // /home/jeffreyz/Applications/java/apache-tomcat/apache-tomcat-8.0.17/bin
		String classesPath = this.getClass().getClassLoader().getResource("").getPath();
		logger.info("classesPath = {}", classesPath);  // /home/jeffreyz/Applications/java/apache-tomcat/apache-tomcat-8.0.17/webapps/report-server/WEB-INF/classes/
		Path absoluteContextPath;
		try {
			absoluteContextPath = Paths.get(classesPath).resolve("..").resolve("..").toRealPath();
			logger.info("absoluteContextPath = {}", absoluteContextPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
}
