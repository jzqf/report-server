package com.qfree.obo.report.rest.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

public class UserTestRestDataSource {

	private static final Logger logger = LoggerFactory.getLogger(UserTestRestDataSource.class);

	/*
	 * TODO This URI should be loaded from a properties file or from a database.
	 */
	private static final String REST_URI = "http://jsonplaceholder.typicode.com/users";

	private List<UserTest> getUsers(String restURI) {

		logger.info("restURI=\"{}\"", restURI);

		List<UserTest> users = new ArrayList<>();

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
		WebTarget webTarget = client.target(restURI);

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
		JsonArray jsonArray = response.readEntity(JsonArray.class);	// this closes the connection

		/*
		 * Alternate technique to get the response entity from 
		 * webTarget without using an Invocation.Builder or Response 
		 * object:
		 */
		//		JsonArray jsonArray = webTarget.request().get(JsonArray.class);

		for (int i = 0; i < jsonArray.size(); i++) {
			JsonObject jsonObject = jsonArray.getJsonObject(i);
			UserTest user = new UserTest();
			user.setId(jsonObject.getJsonNumber("id").longValue());
			user.setName(jsonObject.getString("name"));

			logger.info("user = {}", user);

			users.add(user);
		}
		return users;
	}
	
	private Iterator<UserTest> iterator;

	/* The "open" method will be called by BIRT engine once when the report 
	 * is invoked. It is a mandatory method.
	 */
	public void open(Object appContext, Map<String, Object> dataSetParamValues) {
		List<UserTest> users = getUsers(REST_URI);
		this.iterator = users.iterator();
	}

	/* The "next" method is called by the BIRT engine once for each row of the
	 * data set . It is a mandatory method.
	 */
	public Object next() {
		if (iterator.hasNext()) {
			return iterator.next();
		}
		return null;
	}

	/* The "close" method is called by the BIRT engine once at the end of the 
	 * report. It is a mandatory method.
	 */
	public void close() {
		this.iterator = null;
	}

	public static void main(String[] args) {

		UserTestRestDataSource userTestRestDataSource = new UserTestRestDataSource();

		userTestRestDataSource.open(null, new HashMap<String, Object>());
		while (userTestRestDataSource.iterator.hasNext()) {
			UserTest user = (UserTest) userTestRestDataSource.next();
			System.out.println("user = " + user);
		}

		userTestRestDataSource.close();

	}
} 