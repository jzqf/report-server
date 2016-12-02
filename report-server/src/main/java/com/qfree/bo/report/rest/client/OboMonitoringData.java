package com.qfree.bo.report.rest.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.stream.JsonGenerator;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.bo.report.exceptions.RestDataSourceException;

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

	/*
	 * TODO These constants should be loaded from a properties file or the reporting database.
	 * Use default properties, as described here:
	 * 
	 *     http://stackoverflow.com/questions/1044855/load-java-properties-inside-static-intializer-block
	 */

	private static String REST_SCHEME = "http";
	private static String REST_HOST = "localhost";
	private static int REST_PORT = 8080;
	private static String FLOW_MANAGER_REST_PATH = "/flow";

	private static long REST_CACHE_VALID_MILLIS = 2000;

	private Map<String, CachedData> cache = new ConcurrentHashMap<>();

	//	private URL restURI;

	/*
	 *  Private constructor to avoid client applications using "new".
	 */
	private OboMonitoringData() {
	}

	public static String getREST_SCHEME() {
		return REST_SCHEME;
	}

	public static String getREST_HOST() {
		return REST_HOST;
	}

	public static int getREST_PORT() {
		return REST_PORT;
	}

	public static String getFLOW_MANAGER_REST_PATH() {
		return FLOW_MANAGER_REST_PATH;
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
	 */
	public Object getRestData(URL restURI) {

		long now = System.currentTimeMillis();

		CachedData cachedData = cache.get(restURI.toString());
		/*
		 * If there is nothing in the cache for the given ReSTful URI, or if 
		 * there *is* something in the cache, but it is too old...
		 */
		if (cachedData == null || cachedData.getValidAt() < now - REST_CACHE_VALID_MILLIS) {
			synchronized (cache) {
				cachedData = cache.get(restURI.toString());
				if (cachedData == null || cachedData.getValidAt() < now - REST_CACHE_VALID_MILLIS) {

					/*
					 * If the cached data has merely expired, we reuse the 
					 * cachedData object; otherwise, we must create a new one.
					 */
					if (cachedData == null) {
						cachedData = new CachedData();
					}

					Object parsedJsonObject = null;
					String restPath = restURI.getPath();

					try {
						/*
						 * Each ReST URI is parsed using a custom code block. This is
						 * done for two reasons:
						 * 
						 * 1. The ReST response entity can potentially be either a
						 *    JsonObject or a JsonArray object. The appropriate 
						 *    class is hardwired in each code block.
						 * 
						 * 2. A custom method is used for parse the JSON response 
						 *    entity for each case. The custom method is hardwired
						 *    for each case.
						 */
						if (restPath.equalsIgnoreCase(FLOW_MANAGER_REST_PATH)) {
							JsonObject oboFlowManagerJsonObject = getRestResource(restURI, JsonObject.class);
							/*
							 * Parse the JSON response entity oboFlowManagerJsonObject into
							 * an OboFlowManager instance. This involves also parsing JSON 
							 * elements into OboStepManager, OboStepQueue and ResourceLink 
							 * instances as well.
							 * 
							 * Here, parseOboFlowManagerJson returns an OboFlowManager
							 * object, but we store this in a variable of type Object so 
							 * that any type can be handled below.
							 */
							parsedJsonObject = parseOboFlowManagerJson(oboFlowManagerJsonObject);
						} else {
							RestDataSourceException e = new RestDataSourceException("Untreated ReST path: " + restPath);
							logger.error("Exception thrown:", e);
							throw e;
						}
					} catch (ProcessingException e) {
						/*
						 * This exception will be thrown if there is a processing 
						 * failure of the HTTP ReST request or response processing.
						 * This can be caused by, among other things:
						 * 
						 *  - Incorrect ResT URI, restURI (host, port, path)
						 *  - ReST server is down
						 *  - Inaccessible ReST server (firewall or other network problem)
						 */
						parsedJsonObject = null;
					}

					cachedData.setValidAt(now);
					cachedData.setRestUriData(parsedJsonObject);

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

	/**
	 * 
	 * @param restURI
	 * @param jsonClass	The class of the JSON object to return. This will, in
	 * 					general be JsonObject.class or JsonArray.class.
	 * @return
	 */
	//	private JsonObject getRestResource(URL restURI) {
	private <T> T getRestResource(URL restURI, Class<T> jsonClass) {

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

		int httpStatus = response.getStatus();

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
		T jsonValue = response.readEntity(jsonClass);	// this closes the connection

		if (httpStatus == 200) {
			logger.info("httpStatus={}", httpStatus);
		} else {
			logger.error("httpStatus={}, restURI=\"{}\"", httpStatus, restURI);
			//TODO Should we set jsonValue=null or throw an exception here?
		}

		/*
		 * Alternate technique to get the response entity from 
		 * webTarget without using an Invocation.Builder or Response 
		 * object:
		 */
		// T jsonValue = webTarget.request().get(jsonClass);	// this closes the connection
		return jsonValue;
	}

	/**
	 * @param oboFlowManagerJsonObject
	 * @return
	 */
	private OboFlowManager parseOboFlowManagerJson(JsonObject oboFlowManagerJsonObject) {

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

		logger.info("oboFlowManager=\"{}\"", oboFlowManager);
		return oboFlowManager;
	}

	public static void main(String[] args) {

		//TODO Load ReST URI details from Java property file or the reporting database.
		URL restURI;
		try {
			restURI = new URL(REST_SCHEME, REST_HOST, REST_PORT, FLOW_MANAGER_REST_PATH);
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