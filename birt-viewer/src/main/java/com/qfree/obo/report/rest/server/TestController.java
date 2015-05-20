package com.qfree.obo.report.rest.server;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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
public class TestController extends AbstractBaseController {

	private static final Logger logger = LoggerFactory.getLogger(TestController.class);

	private final ConfigurationService configurationService;

	@Autowired
	public TestController(ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
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

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -i -H "Accept: text/plain;v=1" -X GET http://localhost:8080/rest/test/api_version
	 * 
	 * @Transactional is used to avoid org.hibernate.LazyInitializationException
	 * being thrown when evaluating ...
	 */
	/**
	 * ReST endpoint that can be used to confirm that the API version is being
	 * specified correctly by a client.
	 * 
	 * The response entity is the version number specified in the request.
	 *  
	 * @param acceptHeader
	 * @return
	 */
	@GET
	@Path("/api_version")
	@Produces(MediaType.TEXT_PLAIN)
	public String acceptHeaderApiVersionGet(@HeaderParam("Accept") String acceptHeader) {
		//		logger.info("acceptHeader = {}", acceptHeader);
		//		System.out.println("acceptHeaderApiVersionGet: acceptHeader = " + acceptHeader);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v2);
		//		logger.info("apiVersion, apiVersion.getVersion() = {}, {}", apiVersion, apiVersion.getVersion());
		//		System.out.println("acceptHeaderApiVersionGet: apiVersion, apiVersion.getVersion() = "
		//				+ apiVersion + ", " + apiVersion.getVersion());
		return apiVersion.getVersion();
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -i -H "Accept: text/plain;v=1" -X POST http://localhost:8080/rest/test/api_version
	 */
	@POST
	@Path("/api_version")
	@Produces(MediaType.TEXT_PLAIN)
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
	@Produces(MediaType.TEXT_PLAIN)
	public String formPostProduceText(
			@HeaderParam("Accept") String acceptHeader,
			@FormParam("param1") String param1,
			@FormParam("param2") String param2) {
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v2);
		return "(" + param1 + ", " + param2 + "): " + apiVersion;
	}

	@GET
	@Path("/string_param_default")
	@Produces(MediaType.TEXT_PLAIN)
	public String getTestStringParamDefault(@HeaderParam("Accept") String acceptHeader) {
		//		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v2);
		//		Object stringValueDefaultObject = configurationService.get(ParamName.TEST_STRING);
		//		String stringParam = null;
		//		if (stringValueDefaultObject instanceof String) {
		//			stringParam = (String) stringValueDefaultObject;
		//		}
		//		return stringParam;
		return configurationService.get(ParamName.TEST_STRING, null, String.class);
	}

	@GET
	@Path("/string_param_default")
	@Produces(MediaType.APPLICATION_JSON)
	public String getTestStringParamDefaultAsJson(@HeaderParam("Accept") String acceptHeader) {
		//		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v2);
		String stringValue = configurationService.get(ParamName.TEST_STRING, null, String.class);


		//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
		//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
		//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX


		return configurationService.get(ParamName.TEST_STRING, null, String.class);
	}

	@POST
	@Path("/string_param_default")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	public String postTestStringParamDefault(
			@HeaderParam("Accept") String acceptHeader,
			@FormParam("paramValue") String newParamValue) {
		//		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v2);
		/*
		 * Update parameter's default value.
		 */
		configurationService.set(ParamName.TEST_STRING, newParamValue);
		/*
		 * Return updated value.
		 */
		return configurationService.get(ParamName.TEST_STRING, null, String.class);
	}

	@PUT
	@Path("/string_param_default")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String putTestStringParamDefault(
			@HeaderParam("Accept") String acceptHeader,
			String newParamValue) {
		//		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v2);
		/*
		 * Update parameter's default value.
		 */
		configurationService.set(ParamName.TEST_STRING, newParamValue);
		/*
		 * Return updated value.
		 */
		return configurationService.get(ParamName.TEST_STRING, null, String.class);
	}

	//TODO USE @PUT TO RETURN A JSON object?????????????????

	//TODO USE @PUT TO accept a JSON object, e.g., a new Configuration and then later a new Role?
	//		Insert into DB and then RETURN A JSON object?????????????????

}
