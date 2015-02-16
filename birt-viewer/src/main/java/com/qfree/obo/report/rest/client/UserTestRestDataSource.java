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
import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.jsonp.JsonProcessingFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserTestRestDataSource {

	private static final Logger logger = LoggerFactory.getLogger(UserTestRestDataSource.class);

	//TODO Make this method private after we get everything working
	public List<UserTest> getUsers(String URL) {

		List<UserTest> users = new ArrayList<>();

		/*
		 * Although this statement will execute OK, an exception will be thrown 
		 * when a JSON object is requested from a JAX-RS call. To avoid this
		 * problem, we need to register JSON-P as a provider, as done below.
		 * 
		 */
		//	Client client = ClientBuilder.newClient();

		/* This is the way to create a Client object as described here:
		 * http://www.adam-bien.com/roller/abien/entry/configuring_jax_rs_2_01
		 * 
		 * JsonProcessingFeature is a feature to register JSON-P providers. This
		 * enables a binding between JAX-RS and the Java API for JSON Processing,
		 * which enables JAX-RS return JSON objects.
		 */
		Client client = ClientBuilder.newBuilder().register(JsonProcessingFeature.class)
				.property(JsonGenerator.PRETTY_PRINTING, true).build();

		WebTarget path = client.target(URL);
		JsonArray jsonArray = path.request().get(JsonArray.class);

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
		List<UserTest> users = new UserTestRestDataSource()
				.getUsers("http://jsonplaceholder.typicode.com/users");
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