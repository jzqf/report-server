package com.qfree.obo.report.apps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.qfree.obo.report.ApplicationConfig;

public class CreateCloseApplicationContext {

	private static final Logger logger = LoggerFactory.getLogger(CreateCloseApplicationContext.class);

	/**
	 * Creates an AnnotationConfigApplicationContext and then closes it. This 
	 * can be used to create or update tables in a database via Hibernate's
	 * "hibernate.hbm2ddl.auto" property.
	 */
	public static void main(String[] args) throws Exception {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class);

//		Client client = ClientBuilder.newBuilder()
//				//	.register(JsonProcessingFeature.class)
//				//	.property(JsonGenerator.PRETTY_PRINTING, true)
//				.build();
//
//		/*
//		 * 8081: My local Tomcat server
//		 */
//		String port = "8081";
//		WebTarget webTarget = client.target("http://localhost:" + port + "/report-server/rest");
//
//		/*
//		 * 8080: Embedded Tomcat server started with:
//		 * 
//		 *     $ mvn clean spring-boot:run
//		 */
//		//		String port = "8081";	// 8081: My local Tomcat server
//		//		WebTarget webTarget = client.target("http://localhost:" + port + "/rest");
//
//		Response response = webTarget
//				.path("reportCategories/7a482694-51d2-42d0-b0e2-19dd13bbbc64")
//				.request(MediaType.APPLICATION_JSON_TYPE)
//				//				.request(MediaType.TEXT_PLAIN_TYPE)
//				.get();
//		ReportCategoryResource resource = response.readEntity(ReportCategoryResource.class);
//		//		String resource = response.readEntity(String.class);
//
//		logger.info("resource = {}", resource);
//		System.out.println("resource = " + resource);
//
//		context.close();
	}
}
