package com.qfree.obo.report.rest.server;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.ext.ContextResolver;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.moxy.json.MoxyJsonConfig;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;
import org.springframework.stereotype.Component;

import com.qfree.obo.report.exceptions.GenericExceptionMapper;

@Component
@ApplicationPath("/rest")
public class JAXRSConfiguration extends ResourceConfig {
	public JAXRSConfiguration() {

		//property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);

		/*
		 * This is for Java API for JSON Processing (JSON-P), so it doesn't do 
		 * anything for JSON generated via MOXy.
		 */
		//		 property(JsonGenerator.PRETTY_PRINTING, true);

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
		 * This is needed for multipart support, e.g., file uploads. It is
		 * provided by the Maven dependency:
		 * 		<dependency>
		 * 			<groupId>org.glassfish.jersey.media</groupId>
		 * 			<artifactId>jersey-media-multipart</artifactId>
		 * 			<version>${jersey.version}</version>
		 * 		</dependency>
		 */
		register(MultiPartFeature.class);

		/*
		 * This is a Spring filter to provide a bridge between JAX-RS and Spring
		 * request attributes.
		 */
		register(RequestContextFilter.class);

		/*
		 * This exception mapper handles *all* exceptions.
		 */
		register(GenericExceptionMapper.class);

		/*
		 * ReST controllers (to manage ReST resources):
		 */
		register(AuthorityController.class);
		register(ConfigurationController.class);
		register(DocumentFormatController.class);
		register(JobController.class);
		register(JobParameterController.class);
		register(JobParameterValueController.class);
		register(JobProcessorController.class);
		register(JobStatusController.class);
		register(LoginAttemptController.class);
		register(ParameterGroupController.class);
		register(ReportController.class);
		register(ReportCategoryController.class);
		register(ReportParameterController.class);
		register(ReportSyncController.class);
		register(ReportVersionController.class);
		register(RoleController.class);
		register(SelectionListValueController.class);
		register(TestController.class);
		register(RootController.class);
		register(SubscriptionController.class);
		register(SubscriptionParameterController.class);
		register(SubscriptionParameterValueController.class);
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
