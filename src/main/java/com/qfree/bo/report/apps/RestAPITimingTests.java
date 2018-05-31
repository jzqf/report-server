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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class RestAPITimingTests {

	private static final Logger logger = LoggerFactory.getLogger(RestAPITimingTests.class);

	/**
	 * Creates an AnnotationConfigApplicationContext and then closes it. This
	 * can be used to:
	 * 
	 * 1. Create or update tables in a database via Hibernate's
	 * "hibernate.hbm2ddl.auto" property.
	 */
	public static void main(String[] args) throws Exception {

		BCryptPasswordEncoder bCryptPasswordEncoder_08 = new BCryptPasswordEncoder(8);
		BCryptPasswordEncoder bCryptPasswordEncoder_10 = new BCryptPasswordEncoder(10);
		BCryptPasswordEncoder bCryptPasswordEncoder_12 = new BCryptPasswordEncoder(12);
		String encodedPassword_10 = bCryptPasswordEncoder_10.encode("ui");
		System.out.println("encodedPassword_10 = " + encodedPassword_10);
		System.out.println("bCryptPasswordEncoder_10.matches(\"ui\", encodedPassword_10) = "
				+ bCryptPasswordEncoder_10.matches("ui", encodedPassword_10));
		
//	System.out.println("bCryptPasswordEncoder_08.encode('reportadmin') = "+bCryptPasswordEncoder_08.encode("reportadmin"));
//	System.out.println("bCryptPasswordEncoder_10.encode('reportadmin') = "+bCryptPasswordEncoder_10.encode("reportadmin"));
//	System.out.println("bCryptPasswordEncoder_12.encode('reportadmin') = "+bCryptPasswordEncoder_12.encode("reportadmin"));		
		
		/*
		 * This shows that in order to match an unencoded password with an
		 * encoded one, it is *NOT* necessary to perform the match using a
		 * BCryptPasswordEncoder that is initialized with the same "strength"
		 * value that was used for encoding the password. 
		 */
		System.out.println("bCryptPasswordEncoder_12.matches(\"ui\", encodedPassword_10) = "
				+ bCryptPasswordEncoder_12.matches("ui", encodedPassword_10));
		System.out.println("bCryptPasswordEncoder_12.matches(\"uiXXX\", encodedPassword_10) = "
				+ bCryptPasswordEncoder_12.matches("uiXXX", encodedPassword_10));

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

		int loops = 100;
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

	}
}
