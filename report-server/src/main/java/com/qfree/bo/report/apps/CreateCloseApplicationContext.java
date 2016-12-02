package com.qfree.bo.report.apps;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.qfree.bo.report.ApplicationConfig;

public class CreateCloseApplicationContext {

	private static final Logger logger = LoggerFactory.getLogger(CreateCloseApplicationContext.class);

	/**
	 * Creates an AnnotationConfigApplicationContext and then closes it. This
	 * can be used to:
	 * 
	 * 1. Create or update tables in a database via Hibernate's
	 * "hibernate.hbm2ddl.auto" property.
	 */
	public static void main(String[] args) throws Exception {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class);

		HttpAuthenticationFeature basicAuthenticationFeature = HttpAuthenticationFeature.basic("ui", "ui");

		Client client = ClientBuilder.newBuilder()
				//	.register(JsonProcessingFeature.class)
				//	.property(JsonGenerator.PRETTY_PRINTING, true)
				.register(basicAuthenticationFeature)
				.build();

		/*
		 * 8081: My local Tomcat server
		 */
		//	String port = "8081";
		//	WebTarget webTarget = client.target("http://localhost:" + port + "/report-server/rest");

		/*
		 * 8080: Embedded Tomcat server started with:
		 * 
		 *     $ mvn clean spring-boot:run
		 */
		String port = "8080";
		WebTarget webTarget = client.target("http://localhost:" + port + "/rest");

		//	Response response = webTarget
		//			.path("reportCategories/7a482694-51d2-42d0-b0e2-19dd13bbbc64")
		//			.queryParam(ResourcePath.EXPAND_QP_NAME, ResourcePath.REPORTCATEGORY_EXPAND_PARAM)
		//			.request()
		//			.header("Accept", MediaType.APPLICATION_JSON + ";v=1")
		//			.get();
		//	logger.info("response.getStatus() = {}", response.getStatus());
		//	ReportCategoryResource resource = response.readEntity(ReportCategoryResource.class);
		//	//		String resource = response.readEntity(String.class);
		//	logger.info("resource = {}", resource);
		//	System.out.println("resource = " + resource);

		Invocation.Builder invocationBuilder = webTarget
				.path("test/nop")
				.request()
				.header("Accept", MediaType.TEXT_PLAIN + ";v=1");
		Response response = null;
		String resource = null;

		int loops = 500;
		final long startTime = System.currentTimeMillis();
		for (int i = 0; i < loops; i++) {
			response = invocationBuilder.get();
			//	System.out.println("response.getStatus() = " + response.getStatus());
			//	resource = response.readEntity(String.class);
			//	System.out.println("resource = " + resource);
		}
		final long endTime = System.currentTimeMillis();

		System.out.println(
				String.format("\nRequest time averaged over %s requests: %.2f ms", loops,
						(double) (endTime - startTime) / (double) loops));

		context.close();
	}
}
