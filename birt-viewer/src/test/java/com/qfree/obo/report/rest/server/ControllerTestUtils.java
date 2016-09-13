package com.qfree.obo.report.rest.server;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.obo.report.domain.Role;

/**
 * 
 * @author Jeffrey Zelt
 *
 */
abstract class ControllerTestUtils {

	private static final Logger logger = LoggerFactory.getLogger(ControllerTestUtils.class);

	public static Client setUpJaxRsClient() {
		logger.info("Setting up JAX-RS Client with basic authentication...");

		/*
		 * Use the security credentials of the built-in Q-Free admin role. This
		 * role is always authenticated locally. The password here must match
		 * the hashed password stored in Role.encodedPassword.
		 */
		HttpAuthenticationFeature basicAuthenticationFeature = HttpAuthenticationFeature.basic(
				Role.QFREE_ADMIN_ROLE_NAME,
				"qfreereportserveradmin_Af5Dj%4$");

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
