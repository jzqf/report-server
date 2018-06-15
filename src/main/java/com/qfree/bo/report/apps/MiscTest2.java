package com.qfree.bo.report.apps;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MiscTest2 {

	private static final Logger logger = LoggerFactory.getLogger(MiscTest2.class);

	public static void main(String[] args) {

		String authenticationProviderUrl = "http://www.apple.com";

		//		HttpAuthenticationFeature basicAuthenticationFeature = HttpAuthenticationFeature.basic(name, password);

		Client client = ClientBuilder.newBuilder()
				//					.register(basicAuthenticationFeature)
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
		WebTarget webTarget = client.target(authenticationProviderUrl);

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
				//.path("test/nop")
				.request();
				//.header("Accept", MediaType.TEXT_PLAIN);
		//.header("Accept", MediaType.TEXT_HTML);

		final long startTime = System.currentTimeMillis();
		Response response = invocationBuilder.head();
		//Response response = invocationBuilder.get();
		//	System.out.println("response.getStatus() = " + response.getStatus());
		//	String resource = response.readEntity(String.class);
		//	System.out.println("resource = " + resource);
		final long endTime = System.currentTimeMillis();
		logger.info("Time to authenticate externally = {} ms", endTime - startTime);

		logger.info("response.getStatus() = {}", response.getStatus());
		if (response.getStatus() == Response.Status.OK.getStatusCode()) {
			logger.info("Request passed authentication by: {}", authenticationProviderUrl);
		} else {
			logger.info("Request failed authentications by: {}", authenticationProviderUrl);
		}

	}
}