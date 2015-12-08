package com.qfree.obo.report.rest.server;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Jeffrey Zelt
 *
 */
abstract class ControllerTestUtils {

	private static final Logger logger = LoggerFactory.getLogger(ControllerTestUtils.class);

	public static Client setUpJaxRsClient() {
		logger.info("Setting up JAX-RS Client with basic authentication...");

		//HttpAuthenticationFeature basicAuthenticationFeature = HttpAuthenticationFeature.basic("ui", "ui");
		HttpAuthenticationFeature basicAuthenticationFeature = HttpAuthenticationFeature.basic("a", "anything");

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
		 */
		return client.target("http://localhost:" + port + "/rest");
	}
}
