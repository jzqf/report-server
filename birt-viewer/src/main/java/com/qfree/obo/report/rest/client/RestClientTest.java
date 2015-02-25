package com.qfree.obo.report.rest.client;

import javax.json.JsonArray;
import javax.json.stream.JsonGenerator;
//import javax.json.stream.JsonGenerator;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

// For this, we must uncomment "jersey-media-json-processing" Maven depenency:
//import org.glassfish.jersey.jsonp.JsonProcessingFeature;

public class RestClientTest {

	public JsonArray get() {

		/* This is the way to create a Client object as described here:
		 * http://www.adam-bien.com/roller/abien/entry/configuring_jax_rs_2_01
		 */
		Client client = ClientBuilder.newBuilder()
				//	.register(JsonProcessingFeature.class)
				.property(JsonGenerator.PRETTY_PRINTING, true).build();

		//		Client client = ClientBuilder.newClient();	// <-- Exception thrown!!!!!!!!!!!!!!

		//		WebTarget path = this.client.target("...");
		WebTarget path = client.target("http://jsonplaceholder.typicode.com/users");
		return path.request().get(JsonArray.class);
	}

}
