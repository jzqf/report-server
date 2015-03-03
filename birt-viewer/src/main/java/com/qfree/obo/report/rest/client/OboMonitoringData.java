package com.qfree.obo.report.rest.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.stream.JsonGenerator;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Retrieves OBO monitoring data using a ReSTful API. This class implements
 * JAX-RS client functionality.
 * 
 * TODO This class can eventually be declared as a Spring singleton bean. 
 * The idea is that the bean will eventually be *injected* where it is needed, 
 * at which time we can remove the code here for implementing the singleton 
 * design pattern.
 */
public class OboMonitoringData {

	private static final Logger logger = LoggerFactory.getLogger(OboMonitoringData.class);
	private static final OboMonitoringData instance = new OboMonitoringData();
	private static final long OBO_FLOW_REST_CACHE_VALID_MILLIS = 2000;

	private Map<String, CachedData> cache = new ConcurrentHashMap<>();

	//	private URL restURI;

	/*
	 *  Private constructor to avoid client applications using "new".
	 */
	private OboMonitoringData() {
	}

	public static OboMonitoringData getInstance() {
		return instance;
	}

	/*
	 *  This code is based on:
	 *  	http://www.java8.org/caching-with-ConcurrentHashMap-in-java-8.html
	 *  TODO As soon as I can run on Java 8, improve it using the algorithm
	 *  described there, which eliminates "synchronized (cache)" and makes use of:
	 *  	ConcurrentHashMap.computeIfAbsent(...).
	 *  
	 *  TODO Ensure that either this is thread-safe or that there will not be multi-threaded access:
	 */
	public Object getRestData(URL restURI) {

		//		return new Object();

		long now = System.currentTimeMillis();

		CachedData cachedData = cache.get(restURI.toString());
		/*
		 * If there is nothing in the cache for the given ReSTful URI, or if 
		 * there *is* something in the cache, but it is too old...
		 */
		if (cachedData == null || cachedData.getValidAt() < now - OBO_FLOW_REST_CACHE_VALID_MILLIS) {
			synchronized (cache) {
				cachedData = cache.get(restURI.toString());
				if (cachedData == null || cachedData.getValidAt() < now - OBO_FLOW_REST_CACHE_VALID_MILLIS) {

					/*
					 * If the cached data has merely expired, we reuse the 
					 * cachedData object.
					 */
					if (cachedData == null) {
						cachedData = new CachedData();
					}

					//TODO Extract code for ReSTful call into a separate method.

					logger.info("msg=\"Retrieving monitoring data\", restEndpoint=\"{}\"", restURI);

					ClientConfig clientConfig = new ClientConfig()
							//	.register(JsonProcessingFeature.class)
							.property(JsonGenerator.PRETTY_PRINTING, true);

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
					 * uncommented here, but it is not necessary to do so. What is necessary
					 * is that the jersey-media-json-processing Maven artifact is specified
					 * in pom.xml; otherwise, an exception will be thrown when a JSON object
					 * is requested from a JAX-RS call.
					 */
					Client client = ClientBuilder.newClient(clientConfig);

					/*
					 * Alternate technique to build Client object.
					 */
					//		Client client = ClientBuilder.newBuilder()
					//				// .register(JsonProcessingFeature.class)
					//				.property(JsonGenerator.PRETTY_PRINTING, true)
					//				.build();

					/*
					 * Build a JAX-RS resource (instance of WebTarget).
					 */
					WebTarget webTarget = client.target(restURI.toString());

					/*
					 * Set up HTTP request invocation.
					 */
					Invocation.Builder invocationBuilder = webTarget
							.request(MediaType.APPLICATION_JSON_TYPE);
					//	.header("some-header", "some-value");

					/*
					 * Retrieve response from remote ReSTful server. The 
					 * connection remains open until the response entity is read
					 * below.
					 */
					Response response = invocationBuilder.get();

					/*
					 * Much of the above can be implemented as a single command 
					 * using chained method calls, although this assumes that a
					 * ClientConfig object has first been constructed.
					 */
					//		Response response = ClientBuilder.newClient(clientConfig)
					//				.target(restURI)
					//				.request(MediaType.APPLICATION_JSON_TYPE)
					//				//	.header("some-header", "some-value")
					//				.get();
					/*
					 * Or all of the above can be combined into a single command
					 * _without_ first building a ClientConfig object:
					 */
					//		Response response = ClientBuilder.newBuilder()
					//				// .register(JsonProcessingFeature.class)
					//				.property(JsonGenerator.PRETTY_PRINTING, true)
					//				.build()
					//				.target(restURI)
					//				.request(MediaType.APPLICATION_JSON_TYPE)
					//				//	.header("some-header", "some-value")
					//				.get();

					int status = response.getStatus();
					logger.info("HttpStatus = {}", status);
					/*
					 * TODO Can response.readEntity(...) return a JAXB bean directly 
					 * instead of us creating such a bean by parsing a JsonObject
					 * object using the Java API for JSON Processing, as we do below?
					 * To do this, we might need to implement a custom MessageBodyReader,
					 * as described here: 
					 * https://jersey.java.net/documentation/latest/message-body-workers.html#d0e7011
					 * although I am not sure that this reference covers everything
					 * we need to do.
					 */
					JsonObject oboFlowManagerJsonObject = response.readEntity(JsonObject.class);	// this closes the connection

					//TODO Must treat status codes other than 200 (OK) as well as unexpected exceptions.

					/*
					 * Alternate technique to get the response entity from 
					 * webTarget without using an Invocation.Builder or Response 
					 * object:
					 */
					//		JsonObject oboFlowManagerJsonObject = webTarget.request().get(JsonObject.class);

					/*
					 * Parse JSON response entity oboFlowManagerJsonObject into an 
					 * OboFlowManager instance. This involves parsing JSON elements
					 * into OboStepManager, OboStepQueue and ResourceLink instances
					 * as well.
					 * TODO Parse the data in another method?
					 */

					OboFlowManager oboFlowManager = new OboFlowManager();

					oboFlowManager.setName(oboFlowManagerJsonObject.getString("name"));
					oboFlowManager.setNumberOfProcessedPassages(
							oboFlowManagerJsonObject.getInt("numberOfProcessedPassages"));
					oboFlowManager.setNumberOfFailedPassages(oboFlowManagerJsonObject.getInt("numberOfFailedPassages"));
					oboFlowManager.setTotalTimeSpent(oboFlowManagerJsonObject.getInt("totalTimeSpent"));
					oboFlowManager.setChildrenCount(oboFlowManagerJsonObject.getInt("childrenCount"));

					/*
					 * Parse all of the "step managers" of the main "flow manager".
					 */
					JsonArray subStatisticsJsonArray = oboFlowManagerJsonObject.getJsonArray("subStatistics");
					List<OboStepManager> subStatistics = new ArrayList<>();
					for (int i = 0; i < subStatisticsJsonArray.size(); i++) {
						JsonObject subStatisticsJsonObject = subStatisticsJsonArray.getJsonObject(i);
						OboStepManager oboStepManager = new OboStepManager();
						oboStepManager.setName(subStatisticsJsonObject.getString("name"));
						oboStepManager.setNumberOfProcessedPassages(
								subStatisticsJsonObject.getInt("numberOfProcessedPassages"));
						oboStepManager.setNumberOfFailedPassages(
								subStatisticsJsonObject.getInt("numberOfFailedPassages"));
						oboStepManager.setTotalTimeSpent(subStatisticsJsonObject.getInt("totalTimeSpent"));
						oboStepManager.setChildrenCount(subStatisticsJsonObject.getInt("childrenCount"));

						/*
						 * stepManagerSubStatisticsJsonArray is not currently used here
						 * because the structure of the subStatistics JSON array elements
						 * for step managers has not yet been defined. As soon as this
						 * element is used, a warning will be logged below as a reminder
						 * to write the necessary code to treat it.
						 */
						JsonArray stepManagerSubStatisticsJsonArray =
								subStatisticsJsonObject.getJsonArray("subStatistics");
						if (stepManagerSubStatisticsJsonArray.size() > 0) {
							/*
							 * TODO Add subStatistics element to class OboStepManager.
							 * If this warning is triggered, it means that the substatistics 
							 * are now defined for step managers. Therefore, it is now time
							 * to treat this JSON data, as is done for the parent "flow manager".
							 */
							logger.warn("stepManagerSubStatisticsJsonArray.size() = {}",
									stepManagerSubStatisticsJsonArray.size());
						}

						/*
						 * Step manager queues:
						 */
						JsonArray stepManagerQueuesJsonArray = subStatisticsJsonObject.getJsonArray("queues");
						List<OboStepQueue> stepManagerQueues = new ArrayList<>();
						for (int j = 0; j < stepManagerQueuesJsonArray.size(); j++) {
							JsonObject stepManagerQueueJsonObject = stepManagerQueuesJsonArray.getJsonObject(j);
							OboStepQueue stepManagerOboStepQueue = new OboStepQueue();
							stepManagerOboStepQueue.setQueueName(stepManagerQueueJsonObject.getString("queueName"));
							stepManagerOboStepQueue.setCapacity(stepManagerQueueJsonObject.getInt("capacity"));
							stepManagerOboStepQueue.setCurrentSize(stepManagerQueueJsonObject.getInt("currentSize"));
							stepManagerOboStepQueue.setCurrentUtilization(
									stepManagerQueueJsonObject.getInt("currentUtilization"));
							stepManagerQueues.add(stepManagerOboStepQueue);
						}
						// logger.debug("stepManagerQueues = \"{}\"", stepManagerQueues);
						oboStepManager.setQueues(stepManagerQueues);

						/*
						 * Step manager links:
						 */
						JsonArray stepManagerLinksJsonArray = subStatisticsJsonObject.getJsonArray("links");
						List<ResourceLink> stepManagerLinks = new ArrayList<>();
						for (int j = 0; j < stepManagerLinksJsonArray.size(); j++) {
							JsonObject stepManagerLinkJsonObject = stepManagerLinksJsonArray.getJsonObject(j);
							ResourceLink stepManagerResourceLink = new ResourceLink();
							stepManagerResourceLink.setRel(stepManagerLinkJsonObject.getString("rel"));
							stepManagerResourceLink.setHref(stepManagerLinkJsonObject.getString("href"));
							stepManagerLinks.add(stepManagerResourceLink);
						}
						// logger.debug("stepManagerLinks = \"{}\"", stepManagerLinks);
						oboStepManager.setLinks(stepManagerLinks);

						logger.debug("oboStepManager = \"{}\"", oboStepManager);
						subStatistics.add(oboStepManager);
					}
					oboFlowManager.setSubStatistics(subStatistics);

					/*
					 * Parse the flow manager queues:
					 */
					JsonArray flowManagerQueuesJsonArray = oboFlowManagerJsonObject.getJsonArray("queues");
					List<OboStepQueue> flowManagerQueues = new ArrayList<>();
					for (int j = 0; j < flowManagerQueuesJsonArray.size(); j++) {
						JsonObject flowManagerQueueJsonObject = flowManagerQueuesJsonArray.getJsonObject(j);
						OboStepQueue flowManagerOboStepQueue = new OboStepQueue();
						flowManagerOboStepQueue.setQueueName(flowManagerQueueJsonObject.getString("queueName"));
						flowManagerOboStepQueue.setCapacity(flowManagerQueueJsonObject.getInt("capacity"));
						flowManagerOboStepQueue.setCurrentSize(flowManagerQueueJsonObject.getInt("currentSize"));
						flowManagerOboStepQueue.setCurrentUtilization(flowManagerQueueJsonObject
								.getInt("currentUtilization"));
						flowManagerQueues.add(flowManagerOboStepQueue);
					}
					// logger.debug("flowManagerQueues = \"{}\"", flowManagerQueues);
					oboFlowManager.setQueues(flowManagerQueues);

					/*
					 * Parse the flow manager links:
					 */
					JsonArray flowManagerLinksJsonArray = oboFlowManagerJsonObject.getJsonArray("links");
					List<ResourceLink> flowManagerLinks = new ArrayList<>();
					for (int j = 0; j < flowManagerLinksJsonArray.size(); j++) {
						JsonObject flowManagerLinkJsonObject = flowManagerLinksJsonArray.getJsonObject(j);
						ResourceLink flowManagerResourceLink = new ResourceLink();
						flowManagerResourceLink.setRel(flowManagerLinkJsonObject.getString("rel"));
						flowManagerResourceLink.setHref(flowManagerLinkJsonObject.getString("href"));
						flowManagerLinks.add(flowManagerResourceLink);
					}
					// logger.debug("flowManagerLinks = \"{}\"", flowManagerLinks);
					oboFlowManager.setLinks(flowManagerLinks);

					//					private List<ResourceLink> links;

					logger.info("oboFlowManager=\"{}\"", oboFlowManager);

					cachedData.setValidAt(now);
					cachedData.setRestUriData(oboFlowManager);

					cache.put(restURI.toString(), cachedData);
				} else {
					logger.info("restEndpoint=\"{}\", cacheValidAt=\"{}\", now=\"{}\"",
							restURI, cachedData.getValidAt(), now);
				}
			}
		} else {
			logger.info("restEndpoint=\"{}\", cacheValidAt=\"{}\", now=\"{}\"",
					restURI, cachedData.getValidAt(), now);
		}
		return cachedData.getRestUriData();
	}

	public static void main(String[] args) {

		//TODO Load ReST URI details from Java property file or the reporting database.
		URL restURI;
		try {
			restURI = new URL("http", "localhost", 8080, "/flow");
			OboMonitoringData oboMonitoringData = null;
			OboFlowManager oboFlowManager = null;

			oboMonitoringData = OboMonitoringData.getInstance();
			oboFlowManager = (OboFlowManager) oboMonitoringData.getRestData(restURI);
			System.out.println("oboFlowManager = " + oboFlowManager);

			oboMonitoringData = OboMonitoringData.getInstance();
			oboFlowManager = (OboFlowManager) oboMonitoringData.getRestData(restURI);
			System.out.println("oboFlowManager = " + oboFlowManager);

			oboMonitoringData = OboMonitoringData.getInstance();
			oboFlowManager = (OboFlowManager) oboMonitoringData.getRestData(restURI);
			System.out.println("oboFlowManager = " + oboFlowManager);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

}