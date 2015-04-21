package com.qfree.obo.report.rest.server;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.qfree.obo.report.domain.Configuration.ParamName;
import com.qfree.obo.report.rest.server.RestUtils.RestApiVersion;
import com.qfree.obo.report.service.ConfigurationService;

@Component
@Path("/test")
public class TestController {

	private static final Logger logger = LoggerFactory.getLogger(TestController.class);

	private final ConfigurationService configurationService;

	@Autowired
	public TestController(ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	@GET
	@Produces("text/plain")
	public String test(@HeaderParam("Accept") String acceptHeader) {
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v2);
		switch (apiVersion) {
		case v1:
			/*
			 * Code for API v1:
			 */
			break;
		default:
			/*
			 * Code for default API version as well as unrecognized version from 
			 * "Accept" header:
			 */
		}
		return "/test endpoint: API version " + apiVersion.getVersion();
	}

	@GET
	@Path("/api_version")
	@Produces("text/plain")
	public String acceptHeaderApiVersionGet(@HeaderParam("Accept") String acceptHeader) {
		//		logger.info("acceptHeader = {}", acceptHeader);
		//		System.out.println("acceptHeaderApiVersionGet: acceptHeader = " + acceptHeader);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v2);
		//		logger.info("apiVersion, apiVersion.getVersion() = {}, {}", apiVersion, apiVersion.getVersion());
		//		System.out.println("acceptHeaderApiVersionGet: apiVersion, apiVersion.getVersion() = "
		//				+ apiVersion + ", " + apiVersion.getVersion());
		return apiVersion.getVersion();
	}

	@POST
	@Path("/api_version")
	@Produces("text/plain")
	public String acceptHeaderApiVersionPost(@HeaderParam("Accept") String acceptHeader) {
		//		logger.info("acceptHeader = {}", acceptHeader);
		//		System.out.println("acceptHeaderApiVersionPost: acceptHeader = " + acceptHeader);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v3);
		//		logger.info("apiVersion, apiVersion.getVersion() = {}, {}", apiVersion, apiVersion.getVersion());
		//		System.out.println("acceptHeaderApiVersionPost: apiVersion, apiVersion.getVersion() = "
		//				+ apiVersion + ", " + apiVersion.getVersion());
		return apiVersion.getVersion();
	}

	@POST
	@Path("/form")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces("text/plain")
	public String formPostProduceText(
			@HeaderParam("Accept") String acceptHeader,
			@FormParam("param1") String param1,
			@FormParam("param2") String param2) {
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v2);
		return "(" + param1 + ", " + param2 + "): " + apiVersion;
	}

	//TODO USE @GET TO RETURN A JSON object?????????????????

	//TODO USE @POST TO RETURN A JSON object?????????????????

	//TODO USE @PUT TO accept a JSON object, e.g., a new role?, insert innto DB and then RETURN A JSON object?????????????????

	// BREAK THIS UP INTO A POST?  (DIRTIES CONSTEXT), PUT? (DIRTIES CONSTEXT) AND A GET (DOES NOT DIRTY CONTEXT), AND THEN TEST WITH TWO SEPARATE TEST METHODS
	@GET
	@Path("/string_param_default")
	@Produces("text/plain")
	public String getTestStringParamDefault(@HeaderParam("Accept") String acceptHeader) {
		//		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v2);
		//		configurationService.set(ParamName.TEST_STRING, "Default value for ParamName.TEST_STRING");
		Object stringValueDefaultObject = configurationService.get(ParamName.TEST_STRING);
		String stringParam = null;
		if (stringValueDefaultObject instanceof String) {
			stringParam = (String) stringValueDefaultObject;
		}
		return stringParam;
	}

}
