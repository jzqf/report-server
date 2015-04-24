package com.qfree.obo.report.rest.server;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.ext.ContextResolver;

import org.glassfish.jersey.moxy.json.MoxyJsonConfig;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;
import org.springframework.stereotype.Component;

@Component
@ApplicationPath("/rest")
public class JAXRSConfiguration extends ResourceConfig {
	public JAXRSConfiguration() {

		/*
		 * This doesn't seem to do anything, so it is not used.
		 */
		// property(JsonGenerator.PRETTY_PRINTING, true);

		/*
		 * This is not needed unless FEATURE_AUTO_DISCOVERY_DISABLE is disabled
		 * because Jersey automatically discovers and performs this registration 
		 * if MOXy is on the classpath.
		 */
		// register(MoxyJsonFeature.class);

		/*
		 * This configures MessageBodyReader<T>s and MessageBodyWriter<T>s 
		 * provided by MOXy.
		 */
		register(createMoxyJsonResolver());

		/*
		 * This is a Spring filter to provide a bridge between JAX-RS and Spring
		 * request attributes.
		 */
		register(RequestContextFilter.class);

		/*
		 * ReST controllers (to manage ReST resources):
		 */
		register(ConfigurationController.class);
		register(TestController.class);
		/*
		 * TODO Decide whether to register *packages* here (not type-safe) or *classes* (type-safe and refactor-safe, but more tedious).
		 */
		//		packages("com.qfree.obo.report.rest.server");
	}

	public static ContextResolver<MoxyJsonConfig> createMoxyJsonResolver() {
		Map<String, String> namespacePrefixMapper = new HashMap<>(1);
		namespacePrefixMapper.put("http://www.w3.org/2001/XMLSchema-instance", "xsi");

		final MoxyJsonConfig moxyJsonConfig = new MoxyJsonConfig()
				.setNamespacePrefixMapper(namespacePrefixMapper)
				.setNamespaceSeparator(':');

		return moxyJsonConfig.resolver();
	}

}
//public class JAXRSConfiguration extends Application {
//
//}
