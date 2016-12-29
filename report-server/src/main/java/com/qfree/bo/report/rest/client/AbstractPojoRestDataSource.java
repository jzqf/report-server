package com.qfree.bo.report.rest.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.bo.report.domain.Role;

/**
 * 
 * @author Jeffrey Zelt
 *
 */
abstract class AbstractPojoRestDataSource {

	private static final Logger logger = LoggerFactory.getLogger(AbstractPojoRestDataSource.class);

	public static Client setUpJaxRsClient() {
		logger.info("Setting up JAX-RS Client with basic authentication...");

		/*
		 * Use the security credentials of the built-in Q-Free admin role. This
		 * role is always authenticated locally. The password here must match
		 * the hashed password stored in Role.encodedPassword.
		 * 
		 * TODO The password for the Q-Free admin role should be stored such that it can be retrieved here
		 */
		HttpAuthenticationFeature basicAuthenticationFeature = HttpAuthenticationFeature.basic(
				Role.QFREE_ADMIN_ROLE_NAME,
				"qfreereportserveradmin_Af5Dj%4$");

		/* JsonProcessingFeature is a feature to register JSON-P providers. This
		 * enables a binding between JAX-RS and the Java API for JSON Processing,
		 * which enables JAX-RS return JSON objects. This JSON-Processing media 
		 * module is one of the modules where you don't need to explicitly 
		 * register it's feature (JsonProcessingFeature) in your client/server 
		 * Configurable as this feature is automatically discovered and 
		 * registered when you add jersey-media-json-processing module to your 
		 * classpath. This is described here:
		 * 
		 * https://jersey.java.net/documentation/latest/user-guide.html#deployment.autodiscoverable
		 * 
		 * So the method call ".register(JsonProcessingFeature.class)" can be 
		 * uncommented here if you want to use the Java API for JSON Processing 
		 * (JSR 353), but it is not necessary to do so. What is necessary (if 
		 * want to use this API) is that the jersey-media-json-processing Maven 
		 * artifact is specified in pom.xml; otherwise, an exception will be 
		 * thrown when a JSON object is requested from a JAX-RS call.
		 */
		return ClientBuilder.newBuilder()
				//	.register(JsonProcessingFeature.class)
				//	.property(JsonGenerator.PRETTY_PRINTING, true)
				.register(basicAuthenticationFeature)
				.build();
	}

	public static WebTarget setUpWebTarget(Client client, int port) {
		logger.info("Setting up JAX-RS WebTarget. port = {}", port);
		/*
		 * This WebTarget can be used in test methods to derive a target that is
		 * specific to a particular resource associated with the test. This
		 * cannot go in @BeforeClass method of the test class because that is a
		 * static method and "port" will be an instance variable in the test 
		 * class.
		 * 
		 * The string "/report-server" here should match exactly the application
		 * context that is set by the "server.contextPath" property in the file:
		 * 
		 *   /src/main/resources/application.properties
		 */
		return client.target("http://localhost:" + port + "/report-server/rest");
	}
}
