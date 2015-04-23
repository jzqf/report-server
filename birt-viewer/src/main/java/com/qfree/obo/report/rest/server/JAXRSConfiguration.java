package com.qfree.obo.report.rest.server;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

@Component
@ApplicationPath("/rest")
public class JAXRSConfiguration extends ResourceConfig {
	public JAXRSConfiguration() {
		/*
		 * TODO Decide whether to register *packages* here (not type-safe) or *classes* (type-safe and refactor-safe, but more tedious).
		 */
		//		packages("com.qfree.obo.report.rest.server");

		/*
		 * TODO What does this actually do?
		 */
		//		register(RequestContextFilter.class);

		register(TestController.class);
	}
}
//public class JAXRSConfiguration extends Application {
//
//}
